package at.chaoticbits.coin

import java.util.Collections.*


/**
 * Holding lists of symbols slugs
 */
object CoinContainer {


    /**
     * Holding a thread safe set of all coins
     */
    @Volatile
    private var coins: MutableSet<Coin> = synchronizedSet(sortedSetOf())



    /**
     * Searches for coins that match the given query.
     * If the symbol or name of the coin starts with the given query, it will be returned
     *
     * @param query [String] Query containing partial name or symbol of a coin
     * @return [List] List of found coins
     */
    @Synchronized
    fun findCoins (query: String): List<Coin> =
            coins.filter { it -> it.symbol.startsWith(query, true) || it.name.startsWith(query, true) }


    /**
     * Searches for a coin by the given currency string
     * and checks if the currency string equals the symbol or name of a coin (case will be ignored)
     *
     * @param currency [String] currency of a coin to search for
     * @return [Coin] The found coin or null
     */
    @Synchronized
    fun findCoinBySymbolOrName (currency: String): Coin? =
            coins.find { it -> it.symbol.equals(currency, ignoreCase = true) }


    /**
     * Adds or replaces a coin in [coins]
     *
     * @param coin [Coin]
     */
    @Synchronized
    fun addOrReplaceCoin (coin: Coin) {
        if (coins.contains(coin))
            coins.remove(coin)

        coins.add(coin)
    }


    /**
     * Returns the set of coins
     */
    fun getCoins(): MutableSet<Coin> = coins

}
