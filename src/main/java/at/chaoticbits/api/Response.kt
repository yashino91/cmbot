package at.chaoticbits.api


/**
 * Illustrates a HttpResponse containing
 * the Http Status Code and the response body
 */
data class Response(val status: Int, val body: String)
