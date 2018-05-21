package at.chaoticbits.updateshandlers

import mu.KotlinLogging


private val log = KotlinLogging.logger {}
object PushNotification {

    private var authorizedUser: Int? = null
    private var status: Int? = null

    fun setStatus(status: Int) {
        this.status = status
    }

    fun startMessage() = "Please send me the token of this Telegram Bot to authorize you!"

    fun authorize(fromId: Int, botToken: String, commandToken: String): String {
        if(botToken == commandToken) {
            this.authorizedUser = fromId
            return "Success! Please send me your notification"
        }

        return "Failed to authorize. Wrong token!"
    }

    fun isAuthorized(fromId: Int) = this.authorizedUser == fromId


    fun getStatus(): Int? = this.status
}
