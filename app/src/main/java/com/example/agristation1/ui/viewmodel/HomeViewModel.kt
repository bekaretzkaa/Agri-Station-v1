package com.example.agristation1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.agristation1.data.alertDetails.AlertDetails
import com.example.agristation1.data.fieldDetails.FieldDetails
import com.example.agristation1.data.alertDetails.AlertDetailsOfflineRepository
import com.example.agristation1.data.farmDetails.FarmDetails
import com.example.agristation1.data.farmDetails.FarmDetailsOfflineRepository
import com.example.agristation1.data.fieldDetails.FieldDetailsOfflineRepository
import com.example.agristation1.data.taskDetails.TaskDetails
import com.example.agristation1.data.taskDetails.TaskDetailsOfflineRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val farmDetails: FarmDetails? = null,
    val fields: List<FieldDetails> = emptyList(),
    val alerts: List<AlertDetails> = emptyList(),
    val attentionFields: List<FieldDetails> = emptyList(),
    val tasks: List<TaskDetails> = emptyList()
    )

class HomeViewModel(
    private val farmDetailsOfflineRepository: FarmDetailsOfflineRepository,
    private val fieldDetailsOfflineRepository: FieldDetailsOfflineRepository,
    private val alertDetailsOfflineRepository: AlertDetailsOfflineRepository,
    private val taskDetailsOfflineRepository: TaskDetailsOfflineRepository
): ViewModel() {

    val uiState: StateFlow<HomeUiState> =
        combine(
            farmDetailsOfflineRepository.getFarmDetailsStream(),
            fieldDetailsOfflineRepository.getAllFieldsStream(),
            alertDetailsOfflineRepository.getAllAlertsStream(),
            fieldDetailsOfflineRepository.getImmediateAttentionFieldsStream(),
            taskDetailsOfflineRepository.getAllTasksStream()
        ) { farmDetails, fields, alerts, attentionFields, tasks ->
            HomeUiState(
                farmDetails = farmDetails,
                fields = fields,
                alerts = alerts,
                attentionFields = attentionFields,
                tasks = tasks
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState()
        )

    fun getAlertsCountByFieldId(fieldId: Int): Int {
        var total = 0
        viewModelScope.launch {
            total = alertDetailsOfflineRepository.getAlertsByFieldIdStream(fieldId).count()
        }
        return total
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HomeViewModel(
                    agriStationApplication().container.farmDetailsOfflineRepository,
                    agriStationApplication().container.fieldDetailsOfflineRepository,
                    agriStationApplication().container.alertDetailsOfflineRepository,
                    agriStationApplication().container.taskDetailsOfflineRepository
                )
            }
        }
    }
}