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
import com.example.agristation1.data.AgriStationDatabase
import com.example.agristation1.data.taskDetails.TaskDetails
import com.example.agristation1.data.alertDetails.AlertDetails
import com.example.agristation1.data.alertDetails.AlertDetailsOfflineRepository
import com.example.agristation1.data.alertDetails.AlertLifecycle
import com.example.agristation1.data.alertDetails.AlertSeverity
import com.example.agristation1.data.alertDetails.AlertType
import com.example.agristation1.data.fieldDetails.FieldDetails
import com.example.agristation1.data.fieldDetails.FieldDetailsOfflineRepository
import com.example.agristation1.data.taskDetails.TaskDetailsOfflineRepository
import com.example.agristation1.data.taskDetails.TaskPriority
import com.example.agristation1.data.taskDetails.TaskStatus
import com.example.agristation1.data.taskDetails.TaskType
import com.example.agristation1.network.alertNetwork.AlertPendingOperation
import com.example.agristation1.network.alertNetwork.AlertPendingOperationDao
import com.example.agristation1.network.alertNetwork.AlertPendingOperationRepository
import com.example.agristation1.network.alertNetwork.AlertPendingOperationType
import com.example.agristation1.network.taskNetwork.TaskPendingOperation
import com.example.agristation1.network.taskNetwork.TaskPendingOperationRepository
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
    private val taskDetailsOfflineRepository: TaskDetailsOfflineRepository,
    private val alertPendingOperationRepository: AlertPendingOperationRepository,
    private val taskPendingOperationRepository: TaskPendingOperationRepository
): ViewModel() {
    private val alertId: Long = savedStateHandle.get<String>("alertId")?.toLongOrNull() ?: 0L

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

    fun onLifecycleChange(alertId: Long, newLifecycle: AlertLifecycle) {
        viewModelScope.launch {
            _isUpdating.value = true
            try {
                alertDetailsOfflineRepository.updateLifecycle(alertId, newLifecycle)

                alertPendingOperationRepository.deleteByAlertIdAndOperation(alertId, AlertPendingOperationType.UPDATE_LIFECYCLE)

                alertPendingOperationRepository.insert(
                    AlertPendingOperation(
                        entityId = alertId,
                        operation = AlertPendingOperationType.UPDATE_LIFECYCLE,
                        payload = newLifecycle.code.toString(),
                    )
                )
            } finally {
                _isUpdating.value = false
            }
        }
    }

    fun deleteAlert(id: Long) {
        viewModelScope.launch {
            _isUpdating.value = true
            try {
                alertDetailsOfflineRepository.deleteAlert(id)
                alertPendingOperationRepository.insert(
                    AlertPendingOperation(
                        entityId = id,
                        operation = AlertPendingOperationType.DELETE_ALERT,
                        payload = "",
                    )
                )
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
                fieldId = it?.fieldId ?: 0L,
                priority = when(it?.severity ?: AlertSeverity.INFO) {
                    AlertSeverity.INFO, AlertSeverity.NOTICE -> TaskPriority.LOW
                    AlertSeverity.WARNING -> TaskPriority.MEDIUM
                    AlertSeverity.CRITICAL -> TaskPriority.HIGH
                },
                type = when(it?.type) {
                    AlertType.SOIL_MOISTURE -> TaskType.SOIL_MOISTURE
                    AlertType.SOIL_TEMPERATURE -> TaskType.SOIL_TEMPERATURE
                    AlertType.AIR_TEMPERATURE -> TaskType.AIR_TEMPERATURE
                    AlertType.AIR_HUMIDITY -> TaskType.AIR_HUMIDITY
                    AlertType.LUX -> TaskType.LUX
                    AlertType.SENSOR -> TaskType.SENSOR
                    AlertType.GATE -> TaskType.GATE
                    AlertType.UNKNOWN -> TaskType.UNKNOWN
                    null -> TaskType.UNKNOWN
                }
            )
        }
    }

    fun addTask() {
        viewModelScope.launch {
            _isUpdating.value = true
            val taskToInsert = TaskDetails(
                id = 0L,
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

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AlertDetailsViewModel(
                    this.createSavedStateHandle(),
                    agriStationApplication().container.alertDetailsOfflineRepository,
                    agriStationApplication().container.fieldDetailsOfflineRepository,
                    agriStationApplication().container.taskDetailsOfflineRepository,
                    agriStationApplication().container.alertPendingOperationRepository,
                    agriStationApplication().container.taskPendingOperationRepository
                )
            }
        }
    }
}