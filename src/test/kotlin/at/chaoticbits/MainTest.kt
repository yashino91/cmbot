package at.chaoticbits

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
    fun testFailureInitTelegramBot() {
        environmentVariables.set("CMBOT_TELEGRAM_TOKEN", null)
        Assert.assertNull(initTelegramBot())
    }
}