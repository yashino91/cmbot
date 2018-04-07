package at.chaoticbits.coinmarket

import at.chaoticbits.config.DecimalFormatter
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import java.io.InputStream
import java.math.BigDecimal

class CoinMarketCapServiceTest {


    @BeforeClass
    fun setup() {

        CoinMarketContainer.symbolSlugs["eth"] = "ethereum"

        val coinMarketScheduler = CoinMarketScheduler()

        coinMarketScheduler.run()
    }


    @Test
    fun testFormatPrice() {
        Assert.assertEquals(DecimalFormatter.formatPrice(null), "-")
        Assert.assertEquals(DecimalFormatter.formatPrice(BigDecimal("10.5"), '€'), " €10.50")
        Assert.assertEquals(DecimalFormatter.formatPrice(BigDecimal("0.12345678"), '€'), " €0.12345678")
    }

    @Test
    fun testGetUpOrDownEmoji() {
        Assert.assertEquals(CoinMarketCapService.getUpOrDownEmoji(BigDecimal("1")), ":chart_with_upwards_trend:")
        Assert.assertEquals(CoinMarketCapService.getUpOrDownEmoji(BigDecimal("-1")), ":chart_with_downwards_trend:")
    }

    @Test
    fun testFormatPercentage() {
        Assert.assertEquals(DecimalFormatter.formatPercentage(null), "-")
        Assert.assertEquals(DecimalFormatter.formatPercentage(BigDecimal("10.5")), " 10.50%")
        Assert.assertEquals(DecimalFormatter.formatPercentage(BigDecimal("0.53")), " 0.53%")
    }

    @Test
    fun testFormatPercentageWithEmoji() {
        Assert.assertEquals(CoinMarketCapService.formatPercentageWithEmoji(null), "-")
        Assert.assertEquals(CoinMarketCapService.formatPercentageWithEmoji(BigDecimal("10.5")), " 10.50%\t:chart_with_upwards_trend:")
    }

    @Test
    fun testFormatCurrencyResult() {
        TestData.currencyDetails().forEach {
            Assert.assertNotNull(CoinMarketCapService.formatCurrencyResult(it))
        }

    }

    @Test
    fun testGetCurrencySlug() {
        Assert.assertEquals(CoinMarketCapService.getCurrencySlug("slugnotfound"), "slugnotfound")
        Assert.assertEquals(CoinMarketCapService.getCurrencySlug("eth"), "ethereum")
    }


    @Test(expectedExceptions = [(IllegalStateException::class)], expectedExceptionsMessageRegExp = "Currency not found.*")
    fun testFetchCurrency() {
        val currencyDetails= CoinMarketCapService.fetchCurrency("bat")

        Assert.assertEquals(currencyDetails.name, "Basic Attention Token")

        CoinMarketCapService.fetchCurrency("currencynotfound")
    }


    @Test
    fun testGetFormattedCurrencyDetails() {
        val formattedCurrencyDetails = CoinMarketCapService.getFormattedCurrencyDetails("bat")
        Assert.assertNotNull(formattedCurrencyDetails)
    }

    @Test
    fun testGetCurrencyDetailsImage() {
        val image = CoinMarketCapService.getCurrencyDetailsImage("bat")
        Assert.assertNotNull(image)
    }
}
