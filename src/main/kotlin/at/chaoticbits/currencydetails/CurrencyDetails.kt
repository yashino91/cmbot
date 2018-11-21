package at.chaoticbits.currencydetails

import at.chaoticbits.coin.Coin
import at.chaoticbits.util.getBigDecimalOrNull
import org.json.JSONObject
import java.math.BigDecimal


/**
 * Illustrates a class, holding all available information about a crypto currency
 */
class CurrencyDetails(
    val rank: Int = 0,
    val name: String,
    val symbol: String,
    val low24h: BigDecimal?,
    val change24h: BigDecimal?,
    val high24h: BigDecimal?,
    val open24h: BigDecimal?,
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
                usdDetails.getBigDecimalOrNull("OPEN24HOUR"),
                usdDetails.getBigDecimalOrNull("TOTALVOLUME24H"),
                usdDetails.getBigDecimalOrNull("MKTCAP"),
                usdDetails.getBigDecimalOrNull("PRICE"),
                eurDetails.getBigDecimalOrNull("PRICE"),
                btcDetails.getBigDecimalOrNull("PRICE")
            )
        }
    }


}