package com.example.agristation1.data.chatDetails

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "chats_history")
data class ChatEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Instant,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Instant
)
