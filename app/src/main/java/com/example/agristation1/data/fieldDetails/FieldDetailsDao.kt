package com.example.agristation1.data.fieldDetails

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FieldDetailsDao {
    @Query("SELECT * FROM field_details WHERE lifecycle !=  3 ORDER BY lifecycle ASC")
    fun getAllFields(): Flow<List<FieldDetails>>

    @Query("SELECT * FROM field_details WHERE lifecycle = 3")
    fun getArchivedFields(): Flow<List<FieldDetails>>

    @Query("SELECT * FROM field_details WHERE id = :id")
    fun getFieldById(id: Int): Flow<FieldDetails?>

    @Query("SELECT * FROM field_details WHERE health = 1 OR health = 2")
    fun getImmediateAttentionFields(): Flow<List<FieldDetails>>
}