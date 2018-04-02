package at.chaoticbits.render

import at.chaoticbits.coinmarket.TestData
import org.testng.Assert
import org.testng.annotations.Test

import java.io.InputStream

class HtmlServiceTest {


    @Test
    fun testGenerateCryptoDetailsImage() {
        val image = HtmlImageService.generateCryptoDetailsImage(TestData.currencyDetails())
        Assert.assertNotNull(image)
    }


}
