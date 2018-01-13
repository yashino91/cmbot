package at.chaoticbits.coinmarket;

import at.chaoticbits.api.Api;
import at.chaoticbits.api.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.logging.BotLogger;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.TimerTask;

/**
 * Update symbol slug list periodically
 */
public class CoinMarketScheduler extends TimerTask {

    private static final String LOGTAG  = "CoinMarketScheduler";


    public CoinMarketScheduler () {
        BotLogger.info(LOGTAG, "Initialize");
    }



    @Override
    public void run () {

        updateErc20TokenList();
        updateSymbolSlugs();
    }


    /**
     * Fetch CMC coin information and populate map with symbols related to their slug,
     * in order to support searching via symbols (btc, eth,...)
     * CoinNarketCap API currently only supports search by slug (itcoin, ethereum,...)
     */
    private void updateSymbolSlugs() {

        try {

            Response response = Api.fetch("https://files.coinmarketcap.com/generated/search/quick_search.json");

            if (Objects.requireNonNull(response).getStatus() == 200) {

                JSONArray jsonArray = new JSONArray(response.getBody());


                try (PrintWriter writer = new PrintWriter("./telegram-commands/top-coins.txt", "UTF-8")) {

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        // populate top 85 coins in telegram commands
                        if (i < 85)
                            updateBotCommands(writer, jsonObject);

                        CoinMarketContainer.symbolSlugs.put(jsonObject.getString("symbol"), jsonObject.getString("slug"));
                    }

                    BotLogger.info(LOGTAG, "Successfully updated symbol slugs");

                } catch (FileNotFoundException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                BotLogger.warn(LOGTAG, "StatusCode: " + response.getStatus());

            }
        } catch (Exception e) {
            BotLogger.error(LOGTAG, "Error parsing new symbol slug list: " + e);
        }
    }


    /**
     * Fetch Erc20 tokens and update cache
     */
    private void updateErc20TokenList() {

        Response response = Api.fetch("https://raw.githubusercontent.com/kvhnuke/etherwallet/mercury/app/scripts/tokens/ethTokens.json");

        if (Objects.requireNonNull(response).getStatus() == 200) {

            JSONArray jsonArray = new JSONArray(response.getBody());

            for (int i = 0; i < jsonArray.length(); i++) {
                String erc20Symbol = jsonArray.getJSONObject(i).getString("symbol");
                String erc20Address = jsonArray.getJSONObject(i).getString("address");
                CoinMarketContainer.erc20Tokens.put(erc20Symbol, erc20Address);
            }

            BotLogger.info(LOGTAG, "Successfully updated erc20 tokens");

        } else {
            BotLogger.warn(LOGTAG, "StatusCode: " + response.getStatus());

        }
    }


    private void updateBotCommands(PrintWriter writer, JSONObject jsonObject) {
            writer.println(jsonObject.getString("symbol").toLowerCase() + " - " + jsonObject.getString("name"));

    }
}