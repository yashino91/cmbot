package at.chaoticbits.coinmarket

import at.chaoticbits.api.Api
import at.chaoticbits.api.Response
import at.chaoticbits.config.Bot
import at.chaoticbits.config.DecimalFormatter
import at.chaoticbits.render.HtmlImageService
import org.json.JSONArray

import java.io.*
import java.math.BigDecimal
import java.net.URLEncoder
import java.util.Objects

import at.chaoticbits.config.DecimalFormatter.formatPercentage
import at.chaoticbits.config.DecimalFormatter.formatPrice


/**
 * Interacts with CoinMarketCap
 */
object CoinMarketCapService {


    private const val API_URL = "https://api.coinmarketcap.com/v1/ticker/"

    /**
     * Fetch all details about the given currency at CoinMarketCap
     * and generates a styled image containing all information about the requested currency
     * @param currency currency
     * @return InputStream containing the image information
     */
    @Throws(IllegalStateException::class)
    fun getCurrencyDetailsImage(currency: String): InputStream {
        val currencyDetails: CurrencyDetails = fetchCurrency(currency)
        return HtmlImageService.generateCryptoDetailsImage(currencyDetails)
    }


    /**
     * Fetch all details about the given currency at CoinMarketCap
     * and formats the result as a string
     * @param currency currency
     * @return formatted string with detailed information about the requested currency
     */
    @Throws(IllegalStateException::class)
    fun getFormattedCurrencyDetails(currency: String): String {
        val currencyDetails: CurrencyDetails = fetchCurrency(currency)
        return formatCurrencyResult(currencyDetails)
    }


    /**
     * Fetch the information of the given currency
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
     * Checks if the given currency slug is allowed
     * @param slug currency slug
     * @return true/false
     */
    private fun slugAllowed(slug: String): Boolean {
        return Bot.config.allowedCurrencySlugs.isEmpty() || Bot.config.allowedCurrencySlugs.contains(slug)

    }


    /**
     * Maps the requested currency to the appropriate slug for later price fetching
     * @param currency currency
     * @return slug
     */
    fun getCurrencySlug(currency: String): String {
        return CoinMarketContainer.symbolSlugs[currency.toUpperCase()] ?: return currency
    }


    /**
     * Format the given json object containing currency information
     * to a readable string
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

    fun formatPercentageWithEmoji(percentage: BigDecimal?): String {
        val formattedPercentage = formatPercentage(percentage)

        return if (formattedPercentage == "-") formattedPercentage else formattedPercentage + "\t" + getUpOrDownEmoji(percentage!!)

    }

    fun getUpOrDownEmoji(price: BigDecimal): String {
        return if (price > BigDecimal.ZERO) ":chart_with_upwards_trend:" else ":chart_with_downwards_trend:"

    }


}
