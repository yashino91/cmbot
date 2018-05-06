package at.chaoticbits.coinmarket


/**
 * Holding lists of symbols slugs and erc20 tokens
 */
object CoinMarketContainer {

    val coinListings: MutableMap<String, Coin> = mutableMapOf()

    val erc20Tokens: MutableMap<String, String> = mutableMapOf()
}
