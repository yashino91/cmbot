package at.chaoticbits.coinmarket;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.telegram.telegrambots.logging.BotLogger;

import java.math.BigDecimal;
import java.text.DecimalFormat;



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


    /**
     * Fetch the information of the given currency
     * @param currency currency (bitcoin, ethereum, etc..)
     * @return formatted string containing currency information or error details
     */
    public String fetchCurrency(String currency) {

        String slug = getCurrencySlug(currency);

        try {
            String completURL = API_URL + slug + "/?convert=EUR";
            CloseableHttpClient client = HttpClientBuilder.create().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
            HttpGet request = new HttpGet(completURL);

            CloseableHttpResponse response = client.execute(request);
            HttpEntity ht = response.getEntity();

            BufferedHttpEntity buf = new BufferedHttpEntity(ht);
            String responseString = EntityUtils.toString(buf, "UTF-8");

            if (response.getStatusLine().getStatusCode() == 200) {

                JSONObject jsonObject;

                // correct received coinmarket cap json
                StringBuilder sb = new StringBuilder(responseString);
                sb.deleteCharAt(0);
                sb.deleteCharAt(responseString.length() - 2);

                jsonObject = new JSONObject("{result:" + sb.toString() + "}");

                return formatCurrencyResult(jsonObject.getJSONObject("result"));

            } else {
                BotLogger.warn(LOGTAG, "StatusCode: " + response.getStatusLine().getStatusCode());
                return "*Error: * \"" + currency + "\" not found";
            }
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
            return  "*Error parsing response: *" + e.getMessage();
        }
    }

    private String getCurrencySlug(String currency) {

        String slug = CoinMarketContainer.instance.findSlug(currency.toUpperCase());

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

        String change1h     = currencyInfo.isNull("percent_change_1h") ? "-" : currencyInfo.getString("percent_change_1h") + " %";
        String change24h    = currencyInfo.isNull("percent_change_24h") ? "-" : currencyInfo.getString("percent_change_24h") + " %";
        String change7d     = currencyInfo.isNull("percent_change_7d") ? "-" : currencyInfo.getString("percent_change_7d") + " %";
        String volume24h    = currencyInfo.isNull("24h_volume_usd") ? "-" : formatPrice(currencyInfo.getBigDecimal("24h_volume_usd"));
        String marketCap    = currencyInfo.isNull("market_cap_usd") ? "-" : formatPrice(currencyInfo.getBigDecimal("market_cap_usd"));

        return  "[" + currencyInfo.getString("name") + "](https://coinmarketcap.com/currencies/" + currencyInfo.getString("name")  + ") (" + currencyInfo.getString("symbol") + ")" + "\n\n" +
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
