package at.chaoticbits.updatehandlers

import at.chaoticbits.coinmarket.Coin
import at.chaoticbits.coinmarket.CoinMarketContainer
import at.chaoticbits.coinmarket.CurrencyNotFoundException
import at.chaoticbits.config.Bot
import at.chaoticbits.config.Commands
import at.chaoticbits.testdata.Config
import at.chaoticbits.testdata.TestData
import at.chaoticbits.updateshandlers.CryptoHandler
import at.chaoticbits.updateshandlers.coinCommand
import org.hamcrest.CoreMatchers.containsString
import org.junit.*
import org.junit.contrib.java.lang.system.EnvironmentVariables
import org.slf4j.event.Level
import org.telegram.telegrambots.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent
import org.telegram.telegrambots.api.objects.inlinequery.result.InlineQueryResultArticle


/**
 * Test Telegram Bot Update Handler
 */
open class CryptoHandlerTest {


    companion object {

        val mockedCryptoHandler = CryptoHandler()

        @ClassRule
        @JvmField
        val environmentVariables: EnvironmentVariables = EnvironmentVariables()

        @BeforeClass
        @JvmStatic
        fun setup() {
            environmentVariables.set("CMBOT_TELEGRAM_TOKEN", Config.testBotToken)
            CoinMarketContainer.coinListings.add(Coin(1, 1, "Ethereum", "ETH", "ethereum"))
        }
    }


    @Test
    fun testGetBotUsername() {
        val botUsername = mockedCryptoHandler.botUsername
        Assert.assertNotNull(botUsername)
        Assert.assertEquals(botUsername, Bot.config.botName)
    }

    @Test
    fun testGetBotToken() {
        val botToken = mockedCryptoHandler.botToken
        Assert.assertEquals(botToken, Config.testBotToken)
    }

    @Test
    fun testInitSendMessage() {
        val sendMessageRequest = mockedCryptoHandler.initSendMessageRequest(Config.chatId)
        Assert.assertEquals(sendMessageRequest.chatId, Config.chatId.toString())
    }

    @Test
    fun testValidAnswerInlineQuery() {
        val answerInlineQuery = mockedCryptoHandler.answerInlineQuery(TestData.validInlineQuery()!!)
        val inlineQueryResults = answerInlineQuery.results

        Assert.assertNotNull(answerInlineQuery)
        Assert.assertNotNull(inlineQueryResults)

        val firstResult: InlineQueryResultArticle = inlineQueryResults.first() as InlineQueryResultArticle

        Assert.assertThat(firstResult.description, containsString("Rank"))

        val textMessageContent = firstResult.inputMessageContent as InputTextMessageContent
        Assert.assertEquals(textMessageContent.messageText,"/coin ethereum")
    }

    @Test
    fun testAnswerInlineQueryNotFound() {
        val answerInlineQuery = mockedCryptoHandler.answerInlineQuery(TestData.inlineQueryNotFound()!!)
        val inlineQueryResults = answerInlineQuery.results

        Assert.assertNotNull(answerInlineQuery)
        Assert.assertEquals(inlineQueryResults.size, 0)
    }


    @Test
    fun testTextRequest() {
        var replyMessage = mockedCryptoHandler.textRequest(Commands.start)
        Assert.assertThat(replyMessage, containsString("Welcome"))

        replyMessage = mockedCryptoHandler.textRequest(Commands.help)
        Assert.assertThat(replyMessage, containsString("You can control me"))

        replyMessage = mockedCryptoHandler.textRequest("/commandnotfound")
        Assert.assertThat(replyMessage, containsString("Command not found"))
    }


    @Test
    fun testFailure() {
        var message = mockedCryptoHandler.failure(CurrencyNotFoundException("Currency not found _"), Level.WARN)
        Assert.assertEquals(message, "Currency not found \\_")

        message = mockedCryptoHandler.failure(IllegalStateException("Error! StatusCode: 500"), Level.ERROR)
        Assert.assertEquals(message, "Error! StatusCode: 500")
    }

    @Test
    fun testOnUpdateReceived() {
        mockedCryptoHandler.onUpdateReceived(TestData.requestImageUpdate()!!)
        mockedCryptoHandler.onUpdateReceived(TestData.invalidCurrencyUpdate()!!)
    }

    @Test
    fun testCoinCommand() {
        val update  = TestData.requestImageUpdate()
        val photo   = coinCommand(update!!.message, "ethereum")

        Assert.assertNotNull(photo)
    }
}
