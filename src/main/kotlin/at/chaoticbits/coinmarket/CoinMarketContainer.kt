package at.chaoticbits.coinmarket


/**
 * Holding lists of symbols slugs and erc20 tokens
 */
object CoinMarketContainer {

    val coinListings: MutableList<Coin> = mutableListOf()

    val erc20Tokens: MutableMap<String, String> = mutableMapOf()


    fun findCoins (query: String): List<Coin> =
        CoinMarketContainer.coinListings
                .filter { it -> it.symbol.startsWith(query, true) || it.name.startsWith(query, true) }

}
