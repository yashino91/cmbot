package at.chaoticbits.updateshandlers

import at.chaoticbits.coinmarket.*
import at.chaoticbits.config.Bot
import at.chaoticbits.config.Commands
import at.chaoticbits.database.DatabaseManager
import mu.KotlinLogging
import org.telegram.telegrambots.api.methods.AnswerInlineQuery
import org.telegram.telegrambots.api.methods.BotApiMethod
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.api.objects.inlinequery.InlineQuery
import org.telegram.telegrambots.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent
import org.telegram.telegrambots.api.objects.inlinequery.result.InlineQueryResult
import org.telegram.telegrambots.api.objects.inlinequery.result.InlineQueryResultArticle
import org.telegram.telegrambots.api.objects.replykeyboard.ForceReplyKeyboard
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.exceptions.TelegramApiException
import org.telegram.telegrambots.exceptions.TelegramApiRequestException
import org.telegram.telegrambots.updateshandlers.SentCallback
import java.io.UnsupportedEncodingException
import java.util.*
import java.util.Collections.synchronizedSet
import kotlin.concurrent.scheduleAtFixedRate



/**
 * Crypto Polling Bot, that processes currency requests
 */
private val log = KotlinLogging.logger {}
open class CryptoHandler(defaultBotOptions: DefaultBotOptions) : TelegramLongPollingBot(defaultBotOptions) {


    /**
     * Holds a Triple(chatId, MessageId, date) of each sent photo message until they are deleted
     * Only used if autoclearMessages is set to true.
     */
    @Volatile
    private var sentPhotoMessages: MutableSet<Triple<Long, Int, Int>> = synchronizedSet(mutableSetOf())

    private val WAITING_BOT_TOKEN = 0
    private val WAITING_NOTIFICATION = 1


    /**
     * Start schedulers
     */
    init {

        Timer().scheduleAtFixedRate(CoinMarketScheduler(),0,  60 * 60 * 1000)
        if (Bot.config.autoclearMessages) {
            Timer().scheduleAtFixedRate(0, 10 * 1000) { clearOldPhotoMessages() }
        }

        DatabaseManager
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
            else if (update.hasMessage() && update.message.hasText())
                this.handleIncomingMessage(update.message)

        } catch (e: TelegramApiException) {
            log.error { e.message }
        }
    }


    @Throws(TelegramApiException::class)
    private fun handleIncomingMessage(message: Message) {

        val command = message.text


        // handle reply messages / execute async
        if (message.isReply ||command.startsWith(Commands.pushNotification))
            this.handleReplyMessages(message, command)

        //handle one time message requests
        else if (message.text.startsWith("/")) {
            DatabaseManager.saveChatIfNotExist(message.chatId)

            val sendMessageRequest  = initSendMessageRequest(message.chatId)

            when {
                command.startsWith(Commands.coin) ->
                    sendMessageRequest.text = handleCoinRequest(message, command)
                command.substring(0, indexOfCommandEnd(command)) == Commands.start ->
                    sendMessageRequest.text = startCommand()
                command.substring(0, indexOfCommandEnd(command)) == Commands.help ->
                    sendMessageRequest.text = helpCommand(this.botUsername)
                else ->
                    sendMessageRequest.text = commandNotFound(command)
            }

            if (sendMessageRequest.text != null)
                execute(sendMessageRequest)
        }
    }

    @Throws(TelegramApiException::class)
    fun handleReplyMessages(message: Message, command: String) {
        when {
            message.isReply && PushNotification.getStatus() == WAITING_BOT_TOKEN ->
                this.onReceiveBotToken(message, command)
            message.isReply && PushNotification.getStatus() == WAITING_NOTIFICATION ->
                this.onReceiveNotificationMessage(message, command)
            else ->
                this.handlePushNotificationStart(message, command)
        }
    }

    private fun onReceiveNotificationMessage(message: Message, command: String) {

        log.info { "Notification: $command" }
    }

    @Throws(TelegramApiException::class)
    fun handlePushNotificationStart(message: Message, command: String) {
        val forceReplyKeyboard = ForceReplyKeyboard()
        val sendMessageRequest = this.initSendMessageRequest(message.chatId)

        forceReplyKeyboard.selective = true
        sendMessageRequest.replyToMessageId = message.messageId
        sendMessageRequest.replyMarkup = forceReplyKeyboard
        sendMessageRequest.text = PushNotification.startMessage()


        executeAsync(sendMessageRequest, object : SentCallback<Message> {
            override fun onResult(method: BotApiMethod<Message>, sentMessage: Message?) {
                if (sentMessage != null)
                    PushNotification.setStatus(WAITING_BOT_TOKEN)
            }
            override fun onError(botApiMethod: BotApiMethod<Message>, e: TelegramApiRequestException) {}
            override fun onException(botApiMethod: BotApiMethod<Message>, e: Exception) {}
        })
    }

    @Throws(TelegramApiException::class)
    fun onReceiveBotToken(message: Message, command: String) {
        val forceReplyKeyboard = ForceReplyKeyboard()
        val sendMessageRequest = this.initSendMessageRequest(message.chatId)

        forceReplyKeyboard.selective = true
        sendMessageRequest.replyToMessageId = message.messageId
        sendMessageRequest.replyMarkup = forceReplyKeyboard
        sendMessageRequest.text = PushNotification.authorize(
                message.from.id,
                this.botToken,
                command
        )


        executeAsync(sendMessageRequest, object : SentCallback<Message> {
            override fun onResult(method: BotApiMethod<Message>, sentMessage: Message?) {
                if (sentMessage != null)
                    PushNotification.setStatus(WAITING_NOTIFICATION)
            }
            override fun onError(botApiMethod: BotApiMethod<Message>, e: TelegramApiRequestException) {}
            override fun onException(botApiMethod: BotApiMethod<Message>, e: Exception) {}
        })
    }



    @Throws(TelegramApiException::class)
    fun handleCoinRequest(message: Message, command: String): String? {

        try {

            val msg = sendPhoto(coinCommand(message, command.substring(Commands.coin.length, indexOfCommandEnd(command))))
            if (Bot.config.autoclearMessages)
                sentPhotoMessages.add(Triple(msg.chatId, msg.messageId, msg.date))

            return null

        } catch (e: Exception) {
            return when(e) {
                is IllegalStateException, is UnsupportedEncodingException -> {
                    log.error { e.message }
                    e.message
                }
                is CurrencyNotFoundException -> {
                    log.warn { e.message }
                    e.message
                }
                else -> throw e
            }
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



    /**
     * Takes care of clearing old sent photo messages
     */
    private fun clearOldPhotoMessages() {
        val unixTime = System.currentTimeMillis() / 1000L

        // filter for outdated messages and call DeleteMessage API for each of them
        val toBeCleared = this.sentPhotoMessages.filter {
            unixTime > it.third + Bot.config.autoclearMessagesDurationSec
        }
        if (toBeCleared.isEmpty())
            return

        log.debug { "Clearing ${toBeCleared.size} sent photo messages from history"}
        toBeCleared.forEach { execute(DeleteMessage(it.first, it.second)) }

        // remove all deleted messages from set
        this.sentPhotoMessages.removeAll(toBeCleared)
    }
}