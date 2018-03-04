package at.chaoticbits.coinmarket;

import java.math.BigDecimal;

public class TestData {

    public static CurrencyDetails currencyDetails() {
        return new CurrencyDetails(
                134,
                true,
                "Golem Network Token",
                "GNT",
                BigDecimal.valueOf(-1.23),
                BigDecimal.valueOf(3.47),
                BigDecimal.valueOf(23.09),
                BigDecimal.valueOf(848575.2),
                BigDecimal.valueOf(4585934.9),
                BigDecimal.valueOf(0.63),
                BigDecimal.valueOf(0.48),
                BigDecimal.valueOf(0.00000234)
        );
    }
}
