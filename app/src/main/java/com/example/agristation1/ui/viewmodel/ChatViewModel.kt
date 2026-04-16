package com.example.agristation1.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.agristation1.data.chatDetails.ChatDetailsOfflineRepository
import com.example.agristation1.data.chatDetails.ChatEntity
import com.example.agristation1.data.chatDetails.ChatMessageEntity
import com.example.agristation1.data.chatDetails.MessageRole
import com.example.agristation1.data.chatDetails.MessageStatus
import com.example.agristation1.network.gemini.GeminiRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.lang.Exception
import java.time.Instant

data class ChatUiState(
    val chatHistory: List<ChatEntity> = emptyList(),
    val messages: List<ChatMessageEntity> = emptyList(),
    val input: String = "",
    val isSending: Boolean = false
)

class ChatViewModel(
    private val chatDetailsOfflineRepository: ChatDetailsOfflineRepository,
    private val geminiRepository: GeminiRepository
) : ViewModel() {

    private var selectedChatId = MutableStateFlow<Long?>(null)
    private var input = MutableStateFlow("")
    private var isSending = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            chatDetailsOfflineRepository.getAllChats().first().let { chats ->
                if(chats.isEmpty()) {
                    onNewChatClick()
                } else {
                    selectedChatId.value = chats.first().id
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ChatUiState> =
        combine(
            chatDetailsOfflineRepository.getAllChats(),
            selectedChatId.flatMapLatest { id ->
                if(id == null) {
                    flowOf(emptyList())
                } else {
                    chatDetailsOfflineRepository.getMessagesByChatId(id)
                }
            },
            input,
            isSending
        ) { chats, messages, inputValue, sending ->
            ChatUiState(
                chatHistory = chats,
                messages = messages,
                input = inputValue,
                isSending = sending
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ChatUiState()
        )

    fun onNewChatClick() {
        viewModelScope.launch {
            val newChatId = chatDetailsOfflineRepository.insertChat(
                ChatEntity(
                    title = "Новый чат",
                    createdAt = Instant.now(),
                    updatedAt = Instant.now()
                )
            )

            selectedChatId.value = newChatId

            chatDetailsOfflineRepository.insertMessage(
                ChatMessageEntity(
                    chatId = selectedChatId.value!!,
                    positionInChat = 1,
                    text = "What would you like to ask?",
                    role = MessageRole.ASSISTANT,
                    status = MessageStatus.SENT
                )
            )
        }
    }

    fun onDeleteChat() {
        viewModelScope.launch {
            val chatId = selectedChatId.value ?: return@launch
            chatDetailsOfflineRepository.deleteChat(chatId)

            val chats = chatDetailsOfflineRepository.getAllChats().first()
            if (chats.isEmpty()) {
                onNewChatClick()
            } else {
                onChatClick(chats.first().id)
            }
        }
    }

    fun onChatClick(chatId: Long) {
        selectedChatId.value = chatId
    }

    fun onInputChange(value: String) {
        input.value = value
    }

    private var lastUserInput = ""

    fun onSendQuery() {
        val text = input.value.trim()
        if(text.isBlank()) return
        lastUserInput = text

        viewModelScope.launch {
            isSending.value = true
            val position = uiState.value.messages.size.toLong() + 1
            val assistantPosition = position + 1

            val chatId = selectedChatId.value ?: return@launch
            try {
                val isFirstUserMessage = uiState.value.messages.none { it.role == MessageRole.USER }
                if (isFirstUserMessage) {
                    val title = text.take(30).let { if (text.length > 30) "$it..." else it }
                    chatDetailsOfflineRepository.updateChatTitle(chatId, title)
                }

                chatDetailsOfflineRepository.insertMessage(
                    ChatMessageEntity(
                        chatId = chatId,
                        positionInChat = position,
                        text = text,
                        role = MessageRole.USER,
                        status = MessageStatus.SENT
                    )
                )

                input.value = ""

                chatDetailsOfflineRepository.insertMessage(
                    ChatMessageEntity(
                        chatId = chatId,
                        positionInChat = assistantPosition,
                        text = "Thinking...",
                        role = MessageRole.ASSISTANT,
                        status = MessageStatus.SENDING
                    )
                )

                val answer = geminiRepository.sendMessage(chatId, text)
                Log.d("ChatViewModel", "Answer: $answer")

                chatDetailsOfflineRepository.updateMessage(
                    positionInChat = assistantPosition,
                    status = MessageStatus.SENT,
                    text = answer
                )

            } catch (e: Exception) {
                chatDetailsOfflineRepository.updateMessage(
                    positionInChat = assistantPosition,
                    status = MessageStatus.ERROR,
                    text = "Error when sending messages"
                )
            } finally {
                isSending.value = false
                chatDetailsOfflineRepository.updateChatUpdatedAt(
                    chatId = chatId,
                    updatedAt = Instant.now()
                )
            }
        }
    }

    fun onRetry() {
        if (lastUserInput.isBlank()) return
        input.value = lastUserInput
        onSendQuery()
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ChatViewModel(
                    agriStationApplication().container.chatDetailsOfflineRepository,
                    agriStationApplication().container.geminiRepository
                )
            }
        }
    }
}