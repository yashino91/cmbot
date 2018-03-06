package at.chaoticbits.updatehandlers;

import at.chaoticbits.updateshandlers.CryptoHandler;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CryptoHandlerTest {

    private CryptoHandler cryptoHandler;


    @BeforeClass
    public void setup() {
        cryptoHandler = new CryptoHandler();
    }


    @Test
    public void testGetBotUsername() {
        String botUsername = cryptoHandler.getBotUsername();
        Assert.assertNotNull(botUsername);
        Assert.assertEquals(botUsername, "priceLeecherBot");
    }

}
