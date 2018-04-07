package at.chaoticbits.render

import at.chaoticbits.coinmarket.TestData
import org.testng.Assert
import org.testng.annotations.Test


/**
 * Test Image rendering of currency details
 */
class HtmlServiceTest {


    @Test
    private fun testGenerateCryptoDetailsImage() {

        TestData.currencyDetails().forEach {
            val image = HtmlImageService.generateCryptoDetailsImage(it)
            Assert.assertNotNull(image)
        }

    }


}
