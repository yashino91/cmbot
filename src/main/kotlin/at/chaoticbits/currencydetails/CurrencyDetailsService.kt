package at.chaoticbits.currencydetails

import at.chaoticbits.api.Api
import at.chaoticbits.api.Response
import at.chaoticbits.coin.CoinContainer
import at.chaoticbits.config.Bot
import org.json.JSONObject
import java.io.UnsupportedEncodingException


/**
 * Handles the fetching of currency details
 */
object CurrencyDetailsService {


    const val API_URL = "https://min-api.cryptocompare.com/data"
    const val BASE_URL = "https://www.cryptocompare.com"


    /**
     * Fetch the given currency from cryptocompare.com and populate a [CurrencyDetails] object
     *
     * @param currency [String] Currency (Bitcoin, Ethereum, etc..)
     * @return [CurrencyDetails] Including price information
     */
    @Throws(IllegalStateException::class, UnsupportedEncodingException::class, CurrencyNotFoundException::class)
    fun fetchCurrency(currency: String): CurrencyDetails {
        val coin = CoinContainer.findCoinBySymbolOrName(currency)

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
     * Determines if the given symbol is allowed or not
     *
     * @param slug [String] Coin Symbol (BTC, ETH,...)
     * @return [Boolean] True if allowed
     */
    private fun symbolAllowed(symbol: String): Boolean {
        return Bot.config.allowedSymbols.isEmpty() || !Bot.config.allowedSymbols.any { it.equals(symbol, true) }
    }

}
