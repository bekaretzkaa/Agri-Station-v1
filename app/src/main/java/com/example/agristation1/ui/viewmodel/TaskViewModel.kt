package com.example.agristation1.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.agristation1.data.fieldDetails.FieldDetails
import com.example.agristation1.data.fieldDetails.FieldDetailsOfflineRepository
import com.example.agristation1.data.taskDetails.TaskDetails
import com.example.agristation1.data.taskDetails.TaskDetailsOfflineRepository
import com.example.agristation1.data.taskDetails.TaskPriority
import com.example.agristation1.data.taskDetails.TaskStatus
import com.example.agristation1.data.taskDetails.TaskType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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
    val completedTasks: List<TaskDetails> = emptyList(),
    val cancelledTasks: List<TaskDetails> = emptyList(),
    val fields: List<FieldDetails> = emptyList(),
    val selectedFilter: TaskFilter = TaskFilter.All
) {
    val filteredTasks: List<TaskDetails> = when(selectedFilter) {
        TaskFilter.All -> tasks
        TaskFilter.Low -> tasks.filter { it.priority == TaskPriority.LOW }
        TaskFilter.Medium -> tasks.filter { it.priority == TaskPriority.MEDIUM }
        TaskFilter.High -> tasks.filter { it.priority == TaskPriority.HIGH }
    }

    val allCount = tasks.size
    val lowPriorityCount = tasks.count { it.priority == TaskPriority.LOW }
    val mediumPriorityCount = tasks.count { it.priority == TaskPriority.MEDIUM }
    val highPriorityCount = tasks.count { it.priority == TaskPriority.HIGH }
}

data class TaskFormState(
    val title: String? = null,
    val description: String? = null,
    val fieldId: Int = -1,
    val priority: TaskPriority = TaskPriority.LOW,
    val timeDue: LocalDate? = null,
    val type: TaskType = TaskType.UNKNOWN
)

class TaskViewModel(
    private val taskDetailsOfflineRepository: TaskDetailsOfflineRepository,
    private val fieldDetailsOfflineRepository: FieldDetailsOfflineRepository
): ViewModel() {

    private val selectedFilter = MutableStateFlow<TaskFilter>(TaskFilter.All)

    val uiState: StateFlow<TaskUiState> =
        combine(
            taskDetailsOfflineRepository.getAllTasksStream(),
            taskDetailsOfflineRepository.getCompletedTasksStream(),
            taskDetailsOfflineRepository.getCancelledTasksStream(),
            fieldDetailsOfflineRepository.getAllFieldsStream(),
            selectedFilter,
        ) { tasks, completedTasks, cancelledTasks, fields, filter ->
            TaskUiState(
                tasks = tasks,
                fields = fields,
                completedTasks = completedTasks,
                cancelledTasks = cancelledTasks,
                selectedFilter = filter,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TaskUiState()
        )

    fun checkAndMarkAsOverdueTask() {
        viewModelScope.launch {
            val today = LocalDate.now()

            val tasks = taskDetailsOfflineRepository.getAllTasksStream().first()

            tasks.filter { task ->
                task.timeDue != null &&
                        task.timeDue.isBefore(today) &&
                        task.status != TaskStatus.OVERDUE
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
            taskDetailsOfflineRepository.insertTask(
                TaskDetails(
                    id = 0,
                    title = state.title?.trim(),
                    description = state.description?.trim(),
                    fieldId = state.fieldId,
                    alertId = null,
                    priority = state.priority,
                    status = TaskStatus.OPEN,
                    timeDue = state.timeDue,
                    timeCreated = Instant.now(),
                    type = state.type
                )
            )
        }
    }

    fun onTitleChange(value: String) {
        state = state.copy(title = value)
    }

    fun onDescriptionChange(value: String) {
        state = state.copy(description = value)
    }

    fun onFieldChange(value: Int) {
        state = state.copy(fieldId = value)
    }

    fun onPriorityChange(value: TaskPriority) {
        state = state.copy(priority = value)
    }

    fun onDateChange(value: LocalDate) {
        state = state.copy(timeDue = value)
    }

    fun onTypeChange(value: TaskType) {
        state = state.copy(type = value)
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TaskViewModel(
                    agriStationApplication().container.taskDetailsOfflineRepository,
                    agriStationApplication().container.fieldDetailsOfflineRepository
                )
            }
        }
    }
}