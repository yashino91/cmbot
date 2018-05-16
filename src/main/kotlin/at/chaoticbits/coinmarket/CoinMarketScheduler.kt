package at.chaoticbits.coinmarket

import at.chaoticbits.api.Api
import mu.KotlinLogging
import org.json.JSONArray
import java.util.*

/**
 * CoinMarketCap Scheduler, that periodically updates Coins and ERC20 Tokens
 */
private val log = KotlinLogging.logger {}
class CoinMarketScheduler : TimerTask() {


    override fun run() {
        updateErc20TokenList()
        updateSymbolSlugs()
    }


    /**
     * Fetch all coins from CoinMarketCap and populate a set of it for later usage.
     * This set will be used for Inline Query Search or symbol <-> name mappings
     */
    private fun updateSymbolSlugs() {

        val response = Api.fetch("https://s2.coinmarketcap.com/generated/search/quick_search.json")

        if (response.status == 200) {
            val jsonCoinArray = JSONArray(response.body)

            for (i in 0 until jsonCoinArray.length())
                CoinMarketContainer.addOrReplaceCoin(Coin(jsonCoinArray.getJSONObject(i)))

            log.info { "Successfully updated coins" }

        } else {
            log.error { "Error fetching coins! StatusCode: ${response.status}" }
        }
    }


    /**
     * Fetch Erc20 tokens and populate a map of it.
     * This map will be used to check if a coin is an ERC20 token
     */
    private fun updateErc20TokenList() {

        val response = Api.fetch("https://raw.githubusercontent.com/kvhnuke/etherwallet/mercury/app/scripts/tokens/ethTokens.json")

        if (response.status == 200) {
            val jsonArray = JSONArray(response.body)

            for (i in 0 until jsonArray.length()) {
                val erc20Symbol = jsonArray.getJSONObject(i).getString("symbol")
                val erc20Address = jsonArray.getJSONObject(i).getString("address")

                CoinMarketContainer.addOrReplaceErc20Token(erc20Symbol, erc20Address)
            }
            log.info { "Successfully updated ERC20 tokens" }

        } else
            log.error { "Error updating ERC20 Tokens! StatusCode: ${response.status}" }
    }
}