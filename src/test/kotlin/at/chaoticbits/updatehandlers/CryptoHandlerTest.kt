package at.chaoticbits.updatehandlers

import at.chaoticbits.coinmarket.Coin
import at.chaoticbits.coinmarket.CoinMarketContainer
import at.chaoticbits.config.Bot
import at.chaoticbits.config.Commands
import at.chaoticbits.testdata.Config
import at.chaoticbits.testdata.TestData
import at.chaoticbits.updateshandlers.CryptoHandler
import at.chaoticbits.updateshandlers.coinCommand
import org.hamcrest.CoreMatchers.containsString
import org.junit.*
import org.junit.contrib.java.lang.system.EnvironmentVariables
import org.telegram.telegrambots.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent
import org.telegram.telegrambots.api.objects.inlinequery.result.InlineQueryResultArticle
import org.telegram.telegrambots.bots.DefaultBotOptions


/**
 * Test Telegram Bot Update Handler
 */
class CryptoHandlerTest {


    companion object {

        val cryptoHandler = CryptoHandler(DefaultBotOptions())

        @ClassRule
        @JvmField
        val environmentVariables: EnvironmentVariables = EnvironmentVariables()

        @BeforeClass
        @JvmStatic
        fun setup() {
            environmentVariables.set("CMBOT_TELEGRAM_TOKEN", Config.testBotToken)
            CoinMarketContainer.addOrReplaceCoin(Coin(1, 1, "Ethereum", "ETH", "ethereum"))
        }

        @AfterClass
        @JvmStatic
        fun cleanUp() {
            cryptoHandler.onClosing()
        }
    }


    @Test
    fun testGetBotUsername() {
        val botUsername = cryptoHandler.botUsername
        Assert.assertNotNull(botUsername)
        Assert.assertEquals( Bot.config.botName, botUsername)
    }

    @Test
    fun testGetBotToken() {
        val botToken = cryptoHandler.botToken
        Assert.assertEquals(Config.testBotToken, botToken)
    }

    @Test
    fun testInitSendMessage() {
        val sendMessageRequest = cryptoHandler.initSendMessageRequest(Config.chatId)
        Assert.assertEquals(Config.chatId.toString(), sendMessageRequest.chatId)
    }

    @Test
    fun testValidAnswerInlineQuery() {
        val answerInlineQuery = cryptoHandler.answerInlineQuery(TestData.validInlineQuery()!!)
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
        val answerInlineQuery = cryptoHandler.answerInlineQuery(TestData.inlineQueryNotFound()!!)
        val inlineQueryResults = answerInlineQuery.results

        Assert.assertNotNull(answerInlineQuery)
        Assert.assertEquals(0, inlineQueryResults.size)
    }


//    @Test
//    fun testTextRequest() {
//        var replyMessage = cryptoHandler.textRequest(Commands.start)
//        Assert.assertThat(replyMessage, containsString("Welcome"))
//
//        replyMessage = cryptoHandler.textRequest(Commands.help)
//        Assert.assertThat(replyMessage, containsString("You can control me"))
//
//        replyMessage = cryptoHandler.textRequest("/commandnotfound")
//        Assert.assertThat(replyMessage, containsString("Command not found"))
//    }


    @Test
    fun testEscapeMessage() {
        val message = cryptoHandler.escapeMessage("Currency not found _bla_")
        Assert.assertEquals("Currency not found \\_bla\\_", message)
    }

    @Test
    fun testOnUpdateReceived() {
        cryptoHandler.onUpdateReceived(TestData.requestImageUpdate()!!)
        cryptoHandler.onUpdateReceived(TestData.invalidCurrencyUpdate()!!)
    }

    @Test
    fun testCoinCommand() {
        val update  = TestData.requestImageUpdate()
        val photo   = coinCommand(update!!.message, "ethereum")

        Assert.assertNotNull(photo)
    }

    @Test
    fun testIndexOfCommandEnd() {
        val index = cryptoHandler.indexOfCommandEnd("/coin ethereum@BotName")
        Assert.assertEquals(14, index)
    }
}
