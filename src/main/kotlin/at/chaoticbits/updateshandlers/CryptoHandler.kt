package at.chaoticbits.updateshandlers

import at.chaoticbits.coinmarket.CoinMarketScheduler
import at.chaoticbits.config.Bot
import at.chaoticbits.coinmarket.CoinMarketCapService
import com.vdurmont.emoji.EmojiParser
import mu.KotlinLogging
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.methods.send.SendPhoto
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.exceptions.TelegramApiException
import java.io.UnsupportedEncodingException

import java.util.Timer


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

                        val currency = command.substring(Bot.config.stringCommand.length, getCurrencyEnd(command))

                        logCurrencyRequest(message, currency, "string")

                        sendMessageRequest.text = EmojiParser.parseToUnicode(
                                CoinMarketCapService.getFormattedCurrencyDetails(currency))
                        sendMessage(sendMessageRequest)

                        // request currency details as a rendered image
                    } else if (!Bot.config.imageCommand.isEmpty() && command.startsWith(Bot.config.imageCommand)) {

                        val currency = command.substring(Bot.config.imageCommand.length, getCurrencyEnd(command))

                        logCurrencyRequest(message, currency, "image")

                        val imageInputStream = CoinMarketCapService.getCurrencyDetailsImage(currency)

                        val photo = SendPhoto()
                        photo.setChatId(message.chatId)
                        photo.setNewPhoto(command, imageInputStream)
                        sendPhoto(photo)
                    }

                } catch (e: TelegramApiException) {
                    log.error { e.message }
                } catch (e: IllegalStateException) {
                    sendError(sendMessageRequest, e)
                } catch (e: UnsupportedEncodingException) {
                    sendError(sendMessageRequest, e)
                }
            }
        }
    }

    override fun getBotUsername(): String? =
            Bot.config.botName


    override fun getBotToken(): String =
            System.getenv("CMBOT_TELEGRAM_TOKEN")

    private fun logCurrencyRequest(message: Message, currency: String, type: String) =
            log.info { "${message.from} from ${message.chat} requested Currency{name='$currency', type='$type'}" }


    private fun getCurrencyEnd(command: String): Int =
            if (command.indexOf('@') == -1) command.length else command.indexOf('@')


    private fun sendError(sendMessageRequest: SendMessage, e: Exception) {

        val errorMessage = e.message

        log.error { errorMessage }

        // replace '_' characters because of telegram markdown
        sendMessageRequest.text = errorMessage?.replace("_".toRegex(), "\\\\_")

        try {
            sendMessage(sendMessageRequest)
        } catch (e: TelegramApiException) {
            log.error { e.message }
        }
    }

}
