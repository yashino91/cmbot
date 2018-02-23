package at.chaoticbits.coinmarket;

import at.chaoticbits.api.Api;
import at.chaoticbits.api.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.logging.BotLogger;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Objects;


/**
 * Interact With Coin Market Cap
 */
public class CoinMarketCapService {

    private static final String LOGTAG  = "CoinMarketCapService";
    private static final String API_URL = "https://api.coinmarketcap.com/v1/ticker/";

    private static volatile CoinMarketCapService instance;



    /**
     * Constructor (private due to singleton pattern)
     */
    private CoinMarketCapService() { }


    /**
     * Singleton
     *
     * @return Return the instance of this class
     */
    public static CoinMarketCapService getInstance() {
        CoinMarketCapService currentInstance;

        if (instance == null) {

            synchronized (CoinMarketCapService.class) {
                if (instance == null)
                    instance = new CoinMarketCapService();

                currentInstance = instance;
            }

        } else
            currentInstance = instance;

        return currentInstance;
    }


    public String getFormattedCurrencyDetails(String currency) {
        JSONObject fetchedCurrency;

        try {
            fetchedCurrency = fetchCurrency(currency);
        } catch (IllegalStateException e) {
            BotLogger.error(LOGTAG,  e.getMessage());
            return e.getMessage();
        }

        return formatCurrencyResult(fetchedCurrency);
    }


    /**
     * Fetch the information of the given currency
     * @param currency currency (bitcoin, ethereum, etc..)
     * @return formatted string containing currency information or error details
     */
    private JSONObject fetchCurrency(String currency) throws IllegalStateException{

        String slug = getCurrencySlug(currency);

        if (currency.toLowerCase().equals("bat"))
            slug = "basic-attention-token";


        Response response;
        try {
            response = Api.fetch(API_URL + URLEncoder.encode(slug, "UTF-8") + "/?convert=EUR");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Error encoding currency string: " + e.getMessage());
        }

        if (Objects.requireNonNull(response).getStatus() == 200)
            return new JSONArray(response.getBody()).getJSONObject(0);

        else if (Objects.requireNonNull(response).getStatus() == 404)
            throw new IllegalStateException("Currency not found: *" + currency + "*");
        else
            throw new IllegalStateException("Error! StatusCode: " + response.getStatus());

    }

    private String getCurrencySlug(String currency) {

        String slug = CoinMarketContainer.symbolSlugs.get(currency.toUpperCase());

        if(slug == null)
            return currency;

        return slug;
    }


    /**
     * Format the given json object containing currency information
     * to a readable string
     * @param currencyInfo JsonObject containing currency information
     * @return formatted currency information
     */
    private String formatCurrencyResult(JSONObject currencyInfo) {

        String symbol       = currencyInfo.getString("symbol");
        String change1h     = currencyInfo.isNull("percent_change_1h") ? "-" : currencyInfo.getString("percent_change_1h") + " %";
        String change24h    = currencyInfo.isNull("percent_change_24h") ? "-" : currencyInfo.getString("percent_change_24h") + " %";
        String change7d     = currencyInfo.isNull("percent_change_7d") ? "-" : currencyInfo.getString("percent_change_7d") + " %";
        String volume24h    = currencyInfo.isNull("24h_volume_usd") ? "-" : formatPrice(currencyInfo.getBigDecimal("24h_volume_usd"));
        String marketCap    = currencyInfo.isNull("market_cap_usd") ? "-" : formatPrice(currencyInfo.getBigDecimal("market_cap_usd"));


        String erc20Token =  CoinMarketContainer.erc20Tokens.containsKey(symbol) ? "_Erc20_\n\n" : "\n";

        return  "[" + currencyInfo.getString("name") + "](https://coinmarketcap.com/currencies/" + currencyInfo.getString("name")  + ") (" + symbol + ")" + "\n" +
                erc20Token +
                "*Rank: *" + currencyInfo.getString("rank") + "\n" +
                "*EUR: *" + formatPrice(currencyInfo.getBigDecimal("price_eur"), "€") + "\n" +
                "*USD: *" + formatPrice(currencyInfo.getBigDecimal("price_usd")) + "\n" +
                "*1h: *" + change1h + "\n" +
                "*24h: *" + change24h + "\n" +
                "*7d: *" + change7d + "\n" +
                "*Volume24h: *" + volume24h + "\n" +
                "*MarketCap: *" + marketCap;

    }


    /**
     * Format price according to decimal dimension in USD
     * @param price price as BigDecimal
     * @return formatted string price
     */
    private String formatPrice(BigDecimal price) {
        return formatPrice(price, "$");
    }


    /**
     * Format price according to decimal dimension in the given symbol
     * @param price price as BigDecimal
     * @param symbol currency symbol (EUR, USD,..)
     * @return formatted string price
     */
    private String formatPrice(BigDecimal price, String symbol) {

        if(price == null)
            return "-";

        DecimalFormat df;

        if (price.multiply(new BigDecimal("1")).compareTo(BigDecimal.ONE) > 0)
            df = new DecimalFormat("#,###.00");
        else
            df = new DecimalFormat("0.00000");

        return " " + symbol + df.format(price);
    }

}
