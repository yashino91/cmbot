package at.chaoticbits.coinmarket

import at.chaoticbits.api.Api
import at.chaoticbits.api.Response
import at.chaoticbits.config.Bot
import at.chaoticbits.config.DecimalFormatter
import at.chaoticbits.config.DecimalFormatter.formatPercentage
import org.json.JSONArray
import java.io.UnsupportedEncodingException
import java.math.BigDecimal
import java.net.URLEncoder


/**
 * Interacts with CoinMarketCap
 */
object CoinMarketCapService {


    private const val API_URL = "https://api.coinmarketcap.com/v1/ticker/"


    /**
     * Fetch the information of the given currency
     *
     * @param currency currency (bitcoin, ethereum, etc..)
     * @return JSONObject including price information
     */
    @Throws(IllegalStateException::class, UnsupportedEncodingException::class)
    fun fetchCurrency(currency: String): CurrencyDetails {

        var slug = getCurrencySlug(currency)

        if (!slugAllowed(slug))
            throw IllegalStateException("Currency not found: *$currency*")

        // Prefer basic attention token (bat is ambiguous)
        if (currency.toLowerCase() == "bat")
            slug = "basic-attention-token"


        val response: Response = Api.fetch(API_URL + URLEncoder.encode(slug, "UTF-8") + "/?convert=EUR")

        if (response.status == 200 && response.body != null)
            return CurrencyDetails(JSONArray(response.body).getJSONObject(0))
        else if (response.status == 404)
            throw IllegalStateException("Currency not found: *$currency*")
        else
            throw IllegalStateException("Error! StatusCode: " + response.status)
    }


    /**
     * Maps the requested currency to the appropriate slug for later price fetching
     *
     * @param currency currency
     * @return slug
     */
    fun getCurrencySlug(currency: String): String {
        return CoinMarketContainer.symbolSlugs[currency.toUpperCase()] ?: return currency
    }


    /**
     * Format the given json object containing currency information
     * to a readable string
     *
     * @param currencyDetails containing information about a crypto currency
     * @return formatted currency information
     */
    fun formatCurrencyResult(currencyDetails: CurrencyDetails): String {


        val erc20Token = if (currencyDetails.isErc20) "_Erc20_\n\n" else "\n"

        return "[" + currencyDetails.name + "](https://coinmarketcap.com/currencies/" + currencyDetails.name + ") (" + currencyDetails.symbol + ")" + "\n" +
                erc20Token +
                "*Rank: *" + currencyDetails.rank + "\n" +
                "*EUR: *" + DecimalFormatter.formatPrice(currencyDetails.priceEur, '€') + "\n" +
                "*USD: *" + DecimalFormatter.formatPrice(currencyDetails.priceUsd) + "\n" +
                "*BTC: *" + DecimalFormatter.formatPrice(currencyDetails.priceBtc, ' ') + "\n" +
                "*1h: *" + formatPercentageWithEmoji(currencyDetails.change1h) + "\n" +
                "*24h: *" + formatPercentageWithEmoji(currencyDetails.change24h) + "\n" +
                "*7d: *" + formatPercentageWithEmoji(currencyDetails.change7d) + "\n" +
                "*Volume24h: *" + DecimalFormatter.formatPrice(currencyDetails.volume24h) + "\n" +
                "*MarketCap: *" + DecimalFormatter.formatPrice(currencyDetails.marketCap)

    }


    /**
     * Formats the given percentage with an upwards or downwards trend emoji,
     * depending if the value is negative or positive
     *
     * @param percentage percentage as BigDecimal
     * @return formatted percentage with appropriate emoji
     */
    fun formatPercentageWithEmoji(percentage: BigDecimal?): String {
        return if (percentage == null) "-" else formatPercentage(percentage) + "\t" + getUpOrDownEmoji(percentage)
    }


    /**
     * Creates an upwards or downwards trend emoji
     * depending on a negative or positive value
     *
     * @param value value as BigDecimal
     * @return string representation of matching emoji
     */
    fun getUpOrDownEmoji(value: BigDecimal): String {
        return if (value > BigDecimal.ZERO) ":chart_with_upwards_trend:" else ":chart_with_downwards_trend:"

    }


    private fun slugAllowed(slug: String): Boolean {
        return Bot.config.allowedCurrencySlugs.isEmpty() || Bot.config.allowedCurrencySlugs.contains(slug)
    }

}
