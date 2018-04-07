package at.chaoticbits.config

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols


/**
 * Decimal Format Helper Class.
 * Uses '.' for decimal separator and ',' for grouping thousands
 */
object DecimalFormatter {


    private val decimalFormatSymbols = DecimalFormatSymbols()

    init {
        decimalFormatSymbols.decimalSeparator = '.'
        decimalFormatSymbols.groupingSeparator = ','
    }


    /**
     * Format price according to decimal dimension in USD
     * @param price price as BigDecimal
     * @return formatted string price
     */
    fun formatPrice(price: BigDecimal?): String {

        if (price == null)
            return "-"

        return formatPrice(price, '$')
    }


    /**
     * Format price according to decimal dimension in the given symbol
     * @param price price as BigDecimal
     * @param symbol currency symbol (EUR, USD,..)
     * @return formatted string price
     */
    fun formatPrice(price: BigDecimal?, symbol: Char = '$'): String {

        if (price == null)
            return "-"

        val df: DecimalFormat = when {
            price.multiply(BigDecimal("1")) > BigDecimal.ONE -> DecimalFormat("#,###.00")
            else -> DecimalFormat("0.00000000")
        }

        df.decimalFormatSymbols = decimalFormatSymbols

        return  " $symbol${df.format(price)}"
    }


    /**
     * Format percentages according to decimal dimension
     * @param percentage percentage as BigDecimal
     * @return formatted string percentages
     */
    fun formatPercentage(percentage: BigDecimal?): String {


        if (percentage == null)
            return "-"

        val df: DecimalFormat = when {
            percentage.multiply(BigDecimal("1")) > BigDecimal.ONE -> DecimalFormat("#,###.00")
            else -> DecimalFormat("0.00")
        }

        df.decimalFormatSymbols = decimalFormatSymbols

        return " ${df.format(percentage)}%"

    }
}
