package at.chaoticbits.api


import org.junit.Assert
import org.junit.Test


/**
 * Test API Calls
 */
class ApiTest {

    companion object {
        private const val urlOK = "http://ip.jsontest.com/"
        private const val urlFail = "fail-test"
    }

    @Test
    fun testFetch() {
        val response = Api.fetch(urlOK)

        Assert.assertNotNull(response)
        Assert.assertEquals(response.status, 200)
        Assert.assertNotNull(response.body)
    }

    @Test(expected = IllegalStateException::class)
    fun testFetchFail() {
        val response = Api.fetch(urlFail)
        Assert.assertNull(response)
    }
}
