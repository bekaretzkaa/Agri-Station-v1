package com.example.agristation1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.agristation1.data.chatDetails.ChatDetailsOfflineRepository
import com.example.agristation1.data.chatDetails.ChatDetailsRepository
import com.example.agristation1.data.chatDetails.ChatEntity
import com.example.agristation1.data.chatDetails.ChatMessageEntity
import com.example.agristation1.data.chatDetails.MessageRole
import com.example.agristation1.data.chatDetails.MessageStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
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
    private val chatDetailsOfflineRepository: ChatDetailsOfflineRepository
) : ViewModel() {

    private var selectedChatId = MutableStateFlow<Int?>(null)
    private var input = MutableStateFlow<String>("")
    private var isSending = MutableStateFlow<Boolean>(false)

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
                    text = "Что хотели бы спросить?",
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

    fun onChatClick(chatId: Int) {
        selectedChatId.value = chatId
    }

    fun onInputChange(value: String) {
        input.value = value
    }

    fun onSendQuery() {
        val text = input.value.trim()
        if(text.isBlank()) return

        viewModelScope.launch {
            isSending.value = true

            val chatId = selectedChatId.value ?: return@launch
            try {
                val position = uiState.value.messages.size + 1

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

                val assistantPosition = position + 1
                chatDetailsOfflineRepository.insertMessage(
                    ChatMessageEntity(
                        chatId = chatId,
                        positionInChat = assistantPosition,
                        text = "Думаю над ответом...",
                        role = MessageRole.ASSISTANT,
                        status = MessageStatus.SENDING
                    )
                )
                // IMPLEMENTATION OF AI RESPONSE
                delay(5000)

                val answer = "Это тестовый ответ от ИИ"

                chatDetailsOfflineRepository.updateMessage(
                    positionInChat = assistantPosition,
                    status = MessageStatus.SENT,
                    text = answer
                )

            } catch (e: Exception) {
                val chatId = selectedChatId.value
                val position = uiState.value.messages.size + 1
                chatDetailsOfflineRepository.updateMessage(
                    positionInChat = position,
                    status = MessageStatus.ERROR,
                    text = "Ошибка при отправке сообщений"
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

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ChatViewModel(
                    agriStationApplication().container.chatDetailsOfflineRepository
                )
            }
        }
    }
}