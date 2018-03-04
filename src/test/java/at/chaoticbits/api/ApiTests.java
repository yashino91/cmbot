package at.chaoticbits.api;


import org.testng.Assert;
import org.testng.annotations.Test;

public class ApiTests {

    private static final String urlOK = "http://ip.jsontest.com/";
    private static final String urlFail = "fail-test";


    @Test
    public void testFetch() {
        Response response = Api.fetch(urlOK);

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getStatus(), 200);
        Assert.assertNotNull(response.getBody());
    }

    @Test
    public void testFetchFail() {
        Response response = Api.fetch(urlFail);

        Assert.assertNull(response);
    }
}
