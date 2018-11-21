package at.chaoticbits.coin

import at.chaoticbits.util.getStringOrNull
import org.json.JSONObject


/**
 * Illustrates a coin with basic information
 */
data class Coin(
        val id: Int,
        val rank: Int,
        val name: String,
        val symbol: String,
        val imageUrl: String?
): Comparable<Coin> {

    constructor(jsonObject: JSONObject): this (
                jsonObject.getInt("Id"),
                jsonObject.getInt("SortOrder"),
                jsonObject.getString("CoinName"),
                jsonObject.getString("Symbol"),
                jsonObject.getStringOrNull("ImageUrl")
    )


    override fun compareTo(other: Coin): Int = comparator.compare(this, other)


    /**
     * Custom comparator to sort by name
     */
    companion object {
        val comparator = compareBy(Coin::rank)
    }
}