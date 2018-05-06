package at.chaoticbits.coinmarket

import org.json.JSONObject


/**
 * Illustrates a coin from the CoinMarketCap listings endpoint
 */
data class Coin(
        val id: Int,
        val name: String,
        val symbol: String,
        val slug: String
) {

    constructor(jsonObject: JSONObject): this (
                jsonObject.getInt("id"),
                jsonObject.getString("name"),
                jsonObject.getString("symbol"),
                jsonObject.getString("website_slug")
    )
}