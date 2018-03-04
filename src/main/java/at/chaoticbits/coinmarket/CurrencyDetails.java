package at.chaoticbits.coinmarket;

import org.json.JSONObject;
import java.math.BigDecimal;


public class CurrencyDetails {

    private int rank;

    private String name;
    private String symbol;

    private boolean isErc20;

    private BigDecimal change1h;
    private BigDecimal change24h;
    private BigDecimal change7d;
    private BigDecimal volume24h;
    private BigDecimal marketCap;

    private BigDecimal priceUsd;
    private BigDecimal priceEur;
    private BigDecimal priceBtc;

    public CurrencyDetails(JSONObject jsonObject) {

        this.rank = jsonObject.getInt("rank");
        this.name = jsonObject.getString("name");
        this.symbol = jsonObject.getString("symbol");
        this.isErc20 = CoinMarketContainer.erc20Tokens.containsKey(symbol);

        this.change1h = getValueOrNull("percent_change_1h", jsonObject);
        this.change24h = getValueOrNull("percent_change_24h", jsonObject);
        this.change7d = getValueOrNull("percent_change_7d", jsonObject);

        this.volume24h = getValueOrNull("24h_volume_usd", jsonObject);
        this.marketCap = getValueOrNull("market_cap_usd", jsonObject);

        this.priceUsd = getValueOrNull("price_usd", jsonObject);
        this.priceEur = getValueOrNull("price_eur", jsonObject);
        this.priceBtc = getValueOrNull("price_btc", jsonObject);

    }

    public CurrencyDetails(int rank, boolean isErc20, String name, String symbol, BigDecimal change1h, BigDecimal change24h, BigDecimal change7d, BigDecimal volume24h, BigDecimal marketCap, BigDecimal priceUsd, BigDecimal priceEur, BigDecimal priceBtc) {
        this.rank = rank;
        this.name = name;
        this.symbol = symbol;
        this.isErc20 = isErc20;
        this.change1h = change1h;
        this.change24h = change24h;
        this.change7d = change7d;
        this.volume24h = volume24h;
        this.marketCap = marketCap;
        this.priceUsd = priceUsd;
        this.priceEur = priceEur;
        this.priceBtc = priceBtc;
    }

    private BigDecimal getValueOrNull(String key, JSONObject jsonObject) {
        return jsonObject.isNull(key) ? null : jsonObject.getBigDecimal(key);
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public boolean isErc20() {
        return isErc20;
    }

    public void setErc20(boolean erc20) {
        isErc20 = erc20;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getChange1h() {
        return change1h;
    }

    public void setChange1h(BigDecimal change1h) {
        this.change1h = change1h;
    }

    public BigDecimal getChange24h() {
        return change24h;
    }

    public void setChange24h(BigDecimal change24h) {
        this.change24h = change24h;
    }

    public BigDecimal getChange7d() {
        return change7d;
    }

    public void setChange7d(BigDecimal change7d) {
        this.change7d = change7d;
    }

    public BigDecimal getVolume24h() {
        return volume24h;
    }

    public void setVolume24h(BigDecimal volume24h) {
        this.volume24h = volume24h;
    }

    public BigDecimal getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(BigDecimal marketCap) {
        this.marketCap = marketCap;
    }

    public BigDecimal getPriceUsd() {
        return priceUsd;
    }

    public void setPriceUsd(BigDecimal priceUsd) {
        this.priceUsd = priceUsd;
    }

    public BigDecimal getPriceEur() {
        return priceEur;
    }

    public void setPriceEur(BigDecimal priceEur) {
        this.priceEur = priceEur;
    }

    public BigDecimal getPriceBtc() {
        return priceBtc;
    }

    public void setPriceBtc(BigDecimal priceBtc) {
        this.priceBtc = priceBtc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
