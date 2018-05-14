package at.chaoticbits

import at.chaoticbits.testdata.Config
import org.junit.Assert
import org.junit.ClassRule
import org.junit.Test
import org.junit.contrib.java.lang.system.EnvironmentVariables

class MainTest {

    companion object {

        @ClassRule
        @JvmField
        val environmentVariables: EnvironmentVariables = EnvironmentVariables()

    }

    @Test
    fun testSuccessInitTelegramBot() {
        environmentVariables.set("CMBOT_TELEGRAM_TOKEN", Config.testBotToken)
        Assert.assertEquals(initTelegramBot(), true)
    }

    @Test
    fun testFailureInitTelegramBot() {
        environmentVariables.set("CMBOT_TELEGRAM_TOKEN", null)
        Assert.assertEquals(initTelegramBot(), false)
    }
}