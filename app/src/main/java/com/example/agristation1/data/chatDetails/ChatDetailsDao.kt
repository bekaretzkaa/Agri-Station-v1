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
    fun getMessagesByChatId(chatId: Long): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_message WHERE chat_id = :chatId ORDER BY position_in_chat ASC")
    suspend fun getMessagesByChatIdList(chatId: Long): List<ChatMessageEntity>

    @Insert
    suspend fun insertChat(chat: ChatEntity): Long

    @Query("UPDATE chats_history SET updated_at = :updatedAt WHERE id = :chatId")
    suspend fun updateChatUpdatedAt(chatId: Long, updatedAt: Instant)

    @Query("UPDATE chats_history SET title = :title WHERE id = :chatId")
    suspend fun updateChatTitle(chatId: Long, title: String?)

    @Insert
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("UPDATE chat_message SET status = :status, text = :text WHERE position_in_chat = :positionInChat")
    suspend fun updateMessage(positionInChat: Long, status: MessageStatus, text: String)

    @Query("DELETE FROM chats_history WHERE id = :chatId")
    suspend fun deleteChat(chatId: Long)
}