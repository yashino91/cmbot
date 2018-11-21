package at.chaoticbits.coin

import at.chaoticbits.api.Api
import at.chaoticbits.currencydetails.CurrencyDetailsService.API_URL
import mu.KotlinLogging
import org.json.JSONObject
import java.util.*


/**
 * Coin Scheduler, that periodically updates available Coins, containing basic information
 */
private val log = KotlinLogging.logger {}
class CoinScheduler : TimerTask() {


    override fun run() {
        updateSymbolSlugs()
    }


    /**
     * Fetch all coins from crypto compare and populate a set of it for later usage.
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
                    CoinContainer.addOrReplaceCoin(Coin(coin))
            }

            log.info { "Successfully updated coins" }

        } else {
            log.error { "Error fetching coins! StatusCode: ${response.status}" }
        }
    }
}