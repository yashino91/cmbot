package at.chaoticbits.config;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents the Bot Configuration from the config.yaml file
 * in the resource directory
 */
public class Config {

    public String botName;
    public List<String> allowedCurrencySlugs = new ArrayList<>();
}
