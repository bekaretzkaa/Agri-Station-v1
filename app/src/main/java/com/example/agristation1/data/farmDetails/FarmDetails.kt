package com.example.agristation1.data.farmDetails

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farm_details")
data class FarmDetails(
    @PrimaryKey
    val id: Long,

    @ColumnInfo(name = "farm_name")
    val farmName: String,

    @ColumnInfo(name = "active_sensors")
    val activeSensors: Int,
    @ColumnInfo(name = "total_sensors")
    val totalSensors: Int
)