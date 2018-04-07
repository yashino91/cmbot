package at.chaoticbits.updateshandlers

import at.chaoticbits.coinmarket.CoinMarketScheduler
import at.chaoticbits.config.Bot
import at.chaoticbits.coinmarket.CoinMarketCapService
import com.vdurmont.emoji.EmojiParser
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.methods.send.SendPhoto
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.exceptions.TelegramApiException
import org.telegram.telegrambots.logging.BotLogger
import java.io.UnsupportedEncodingException

import java.util.Timer


/**
 * Crypto Polling Bot, that processes currency requests
 */
class CryptoHandler : TelegramLongPollingBot() {

    companion object {

        private const val LOG_TAG = "CryptoHandler"
    }


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
            val message = update.message

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

                        sendMessageRequest.text = EmojiParser.parseToUnicode(
                                CoinMarketCapService.getFormattedCurrencyDetails(
                                        command.substring(Bot.config.stringCommand.length, getCurrencyEnd(command))))
                        sendMessage(sendMessageRequest)

                        // request currency details as a rendered image
                    } else if (!Bot.config.imageCommand.isEmpty() && command.startsWith(Bot.config.imageCommand)) {

                        val imageInputStream = CoinMarketCapService.getCurrencyDetailsImage(
                                command.substring(Bot.config.imageCommand.length, getCurrencyEnd(command)))

                        val photo = SendPhoto()
                        photo.setChatId(message.chatId)
                        photo.setNewPhoto(command, imageInputStream)
                        sendPhoto(photo)
                    }

                } catch (e: TelegramApiException) {
                    BotLogger.error(LOG_TAG, e.message)
                } catch (e: IllegalStateException) {
                    sendError(sendMessageRequest, e)
                } catch (e: UnsupportedEncodingException) {
                    sendError(sendMessageRequest, e)
                }
            }
        }
    }

    override fun getBotUsername(): String? {
        return Bot.config.botName
    }

    override fun getBotToken(): String {
        return System.getenv("CMBOT_TELEGRAM_TOKEN")
    }


    private fun getCurrencyEnd(command: String): Int {
        return if (command.indexOf('@') == -1) command.length else command.indexOf('@')
    }

    private fun sendError(sendMessageRequest: SendMessage, e: Exception) {

        val errorMessage = e.message
        BotLogger.error(LOG_TAG, errorMessage)

        // replace '_' characters because of telegram markdown
        sendMessageRequest.text = errorMessage?.replace("_".toRegex(), "\\\\_")

        try {
            sendMessage(sendMessageRequest)
        } catch (te: TelegramApiException) {
            BotLogger.error(LOG_TAG, te.message)
        }
    }

}
