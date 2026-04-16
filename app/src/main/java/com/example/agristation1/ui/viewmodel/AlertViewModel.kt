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
import com.example.agristation1.data.alertDetails.AlertLifecycle
import com.example.agristation1.data.alertDetails.AlertSeverity
import com.example.agristation1.data.fieldDetails.FieldDetailsOfflineRepository
import com.example.agristation1.network.alertNetwork.AlertSyncManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AlertFilter {
    data object All : AlertFilter
    data object Open : AlertFilter
    data object Critical : AlertFilter
    data object Warning : AlertFilter
    data object Archived : AlertFilter
}

data class AlertUiState(
    val alerts: List<AlertDetails> = emptyList(),
    val filteredAlerts: List<AlertDetails> = emptyList(),
    val archivedAlerts: List<AlertDetails> = emptyList(),
    val fields: List<FieldDetails> = emptyList(),
    val selectedFilter: AlertFilter = AlertFilter.All,
    val isLoading: Boolean = true,

    val allCount: Int = 0,
    val openCount: Int = 0,
    val criticalCount: Int = 0,
    val warningCount: Int = 0,
    val archivedCount: Int = 0,
)

class AlertViewModel(
    private val alertDetailsOfflineRepository: AlertDetailsOfflineRepository,
    private val fieldDetailsOfflineRepository: FieldDetailsOfflineRepository,
    private val syncOrchestrator: SyncOrchestrator,
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {

    private val selectedFilter = MutableStateFlow<AlertFilter>(AlertFilter.All)
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _refreshError = MutableStateFlow<String?>(null)
    val refreshError: StateFlow<String?> = _refreshError.asStateFlow()

    val uiState: StateFlow<AlertUiState> =
        combine(
            alertDetailsOfflineRepository.getAllAlertsStream(),
            fieldDetailsOfflineRepository.getAllFieldsStream(),
            selectedFilter
        ) { alerts, fields, filter ->

            val newAlerts =
                alerts.filter { it.lifecycle != AlertLifecycle.RESOLVED && it.lifecycle != AlertLifecycle.DISMISSED }

            val archivedAlerts =
                alerts.filter { it.lifecycle == AlertLifecycle.DISMISSED || it.lifecycle == AlertLifecycle.RESOLVED }

            val filteredAlerts = when (filter) {
                AlertFilter.All -> newAlerts
                AlertFilter.Open -> newAlerts.filter { it.lifecycle == AlertLifecycle.OPEN }
                AlertFilter.Critical -> newAlerts.filter { it.severity == AlertSeverity.CRITICAL }
                AlertFilter.Warning -> newAlerts.filter { it.severity == AlertSeverity.WARNING }
                AlertFilter.Archived -> archivedAlerts
            }
            val allCount = newAlerts.size
            val openCount = newAlerts.count { it.lifecycle == AlertLifecycle.OPEN }
            val criticalCount = newAlerts.count { it.severity == AlertSeverity.CRITICAL }
            val warningCount = newAlerts.count { it.severity == AlertSeverity.WARNING }
            val archivedCount = newAlerts.count { it.lifecycle == AlertLifecycle.RESOLVED }

            AlertUiState(
                alerts = alerts,
                filteredAlerts = filteredAlerts,
                archivedAlerts = archivedAlerts,
                fields = fields,
                selectedFilter = filter,
                isLoading = false,
                allCount = allCount,
                openCount = openCount,
                criticalCount = criticalCount,
                warningCount = warningCount,
                archivedCount = archivedCount
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AlertUiState(isLoading = true)
        )

    fun onFilterChange(filter: AlertFilter) {
        selectedFilter.value = filter
    }

    fun refresh() {
        if (_isRefreshing.value) return

        viewModelScope.launch {
            _isRefreshing.value = true
            _refreshError.value = null

            val result = syncOrchestrator.syncAll(userPreferencesRepository.lastSync.first())
            Log.d("TaskSyncManager", "Result: $result")

            when (result) {
                is SyncResult.Success -> {
                    userPreferencesRepository.saveLastSync(System.currentTimeMillis())
                }
                is SyncResult.PartialSuccess -> {
                    _refreshError.value = "AlertSynced partially ${result.failedOps}"
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
                AlertViewModel(
                    agriStationApplication().container.alertDetailsOfflineRepository,
                    agriStationApplication().container.fieldDetailsOfflineRepository,
                    agriStationApplication().container.syncOrchestrator,
                    agriStationApplication().userPreferencesRepository
                )
            }
        }
    }
}