package at.chaoticbits.currencydetails

import at.chaoticbits.config.DecimalFormatter
import org.junit.Assert
import org.junit.Test
import java.math.BigDecimal


/**
 * Test CoinMarketCap related service functions
 */
class CurrencyDetailsServiceTest {

    @Test
    fun testFormatPrice() {
        Assert.assertEquals("-", DecimalFormatter.formatPrice(null))
        Assert.assertEquals(" €10.50", DecimalFormatter.formatPrice(BigDecimal("10.5"), '€'))
        Assert.assertEquals(" €0.12345678", DecimalFormatter.formatPrice(BigDecimal("0.12345678"), '€'))
    }


    @Test
    fun testFormatPercentage() {
        Assert.assertEquals("-", DecimalFormatter.formatPercentage(null))
        Assert.assertEquals(" 10.50%", DecimalFormatter.formatPercentage(BigDecimal("10.5")))
        Assert.assertEquals(" 0.53%", DecimalFormatter.formatPercentage(BigDecimal("0.53")))
    }


    @Test(expected = CurrencyNotFoundException::class)
    fun testFetchCurrency() {
        val currencyDetails= CurrencyDetailsService.fetchCurrency("ethereum")

        Assert.assertEquals("Ethereum", currencyDetails.name)

        CurrencyDetailsService.fetchCurrency("currencynotfound")
    }
}
