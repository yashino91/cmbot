package at.chaoticbits.testdata

import at.chaoticbits.coinmarket.CurrencyDetails
import at.chaoticbits.config.Commands
import com.fasterxml.jackson.databind.ObjectMapper
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.api.objects.inlinequery.InlineQuery
import java.io.IOException
import java.math.BigDecimal


/**
 * Holding data for test cases
 */
object TestData {


    /**
     * Returns an array of CurrencyDetails test data
     */
    fun currencyDetails(): Array<CurrencyDetails> = arrayOf(
            CurrencyDetails(
                    134,
                    true,
                    "Golem Network Token",
                    "GNT",
                    BigDecimal.valueOf(-1.23),
                    BigDecimal.valueOf(-3.47),
                    BigDecimal.valueOf(-23.09),
                    BigDecimal.valueOf(848575.2),
                    BigDecimal.valueOf(4585934.9),
                    BigDecimal.valueOf(0.63),
                    BigDecimal.valueOf(0.48),
                    BigDecimal.valueOf(0.00000234)),
            CurrencyDetails(
                    188,
                    true,
                    "AppCoins",
                    "APPC",
                    BigDecimal.valueOf(1.23),
                    BigDecimal.valueOf(3.47),
                    BigDecimal.valueOf(23.09),
                    BigDecimal.valueOf(848575.2),
                    BigDecimal.valueOf(4585934.9),
                    BigDecimal.valueOf(0.63),
                    BigDecimal.valueOf(0.48),
                    BigDecimal.valueOf(0.00000234)),
            CurrencyDetails(
                    2500,
                    true,
                    "NoName",
                    "NON",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null)

    )


    /**
     * Inline Queries
     */
    fun validInlineQuery(): InlineQuery? {
        val mapper = ObjectMapper()
        return try {
            mapper.readValue("{\"id\": 10,\"query\": \"eth\"}", InlineQuery::class.java)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun inlineQueryNotFound(): InlineQuery? {
        val mapper = ObjectMapper()
        return try {
            mapper.readValue("{\"id\": 10,\"query\": \"notfound\"}", InlineQuery::class.java)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


    /**
     * Telegram Updates
     */
    fun requestImageUpdate(): Update? {
        val mapper = ObjectMapper()
        return try {
            mapper.readValue("{\"update_id\": 10,\"message\": {\"message_id\": 1, \"text\": \"" + Commands.coin + " eth\", \"chat\": {\"id\": 2}}}", Update::class.java)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun requestFormattedStringUpdate(): Update? {
        val mapper = ObjectMapper()
        return try {
            mapper.readValue("{\"update_id\": 10,\"message\": {\"message_id\": 1, \"text\": \"" + Commands.coin + " eth\", \"chat\": {\"id\": 2}}}", Update::class.java)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun invalidCurrencyUpdate(): Update? {
        val mapper = ObjectMapper()
        return try {
            mapper.readValue("{\"update_id\": 10,\"message\": {\"message_id\": 1, \"text\": \"" + Commands.coin + " currencynotfound\", \"chat\": {\"id\": 2}}}", Update::class.java)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

}