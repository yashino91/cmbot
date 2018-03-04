package at.chaoticbits.coinmarket;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;

public class CoinMarketCapServiceTest {


    @BeforeClass
    public void setup() {
        CoinMarketContainer.symbolSlugs = ImmutableMap.of("eth", "ethereum");
    }


    @Test
    public void testFormatPrice() {
        Assert.assertEquals(CoinMarketCapService.formatPrice(null), "-");
        Assert.assertEquals(CoinMarketCapService.formatPrice(new BigDecimal("10.5"), '€'), " €10.50");
        Assert.assertEquals(CoinMarketCapService.formatPrice(new BigDecimal("0.12345678"), '€'), " €0.12345678");
    }

    @Test
    public void testGetUpOrDownEmoji() {
        Assert.assertEquals(CoinMarketCapService.getUpOrDownEmoji(new BigDecimal("1")), ":chart_with_upwards_trend:");
        Assert.assertEquals(CoinMarketCapService.getUpOrDownEmoji(new BigDecimal("-1")), ":chart_with_downwards_trend:");
    }

    @Test
    public void testFormatPercentage() {
        Assert.assertEquals(CoinMarketCapService.formatPercentage(null), "-");
        Assert.assertEquals(CoinMarketCapService.formatPercentage(new BigDecimal("10.5")), " 10.50%");
        Assert.assertEquals(CoinMarketCapService.formatPercentage(new BigDecimal("0.53")), " 0.53%");
    }

    @Test
    public void testFormatPercentageWithEmoji() {
        Assert.assertEquals(CoinMarketCapService.formatPercentageWithEmoji(null), "-");
        Assert.assertEquals(CoinMarketCapService.formatPercentageWithEmoji(new BigDecimal("10.5")), " 10.50%\t:chart_with_upwards_trend:");
    }

    @Test
    public void testFormatCurrencyResult() {
        Assert.assertNotNull(CoinMarketCapService.formatCurrencyResult(TestData.currencyDetails()));
    }

    @Test
    public void testGetCurrencySlug() {
        Assert.assertEquals(CoinMarketCapService.getCurrencySlug("slugnotfound"), "slugnotfound");
        Assert.assertEquals(CoinMarketCapService.getCurrencySlug("eth"), "ethereum");
    }



    @Test(
            expectedExceptions = IllegalStateException.class,
            expectedExceptionsMessageRegExp = "Currency not found.*"
    )
    public void testFetchCurrency() {
        CurrencyDetails currencyDetails = CoinMarketCapService.fetchCurrency("bat");

        Assert.assertEquals(currencyDetails.getName(), "Basic Attention Token");

        CoinMarketCapService.fetchCurrency("currencynotfound");
    }

    @Test
    public void testCoinMarketCapServiceInstance() {
        Assert.assertNotNull(CoinMarketCapService.getInstance());
        Assert.assertNotNull(CoinMarketCapService.getInstance());
    }


}
