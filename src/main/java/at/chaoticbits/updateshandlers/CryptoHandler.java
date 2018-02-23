package at.chaoticbits.updateshandlers;

import at.chaoticbits.coinmarket.CoinMarketScheduler;
import at.chaoticbits.config.BotConfig;
import at.chaoticbits.coinmarket.CoinMarketCapService;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.util.Timer;


/**
 * Crypto Polling Bot, that processes currency requests
 */
public class CryptoHandler extends TelegramLongPollingBot {

    private static final String LOGTAG  = "CryptoHandler";


    /**
     * Instantiate CryptoHandler and start coin market scheduler
     */
    public CryptoHandler() {

        CoinMarketScheduler cmScheduler = new CoinMarketScheduler () ;

        int initialDelay = 100 ;
        int fixedRate = 60 * 60 * 1000; // every hour
        new Timer().schedule(cmScheduler, initialDelay, fixedRate) ;
    }


    @Override
    public void onUpdateReceived(Update update) {

        //check if the update has a message
        if(update.hasMessage()){
            Message message = update.getMessage();

            //check if the message has text. it could also  contain for example a location ( message.hasLocation() )
            if(message.hasText()) {

                //create a object that contains the information to send back the message
                SendMessage sendMessageRequest = new SendMessage();
                sendMessageRequest.enableMarkdown(true);
                sendMessageRequest.setChatId(message.getChatId());

                String command = message.getText();

                if (command.startsWith("/")) {
                    sendMessageRequest.setText(CoinMarketCapService.getInstance().getFormattedCurrencyDetails(command.substring(1, command.length())));

                    try {
                        sendMessage(sendMessageRequest);
                    } catch (TelegramApiException e) {
                        BotLogger.error(LOGTAG, e);
                    }
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return BotConfig.PRICE_LEECHER_BOT;
    }

    @Override
    public String getBotToken() {
        return System.getenv("CMBOT_TELEGRAM_TOKEN");
    }
}
