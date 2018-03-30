package at.chaoticbits.coinmarket;

import at.chaoticbits.api.Api;
import at.chaoticbits.api.Response;
import at.chaoticbits.config.Bot;
import at.chaoticbits.config.DecimalFormatter;
import at.chaoticbits.render.HtmlImageService;
import org.json.JSONArray;

import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Objects;

import static at.chaoticbits.config.DecimalFormatter.formatPercentage;
import static at.chaoticbits.config.DecimalFormatter.formatPrice;


/**
 * Interact With Coin Market Cap
 */
public final class CoinMarketCapService {

    private static final String LOGTAG  = "CoinMarketCapService";
    private static final String API_URL = "https://api.coinmarketcap.com/v1/ticker/";

    private static volatile CoinMarketCapService instance;




    /**
     * Constructor (private due to singleton pattern)
     */
    private CoinMarketCapService() {
        DecimalFormatter.getInstance();
    }


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
     * Fetch all details about the given currency at CoinMarketCap
     * and generates a styled image containing all information about the requested currency
     * @param currency currency
     * @return InputStream containing the image information
     */
    public static InputStream getCurrencyDetailsImage(String currency) throws IllegalStateException {

        if (System.getenv("PDF_CROWD_API_KEY") == null)
            throw new IllegalStateException("No PDF Crowd Api Key specified! Please declare the following System Environment Variable: PDF_CROWD_API_KEY={YOUR_API_KEY}");
        if (System.getenv("PDF_CROWD_USERNAME") == null)
            throw new IllegalStateException("No PDF Crowd Username specified! Please declare the following System Environment Variable: PDF_CROWD_USERNAME={YOUR_USERNAME}");

        CurrencyDetails currencyDetails;
        currencyDetails = fetchCurrency(currency);
        return HtmlImageService.getInstance().generateCryptoDetailsImage(currencyDetails);
    }


    /**
     * Fetch all details about the given currency at CoinMarketCap
     * and formats the result as a string
     * @param currency currency
     * @return formatted string with detailed information about the requested currency
     */
    public static String getFormattedCurrencyDetails(String currency) throws IllegalStateException {
        CurrencyDetails currencyDetails;
        currencyDetails = fetchCurrency(currency);
        return formatCurrencyResult(currencyDetails);
    }


    /**
     * Fetch the information of the given currency
     * @param currency currency (bitcoin, ethereum, etc..)
     * @return JSONObject including price information
     */
    public static CurrencyDetails fetchCurrency(String currency) throws IllegalStateException {

        String slug = getCurrencySlug(currency);

        if (!slugAllowed(slug))
            throw new IllegalStateException("Currency not found: *" + currency + "*");

        // Prefer basic attention token (bat is ambiguous)
        if (currency.toLowerCase().equals("bat"))
            slug = "basic-attention-token";


        Response response;
        try {
            response = Api.fetch(API_URL + URLEncoder.encode(slug, "UTF-8") + "/?convert=EUR");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Error encoding currency string: " + e.getMessage());
        }

        if (Objects.requireNonNull(response).getStatus() == 200)
            return new CurrencyDetails(new JSONArray(response.getBody()).getJSONObject(0));

        else if (Objects.requireNonNull(response).getStatus() == 404)
            throw new IllegalStateException("Currency not found: *" + currency + "*");
        else
            throw new IllegalStateException("Error! StatusCode: " + response.getStatus());

    }

    /**
     * Checks if the given currency slug is allowed
     * @param slug currency slug
     * @return true/false
     */
    private static boolean slugAllowed(String slug) {
        return Bot.config.allowedCurrencySlugs.isEmpty() ||
                Bot.config.allowedCurrencySlugs.contains(slug);

    }


    /**
     * Maps the requested currency to the appropriate slug for later price fetching
     * @param currency currency
     * @return slug
     */
    public static String getCurrencySlug(String currency) {

        String slug = CoinMarketContainer.symbolSlugs.get(currency.toUpperCase());

        if(slug == null)
            return currency;

        return slug;
    }


    /**
     * Format the given json object containing currency information
     * to a readable string
     * @param currencyDetails containing information about a crypto currency
     * @return formatted currency information
     */
    public static String formatCurrencyResult(CurrencyDetails currencyDetails) {


        String erc20Token =  currencyDetails.isErc20() ? "_Erc20_\n\n" : "\n";

        return  "[" + currencyDetails.getName() + "](https://coinmarketcap.com/currencies/" + currencyDetails.getName()  + ") (" + currencyDetails.getSymbol() + ")" + "\n" +
                erc20Token +
                "*Rank: *" + currencyDetails.getRank() + "\n" +
                "*EUR: *" + formatPrice(currencyDetails.getPriceEur(), '€') + "\n" +
                "*USD: *" + formatPrice(currencyDetails.getPriceUsd()) + "\n" +
                "*BTC: *" + formatPrice(currencyDetails.getPriceBtc(), ' ') + "\n" +
                "*1h: *" + formatPercentageWithEmoji(currencyDetails.getChange1h()) + "\n" +
                "*24h: *" + formatPercentageWithEmoji(currencyDetails.getChange24h()) + "\n" +
                "*7d: *" + formatPercentageWithEmoji(currencyDetails.getChange7d()) + "\n" +
                "*Volume24h: *" + formatPrice(currencyDetails.getVolume24h()) + "\n" +
                "*MarketCap: *" + formatPrice(currencyDetails.getMarketCap());

    }

    public static String formatPercentageWithEmoji(BigDecimal percentage) {
        String formattedPercentage = formatPercentage(percentage);

        if (formattedPercentage.equals("-"))
            return formattedPercentage;

        return formattedPercentage + "\t" + getUpOrDownEmoji(percentage);
    }

    public static String getUpOrDownEmoji(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) > 0)
            return ":chart_with_upwards_trend:";

        return ":chart_with_downwards_trend:";
    }

}
