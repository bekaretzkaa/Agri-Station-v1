package com.example.agristation1.ui.viewmodel

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
import com.example.agristation1.data.taskDetails.TaskDetails
import com.example.agristation1.data.alertDetails.AlertDetails
import com.example.agristation1.data.alertDetails.AlertDetailsOfflineRepository
import com.example.agristation1.data.alertDetails.AlertSeverity
import com.example.agristation1.data.fieldDetails.FieldDetails
import com.example.agristation1.data.fieldDetails.FieldDetailsOfflineRepository
import com.example.agristation1.data.taskDetails.TaskDetailsOfflineRepository
import com.example.agristation1.data.taskDetails.TaskPriority
import com.example.agristation1.data.taskDetails.TaskStatus
import com.example.agristation1.data.taskDetails.TaskType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate

data class AlertDetailsUiState(
    val alertDetails: AlertDetails? = null,
    val field: FieldDetails? = null,
    val task: TaskDetails? = null,
    val isUpdating: Boolean = false,
    val fields: List<FieldDetails> = emptyList()
)

class AlertDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val alertDetailsOfflineRepository: AlertDetailsOfflineRepository,
    private val fieldDetailsOfflineRepository: FieldDetailsOfflineRepository,
    private val taskDetailsOfflineRepository: TaskDetailsOfflineRepository
): ViewModel() {
    private val alertId: Int = savedStateHandle.get<String>("alertId")?.toIntOrNull() ?: 0

    private val alertFlow = alertDetailsOfflineRepository.getAlertByIdStream(alertId)

    private val _isUpdating = MutableStateFlow(false)

    val uiState: StateFlow<AlertDetailsUiState> =
        combine(
            alertFlow,
            _isUpdating
        ) { alert, isUpdating ->
            Pair(alert, isUpdating)
        }.flatMapLatest { (alert, isUpdating) ->
                if (alert == null) {
                    flowOf(AlertDetailsUiState(isUpdating = isUpdating))
                } else {
                    combine(
                        fieldDetailsOfflineRepository.getFieldByIdStream(alert.fieldId),
                        taskDetailsOfflineRepository.getTaskByAlertIdStream(alertId),
                        fieldDetailsOfflineRepository.getAllFieldsStream()
                    ) { field, task, fields ->
                        AlertDetailsUiState(
                            alertDetails = alert,
                            field = field,
                            task = task,
                            isUpdating = isUpdating,
                            fields = fields
                        )
                    }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AlertDetailsUiState()
            )


    fun markAlertAsAcknowledged() {
        viewModelScope.launch {
            _isUpdating.value = true
            try {
                alertDetailsOfflineRepository.markAlertAsAcknowledged(alertId)
            } finally {
                _isUpdating.value = false
            }
        }
    }

    fun unMarkAlertAsAcknowledged() {
        viewModelScope.launch {
            _isUpdating.value = true
            try {
                alertDetailsOfflineRepository.markAlertAsUnAcknowledged(alertId)
            } finally {
                _isUpdating.value = false
            }
        }
    }

    fun markAlertAsResolved() {
        viewModelScope.launch {
            _isUpdating.value = true
            try {
                alertDetailsOfflineRepository.markAlertAsResolved(alertId)
            } finally {
                _isUpdating.value = false
            }
        }
    }

    fun markAlertAsDismissed() {
        viewModelScope.launch {
            _isUpdating.value = true
            try {
                alertDetailsOfflineRepository.markAlertAsDismissed(alertId)
            } finally {
                _isUpdating.value = false
            }
        }
    }

    fun deleteAlert(id: Int) {
        viewModelScope.launch {
            _isUpdating.value = true
            try {
                alertDetailsOfflineRepository.deleteAlert(id)
            } finally {
                _isUpdating.value = false
            }
        }
    }

    var state by mutableStateOf(TaskFormState())

    fun initializeState() {
        val alert = uiState.value.alertDetails

        alert.let {
            state = TaskFormState(
                title = "Resolve: ${it?.title}",
                description = it?.recommendation ?: "",
                fieldId = it?.fieldId ?: 0,
                priority = when(it?.severity ?: AlertSeverity.INFO) {
                    AlertSeverity.INFO, AlertSeverity.NOTICE -> TaskPriority.LOW
                    AlertSeverity.WARNING -> TaskPriority.MEDIUM
                    AlertSeverity.CRITICAL -> TaskPriority.HIGH
                }
            )
        }
    }

    fun addTask() {
        viewModelScope.launch {
            taskDetailsOfflineRepository.insertTask(
                TaskDetails(
                    id = 0,
                    title = state.title?.trim(),
                    description = state.description?.trim(),
                    fieldId = state.fieldId,
                    alertId = alertId,
                    priority = state.priority,
                    status = TaskStatus.OPEN,
                    timeDue = state.timeDue,
                    timeCreated = Instant.now(),
                    type = TaskType.UNKNOWN,
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
                AlertDetailsViewModel(
                    this.createSavedStateHandle(),
                    agriStationApplication().container.alertDetailsOfflineRepository,
                    agriStationApplication().container.fieldDetailsOfflineRepository,
                    agriStationApplication().container.taskDetailsOfflineRepository
                )
            }
        }
    }
}