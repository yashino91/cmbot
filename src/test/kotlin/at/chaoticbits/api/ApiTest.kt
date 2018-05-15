package at.chaoticbits.api


import org.junit.Assert
import org.junit.Test


/**
 * Test API Calls
 */
class ApiTest {

    companion object {
        private const val urlOK = "https://reqres.in/api/users?page=1"
        private const val urlFail = "fail-test"
    }

    @Test
    fun testFetch() {
        val response = Api.fetch(urlOK)

        Assert.assertNotNull(response)
        Assert.assertEquals(200, response.status)
        Assert.assertNotNull(response.body)
    }

    @Test(expected = IllegalStateException::class)
    fun testFetchFail() {
        val response = Api.fetch(urlFail)
        Assert.assertNull(response)
    }
}
