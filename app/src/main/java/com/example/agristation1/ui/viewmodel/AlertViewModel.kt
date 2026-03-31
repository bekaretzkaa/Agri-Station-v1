package com.example.agristation1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.agristation1.data.alertDetails.AlertDetails
import com.example.agristation1.data.fieldDetails.FieldDetails
import com.example.agristation1.data.alertDetails.AlertDetailsOfflineRepository
import com.example.agristation1.data.alertDetails.AlertLifecycle
import com.example.agristation1.data.alertDetails.AlertSeverity
import com.example.agristation1.data.fieldDetails.FieldDetailsOfflineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

sealed interface AlertFilter {
    data object All : AlertFilter
    data object Open : AlertFilter
    data object Critical : AlertFilter
    data object Warning : AlertFilter
    data object Resolved : AlertFilter
}

data class AlertUiState(
    val alerts: List<AlertDetails> = emptyList(),
    val resolvedAlerts: List<AlertDetails> = emptyList(),
    val dismissedAlerts: List<AlertDetails> = emptyList(),
    val fields: List<FieldDetails> = emptyList(),
    val selectedFilter: AlertFilter = AlertFilter.All
) {
    val filteredAlerts: List<AlertDetails> = when(selectedFilter) {
        AlertFilter.All -> alerts
        AlertFilter.Open -> alerts.filter { it.lifecycle == AlertLifecycle.OPEN }
        AlertFilter.Critical -> alerts.filter { it.severity == AlertSeverity.CRITICAL }
        AlertFilter.Warning -> alerts.filter { it.severity == AlertSeverity.WARNING }
        AlertFilter.Resolved -> alerts.filter { it.lifecycle == AlertLifecycle.RESOLVED }
    }

    val allCount = alerts.size
    val openCount = alerts.count { it.lifecycle == AlertLifecycle.OPEN }
    val criticalCount = alerts.count { it.severity == AlertSeverity.CRITICAL }
    val warningCount = alerts.count { it.severity == AlertSeverity.WARNING }
    val resolvedCount = alerts.count { it.lifecycle == AlertLifecycle.RESOLVED }
}

class AlertViewModel(
    private val alertDetailsOfflineRepository: AlertDetailsOfflineRepository,
    private val fieldDetailsOfflineRepository: FieldDetailsOfflineRepository
): ViewModel() {

    private val selectedFilter = MutableStateFlow<AlertFilter>(AlertFilter.All)

    val uiState: StateFlow<AlertUiState> =
        combine(
            alertDetailsOfflineRepository.getAllAlertsStream(),
            alertDetailsOfflineRepository.getResolvedAlertsStream(),
            alertDetailsOfflineRepository.getDismissedAlertsStream(),
            fieldDetailsOfflineRepository.getAllFieldsStream(),
            selectedFilter,
        ) { alerts, resolvedAlerts, dismissedAlerts, fields, filter ->
            AlertUiState(
                alerts = alerts,
                resolvedAlerts = resolvedAlerts,
                dismissedAlerts = dismissedAlerts,
                fields = fields,
                selectedFilter = filter,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AlertUiState()
        )

    fun onFilterChange(filter: AlertFilter) {
        selectedFilter.value = filter
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AlertViewModel(
                    agriStationApplication().container.alertDetailsOfflineRepository,
                    agriStationApplication().container.fieldDetailsOfflineRepository
                )
            }
        }
    }
}