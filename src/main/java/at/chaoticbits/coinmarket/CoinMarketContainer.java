package at.chaoticbits.coinmarket;

import java.util.HashMap;
import java.util.Map;


public class CoinMarketContainer {

    public static final CoinMarketContainer instance = new CoinMarketContainer();


    private Map<String, String> symbolSlugs = new HashMap<>();


    public void addOrUpdateSymbolSlug(String symbol, String slug) {
        this.symbolSlugs.put(symbol, slug);
    }

    public String findSlug(String symbol) {
        return this.symbolSlugs.get(symbol);
    }
}
