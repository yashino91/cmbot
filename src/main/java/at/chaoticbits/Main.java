package at.chaoticbits;

import at.chaoticbits.updateshandlers.CryptoHandler;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;
import org.telegram.telegrambots.logging.BotsFileHandler;

import java.io.IOException;
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
            BotLogger.registerLogger(new BotsFileHandler("./logs/TelegramBots%g.%u.log"));
        } catch (IOException e) {
            BotLogger.severe(LOGTAG, e);
        }

        try {

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
