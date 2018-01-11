package at.chaoticbits.api;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.telegram.telegrambots.logging.BotLogger;

import java.io.IOException;



/**
 * Http Client Helper
 */
public class Api {

    private static final String LOGTAG  = "Api";

    private static final CloseableHttpClient client = HttpClientBuilder.create().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();



    /**
     * Request a resource from the given url
     * @param url url
     * @return {@link Response} containing the body and status code
     */
    public static Response fetch(String url) {

        HttpGet request = new HttpGet(url);

        try (CloseableHttpResponse response = client.execute(request)) {

            HttpEntity ht = response.getEntity();
            BufferedHttpEntity buf = new BufferedHttpEntity(ht);

            return new Response(response.getStatusLine().getStatusCode(), EntityUtils.toString(buf, "UTF-8"));

        } catch (IOException e) {
            BotLogger.error(LOGTAG, "Error fetching " + url + "\n" + e);
            return null;
        }
    }
}
