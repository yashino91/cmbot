package at.chaoticbits.coinmarket;

import org.testng.Assert;
import org.testng.annotations.Test;


public class CoinMarketSchedulerTest {

    @Test
    public void testRun() {
        CoinMarketScheduler coinMarketScheduler = new CoinMarketScheduler();

        coinMarketScheduler.run();

        Assert.assertNotNull(CoinMarketContainer.erc20Tokens);
        Assert.assertNotNull(CoinMarketContainer.symbolSlugs);
    }
}
