package at.chaoticbits.config

import com.jdiazcano.cfg4k.bytebuddy.ByteBuddyBinder
import com.jdiazcano.cfg4k.providers.DefaultConfigProvider
import com.jdiazcano.cfg4k.providers.bind
import com.jdiazcano.cfg4k.sources.URLConfigSource
import com.jdiazcano.cfg4k.yaml.YamlConfigLoader


/**
 * Static Bot Config Resolver
 */
object Bot {

    private val loader      = YamlConfigLoader(URLConfigSource(Config::class.java.getResource("/config.yaml")))
    private val provider    = DefaultConfigProvider(loader, binder = ByteBuddyBinder())

    var config = provider.bind<Config>()
}


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
     * Command to request currency details as a rendered image
     */
    val imageCommand: String

    /**
     * Command to request currency details as a formatted string
     */
    val stringCommand: String

    /**
     * List of allowed currency slugs, that can be requested by coin market cap.
     */
    val allowedCurrencySlugs: List<String>
}
