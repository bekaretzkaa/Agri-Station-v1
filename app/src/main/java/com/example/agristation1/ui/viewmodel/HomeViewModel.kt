package com.example.agristation1.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.agristation1.data.SyncOrchestrator
import com.example.agristation1.data.SyncResult
import com.example.agristation1.data.UserPreferencesRepository
import com.example.agristation1.data.alertDetails.AlertDetails
import com.example.agristation1.data.fieldDetails.FieldDetails
import com.example.agristation1.data.alertDetails.AlertDetailsOfflineRepository
import com.example.agristation1.data.farmDetails.FarmDetails
import com.example.agristation1.data.farmDetails.FarmDetailsOfflineRepository
import com.example.agristation1.data.fieldDetails.FieldDetailsOfflineRepository
import com.example.agristation1.data.taskDetails.TaskDetails
import com.example.agristation1.data.taskDetails.TaskDetailsOfflineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant

data class HomeUiState(
    val farmDetails: FarmDetails? = null,
    val fields: List<FieldDetails> = emptyList(),
    val alerts: List<AlertDetails> = emptyList(),
    val attentionFields: List<FieldDetails> = emptyList(),
    val attentionAlerts: List<AlertDetails> = emptyList(),
    val tasks: List<TaskDetails> = emptyList(),
    val lastSync: Instant = Instant.now()
    )

class HomeViewModel(
    private val farmDetailsOfflineRepository: FarmDetailsOfflineRepository,
    private val fieldDetailsOfflineRepository: FieldDetailsOfflineRepository,
    private val alertDetailsOfflineRepository: AlertDetailsOfflineRepository,
    private val taskDetailsOfflineRepository: TaskDetailsOfflineRepository,
    private val syncOrchestrator: SyncOrchestrator,
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {

    val uiState: StateFlow<HomeUiState> =
        combine(
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
            },
            userPreferencesRepository.lastSync,
            alertDetailsOfflineRepository.getImmediateAttentionAlertsStream()
        ) { partialState, lastSync, immediateAlerts ->
            partialState.copy(
                lastSync = Instant.ofEpochMilli(lastSync),
                attentionAlerts = immediateAlerts
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState()
        )

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _refreshError = MutableStateFlow<String?>(null)
    val refreshError: StateFlow<String?> = _refreshError.asStateFlow()

    fun refresh() {
        if (_isRefreshing.value) return

        viewModelScope.launch {
            _isRefreshing.value = true
            _refreshError.value = null

            val result = syncOrchestrator.syncAll(userPreferencesRepository.lastSync.first())
            Log.d("SyncManager", "Result: $result")

            when (result) {
                is SyncResult.Success -> {
                    userPreferencesRepository.saveLastSync(System.currentTimeMillis())
                }
                is SyncResult.PartialSuccess -> {
                    _refreshError.value = "${result.failedOps} operations not synchronized"
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
                HomeViewModel(
                    agriStationApplication().container.farmDetailsOfflineRepository,
                    agriStationApplication().container.fieldDetailsOfflineRepository,
                    agriStationApplication().container.alertDetailsOfflineRepository,
                    agriStationApplication().container.taskDetailsOfflineRepository,
                    agriStationApplication().container.syncOrchestrator,
                    agriStationApplication().userPreferencesRepository
                )
            }
        }
    }
}