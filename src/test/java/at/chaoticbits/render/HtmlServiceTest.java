package at.chaoticbits.render;

import at.chaoticbits.coinmarket.TestData;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.InputStream;

public class HtmlServiceTest {


    @Test
    public void testGenerateCryptoDetailsImage() {
        InputStream image = HtmlImageService.getInstance().generateCryptoDetailsImage(TestData.currencyDetails());
        Assert.assertNotNull(image);
    }


}
