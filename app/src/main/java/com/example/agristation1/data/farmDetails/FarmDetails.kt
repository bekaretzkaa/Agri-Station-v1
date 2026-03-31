package com.example.agristation1.data.farmDetails

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "farm_details")
data class FarmDetails(
    @PrimaryKey
    val id: Int,

    @ColumnInfo(name = "farm_name")
    val farmName: String,

    @ColumnInfo(name = "last_update")
    val lastUpdate: Instant,

    @ColumnInfo(name = "active_sensors")
    val activeSensors: Int,
    @ColumnInfo(name = "total_sensors")
    val totalSensors: Int
)