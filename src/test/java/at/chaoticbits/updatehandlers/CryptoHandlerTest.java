package at.chaoticbits.updatehandlers;

import at.chaoticbits.Main;
import at.chaoticbits.updateshandlers.CryptoHandler;
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
        Update update = getUpdate();
        Main.getCryptoHandler().onUpdateReceived(update);

    }


    private Update getUpdate() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue("{\"update_id\": 10,\"message\": {\"message_id\": 1, \"text\": \"/eth\", \"chat\": {\"id\": 2}}}", Update.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



}
