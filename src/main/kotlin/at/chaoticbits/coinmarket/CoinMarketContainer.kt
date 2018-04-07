package at.chaoticbits.coinmarket


/**
 * Holding lists of symbols slugs and erc20 tokens
 */
object CoinMarketContainer {

    val symbolSlugs: MutableMap<String, String> = mutableMapOf()

    val erc20Tokens: MutableMap<String, String> = mutableMapOf()
}
