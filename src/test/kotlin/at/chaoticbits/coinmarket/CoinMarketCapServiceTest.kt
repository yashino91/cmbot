package at.chaoticbits.coinmarket

import at.chaoticbits.config.DecimalFormatter
import at.chaoticbits.testdata.TestData
import org.junit.Assert
import org.junit.Test
import java.math.BigDecimal


/**
 * Test CoinMarketCap related service functions
 */
class CoinMarketCapServiceTest {

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

    @Test(expected = CurrencyNotFoundException::class)
    fun testFetchCurrency() {
        val currencyDetails= CoinMarketCapService.fetchCurrency("ethereum")

        Assert.assertEquals(currencyDetails.name, "Ethereum")

        CoinMarketCapService.fetchCurrency("currencynotfound")
    }
}
