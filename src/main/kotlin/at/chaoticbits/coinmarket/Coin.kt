package at.chaoticbits.coinmarket

import org.json.JSONObject


/**
 * Illustrates a coin with basic information from CoinMarketCap
 */
data class Coin(
        val id: Int,
        val rank: Int,
        val name: String,
        val symbol: String,
        val slug: String
): Comparable<Coin> {

    constructor(jsonObject: JSONObject): this (
                jsonObject.getInt("id"),
                jsonObject.getInt("rank"),
                jsonObject.getString("name"),
                jsonObject.getString("symbol"),
                jsonObject.getString("slug")
    )


    override fun compareTo(other: Coin): Int = comparator.compare(this, other)


    /**
     * Custom comparator to sort by rank
     */
    companion object {
        val comparator = compareBy(Coin::rank)
    }
}