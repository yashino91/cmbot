package at.chaoticbits.coinmarket

import at.chaoticbits.api.Api
import at.chaoticbits.coinmarket.CoinMarketCapService.API_URL
import mu.KotlinLogging
import org.json.JSONObject
import java.util.*





/**
 * CoinMarketCap Scheduler, that periodically updates Coins and ERC20 Tokens
 */
private val log = KotlinLogging.logger {}
class CoinMarketScheduler : TimerTask() {


    override fun run() {
        updateSymbolSlugs()
    }


    /**
     * Fetch all coins from CoinMarketCap and populate a set of it for later usage.
     * This set will be used for Inline Query Search or symbol <-> name mappings
     */
    private fun updateSymbolSlugs() {
        val response = Api.fetch("$API_URL/all/coinlist")

        if (response.status == 200) {
            val data = JSONObject(response.body).getJSONObject("Data")

            val keys = data.keys()
            while (keys.hasNext()) {
                val coin = data.get(keys.next())
                if (coin is JSONObject)
                    CoinMarketContainer.addOrReplaceCoin(Coin(coin))
            }

            log.info { "Successfully updated coins" }

        } else {
            log.error { "Error fetching coins! StatusCode: ${response.status}" }
        }
    }
}