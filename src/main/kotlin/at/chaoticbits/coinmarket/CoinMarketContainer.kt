package at.chaoticbits.coinmarket

import java.util.Collections.synchronizedList
import java.util.Collections.synchronizedMap


/**
 * Holding lists of symbols slugs and erc20 tokens
 */
object CoinMarketContainer {

    val coinListings: MutableList<Coin> = synchronizedList(mutableListOf())

    val erc20Tokens: MutableMap<String, String> = synchronizedMap(mutableMapOf())


    fun findCoins (query: String): List<Coin> =
        synchronized(CoinMarketContainer.coinListings) {
            CoinMarketContainer.coinListings.filter { it -> it.symbol.startsWith(query, true) || it.name.startsWith(query, true) }
        }

}
