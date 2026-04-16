package com.example.agristation1.network.taskNetwork

interface TaskPendingOperationRepository {
    suspend fun insert(operation: TaskPendingOperation)

    suspend fun getAllPending(): List<TaskPendingOperation>

    suspend fun getByTaskId(taskId: Long): List<TaskPendingOperation>

    suspend fun updateStatus(id: Long, status: TaskPendingOperationStatus)

    suspend fun markFailed(id: Long)

    suspend fun delete(id: Long)

    suspend fun deleteByTaskId(taskId: Long)

    suspend fun hasPendingForTask(taskId: Long): Boolean

    suspend fun deleteByTaskIdAndOperation(
        taskId: Long,
        operation: TaskPendingOperationType
    )

    suspend fun countAllTaskPending(): Int
}

class TaskPendingOperationRepositoryImpl(
    private val dao: TaskPendingOperationDao
) : TaskPendingOperationRepository {

    override suspend fun insert(operation: TaskPendingOperation) {
        dao.insert(operation)
    }

    override suspend fun getAllPending(): List<TaskPendingOperation> {
        return dao.getAllPending()
    }

    override suspend fun getByTaskId(taskId: Long): List<TaskPendingOperation> {
        return dao.getByTaskId(taskId)
    }

    override suspend fun updateStatus(id: Long, status: TaskPendingOperationStatus) {
        dao.updateStatus(id, status)
    }

    override suspend fun markFailed(id: Long) {
        dao.markFailed(id)
    }

    override suspend fun delete(id: Long) {
        dao.delete(id)
    }

    override suspend fun deleteByTaskId(taskId: Long) {
        dao.deleteByTaskId(taskId)
    }

    override suspend fun hasPendingForTask(taskId: Long): Boolean {
        return dao.hasPendingForTask(taskId) > 0
    }

    override suspend fun deleteByTaskIdAndOperation(
        taskId: Long,
        operation: TaskPendingOperationType
    ) {
        dao.deleteByTaskIdAndOperation(taskId, operation)
    }

    override suspend fun countAllTaskPending(): Int {
        return dao.countAllTaskPending()
    }
}