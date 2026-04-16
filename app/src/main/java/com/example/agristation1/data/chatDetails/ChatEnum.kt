package com.example.agristation1.data.chatDetails

enum class MessageRole(val code: Int) {
    USER(0),
    ASSISTANT(1),
    SYSTEM(2);

    companion object {
        fun fromCode(code: Int): MessageRole {
            return entries.find { it.code == code } ?: SYSTEM
        }
    }
}

fun MessageRole.toText(): String {
    return when (this) {
        MessageRole.USER -> "User"
        MessageRole.ASSISTANT -> "Model"
        MessageRole.SYSTEM -> "Android System"
    }
}

enum class MessageStatus(val code: Int) {
    SENDING(0),
    SENT(1),
    ERROR(2);

    companion object {
        fun fromCode(code: Int): MessageStatus {
            return entries.find { it.code == code } ?: ERROR
        }
    }
}