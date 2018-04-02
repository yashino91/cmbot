package at.chaoticbits.coinmarket

import org.testng.Assert
import org.testng.annotations.Test


class CoinMarketSchedulerTest {

    @Test
    fun testRun() {
        val coinMarketScheduler = CoinMarketScheduler()

        coinMarketScheduler.run()

        Assert.assertNotNull(CoinMarketContainer.erc20Tokens)
        Assert.assertNotNull(CoinMarketContainer.symbolSlugs)
    }
}
