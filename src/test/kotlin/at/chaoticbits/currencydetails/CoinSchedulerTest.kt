package at.chaoticbits.currencydetails

import at.chaoticbits.coin.CoinContainer
import at.chaoticbits.coin.CoinScheduler
import org.junit.Assert
import org.junit.Test


/**
 * Test CoinMarketCap Schedulers
 */
class CoinSchedulerTest {

    @Test
    fun testRun() {

        val coinMarketScheduler = CoinScheduler()

        coinMarketScheduler.run()

        Assert.assertNotNull(CoinContainer.getCoins())
    }
}
