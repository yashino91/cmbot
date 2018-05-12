package at.chaoticbits.updateshandlers

import at.chaoticbits.coinmarket.CoinMarketCapService
import at.chaoticbits.coinmarket.CurrencyDetails
import at.chaoticbits.coinmarket.CurrencyNotFoundException
import at.chaoticbits.render.HtmlImageService
import mu.KotlinLogging
import net.logstash.logback.marker.Markers
import org.telegram.telegrambots.api.methods.send.SendPhoto
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.exceptions.TelegramApiException
import java.io.UnsupportedEncodingException


private val log = KotlinLogging.logger {}



/**
 * Returns the welcome message for first time bot users
 */
fun startCommand() =
        "*Welcome to cmbot!*\n\n" +
                "I am programmed to give you the newest price information about all crypto currencies from CoinMarketCap.\n\n" +
                "Use /help to see a list of my supported commands"



/**
 * Returns the help command
 */
fun helpCommand(botUsername: String): String =
        "You can control me by sending these commands:\n\n" +
                "*Commands*\n" +
                "/coin currency *-* Request a coin from CoinMarketCap. *(i.e. /coin eth)*\n" +
                "/help *-* Display the current help\n\n" +
                "*Inline Queries*\n" +
                "This is the recommended way to request price information. " +
                "Just use @$botUsername to search through all coins on CoinMarketCap."



/**
 * Returns a command not found message
 */
fun commandNotFound(command: String): String = "Command not found: *$command*"



/**
 * Queries the given currency from CoinMarketCap and returns the result as a rendered image
 */
@Throws(IllegalStateException::class, UnsupportedEncodingException::class, CurrencyNotFoundException::class)
fun coinCommand(message: Message, currency: String): SendPhoto {

    val currencyDetails: CurrencyDetails = CoinMarketCapService.fetchCurrency(currency)

    logCurrencyRequest(message, currencyDetails, "image")

    val photo = SendPhoto()
    photo.setChatId(message.chatId)
    photo.setNewPhoto(currency, HtmlImageService.generateCryptoDetailsImage(currencyDetails))

    return photo
}



/**
 * Logs a currency request in json format for logging frameworks like logstash
 */
fun logCurrencyRequest(message: Message, currencyDetails: CurrencyDetails, type: String) {
    val loggingObjects = mapOf(
            "from" to message.from,
            "chat" to message.chat,
            "currency" to currencyDetails,
            "requestType" to type)

    log.info(Markers.appendEntries(loggingObjects), "${message.from} from ${message.chat} requested $currencyDetails, type='$type'}")
}
