package com.example.agristation1.data.userDetails

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_details",
)
data class UserDetails(
    @PrimaryKey
    val id: Long,

    @ColumnInfo(name = "farm_id")
    val farmId: Long?,
    val role: String,
    val name: String,
    val surname: String,
    val email: String,
    val company: String
)
