package at.chaoticbits.config

import java.util.ArrayList


/**
 * Represents the Bot Configuration from the config.yaml file
 * in the resource directory
 */
class Config {

    /**
     * Name of Telegram Bot
     */
    var botName: String? = null

    /**
     * Command to request currency details as a rendered image
     */
    var imageCommand: String? = null

    /**
     * Command to request currency details as a formatted string
     */
    var stringCommand: String? = null

    /**
     * List of allowed currency slugs, that can be requested by coin market cap.
     */
    var allowedCurrencySlugs: List<String> = ArrayList()
}
