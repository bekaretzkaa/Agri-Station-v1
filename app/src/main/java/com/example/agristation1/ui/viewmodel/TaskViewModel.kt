package com.example.agristation1.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.agristation1.data.SyncOrchestrator
import com.example.agristation1.data.SyncResult
import com.example.agristation1.data.UserPreferencesRepository
import com.example.agristation1.data.fieldDetails.FieldDetails
import com.example.agristation1.data.fieldDetails.FieldDetailsOfflineRepository
import com.example.agristation1.data.taskDetails.TaskDetails
import com.example.agristation1.data.taskDetails.TaskDetailsOfflineRepository
import com.example.agristation1.data.taskDetails.TaskPriority
import com.example.agristation1.data.taskDetails.TaskStatus
import com.example.agristation1.data.taskDetails.TaskType
import com.example.agristation1.network.taskNetwork.TaskPendingOperation
import com.example.agristation1.network.taskNetwork.TaskPendingOperationRepository
import com.example.agristation1.network.taskNetwork.TaskPendingOperationType
import com.example.agristation1.network.taskNetwork.TaskSyncManager
import com.example.agristation1.network.taskNetwork.toNetwork
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDate

sealed interface TaskFilter {
    data object All : TaskFilter
    data object Low : TaskFilter
    data object Medium : TaskFilter
    data object High : TaskFilter
}

data class TaskUiState(
    val tasks: List<TaskDetails> = emptyList(),
    val filteredTasks: List<TaskDetails> = emptyList(),
    val archivedTasks: List<TaskDetails> = emptyList(),
    val fields: List<FieldDetails> = emptyList(),
    val selectedFilter: TaskFilter = TaskFilter.All,
    val isLoading: Boolean = true,

    val allCount: Int = 0,
    val lowCount: Int = 0,
    val mediumCount: Int = 0,
    val highCount: Int = 0
)

data class TaskFormState(
    val title: String? = null,
    val description: String? = null,
    val fieldId: Long = -1L,
    val priority: TaskPriority = TaskPriority.LOW,
    val timeDue: Instant? = null,
    val type: TaskType = TaskType.UNKNOWN
)

class TaskViewModel(
    private val taskDetailsOfflineRepository: TaskDetailsOfflineRepository,
    private val fieldDetailsOfflineRepository: FieldDetailsOfflineRepository,
    private val taskPendingOperationRepository: TaskPendingOperationRepository,
    private val syncOrchestrator: SyncOrchestrator,
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {

    private val selectedFilter = MutableStateFlow<TaskFilter>(TaskFilter.All)

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _refreshError = MutableStateFlow<String?>(null)
    val refreshError: StateFlow<String?> = _refreshError.asStateFlow()

    private val _isUpdating = MutableStateFlow(false)

    val uiState: StateFlow<TaskUiState> =
        combine(
            taskDetailsOfflineRepository.getAllTasksStream(),
            fieldDetailsOfflineRepository.getAllFieldsStream(),
            selectedFilter,
        ) { tasks, fields, filter ->

            val newTasks =
                tasks.filter { it.status != TaskStatus.COMPLETED && it.status != TaskStatus.CANCELLED  }

            val archivedTasks =
                tasks.filter { it.status == TaskStatus.COMPLETED || it.status == TaskStatus.CANCELLED }

            val filteredTasks = when(filter) {
                TaskFilter.All -> newTasks
                TaskFilter.Low -> newTasks.filter { it.priority == TaskPriority.LOW }
                TaskFilter.Medium -> newTasks.filter { it.priority == TaskPriority.MEDIUM }
                TaskFilter.High -> newTasks.filter { it.priority == TaskPriority.HIGH }
            }
            val allCount = newTasks.size
            val lowCount = newTasks.count { it.priority == TaskPriority.LOW }
            val mediumCount = newTasks.count { it.priority == TaskPriority.MEDIUM }
            val highCount = newTasks.count { it.priority == TaskPriority.HIGH }

            TaskUiState(
                tasks = tasks,
                filteredTasks = filteredTasks,
                archivedTasks = archivedTasks,
                fields = fields,
                selectedFilter = filter,
                isLoading = false,
                allCount = allCount,
                lowCount = lowCount,
                mediumCount = mediumCount,
                highCount = highCount
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TaskUiState()
        )

    fun checkAndMarkAsOverdueTask() {
        viewModelScope.launch {
            val today = Instant.now()

            val tasks = taskDetailsOfflineRepository.getAllTasksStream().first()

            tasks.filter { task ->
                task.timeDue != null &&
                        task.timeDue.isBefore(today) &&
                        (task.status == TaskStatus.OPEN || task.status == TaskStatus.IN_PROGRESS)
            }
                .forEach { task ->
                    taskDetailsOfflineRepository.markTaskAsOverdue(task.id)
                }
        }
    }

    fun onFilterChange(filter: TaskFilter) {
        selectedFilter.value = filter
    }

    var state by mutableStateOf(TaskFormState())

    fun addTask() {
        viewModelScope.launch {
            _isUpdating.value = true
            val taskToInsert = TaskDetails(
                id = 0,
                title = state.title?.trim(),
                description = state.description?.trim(),
                notes = null,
                fieldId = state.fieldId,
                alertId = null,
                priority = state.priority,
                status = TaskStatus.OPEN,
                timeDue = state.timeDue,
                timeCreated = Instant.now(),
                type = state.type
            )
            try {
                val createdTaskId = taskDetailsOfflineRepository.insertTask(taskToInsert)

                taskPendingOperationRepository.insert(
                    TaskPendingOperation(
                        entityId = createdTaskId,
                        operation = TaskPendingOperationType.CREATE_TASK,
                        payload = Json.encodeToString(taskToInsert.toNetwork()),
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

    fun refresh() {
        if(_isRefreshing.value) return

        viewModelScope.launch {
            _isRefreshing.value = true
            _refreshError.value = null

            val result = syncOrchestrator.syncAll(userPreferencesRepository.lastSync.first())
            Log.d("TaskSyncManager", "Result: $result")

            when(result) {
                is SyncResult.Success -> {
                    userPreferencesRepository.saveLastSync(System.currentTimeMillis())
                }
                is SyncResult.PartialSuccess -> {
                    _refreshError.value = "TaskSynced partially ${result.failedOps}"
                }
                is SyncResult.Error -> {
                    _refreshError.value = result.message
                }
            }

            _isRefreshing.value = false
        }
    }

    fun clearRefreshError() {
        _refreshError.value = null
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TaskViewModel(
                    agriStationApplication().container.taskDetailsOfflineRepository,
                    agriStationApplication().container.fieldDetailsOfflineRepository,
                    agriStationApplication().container.taskPendingOperationRepository,
                    agriStationApplication().container.syncOrchestrator,
                    agriStationApplication().userPreferencesRepository
                )
            }
        }
    }
}