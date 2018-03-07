package at.chaoticbits.updatehandlers;

import at.chaoticbits.Main;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.api.objects.Update;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

public class CryptoHandlerTest {


    @BeforeClass
    public void setup() {
        Main.main(new String[1]);
    }


    @Test
    public void testGetBotUsername() {
        String botUsername = Main.getCryptoHandler().getBotUsername();
        Assert.assertNotNull(botUsername);
        Assert.assertEquals(botUsername, "priceLeecherBot");
    }

    @Test
    public void testGetBotToken() {
        String botUsername = Main.getCryptoHandler().getBotToken();
        Assert.assertNotNull(botUsername);
    }

    @Test
    public void testOnUpdateReceived() {
        Main.getCryptoHandler().onUpdateReceived(getRequestImageUpdate());
        Main.getCryptoHandler().onUpdateReceived(getRequestFormattedStringUpdate());
        Main.getCryptoHandler().onUpdateReceived(getInvalidCurrencyUpdate());
    }


    private Update getRequestImageUpdate() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue("{\"update_id\": 10,\"message\": {\"message_id\": 1, \"text\": \"/eth\", \"chat\": {\"id\": 2}}}", Update.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Update getRequestFormattedStringUpdate() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue("{\"update_id\": 10,\"message\": {\"message_id\": 1, \"text\": \"//eth\", \"chat\": {\"id\": 2}}}", Update.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Update getInvalidCurrencyUpdate() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue("{\"update_id\": 10,\"message\": {\"message_id\": 1, \"text\": \"/currencynotfound\", \"chat\": {\"id\": 2}}}", Update.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



}
