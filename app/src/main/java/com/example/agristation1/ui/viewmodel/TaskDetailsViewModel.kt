package com.example.agristation1.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.agristation1.data.AgriStationDatabase
import com.example.agristation1.data.alertDetails.AlertDetails
import com.example.agristation1.data.alertDetails.AlertDetailsOfflineRepository
import com.example.agristation1.data.fieldDetails.FieldDetails
import com.example.agristation1.data.fieldDetails.FieldDetailsOfflineRepository
import com.example.agristation1.data.taskDetails.TaskDetails
import com.example.agristation1.data.taskDetails.TaskDetailsOfflineRepository
import com.example.agristation1.data.taskDetails.TaskPriority
import com.example.agristation1.data.taskDetails.TaskStatus
import com.example.agristation1.data.taskDetails.TaskType
import com.example.agristation1.network.taskNetwork.TaskDetailsNetwork
import com.example.agristation1.network.taskNetwork.TaskPendingOperation
import com.example.agristation1.network.taskNetwork.TaskPendingOperationDao
import com.example.agristation1.network.taskNetwork.TaskPendingOperationRepository
import com.example.agristation1.network.taskNetwork.TaskPendingOperationStatus
import com.example.agristation1.network.taskNetwork.TaskPendingOperationType
import com.example.agristation1.network.taskNetwork.toNetwork
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDate

data class TaskDetailsUiState(
    val taskDetails: TaskDetails? = null,
    val field: FieldDetails? = null,
    val alert: AlertDetails? = null,
    val isUpdating: Boolean = false,
    val fields: List<FieldDetails> = emptyList()
)

class TaskDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val taskDetailsOfflineRepository: TaskDetailsOfflineRepository,
    private val fieldDetailsOfflineRepository: FieldDetailsOfflineRepository,
    private val alertDetailsOfflineRepository: AlertDetailsOfflineRepository,
    private val taskPendingOperationRepository: TaskPendingOperationRepository
    ) : ViewModel() {

    private val taskId: Long = savedStateHandle.get<String>("taskId")?.toLongOrNull() ?: 0L

    private val taskFlow = taskDetailsOfflineRepository.getTaskByIdStream(taskId)

    private val _isUpdating = MutableStateFlow(false)

    val uiState: StateFlow<TaskDetailsUiState> =
        combine(
            taskFlow,
            _isUpdating
        ) { task, isUpdating ->
            Pair(task, isUpdating)
        }.flatMapLatest { (task, isUpdating) ->
            if(task == null) {
                flowOf(TaskDetailsUiState(isUpdating = isUpdating))
            } else {
                combine(
                    fieldDetailsOfflineRepository.getFieldByIdStream(task.fieldId),
                    if(task.alertId != null) alertDetailsOfflineRepository.getAlertByIdStream(task.alertId) else flowOf(null),
                    fieldDetailsOfflineRepository.getAllFieldsStream()
                ) { field, alert, fields ->
                    TaskDetailsUiState(
                        taskDetails = task,
                        field = field,
                        alert = alert,
                        isUpdating = isUpdating,
                        fields = fields
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TaskDetailsUiState()
        )

    fun onTaskStatusChange(taskId: Long, newStatus: TaskStatus) {
        viewModelScope.launch {
            _isUpdating.value = true
            try {
                taskDetailsOfflineRepository.updateTaskStatus(taskId, newStatus.code)

                taskPendingOperationRepository.deleteByTaskIdAndOperation(taskId, TaskPendingOperationType.UPDATE_STATUS)

                taskPendingOperationRepository.insert(
                    TaskPendingOperation(
                        entityId = taskId,
                        operation = TaskPendingOperationType.UPDATE_STATUS,
                        payload = newStatus.code.toString(),
                    )
                )
            } finally {
                _isUpdating.value = false
            }
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            _isUpdating.value = true
            try {
                taskDetailsOfflineRepository.deleteTask(taskId)

                taskPendingOperationRepository.insert(
                    TaskPendingOperation(
                        entityId = taskId,
                        operation = TaskPendingOperationType.DELETE_TASK,
                        payload = taskId.toString(),
                    )
                )
            } finally {
                _isUpdating.value = false
            }
        }
    }

    var state by mutableStateOf(TaskFormState())

    fun initializeState() {
        uiState.value.taskDetails?.let { task ->
            state = TaskFormState(
                title = task.title,
                description = task.description,
                fieldId = task.fieldId,
                priority = task.priority,
                timeDue = task.timeDue,
                type = task.type
            )
        }
    }

    fun updateTask(taskId: Long) {
        val updatedTask = uiState.value.taskDetails?.copy(
            title = state.title?.trim(),
            description = state.description?.trim(),
            fieldId = state.fieldId,
            priority = state.priority,
            timeDue = state.timeDue,
            type = state.type
        )
        if (updatedTask != null && updatedTask.fieldId != -1L) {
            viewModelScope.launch {
                _isUpdating.value = true
                try {
                    taskDetailsOfflineRepository.updateTask(updatedTask)

                    taskPendingOperationRepository.deleteByTaskIdAndOperation(taskId, TaskPendingOperationType.UPDATE_TASK)

                    taskPendingOperationRepository.insert(
                        TaskPendingOperation(
                            entityId = taskId,
                            operation = TaskPendingOperationType.UPDATE_TASK,
                            payload = Json.encodeToString(updatedTask.toNetwork()),
                        )
                    )
                } finally {
                    _isUpdating.value = false
                }
            }
        }
    }

    fun onUpdateNote(note: String) {
        viewModelScope.launch {
            _isUpdating.value = true
            try {
                taskDetailsOfflineRepository.updateTaskNote(taskId, note)

                taskPendingOperationRepository.deleteByTaskIdAndOperation(taskId,
                    TaskPendingOperationType.UPDATE_NOTE)

                taskPendingOperationRepository.insert(
                    TaskPendingOperation(
                        entityId = taskId,
                        operation = TaskPendingOperationType.UPDATE_NOTE,
                        payload = note,
                    )
                )
            } finally {
                _isUpdating.value = false
            }
        }
    }

    fun onTitleChange(value: String) {
        state = state.copy(title = value)
    }

    fun onDescriptionChange(value: String) {
        state = state.copy(description = value)
    }

    fun onFieldChange(value: Long) {
        state = state.copy(fieldId = value)
    }

    fun onPriorityChange(value: TaskPriority) {
        state = state.copy(priority = value)
    }

    fun onDateChange(value: Instant) {
        state = state.copy(timeDue = value)
    }

    fun onTypeChange(value: TaskType) {
        state = state.copy(type = value)
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TaskDetailsViewModel(
                    this.createSavedStateHandle(),
                    agriStationApplication().container.taskDetailsOfflineRepository,
                    agriStationApplication().container.fieldDetailsOfflineRepository,
                    agriStationApplication().container.alertDetailsOfflineRepository,
                    agriStationApplication().container.taskPendingOperationRepository
                )
            }
        }
    }
}