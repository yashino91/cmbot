package at.chaoticbits.coinmarket

import java.math.BigDecimal


/**
 * Holding data for test cases
 */
object TestData {


    /**
     * Returns an array of CurrencyDetails test data
     */
    fun currencyDetails(): Array<CurrencyDetails> = arrayOf(
            CurrencyDetails(
                134,
                true,
                "Golem Network Token",
                "GNT",
                BigDecimal.valueOf(-1.23),
                BigDecimal.valueOf(-3.47),
                BigDecimal.valueOf(-23.09),
                BigDecimal.valueOf(848575.2),
                BigDecimal.valueOf(4585934.9),
                BigDecimal.valueOf(0.63),
                BigDecimal.valueOf(0.48),
                BigDecimal.valueOf(0.00000234)),
            CurrencyDetails(
                    188,
                    true,
                    "AppCoins",
                    "APPC",
                    BigDecimal.valueOf(1.23),
                    BigDecimal.valueOf(3.47),
                    BigDecimal.valueOf(23.09),
                    BigDecimal.valueOf(848575.2),
                    BigDecimal.valueOf(4585934.9),
                    BigDecimal.valueOf(0.63),
                    BigDecimal.valueOf(0.48),
                    BigDecimal.valueOf(0.00000234)),
            CurrencyDetails(
                    2500,
                    true,
                    "NoName",
                    "NON",
                    null,
                   null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null)

    )

}