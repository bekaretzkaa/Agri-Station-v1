package com.example.agristation1.data.chatDetails

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chat_message",
    foreignKeys = [
        ForeignKey(
            entity = ChatEntity::class,
            parentColumns = ["id"],
            childColumns = ["chat_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = ["chat_id"], name = "idx_chat_message_chat_id"),
        Index(
            value = ["chat_id", "position_in_chat"],
            unique = true,
            name = "idx_chat_message_chat_id_position"
        )
    ]
)
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "message_id")
    val messageId: Long = 0,
    @ColumnInfo(name = "chat_id")
    val chatId: Long,
    @ColumnInfo(name = "position_in_chat")
    val positionInChat: Long,
    val text: String,
    val role: MessageRole,
    val status: MessageStatus
)
