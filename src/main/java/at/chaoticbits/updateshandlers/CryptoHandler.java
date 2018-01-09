package at.chaoticbits.updateshandlers;

import at.chaoticbits.utils.BotConfig;
import at.chaoticbits.services.CoinMarketCapService;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;


/**
 * Crypto Polling Bot, that processes currency requests
 */
public class CryptoHandler extends TelegramLongPollingBot {

    private static final String LOGTAG  = "CryptoHandler";


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
                sendMessageRequest.setChatId(message.getChatId().toString());

                String command = message.getText();

                if (command.startsWith("/")) {
                    sendMessageRequest.setText(CoinMarketCapService.getInstance().fetchCurrency(command.substring(1, command.length())));
                }


                try {
                    sendMessage(sendMessageRequest);
                } catch (TelegramApiException e) {
                    BotLogger.error(LOGTAG, e);
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
        return BotConfig.CMBOTTOKEN;
    }
}
