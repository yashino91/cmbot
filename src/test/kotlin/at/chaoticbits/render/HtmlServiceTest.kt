package at.chaoticbits.render

import at.chaoticbits.testdata.TestData
import org.junit.Assert
import org.junit.Test
import javax.imageio.ImageIO
import java.io.File


/**
 * Test Image rendering of currency details
 */
class HtmlServiceTest {


    @Test
    fun testGenerateCryptoDetailsImage() {

        TestData.currencyDetails().forEach {
            val image = HtmlImageService.generateCryptoDetailsImage(it)
            ImageIO.write(ImageIO.read(image), "png", File("${it.name}.png"))
            Assert.assertNotNull(image)
        }
    }
}
