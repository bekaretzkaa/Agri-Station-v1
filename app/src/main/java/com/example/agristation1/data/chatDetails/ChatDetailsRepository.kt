package com.example.agristation1.data.chatDetails

import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface ChatDetailsRepository {

    fun getAllChats(): Flow<List<ChatEntity>>

    fun getMessagesByChatId(chatId: Long): Flow<List<ChatMessageEntity>>

    suspend fun getMessagesByChatIdList(chatId: Long): List<ChatMessageEntity>

    suspend fun insertChat(chat: ChatEntity): Long

    suspend fun updateChatUpdatedAt(chatId: Long, updatedAt: Instant)

    suspend fun updateChatTitle(chatId: Long, title: String?)

    suspend fun insertMessage(message: ChatMessageEntity)

    suspend fun updateMessage(positionInChat: Long, status: MessageStatus, text: String)

    suspend fun deleteChat(chatId: Long)

}

class ChatDetailsOfflineRepository(private val chatDetailsDao: ChatDetailsDao) : ChatDetailsRepository {

    override fun getAllChats(): Flow<List<ChatEntity>> {
        return chatDetailsDao.getAllChats()
    }

    override fun getMessagesByChatId(chatId: Long): Flow<List<ChatMessageEntity>> {
        return chatDetailsDao.getMessagesByChatId(chatId)
    }

    override suspend fun getMessagesByChatIdList(chatId: Long): List<ChatMessageEntity> {
        return chatDetailsDao.getMessagesByChatIdList(chatId)
    }

    override suspend fun insertChat(chat: ChatEntity): Long {
        return chatDetailsDao.insertChat(chat)
    }

    override suspend fun updateChatUpdatedAt(chatId: Long, updatedAt: Instant) {
        chatDetailsDao.updateChatUpdatedAt(chatId, updatedAt)
    }

    override suspend fun updateChatTitle(chatId: Long, title: String?) {
        chatDetailsDao.updateChatTitle(chatId, title)
    }

    override suspend fun insertMessage(message: ChatMessageEntity) {
        chatDetailsDao.insertMessage(message)
    }

    override suspend fun updateMessage(positionInChat: Long, status: MessageStatus, text: String) {
        chatDetailsDao.updateMessage(positionInChat, status, text)
    }

    override suspend fun deleteChat(chatId: Long) {
        chatDetailsDao.deleteChat(chatId)
    }

}