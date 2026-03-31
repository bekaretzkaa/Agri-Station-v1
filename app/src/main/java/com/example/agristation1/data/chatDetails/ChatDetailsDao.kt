package com.example.agristation1.data.chatDetails

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface ChatDetailsDao {

    @Query("SELECT * FROM chats_history ORDER BY updated_at DESC")
    fun getAllChats(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chat_message WHERE chat_id = :chatId ORDER BY position_in_chat ASC")
    fun getMessagesByChatId(chatId: Int): Flow<List<ChatMessageEntity>>

    @Insert
    suspend fun insertChat(chat: ChatEntity): Long

    @Query("UPDATE chats_history SET updated_at = :updatedAt WHERE id = :chatId")
    suspend fun updateChatUpdatedAt(chatId: Int, updatedAt: Instant)

    @Query("UPDATE chats_history SET title = :title WHERE id = :chatId")
    suspend fun updateChatTitle(chatId: Int, title: String?)

    @Insert
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("UPDATE chat_message SET status = :status, text = :text WHERE position_in_chat = :positionInChat")
    suspend fun updateMessage(positionInChat: Int, status: MessageStatus, text: String)

    @Query("DELETE FROM chats_history WHERE id = :chatId")
    suspend fun deleteChat(chatId: Int)
}