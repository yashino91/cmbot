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
        Assert.assertEquals("-", DecimalFormatter.formatPrice(null))
        Assert.assertEquals(" €10.50", DecimalFormatter.formatPrice(BigDecimal("10.5"), '€'))
        Assert.assertEquals(" €0.12345678", DecimalFormatter.formatPrice(BigDecimal("0.12345678"), '€'))
    }

    @Test
    fun testGetUpOrDownEmoji() {
        Assert.assertEquals(":chart_with_upwards_trend:", CoinMarketCapService.getUpOrDownEmoji(BigDecimal("1")))
        Assert.assertEquals(":chart_with_downwards_trend:", CoinMarketCapService.getUpOrDownEmoji(BigDecimal("-1")))
    }

    @Test
    fun testFormatPercentage() {
        Assert.assertEquals("-", DecimalFormatter.formatPercentage(null))
        Assert.assertEquals(" 10.50%", DecimalFormatter.formatPercentage(BigDecimal("10.5")))
        Assert.assertEquals(" 0.53%", DecimalFormatter.formatPercentage(BigDecimal("0.53")))
    }

    @Test
    fun testFormatPercentageWithEmoji() {
        Assert.assertEquals("-", CoinMarketCapService.formatPercentageWithEmoji(null))
        Assert.assertEquals(" 10.50%\t:chart_with_upwards_trend:", CoinMarketCapService.formatPercentageWithEmoji(BigDecimal("10.5")))
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

        Assert.assertEquals("Ethereum", currencyDetails.name)

        CoinMarketCapService.fetchCurrency("currencynotfound")
    }
}
