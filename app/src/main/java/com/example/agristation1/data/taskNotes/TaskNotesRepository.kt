package com.example.agristation1.data.taskNotes

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface TaskNotesRepository {
    fun getTaskNotesByTaskIdStream(taskId: Int): Flow<List<TaskNotes>>

    suspend fun insertTaskNote(taskId: Int, note: String)

    suspend fun deleteTaskNote(id: Int)

    suspend fun updateTaskNote(id: Int, note: String)
}

class TaskNotesOfflineRepository(
    private val taskNotesDao: TaskNotesDao
) : TaskNotesRepository {

    override fun getTaskNotesByTaskIdStream(taskId: Int): Flow<List<TaskNotes>> {
        return taskNotesDao.getTaskNotesByTaskId(taskId)
    }

    override suspend fun insertTaskNote(taskId: Int, note: String) {
        val taskNote = TaskNotes(
            taskId = taskId,
            note = note,
            createdAt = Instant.now()
        )
        taskNotesDao.insertTaskNote(taskNote)
    }

    override suspend fun deleteTaskNote(id: Int) {
        taskNotesDao.deleteTaskNote(id)
    }

    override suspend fun updateTaskNote(id: Int, note: String) {
        taskNotesDao.updateTaskNote(id, note)
    }

}
