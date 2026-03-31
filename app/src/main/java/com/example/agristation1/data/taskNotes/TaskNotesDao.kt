package com.example.agristation1.data.taskNotes

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskNotesDao {
    @Query("SELECT * FROM task_notes WHERE task_id = :taskId ORDER BY created_at DESC")
    fun getTaskNotesByTaskId(taskId: Int): Flow<List<TaskNotes>>

    @Insert
    suspend fun insertTaskNote(taskNote: TaskNotes)

    @Query("DELETE FROM task_notes WHERE id = :id")
    suspend fun deleteTaskNote(id: Int)

    @Query("UPDATE task_notes SET note = :note WHERE id = :id")
    suspend fun updateTaskNote(id: Int, note: String)
}