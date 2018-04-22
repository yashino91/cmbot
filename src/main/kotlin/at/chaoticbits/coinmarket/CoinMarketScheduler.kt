package at.chaoticbits.coinmarket

import at.chaoticbits.api.Api
import mu.KotlinLogging
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileNotFoundException
import java.io.PrintWriter
import java.io.UnsupportedEncodingException
import java.util.*

/**
 * Update symbol slug list periodically
 */
private val log = KotlinLogging.logger {}
class CoinMarketScheduler : TimerTask() {


    override fun run() {
        updateErc20TokenList()
        updateSymbolSlugs()
    }


    /**
     * Fetch CMC coin information and populate map with symbols related to their slug,
     * in order to support searching via symbols (btc, eth,...)
     * CoinMarketCap API currently only supports search by slug (bitcoin, ethereum,...)
     */
    private fun updateSymbolSlugs() {

        try {

            val response = Api.fetch("https://s2.coinmarketcap.com/generated/search/quick_search.json")

            if (response.status == 200) {
                val jsonArray = JSONArray(response.body)

                try {
                    PrintWriter("telegram-commands.txt", "UTF-8").use { writer ->

                        for (i in 0 until jsonArray.length()) {

                            val jsonObject = jsonArray.getJSONObject(i)

                            // populate top 85 coins in telegram commands
                            if (i < 85)
                                updateBotCommands(writer, jsonObject)

                            CoinMarketContainer.symbolSlugs[jsonObject.getString("symbol")] = jsonObject.getString("slug")
                        }

                        log.info { "Successfully updated symbol slugs" }

                    }
                } catch (e: FileNotFoundException) {
                    log.error {  e.message }
                } catch (e: UnsupportedEncodingException) {
                    log.error {  e.message }
                }

            } else {
                log.error { "Error fetching symbol slugs! StatusCode: ${response.status}" }

            }
        } catch (e: Exception) {
            log.error { "Error parsing new symbol slug list: ${e.message}" }
        }

    }


    /**
     * Fetch Erc20 tokens and update cache
     */
    private fun updateErc20TokenList() {

        val response = Api.fetch("https://raw.githubusercontent.com/kvhnuke/etherwallet/mercury/app/scripts/tokens/ethTokens.json")

        if (response.status == 200) {
            val jsonArray = JSONArray(response.body)

            for (i in 0 until jsonArray.length()) {
                val erc20Symbol = jsonArray.getJSONObject(i).getString("symbol")
                val erc20Address = jsonArray.getJSONObject(i).getString("address")
                CoinMarketContainer.erc20Tokens[erc20Symbol] = erc20Address
            }
            log.info { "Successfully updated erc20 tokens" }

        } else
            log.error { "Error updating Erc20 Tokens! StatusCode: ${response.status}" }
    }


    private fun updateBotCommands(writer: PrintWriter, jsonObject: JSONObject) =
        writer.println(jsonObject.getString("symbol").toLowerCase() + " - " + jsonObject.getString("name"))

}