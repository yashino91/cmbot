package at.chaoticbits.coinmarket

import at.chaoticbits.config.DecimalFormatter
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import java.math.BigDecimal


/**
 * Test CoinMarketCap related service functions
 */
class CoinMarketCapServiceTest {


    @BeforeClass
    private fun setup() {

        CoinMarketContainer.symbolSlugs["eth"] = "ethereum"

        val coinMarketScheduler = CoinMarketScheduler()

        coinMarketScheduler.run()
    }

    @Test
    private fun testFormatPrice() {
        Assert.assertEquals(DecimalFormatter.formatPrice(null), "-")
        Assert.assertEquals(DecimalFormatter.formatPrice(BigDecimal("10.5"), '€'), " €10.50")
        Assert.assertEquals(DecimalFormatter.formatPrice(BigDecimal("0.12345678"), '€'), " €0.12345678")
    }

    @Test
    private fun testGetUpOrDownEmoji() {
        Assert.assertEquals(CoinMarketCapService.getUpOrDownEmoji(BigDecimal("1")), ":chart_with_upwards_trend:")
        Assert.assertEquals(CoinMarketCapService.getUpOrDownEmoji(BigDecimal("-1")), ":chart_with_downwards_trend:")
    }

    @Test
    private fun testFormatPercentage() {
        Assert.assertEquals(DecimalFormatter.formatPercentage(null), "-")
        Assert.assertEquals(DecimalFormatter.formatPercentage(BigDecimal("10.5")), " 10.50%")
        Assert.assertEquals(DecimalFormatter.formatPercentage(BigDecimal("0.53")), " 0.53%")
    }

    @Test
    private fun testFormatPercentageWithEmoji() {
        Assert.assertEquals(CoinMarketCapService.formatPercentageWithEmoji(null), "-")
        Assert.assertEquals(CoinMarketCapService.formatPercentageWithEmoji(BigDecimal("10.5")), " 10.50%\t:chart_with_upwards_trend:")
    }

    @Test
    private fun testFormatCurrencyResult() {
        TestData.currencyDetails().forEach {
            Assert.assertNotNull(CoinMarketCapService.formatCurrencyResult(it))
        }

    }

    @Test
    private fun testGetCurrencySlug() {
        Assert.assertEquals(CoinMarketCapService.getCurrencySlug("slugnotfound"), "slugnotfound")
        Assert.assertEquals(CoinMarketCapService.getCurrencySlug("eth"), "ethereum")
    }

    @Test(expectedExceptions = [(IllegalStateException::class)], expectedExceptionsMessageRegExp = "Currency not found.*")
    private fun testFetchCurrency() {
        val currencyDetails= CoinMarketCapService.fetchCurrency("bat")

        Assert.assertEquals(currencyDetails.name, "Basic Attention Token")

        CoinMarketCapService.fetchCurrency("currencynotfound")
    }
}
