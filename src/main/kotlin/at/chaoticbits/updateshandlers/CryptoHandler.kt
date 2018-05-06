package at.chaoticbits.updateshandlers

import at.chaoticbits.coinmarket.CoinMarketCapService
import at.chaoticbits.coinmarket.CoinMarketScheduler
import at.chaoticbits.coinmarket.CurrencyDetails
import at.chaoticbits.coinmarket.CurrencyNotFoundException
import at.chaoticbits.config.Bot
import at.chaoticbits.render.HtmlImageService
import com.vdurmont.emoji.EmojiParser
import mu.KotlinLogging
import net.logstash.logback.marker.Markers
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.methods.send.SendPhoto
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
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


    override fun onUpdateReceived(update: Update) {

        //check if the update has a message
        if (update.hasMessage()) {
            val message: Message = update.message

            //check if the message has text. it could also  contain for example a location ( message.hasLocation() )
            if (message.hasText()) {

                //create a object that contains the information to send back the message
                val sendMessageRequest = SendMessage()
                sendMessageRequest.enableMarkdown(true)
                sendMessageRequest.setChatId(message.chatId!!)

                val command = message.text

                try {

                    // request currency details as a formatted string
                    if (!Bot.config.stringCommand.isEmpty() && command.startsWith(Bot.config.stringCommand)) {

                        val currencyDetails: CurrencyDetails = CoinMarketCapService.fetchCurrency(
                                command.substring(Bot.config.stringCommand.length, getCurrencyEnd(command)))

                        logCurrencyRequest(message, currencyDetails, "string")

                        sendMessageRequest.text = EmojiParser.parseToUnicode(
                                CoinMarketCapService.formatCurrencyResult(currencyDetails))
                        sendMessage(sendMessageRequest)

                        // request currency details as a rendered image
                    } else if (!Bot.config.imageCommand.isEmpty() && command.startsWith(Bot.config.imageCommand)) {

                        val currencyDetails: CurrencyDetails = CoinMarketCapService.fetchCurrency(command.substring(
                                Bot.config.imageCommand.length, getCurrencyEnd(command)))


                        logCurrencyRequest(message, currencyDetails, "image")

                        val photo = SendPhoto()
                        photo.setChatId(message.chatId)
                        photo.setNewPhoto(command, HtmlImageService.generateCryptoDetailsImage(currencyDetails))
                        sendPhoto(photo)
                    }

                } catch (e: TelegramApiException) {
                    log.error { e.message }
                } catch (e: IllegalStateException) {
                    sendFailure(sendMessageRequest, e, LogType.ERROR)
                } catch (e: UnsupportedEncodingException) {
                    sendFailure(sendMessageRequest, e, LogType.ERROR)
                } catch (e: CurrencyNotFoundException) {
                    sendFailure(sendMessageRequest, e, LogType.WARN)
                }
            }
        }
    }

    override fun getBotUsername(): String? =
            Bot.config.botName


    override fun getBotToken(): String =
            System.getenv("CMBOT_TELEGRAM_TOKEN")



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
            sendMessage(sendMessageRequest)
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