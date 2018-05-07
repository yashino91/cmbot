package at.chaoticbits.updateshandlers

import at.chaoticbits.coinmarket.*
import at.chaoticbits.config.Bot
import at.chaoticbits.config.Commands
import at.chaoticbits.render.HtmlImageService
import mu.KotlinLogging
import net.logstash.logback.marker.Markers
import org.telegram.telegrambots.api.methods.AnswerInlineQuery
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.methods.send.SendPhoto
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent
import org.telegram.telegrambots.api.objects.inlinequery.result.InlineQueryResult
import org.telegram.telegrambots.api.objects.inlinequery.result.InlineQueryResultArticle
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.exceptions.TelegramApiException
import java.io.UnsupportedEncodingException
import java.util.*


/**
 * Crypto Polling Bot, that processes currency requests
 */
private val log = KotlinLogging.logger {}
class CryptoHandler : TelegramLongPollingBot() {


    /**
     * Instantiate CryptoHandler and start coin market scheduler
     */
    init {

        val cmScheduler = CoinMarketScheduler()

        val initialDelay = 100
        val fixedRate = 60 * 60 * 1000 // every hour
        Timer().schedule(cmScheduler, initialDelay.toLong(), fixedRate.toLong())
    }


    override fun getBotUsername(): String? =
            Bot.config.botName


    override fun getBotToken(): String =
            System.getenv("CMBOT_TELEGRAM_TOKEN")


    override fun onUpdateReceived(update: Update) {

        if (update.hasInlineQuery())
            this.handleInlineQuery(update)
        else if (update.hasMessage())
            this.handleReceivedMessage(update)
    }


    /**
     * Handle Inline Queries by sending coin suggestions to the chat
     */
    private fun handleInlineQuery(update: Update) {

        val answerQuery = AnswerInlineQuery()
        answerQuery.cacheTime = 300
        answerQuery.inlineQueryId = update.inlineQuery.id

        val coinsOfInterest = CoinMarketContainer.findCoins(update.inlineQuery.query)
        if (coinsOfInterest.isNotEmpty()) {

            val inlineQueryResults: List<InlineQueryResult> = coinsOfInterest.take(50).map { it ->

                val inlineQueryResult = InlineQueryResultArticle()
                inlineQueryResult.id = UUID.randomUUID().toString()
                inlineQueryResult.title = "${it.symbol} (${it.name})"
                inlineQueryResult.thumbUrl = "https://s2.coinmarketcap.com/static/img/coins/64x64/${it.id}.png"

                inlineQueryResult.description = "Rank: ${it.rank}"
                val inputTextMessage = InputTextMessageContent()
                inputTextMessage.messageText = "/coin ${it.slug}"

                inlineQueryResult.inputMessageContent = inputTextMessage

                inlineQueryResult
            }

            answerQuery.results = inlineQueryResults

        } else {
            answerQuery.results = emptyList()
        }

        execute(answerQuery)
    }

    /**
     * Handle user messages with appropriate response
     */
    private fun handleReceivedMessage(update: Update) {
        val message: Message = update.message

        //check if the message has text. it could also  contain for example a location ( message.hasLocation() )
        if (message.hasText() && message.text.startsWith("/")) {

            //create a object that contains the information to send back the message
            val sendMessageRequest = SendMessage()
            sendMessageRequest.enableMarkdown(true)
            sendMessageRequest.setChatId(message.chatId)

            val command = message.text

            try {

                when {
                    command.startsWith(Commands.coin) ->
                        this.handleCoinRequest(sendMessageRequest, message, command.substring(Commands.coin.length, getCurrencyEnd(command)))
                    command.substring(0, getCurrencyEnd(command)) == Commands.start ->
                        this.handleStartRequest(sendMessageRequest)
                    command.substring(0, getCurrencyEnd(command)) == Commands.help ->
                        this.handleHelpRequest(sendMessageRequest)
                    else ->
                        this.handleCommandNotFound(sendMessageRequest, command)
                }

            } catch (e: TelegramApiException) {
                log.error { e.message }
            }
        }
    }


    /**
     * Queries the given currency from CoinMarketCap and sends the result as a rendered image
     */
    @Throws(TelegramApiException::class)
    private fun handleCoinRequest(sendMessageRequest: SendMessage, message: Message, currency: String) {

        try {

            val currencyDetails: CurrencyDetails = CoinMarketCapService.fetchCurrency(currency)

            logCurrencyRequest(message, currencyDetails, "image")

            val photo = SendPhoto()
            photo.setChatId(message.chatId)
            photo.setNewPhoto(currency, HtmlImageService.generateCryptoDetailsImage(currencyDetails))
            sendPhoto(photo)

        } catch (e: IllegalStateException) {
            sendFailure(sendMessageRequest, e, LogType.ERROR)
        } catch (e: UnsupportedEncodingException) {
            sendFailure(sendMessageRequest, e, LogType.ERROR)
        } catch (e: CurrencyNotFoundException) {
            sendFailure(sendMessageRequest, e, LogType.WARN)
        }
    }


    /**
     * Sends the help command
     */
    @Throws(TelegramApiException::class)
    private fun handleHelpRequest(sendMessageRequest: SendMessage) {
        sendMessageRequest.text = "You can control me by sending these commands:\n\n" +
                                  "*Commands*\n" +
                                  "/coin currency *-* Request a coin from CoinMarketCap. *(i.e. /coin eth)*\n" +
                                  "/help *-* Display the current help\n\n" +
                                  "*Inline Queries*\n" +
                                  "This is the recommended way to request price information. " +
                                  "Just use @${this.botUsername} to search through all coins on CoinMarketCap."

        execute(sendMessageRequest)
    }


    /**
     * Sends the welcome message for first time bot users
     */
    @Throws(TelegramApiException::class)
    private fun handleStartRequest(sendMessageRequest: SendMessage) {
        sendMessageRequest.text = "*Welcome to cmbot!*\n\n" +
                                  "I am programmed to give you the newest price information of all crypto currencies from CoinMarketCap.\n\n" +
                                  "Use /help to see a list of my supported commands"
        execute(sendMessageRequest)
    }


    /**
     * Send a command not found message
     */
    @Throws(TelegramApiException::class)
    private fun handleCommandNotFound(sendMessageRequest: SendMessage, command: String) {
        sendMessageRequest.text = "Command not found: *$command*"
        execute(sendMessageRequest)

    }


    private fun getCurrencyEnd(command: String): Int =
            if (command.indexOf('@') == -1) command.length else command.indexOf('@')


    private fun sendFailure(sendMessageRequest: SendMessage, e: Exception, type: LogType) {

        val errorMessage = e.message

        when(type) {
            LogType.WARN -> log.warn { errorMessage }
            LogType.ERROR -> log.error { errorMessage }
        }

        // replace '_' characters because of telegram markdown
        sendMessageRequest.text = errorMessage?.replace("_".toRegex(), "\\\\_")

        try {
            execute(sendMessageRequest)
        } catch (e: TelegramApiException) {
            log.error { e.message }
        }
    }

    private fun logCurrencyRequest(message: Message, currencyDetails: CurrencyDetails, type: String) {
        val loggingObjects = mapOf(
                "from" to message.from,
                "chat" to message.chat,
                "currency" to currencyDetails,
                "requestType" to type)

        log.info(Markers.appendEntries(loggingObjects), "${message.from} from ${message.chat} requested $currencyDetails, type='$type'}")
    }

}

enum class LogType {
    WARN,
    ERROR
}