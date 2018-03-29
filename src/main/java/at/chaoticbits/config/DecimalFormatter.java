package at.chaoticbits.config;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;


/**
 * Decimal Format Helper Class.
 * Uses '.' for decimal separator and ',' for grouping thousands
 */
public class DecimalFormatter {


    private static DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
    private static volatile DecimalFormatter instance;

    private DecimalFormatter() {
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormatSymbols.setGroupingSeparator(',');
    }

    /**
     * Singleton
     *
     * @return Return the instance of this class
     */
    public static DecimalFormatter getInstance() {
        DecimalFormatter currentInstance;

        if (instance == null) {

            synchronized (DecimalFormatter.class) {
                if (instance == null)
                    instance = new DecimalFormatter();

                currentInstance = instance;
            }

        } else
            currentInstance = instance;

        return currentInstance;
    }

    /**
     * Format price according to decimal dimension in USD
     * @param price price as BigDecimal
     * @return formatted string price
     */
    public static String formatPrice(BigDecimal price) {
        return formatPrice(price, '$');
    }


    /**
     * Format price according to decimal dimension in the given symbol
     * @param price price as BigDecimal
     * @param symbol currency symbol (EUR, USD,..)
     * @return formatted string price
     */
    public static String formatPrice(BigDecimal price, Character symbol) {

        if(price == null)
            return "-";

        DecimalFormat df;

        if (price.multiply(new BigDecimal("1")).compareTo(BigDecimal.ONE) > 0)
            df = new DecimalFormat("#,###.00");
        else
            df = new DecimalFormat("0.00000000");

        df.setDecimalFormatSymbols(decimalFormatSymbols);

        return " " + symbol + df.format(price);
    }


    /**
     * Format percentages according to decimal dimension
     * @param percentage percentage as BigDecimal
     * @return formatted string percentages
     */
    public static String formatPercentage(BigDecimal percentage) {
        if(percentage == null)
            return "-";

        DecimalFormat df;

        if (percentage.multiply(new BigDecimal("1")).compareTo(BigDecimal.ONE) > 0)
            df = new DecimalFormat("#,###.00");
        else
            df = new DecimalFormat("0.00");

        df.setDecimalFormatSymbols(decimalFormatSymbols);

        return  " " + df.format(percentage) + "%";
    }
}
