package at.chaoticbits.render

import at.chaoticbits.config.DecimalFormatter
import at.chaoticbits.currencydetails.CurrencyDetails
import java.math.BigDecimal

object TextRenderService {

    /**
     * Format the given currency details to a readable string for telegram chat
     *
     * @param currencyDetails [CurrencyDetails] Containing information about a crypto currency
     * @return [String] Formatted currency information
     */
    fun formatCurrencyResult(currencyDetails: CurrencyDetails): String {

        return "[" + currencyDetails.name + "](https://www.cryptocompare.com/coins/" + currencyDetails.symbol.toLowerCase() + "/overview/USD" + ") (" + currencyDetails.symbol + ")" + "\n" +
                "*Rank: *" + currencyDetails.rank + "\n" +
                "*EUR: *" + DecimalFormatter.formatPrice(currencyDetails.priceEur, '€') + "\n" +
                "*USD: *" + DecimalFormatter.formatPrice(currencyDetails.priceUsd) + "\n" +
                "*BTC: *" + DecimalFormatter.formatPrice(currencyDetails.priceBtc, ' ') + "\n" +
                "*CHANGE24h: *" + DecimalFormatter.formatPercentage(currencyDetails.change24h) + "\n" +
                "*LOW24H: *" + DecimalFormatter.formatPrice(currencyDetails.low24h) + "\n" +
                "*OPEN24H: *" + DecimalFormatter.formatPrice(currencyDetails.open24h) + "\n" +
                "*HIGH24H: *" + DecimalFormatter.formatPrice(currencyDetails.high24h) + "\n" +
                "*VOLUME24H: *" + DecimalFormatter.formatPrice(currencyDetails.volume24h) + "\n" +
                "*MARKETCAP: *" + DecimalFormatter.formatPrice(currencyDetails.marketCap)

    }
}