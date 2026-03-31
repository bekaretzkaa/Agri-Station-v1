package com.example.agristation1.data.chatDetails

import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface ChatDetailsRepository {

    fun getAllChats(): Flow<List<ChatEntity>>

    fun getMessagesByChatId(chatId: Int): Flow<List<ChatMessageEntity>>

    suspend fun insertChat(chat: ChatEntity): Int

    suspend fun updateChatUpdatedAt(chatId: Int, updatedAt: Instant)

    suspend fun updateChatTitle(chatId: Int, title: String?)

    suspend fun insertMessage(message: ChatMessageEntity)

    suspend fun updateMessage(positionInChat: Int, status: MessageStatus, text: String)

    suspend fun deleteChat(chatId: Int)

}

class ChatDetailsOfflineRepository(private val chatDetailsDao: ChatDetailsDao) : ChatDetailsRepository {

    override fun getAllChats(): Flow<List<ChatEntity>> {
        return chatDetailsDao.getAllChats()
    }

    override fun getMessagesByChatId(chatId: Int): Flow<List<ChatMessageEntity>> {
        return chatDetailsDao.getMessagesByChatId(chatId)
    }

    override suspend fun insertChat(chat: ChatEntity): Int {
        return chatDetailsDao.insertChat(chat).toInt()
    }

    override suspend fun updateChatUpdatedAt(chatId: Int, updatedAt: Instant) {
        chatDetailsDao.updateChatUpdatedAt(chatId, updatedAt)
    }

    override suspend fun updateChatTitle(chatId: Int, title: String?) {
        chatDetailsDao.updateChatTitle(chatId, title)
    }

    override suspend fun insertMessage(message: ChatMessageEntity) {
        chatDetailsDao.insertMessage(message)
    }

    override suspend fun updateMessage(positionInChat: Int, status: MessageStatus, text: String) {
        chatDetailsDao.updateMessage(positionInChat, status, text)
    }

    override suspend fun deleteChat(chatId: Int) {
        chatDetailsDao.deleteChat(chatId)
    }

}