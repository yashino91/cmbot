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
    val chatId = varchar("chat_id", length = 20).index()
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

            Chat.new { chatId = "12345" }
            Chat.new { chatId = "67890" }


            log.info { "Chats: ${Chat.all().joinToString { it.chatId }}" }
        }
    }
}