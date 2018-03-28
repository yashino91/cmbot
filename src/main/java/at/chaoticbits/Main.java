package at.chaoticbits;

import at.chaoticbits.config.Bot;
import at.chaoticbits.config.Config;
import at.chaoticbits.updateshandlers.CryptoHandler;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;
import org.telegram.telegrambots.logging.BotsFileHandler;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Level;


/**
 * Main class to create bot
 */
public class Main {

    private static final String LOGTAG = "MAIN";

    private static CryptoHandler cryptoHandler;



    public static void main(String[] args) {

        BotLogger.setLevel(Level.ALL);
        try {
            BotLogger.registerLogger(new BotsFileHandler("./TelegramBots%g.%u.log"));
        } catch (IOException e) {
            BotLogger.severe(LOGTAG, e);
        }

        // exit if no telegram bot token is specified
        if (System.getenv("CMBOT_TELEGRAM_TOKEN") == null) {
            BotLogger.error(LOGTAG, "No Telegram Bot Token specified! Please declare a System Environment Variable with your Telegram API Key. CMBOT_TELEGRAM_TOKEN={YOUR_API_KEY}");
            return;
        }

        initialize();
    }


    /**
     * Initialize and register Telegram Bot
     */
    private static void initialize() {

        try {

            loadBotConfiguration();

            ApiContextInitializer.init();
            TelegramBotsApi telegramBotsApi = createLongPollingTelegramBotsApi();
            try {

                cryptoHandler = new CryptoHandler();

                // Register long polling bots. They work regardless type of TelegramBotsApi
                telegramBotsApi.registerBot(cryptoHandler);

            } catch (TelegramApiException e) {
                BotLogger.error(LOGTAG, e);
            }
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
        }
    }

    /**
     * Loads configuration properties from the provided config.yaml file
     * and populates the Config.class with values.
     */
    private static void loadBotConfiguration() {
        try {
            InputStream configInputStream = Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream("config.yaml"));
            Yaml yaml = new Yaml();
            Bot.config = yaml.loadAs(configInputStream, Config.class);
        } catch (NullPointerException e) {
            BotLogger.error(LOGTAG, "Error loading config.yaml! " + e.getMessage());
        }
    }


    /**
     * Ceates a Telegram Bots Api to use Long Polling (getUpdates) bots.
     * @return TelegramBotsApi to register the bots.
     */
    private static TelegramBotsApi createLongPollingTelegramBotsApi() {
        return new TelegramBotsApi();
    }

    public static CryptoHandler getCryptoHandler() {
        return cryptoHandler;
    }

}
