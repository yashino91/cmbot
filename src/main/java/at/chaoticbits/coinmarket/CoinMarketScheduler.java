package at.chaoticbits.coinmarket;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.logging.BotLogger;

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

        try {

            CloseableHttpClient client = HttpClientBuilder.create().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
            HttpGet request = new HttpGet("https://files.coinmarketcap.com/generated/search/quick_search.json");

            CloseableHttpResponse response = client.execute(request);
            HttpEntity ht = response.getEntity();

            BufferedHttpEntity buf = new BufferedHttpEntity(ht);
            String responseString = EntityUtils.toString(buf, "UTF-8");

            if (response.getStatusLine().getStatusCode() == 200) {

                JSONArray jsonArray = new JSONArray(responseString);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    CoinMarketContainer.instance.addOrUpdateSymbolSlug(jsonObject.getString("symbol"), jsonObject.getString("slug"));
                }

                BotLogger.info(LOGTAG, "Successfully updated symbol slugs");

            } else {
                BotLogger.warn(LOGTAG, "StatusCode: " + response.getStatusLine().getStatusCode());

            }
        } catch (Exception e) {
            BotLogger.error(LOGTAG, "Error parsing new symbol slug list: " + e.getMessage());
        }

    }
}