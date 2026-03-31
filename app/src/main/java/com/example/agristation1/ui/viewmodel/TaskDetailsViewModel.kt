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
import com.example.agristation1.data.alertDetails.AlertDetails
import com.example.agristation1.data.alertDetails.AlertDetailsOfflineRepository
import com.example.agristation1.data.fieldDetails.FieldDetails
import com.example.agristation1.data.fieldDetails.FieldDetailsOfflineRepository
import com.example.agristation1.data.taskDetails.TaskDetails
import com.example.agristation1.data.taskDetails.TaskDetailsOfflineRepository
import com.example.agristation1.data.taskDetails.TaskPriority
import com.example.agristation1.data.taskDetails.TaskType
import com.example.agristation1.data.taskNotes.TaskNotes
import com.example.agristation1.data.taskNotes.TaskNotesOfflineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class TaskDetailsUiState(
    val taskDetails: TaskDetails? = null,
    val field: FieldDetails? = null,
    val alert: AlertDetails? = null,
    val notes: List<TaskNotes> = emptyList(),
    val isUpdating: Boolean = false,
    val fields: List<FieldDetails> = emptyList()
)

class TaskDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val taskDetailsOfflineRepository: TaskDetailsOfflineRepository,
    private val fieldDetailsOfflineRepository: FieldDetailsOfflineRepository,
    private val alertDetailsOfflineRepository: AlertDetailsOfflineRepository,
    private val taskNotesOfflineRepository: TaskNotesOfflineRepository
) : ViewModel() {

    private val taskId: Int = savedStateHandle.get<String>("taskId")?.toIntOrNull() ?: 0

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
                    taskNotesOfflineRepository.getTaskNotesByTaskIdStream(task.id),
                    fieldDetailsOfflineRepository.getAllFieldsStream()
                ) { field, alert, notes, fields ->
                    TaskDetailsUiState(
                        taskDetails = task,
                        field = field,
                        alert = alert,
                        notes = notes,
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

    fun markTaskAsStarted(id: Int) {
        viewModelScope.launch {
            _isUpdating.value = true
            try {
                taskDetailsOfflineRepository.markTaskAsStarted(id)
            } finally {
                _isUpdating.value = false
            }
        }
    }

    fun markTaskAsCompleted(id: Int) {
        viewModelScope.launch {
            _isUpdating.value = true
            try {
                taskDetailsOfflineRepository.markTaskAsCompleted(id)
            } finally {
                _isUpdating.value = false
            }
        }
    }

    fun unMarkTaskAsCompleted(id: Int) {
        viewModelScope.launch {
            _isUpdating.value = true
            try {
                taskDetailsOfflineRepository.unMarkTaskAsCompleted(id)
            } finally {
                _isUpdating.value = false
            }
        }
    }

    fun deleteTask(id: Int) {
        viewModelScope.launch {
            _isUpdating.value = true
            try {
                taskDetailsOfflineRepository.deleteTask(id)
            } finally {
                _isUpdating.value = false
            }
        }
    }

    fun insertTaskNote(taskId: Int, note: String) {
        viewModelScope.launch {
            taskNotesOfflineRepository.insertTaskNote(taskId, note.trim())
        }
    }

    fun deleteTaskNote(id: Int) {
        viewModelScope.launch {
            taskNotesOfflineRepository.deleteTaskNote(id)
        }
    }

    fun updateTaskNote(id: Int, note: String) {
        viewModelScope.launch {
            taskNotesOfflineRepository.updateTaskNote(id, note)
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

    fun updateTask() {
        val updatedTask = uiState.value.taskDetails?.copy(
            title = state.title?.trim(),
            description = state.description?.trim(),
            fieldId = state.fieldId,
            priority = state.priority,
            timeDue = state.timeDue,
            type = state.type
        )
        if (updatedTask != null && updatedTask.fieldId != -1) {
            viewModelScope.launch {
                taskDetailsOfflineRepository.updateTask(updatedTask)
            }
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
                TaskDetailsViewModel(
                    this.createSavedStateHandle(),
                    agriStationApplication().container.taskDetailsOfflineRepository,
                    agriStationApplication().container.fieldDetailsOfflineRepository,
                    agriStationApplication().container.alertDetailsOfflineRepository,
                    agriStationApplication().container.taskNotesOfflineRepository
                )
            }
        }
    }
}