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
     *
     * @param update [Update] Represents an incoming update
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
                        log.error { e.message }
                        sendMessageRequest.text = escapeMessage(e.message)
                    } catch (e: UnsupportedEncodingException) {
                        log.error { e.message }
                        sendMessageRequest.text = escapeMessage(e.message)
                    } catch (e: CurrencyNotFoundException) {
                        log.warn { e.message }
                        sendMessageRequest.text = escapeMessage(e.message)
                    }

                    if (sendMessageRequest.text != null)
                        execute(sendMessageRequest)
                }
            }
        } catch (e: TelegramApiException) {
            log.error { e.message }
        }
    }

    /**
     * Initializes a SendMessage object with the given chatId
     *
     * @param chatId [Long] Users chat id
     * @return [SendMessage] Initialized SendMessage object
     */
    fun initSendMessageRequest(chatId: Long): SendMessage {
        val sendMessageRequest = SendMessage()

        sendMessageRequest.enableMarkdown(true)
        sendMessageRequest.setChatId(chatId)

        return sendMessageRequest
    }


    /**
     * Generates a AnswerInlineQuery for the given update (containing the client inline query)
     *
     * @param inlineQuery [InlineQuery] The incoming inline query
     * @return [AnswerInlineQuery] Contains a list of found coins on CoinMarketCap
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
     * Pocceses user commands that except a String as a response
     *
     * @param command [String] User command
     * @return [String] Contains the response according to the requested command
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
     *
     * @param command [String] Bot command
     * @return [Int] Index of command end
     */
    fun indexOfCommandEnd(command: String): Int =
            if (command.indexOf('@') == -1) command.length else command.indexOf('@')


    /**
     * Escapes the given string for sending it back to the telegram user
     *
     * @param message [String] Message
     * @return [String] Escaped message (For sending messages back to telegram user)
     */
    fun escapeMessage(message: String?): String = message!!.replace("_".toRegex(), "\\\\_")
}