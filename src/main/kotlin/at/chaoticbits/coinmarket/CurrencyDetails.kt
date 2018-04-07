package at.chaoticbits.coinmarket

import org.json.JSONObject
import java.math.BigDecimal


fun getValueOrNull(key: String, jsonObject: JSONObject): BigDecimal? {
    return if (jsonObject.isNull(key)) null else jsonObject.getBigDecimal(key)
}

data class CurrencyDetails(
        val rank: Int = 0,
        val isErc20: Boolean,
        val name: String,
        val symbol: String,
        val change1h: BigDecimal?,
        val change24h: BigDecimal?,
        val change7d: BigDecimal?,
        val volume24h: BigDecimal?,
        val marketCap: BigDecimal?,
        val priceUsd: BigDecimal?,
        val priceEur: BigDecimal?,
        val priceBtc: BigDecimal?
) {

    constructor(jsonObject: JSONObject): this (
                jsonObject.getInt("rank"),
                CoinMarketContainer.erc20Tokens.containsKey(jsonObject.getString("symbol")),
                jsonObject.getString("name"),
                jsonObject.getString("symbol"),

                getValueOrNull("percent_change_1h", jsonObject),
                getValueOrNull("percent_change_24h", jsonObject),
                getValueOrNull("percent_change_7d", jsonObject),

                getValueOrNull("24h_volume_usd", jsonObject),
                getValueOrNull("market_cap_usd", jsonObject),

                getValueOrNull("price_usd", jsonObject),
                getValueOrNull("price_eur", jsonObject),
                getValueOrNull("price_btc", jsonObject)
    )
}
