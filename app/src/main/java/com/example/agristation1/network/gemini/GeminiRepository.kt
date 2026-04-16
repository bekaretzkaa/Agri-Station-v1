package com.example.agristation1.network.gemini

import com.example.agristation1.BuildConfig
import com.example.agristation1.data.alertDetails.AlertDetailsOfflineRepository
import com.example.agristation1.data.chatDetails.ChatDetailsOfflineRepository
import com.example.agristation1.data.chatDetails.MessageRole
import com.example.agristation1.data.chatDetails.toText
import com.example.agristation1.data.fieldDetails.FieldDetailsOfflineRepository
import com.example.agristation1.data.taskDetails.TaskDetailsOfflineRepository

class GeminiRepository(
    private val fieldDetailsOfflineRepository: FieldDetailsOfflineRepository,
    private val alertDetailsOfflineRepository: AlertDetailsOfflineRepository,
    private val taskDetailsOfflineRepository: TaskDetailsOfflineRepository,
    private val chatDetailsOfflineRepository: ChatDetailsOfflineRepository,
    private val geminiApiService: GeminiApiService
) {

    suspend fun sendMessage(chatId: Long, userText: String): String {

        val fields = fieldDetailsOfflineRepository.getAllFieldsList()
        val alerts = alertDetailsOfflineRepository.getAllAlertsList()
        val tasks = taskDetailsOfflineRepository.getAllTasksList()
        val history = chatDetailsOfflineRepository.getMessagesByChatIdList(chatId)

        val systemPrompt = buildSystemPrompt(fields, alerts, tasks)

        val systemContext = GeminiContent(
            role = MessageRole.USER.toText(),
            parts = listOf(GeminiPart(systemPrompt))
        )

        val contents = history.map { msg ->
            GeminiContent(
                role = msg.role.toText(),
                parts = listOf(GeminiPart(msg.text))
            )
        } + GeminiContent(
            role = MessageRole.USER.toText(),
            parts = listOf(GeminiPart(userText))
        )

        val response = geminiApiService.generate(
            apiKey = BuildConfig.GEMINI_API_KEY,
            request = GeminiRequest(
                systemInstruction = systemContext,
                contents = contents
            )
        )

        val replyText = response.candidates[0].content.parts[0].text

        return replyText
    }

}