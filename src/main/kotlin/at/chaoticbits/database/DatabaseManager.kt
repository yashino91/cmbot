package at.chaoticbits.database

import mu.KotlinLogging
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

object Chats: IntIdTable() {
    val chatId = long("chat_id").index()
}


class Chat(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<Chat>(Chats)

    var chatId by Chats.chatId
}


object DatabaseManager {

    private val log = KotlinLogging.logger {}


    init {
        Database.connect("jdbc:sqlite:cmbot.db", driver = "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        transaction {

            SchemaUtils.create(Chats)
            log.info { "Initialized Database" }
        }
    }


    fun saveChatIfNotExist(_chatId: Long) {
        transaction {
            val chats = Chat.find { Chats.chatId eq _chatId }
            val count = chats.count()
            if (count == 0) {
                Chat.new { chatId = _chatId }
                log.debug { "Added new chatId to Database: $_chatId" }
            }
        }
    }

    fun getAllChats(): List<Long> {
        return transaction {
            Chat.all().map { it.chatId }
        }
    }
}