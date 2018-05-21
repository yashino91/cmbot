package at.chaoticbits.config

import com.jdiazcano.cfg4k.bytebuddy.ByteBuddyBinder
import com.jdiazcano.cfg4k.providers.DefaultConfigProvider
import com.jdiazcano.cfg4k.providers.bind
import com.jdiazcano.cfg4k.sources.URLConfigSource
import com.jdiazcano.cfg4k.yaml.YamlConfigLoader



/**
 * Represents the Bot Configuration from the config.yaml file
 * in the resource directory
 */
interface Config {

    /**
     * Name of Telegram Bot
     */
    val botName: String

    /**
     * List of allowed currency slugs, that can be requested by coin market cap.
     */
    val allowedCurrencySlugs: List<String>

    /**
     * Indicates if sent telegram messages should be deleted after a given time
     * to keep the chat history clean.
     */
    val autoclearMessages: Boolean

    /**
     * Duration after which sent messages should be deleted from chat.
     * This is only respected if `autoclearMessages` is true.
     */
    val autoclearMessagesDurationSec: Int
}


/**
 * Static Bot Config Resolver
 */
object Bot {

    private val loader      = YamlConfigLoader(URLConfigSource(Config::class.java.getResource("/config.yaml")))
    private val provider    = DefaultConfigProvider(loader, binder = ByteBuddyBinder())

    var config = provider.bind<Config>()
}

/**
 * Supported Commands
 */
object Commands {
    const val start = "/start"
    const val help = "/help"
    const val coin = "/coin "
    const val pushNotification = "/pushNotification"
}