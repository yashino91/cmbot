package at.chaoticbits.updatehandlers

import at.chaoticbits.Main
import at.chaoticbits.config.Bot
import at.chaoticbits.updateshandlers.CryptoHandler
import com.fasterxml.jackson.databind.ObjectMapper
import org.telegram.telegrambots.api.objects.Update
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import java.io.IOException

class CryptoHandlerTest {

    lateinit var cryptoHandler: CryptoHandler

    @BeforeClass
    fun setup() {
        Main.main(arrayOf())
        cryptoHandler = CryptoHandler()
    }


    @Test
    fun testGetBotUsername() {
        val botUsername = cryptoHandler.botUsername
        Assert.assertNotNull(botUsername)
        Assert.assertEquals(botUsername, Bot.config.botName)
    }

    @Test
    fun testGetBotToken() {
        val botUsername = cryptoHandler.botToken
        Assert.assertNotNull(botUsername)
    }

    @Test
    fun testOnUpdateReceived() {
        cryptoHandler.onUpdateReceived(requestImageUpdate!!)
        cryptoHandler.onUpdateReceived(requestFormattedStringUpdate!!)
        cryptoHandler.onUpdateReceived(invalidCurrencyUpdate!!)
    }


    private val requestImageUpdate: Update?
        get() {
            val mapper = ObjectMapper()
            return try {
                mapper.readValue("{\"update_id\": 10,\"message\": {\"message_id\": 1, \"text\": \"" + Bot.config.imageCommand + "eth\", \"chat\": {\"id\": 2}}}", Update::class.java)
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }

        }

    private val requestFormattedStringUpdate: Update?
        get() {
            val mapper = ObjectMapper()
            return try {
                mapper.readValue("{\"update_id\": 10,\"message\": {\"message_id\": 1, \"text\": \"" + Bot.config.stringCommand + "eth\", \"chat\": {\"id\": 2}}}", Update::class.java)
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }

        }

    private val invalidCurrencyUpdate: Update?
        get() {
            val mapper = ObjectMapper()
            return try {
                mapper.readValue("{\"update_id\": 10,\"message\": {\"message_id\": 1, \"text\": \"" + Bot.config.stringCommand + "currencynotfound\", \"chat\": {\"id\": 2}}}", Update::class.java)
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }

        }


}
