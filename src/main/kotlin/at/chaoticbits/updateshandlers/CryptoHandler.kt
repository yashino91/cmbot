package at.chaoticbits.updateshandlers

import at.chaoticbits.coinmarket.*
import at.chaoticbits.config.Bot
import at.chaoticbits.config.Commands
import mu.KotlinLogging
import org.slf4j.event.Level
import org.telegram.telegrambots.api.methods.AnswerInlineQuery
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.api.objects.inlinequery.InlineQuery
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
open class CryptoHandler : TelegramLongPollingBot() {


    /**
     * Instantiate CryptoHandler and start coin market scheduler
     */
    init {

        val cmScheduler = CoinMarketScheduler()

        val initialDelay = 100
        val fixedRate = 60 * 60 * 1000 // every hour
        Timer().schedule(cmScheduler, initialDelay.toLong(), fixedRate.toLong())
    }


    override fun getBotUsername(): String =
            Bot.config.botName


    override fun getBotToken(): String =
            System.getenv("CMBOT_TELEGRAM_TOKEN")


    /**
     * Listens on received client updates
     */
    override fun onUpdateReceived(update: Update) {

        try {

            // handle inline queries
            if (update.hasInlineQuery())
                execute(this.answerInlineQuery(update.inlineQuery))

            // handle received messages
            else if (update.hasMessage()) {

                val message: Message = update.message

                //check if the message has text
                if (message.hasText() && message.text.startsWith("/")) {

                    val sendMessageRequest  = initSendMessageRequest(message.chatId)
                    val command             = message.text

                    try {

                        if (command.startsWith(Commands.coin))
                            sendPhoto(coinCommand(message, command.substring(Commands.coin.length, indexOfCommandEnd(command))))
                        else
                            sendMessageRequest.text = textRequest(command)

                    } catch (e: IllegalStateException) {
                        sendMessageRequest.text = failure(e, Level.ERROR)
                    } catch (e: UnsupportedEncodingException) {
                        sendMessageRequest.text = failure(e, Level.ERROR)
                    } catch (e: CurrencyNotFoundException) {
                        sendMessageRequest.text = failure(e, Level.WARN)
                    }

                    if (sendMessageRequest.text != null)
                        execute(sendMessageRequest)
                }
            }
        } catch (e: TelegramApiException) {
            log.error { e.message }
        }
    }

    fun initSendMessageRequest(chatId: Long): SendMessage {
        val sendMessageRequest = SendMessage()

        sendMessageRequest.enableMarkdown(true)
        sendMessageRequest.setChatId(chatId)

        return sendMessageRequest
    }


    /**
     * Generates a AnswerInlineQuery for the given update (containing the client inline query)
     */
    fun answerInlineQuery(inlineQuery: InlineQuery): AnswerInlineQuery {

        val answerQuery = AnswerInlineQuery()
        answerQuery.cacheTime = 300
        answerQuery.inlineQueryId = inlineQuery.id

        val coinsOfInterest = CoinMarketContainer.findCoins(inlineQuery.query)
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

        return answerQuery
    }

    /**
     * Handle user messages with appropriate response
     */
    fun textRequest(command: String): String {

        return when {
            command.substring(0, indexOfCommandEnd(command)) == Commands.start ->
                startCommand()
            command.substring(0, indexOfCommandEnd(command)) == Commands.help ->
                helpCommand(this.botUsername)
            else ->
                commandNotFound(command)
        }
    }


    /**
     * Determines the end of a bot command
     */
    fun indexOfCommandEnd(command: String): Int =
            if (command.indexOf('@') == -1) command.length else command.indexOf('@')


    /**
     * Logs an exception with the given log level.
     * In addition this function returns the message escaped for telegram
     */
    fun failure(e: Exception, logLevel: Level): String? {

        val errorMessage = e.message

        when(logLevel) {
            Level.WARN -> log.warn { errorMessage }
            Level.ERROR -> log.error { errorMessage }
        }

        // replace '_' characters because of telegram markdown
        return errorMessage?.replace("_".toRegex(), "\\\\_")
    }


}