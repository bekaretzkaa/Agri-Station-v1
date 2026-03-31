package com.example.agristation1.data.farmDetails

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmDetailsDao {
    @Query("SELECT * FROM farm_details WHERE id = 1")
    fun getFarmDetails(): Flow<FarmDetails>
}