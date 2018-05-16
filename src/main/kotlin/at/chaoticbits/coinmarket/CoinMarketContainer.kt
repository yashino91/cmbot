package at.chaoticbits.coinmarket

import java.util.Collections.*


/**
 * Holding lists of symbols slugs and erc20 tokens
 */
object CoinMarketContainer {


    /**
     * Holding a thread safe set of all coins on CoinMarketCap
     */
    @Volatile
    private var coins: MutableSet<Coin> = synchronizedSet(sortedSetOf())


    /**
     * Holding a thread safe map of all ERC20 tokens
     */
    @Volatile
    private var erc20Tokens: MutableMap<String, String> = synchronizedMap(mutableMapOf())




    /*-------------------------------------*\
     * Coins Access Function
    \*-------------------------------------*/

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
     * Searches for a coin by the given symbol
     *
     * @param symbol [String] Symbol of a coin to search for
     * @return [Coin] The found coin or null
     */
    @Synchronized
    fun findCoinBySymbol (symbol: String): Coin? =
            coins.find { it -> it.symbol == symbol.toUpperCase() }


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




    /*-------------------------------------*\
     * ERC20 Token Access Functions
    \*-------------------------------------*/


    /**
     * Updates the [erc20Tokens] map with the given symbol and address
     *
     * @param symbol [String] Symbol of a coin
     * @param address [String] Contract Address
     */
    @Synchronized
    fun addOrReplaceErc20Token (symbol: String, address: String) =
            erc20Tokens.replace(symbol, address)


    /**
     * Checks if the given symbol is an ERC20 token
     *
     * @param symbol [String] Symbol of a coin (eth, btc,...)
     * @return True if its an ERC20 token, otherwise false
     */
    @Synchronized
    fun isErc20Token(symbol: String?): Boolean =
            erc20Tokens.containsKey(symbol)


    /**
     * Returns the map of ERC20 Tokens
     */
    fun getErc20Tokens(): MutableMap<String, String> = erc20Tokens

}
