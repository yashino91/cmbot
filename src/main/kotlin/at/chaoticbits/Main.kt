package at.chaoticbits

import at.chaoticbits.updateshandlers.CryptoHandler
import mu.KotlinLogging
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi
import org.telegram.telegrambots.exceptions.TelegramApiException
import org.telegram.telegrambots.generics.BotSession


private val log = KotlinLogging.logger {}
class Main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {

            // Exit if bot initialization failed
            if(initTelegramBot() == null) return
        }
    }
}


/**
 * Initializes Telegram Bot with the CryptoHandler
 */
fun initTelegramBot(): BotSession? {

    // no bot token specified
    if (System.getenv("CMBOT_TELEGRAM_TOKEN") == null) {
        log.error { "No Telegram Bot Token specified! Please declare a System Environment Variable with your Telegram API Key. CMBOT_TELEGRAM_TOKEN={YOUR_API_KEY}" }
        return null
    }

    ApiContextInitializer.init()
    val telegramBotsApi = TelegramBotsApi()

    try {

        // Register long polling bots. They work regardless type of TelegramBotsApi
        return telegramBotsApi.registerBot(CryptoHandler())

    } catch (e: TelegramApiException) {
        log.error { e.message }
        return null
    }
}

