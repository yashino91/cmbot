package at.chaoticbits.config;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents the Bot Configuration from the config.yaml file
 * in the resource directory
 */
public class Config {

    /**
     * Name of Telegram Bot
     */
    public String botName;

    /**
     * Command to request currency details as a rendered image
     */
    public String imageCommand;

    /**
     * Command to request currency details as a formatted string
     */
    public String stringCommand;

    /**
     * List of allowed currency slugs, that can be requested by coin market cap.
     */
    public List<String> allowedCurrencySlugs = new ArrayList<>();
}
