package com.example.agristation1.network.taskNetwork

import android.util.Log
import com.example.agristation1.data.SyncResult
import com.example.agristation1.data.taskDetails.TaskDetails
import com.example.agristation1.data.taskDetails.TaskDetailsOfflineRepository
import com.example.agristation1.data.taskDetails.TaskPriority
import com.example.agristation1.data.taskDetails.TaskStatus
import com.example.agristation1.data.taskDetails.TaskType
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException
import java.time.Instant

class TaskSyncManager(
    private val taskDetailsOfflineRepository: TaskDetailsOfflineRepository,
    private val taskPendingOperationDao: TaskPendingOperationDao,
    private val networkTaskRepositoryImpl: NetworkTaskRepositoryImpl
) {

    suspend fun sync(): SyncResult {
        return try {
            val failedOps = pushPendingOperations()

            Log.d("TaskSyncManager", "Failed ops: $failedOps")

            fetchAndMerge()

            if (failedOps == 0) SyncResult.Success
            else SyncResult.PartialSuccess(failedOps)
        } catch (e: IOException) {
            SyncResult.Error("No connection with server")
        } catch (e: HttpException) {
            SyncResult.Error("Server error: ${e.code()}")
        } catch (e: Exception) {
            SyncResult.Error("Unknown error: ${e.message}")
        }
    }


    // PUSH TASK OPERATIONS TO SERVER
    private suspend fun pushPendingOperations(): Int {
        val pending = taskPendingOperationDao.getAllPending()
        var failedOps = 0

        for(op in pending) {
            taskPendingOperationDao.updateStatus(op.id, TaskPendingOperationStatus.IN_FLIGHT)

            val success = when(op.operation) {
                TaskPendingOperationType.CREATE_TASK -> pushCreateTask(op)
                TaskPendingOperationType.UPDATE_TASK -> pushUpdateTask(op)
                TaskPendingOperationType.DELETE_TASK -> pushDeleteTask(op)
                TaskPendingOperationType.UPDATE_STATUS -> pushStatusUpdate(op)
                TaskPendingOperationType.UPDATE_NOTE -> pushNoteUpdate(op)
            }
            if(success) {
                taskPendingOperationDao.delete(op.id)
            } else {
                taskPendingOperationDao.markFailed(op.id)
                failedOps++
            }
        }

        return failedOps
    }
    private suspend fun pushStatusUpdate(op: TaskPendingOperation): Boolean {
        return try {
            val statusCode = op.payload.toIntOrNull() ?: return false
            val result = networkTaskRepositoryImpl.updateStatus(op.entityId, statusCode)

            result.success
        } catch (e: Exception) {
            false
        }
    }
    private suspend fun pushNoteUpdate(op: TaskPendingOperation): Boolean {
        return try {
            val result = networkTaskRepositoryImpl.updateNote(op.entityId, op.payload)

            result.success
        } catch (e: Exception) {
            false
        }
    }
    private suspend fun pushUpdateTask(op: TaskPendingOperation): Boolean {
        return try {
            val networkTask = Json.decodeFromString<TaskDetailsNetwork>(op.payload)
            val result = networkTaskRepositoryImpl.updateTask(op.entityId, networkTask)

            result.success
        } catch (e: Exception) {
            false
        }
    }
    private suspend fun pushDeleteTask(op: TaskPendingOperation): Boolean {
        return try {
            val result = networkTaskRepositoryImpl.deleteTask(op.entityId)

            result.success
        } catch (e: Exception) {
            false
        }
    }
    private suspend fun pushCreateTask(op: TaskPendingOperation): Boolean {
        return try {
            val networkTask = Json.decodeFromString<TaskDetailsNetwork>(op.payload)
            val result = networkTaskRepositoryImpl.createTask(networkTask)

            result.success
        } catch (e: Exception) {
            false
        }
    }


    // FETCH AND MERGE SERVER AND LOCAL DATA

    private suspend fun fetchAndMerge() {
        val remoteTasks = networkTaskRepositoryImpl.getTasks().tasks.orEmpty()
        val localTasks = taskDetailsOfflineRepository.getAllTasksList()

        val remoteIds = remoteTasks.mapNotNull { it.id }.toSet()
        val localIds = localTasks.map { it.id }.toSet()

        val disappearedTasks = localIds - remoteIds
        handleDisappearedTasks(disappearedTasks)

        for(remoteTask in remoteTasks) {
            mergeTask(remoteTask, localTasks)
        }
    }
    private suspend fun handleDisappearedTasks(tasksIds: Set<Long>) {
        for (taskId in tasksIds) {
            val hasPending = taskPendingOperationDao.hasPendingForTask(taskId) > 0
            if (hasPending) continue

            taskDetailsOfflineRepository.deleteTask(taskId)
        }
    }
    private suspend fun mergeTask(
        remoteTask: TaskDetailsNetwork,
        localTasks: List<TaskDetails>
    ) {
        val remoteId = remoteTask.id ?: return
        val localTask = localTasks.find { it.id == remoteId }

        if(localTask == null) {
            val pendingOperations = taskPendingOperationDao.getByTaskId(remoteId)
            if(pendingOperations.any { it.operation == TaskPendingOperationType.DELETE_TASK }) return

            taskDetailsOfflineRepository.insertTask(remoteTask.toEntity())
            return
        }

        val pendingOps = taskPendingOperationDao.getByTaskId(remoteId)

        if(pendingOps.isEmpty()) {
            taskDetailsOfflineRepository.updateTask(remoteTask.toEntity())
            return
        }

        val hasUpdateTask = pendingOps.any { it.operation == TaskPendingOperationType.UPDATE_TASK }
        val hasUpdateStatus = pendingOps.any { it.operation == TaskPendingOperationType.UPDATE_STATUS }
        val hasUpdateNote = pendingOps.any { it.operation == TaskPendingOperationType.UPDATE_NOTE }

        val merged = remoteTask.toEntity().copy(
            title = if (hasUpdateTask) localTask.title else remoteTask.title?.let { it },
            description = if (hasUpdateTask) localTask.description else remoteTask.description,
            notes = if (hasUpdateNote) localTask.notes else remoteTask.notes,
            fieldId = if (hasUpdateTask) localTask.fieldId else remoteTask.fieldId,
            priority = if (hasUpdateTask) localTask.priority else TaskPriority.fromCode(remoteTask.priority),
            timeDue = if (hasUpdateTask) localTask.timeDue else remoteTask.timeDue?.let { Instant.ofEpochMilli(it) },
            type = if (hasUpdateTask) localTask.type else TaskType.fromCode(remoteTask.type),

            status = if (hasUpdateStatus) localTask.status else TaskStatus.fromCode(remoteTask.status),
        )

        taskDetailsOfflineRepository.updateTask(merged)
    }
}