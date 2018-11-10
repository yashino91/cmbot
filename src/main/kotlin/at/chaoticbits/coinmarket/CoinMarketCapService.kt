package at.chaoticbits.coinmarket

import at.chaoticbits.api.Api
import at.chaoticbits.api.Response
import at.chaoticbits.config.Bot
import at.chaoticbits.config.DecimalFormatter
import at.chaoticbits.config.DecimalFormatter.formatPercentage
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.math.BigDecimal


/**
 * Interacts with CoinMarketCap
 */
object CoinMarketCapService {


    const val API_URL = "https://min-api.cryptocompare.com/data"
    const val BASE_URL = "https://www.cryptocompare.com"


    /**
     * Fetch the given currency from CoinMarketCap and populate a [CurrencyDetails] object
     *
     * @param currency [String] Currency (Bitcoin, Ethereum, etc..)
     * @return [CurrencyDetails] Including price information
     */
    @Throws(IllegalStateException::class, UnsupportedEncodingException::class, CurrencyNotFoundException::class)
    fun fetchCurrency(currency: String): CurrencyDetails {
        val coin = CoinMarketContainer.findCoinBySymbolOrName(currency)

        if (coin == null || !symbolAllowed(coin.symbol))
            throw CurrencyNotFoundException("Currency not found: *$currency*")


        val response: Response = Api.fetch("$API_URL/pricemultifull?fsyms=${coin.symbol}&tsyms=USD,EUR,BTC")

        if (response.status == 200 && response.body != null)
            return CurrencyDetails.fromJsonObjectAndCoin(
                JSONObject(response.body).getJSONObject("RAW").getJSONObject(coin.symbol),
                coin
            )
        else if (response.status == 404)
            throw CurrencyNotFoundException("Currency not found: *$currency*")
        else
            throw IllegalStateException("Error! StatusCode: " + response.status)
    }


    /**
     * Format the given currency details to a readable string for telegram chat
     *
     * @param currencyDetails [CurrencyDetails] Containing information about a crypto currency
     * @return [String] Formatted currency information
     */
    fun formatCurrencyResult(currencyDetails: CurrencyDetails): String {

        return "[" + currencyDetails.name + "](https://coinmarketcap.com/currencies/" + currencyDetails.name + ") (" + currencyDetails.symbol + ")" + "\n" +
                "*Rank: *" + currencyDetails.rank + "\n" +
                "*EUR: *" + DecimalFormatter.formatPrice(currencyDetails.priceEur, '€') + "\n" +
                "*USD: *" + DecimalFormatter.formatPrice(currencyDetails.priceUsd) + "\n" +
                "*BTC: *" + DecimalFormatter.formatPrice(currencyDetails.priceBtc, ' ') + "\n" +
                "*1h: *" + formatPercentageWithEmoji(currencyDetails.low24h) + "\n" +
                "*24h: *" + formatPercentageWithEmoji(currencyDetails.change24h) + "\n" +
                "*7d: *" + formatPercentageWithEmoji(currencyDetails.high24h) + "\n" +
                "*Volume24h: *" + DecimalFormatter.formatPrice(currencyDetails.volume24h) + "\n" +
                "*MarketCap: *" + DecimalFormatter.formatPrice(currencyDetails.marketCap)

    }


    /**
     * Formats the given percentage with an upwards or downwards trend emoji,
     * depending if the value is negative or positive
     *
     * @param percentage [BigDecimal] Percentage
     * @return [String] Formatted percentage with appropriate emoji
     */
    fun formatPercentageWithEmoji(percentage: BigDecimal?): String {
        return if (percentage == null) "-" else formatPercentage(percentage) + "\t" + getUpOrDownEmoji(percentage)
    }


    /**
     * Creates an upwards or downwards trend emoji
     * depending on a negative or positive value
     *
     * @param value [BigDecimal]
     * @return [String] Up or Down Emoji
     */
    fun getUpOrDownEmoji(value: BigDecimal): String {
        return if (value > BigDecimal.ZERO) ":chart_with_upwards_trend:" else ":chart_with_downwards_trend:"

    }


    /**
     * Determines if the given symbol is allowed or not
     *
     * @param slug [String] Coin Symbol (BTC, ETH,...)
     * @return [Boolean] True if allowed
     */
    // TODO: manage allowed symbols instead of slugs
    private fun symbolAllowed(symbol: String): Boolean {
        return Bot.config.allowedCurrencySlugs.isEmpty() || Bot.config.allowedCurrencySlugs.contains(symbol)
    }

}
