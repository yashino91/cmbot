package at.chaoticbits.coinmarket

import at.chaoticbits.util.getBigDecimalOrNull
import at.chaoticbits.util.getStringOrNull
import org.json.JSONObject
import java.math.BigDecimal


/**
 * Illustrates a class, holding all available information about a currency
 */
class CurrencyDetails(
    val rank: Int = 0,
    val name: String,
    val symbol: String,
    val low24h: BigDecimal?,
    val change24h: BigDecimal?,
    val high24h: BigDecimal?,
    val volume24h: BigDecimal?,
    val marketCap: BigDecimal?,
    val priceUsd: BigDecimal?,
    val priceEur: BigDecimal?,
    val priceBtc: BigDecimal?
) {

    companion object {
        fun fromJsonObjectAndCoin(jsonObject: JSONObject, coin: Coin): CurrencyDetails {
            val usdDetails = jsonObject.getJSONObject("USD")
            val eurDetails = jsonObject.getJSONObject("EUR")
            val btcDetails = jsonObject.getJSONObject("BTC")

            return CurrencyDetails(
                coin.rank,
                coin.name,
                coin.symbol,
                usdDetails.getBigDecimalOrNull("LOW24HOUR"),
                usdDetails.getBigDecimalOrNull("CHANGEPCT24HOUR"),
                usdDetails.getBigDecimalOrNull("HIGH24HOUR"),
                usdDetails.getBigDecimalOrNull("TOTALVOLUME24H"),
                usdDetails.getBigDecimalOrNull("MKTCAP"),
                usdDetails.getBigDecimalOrNull("PRICE"),
                eurDetails.getBigDecimalOrNull("PRICE"),
                btcDetails.getBigDecimalOrNull("PRICE")
            )
        }
    }

    constructor(jsonObject: JSONObject): this (
                0,
                jsonObject.getString("name"),
                jsonObject.getString("symbol"),

                getValueOrNull("percent_change_1h", jsonObject),
                getValueOrNull("percent_change_24h", jsonObject),
                getValueOrNull("percent_change_7d", jsonObject),

                getValueOrNull("24h_volume_usd", jsonObject),
                getValueOrNull("MKTCAP", jsonObject),

                getValueOrNull("price_usd", jsonObject),
                getValueOrNull("price_eur", jsonObject),
                getValueOrNull("price_btc", jsonObject)
    )
}


private fun getValueOrNull(key: String, jsonObject: JSONObject): BigDecimal? {
    return if (jsonObject.isNull(key)) null else jsonObject.getBigDecimal(key)
}