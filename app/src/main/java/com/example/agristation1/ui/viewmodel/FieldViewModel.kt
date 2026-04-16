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
import com.example.agristation1.data.fieldDetails.FieldDetailsOfflineRepository
import com.example.agristation1.data.fieldDetails.FieldDetails
import com.example.agristation1.data.fieldDetails.FieldHealth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


sealed interface FieldFilter {
    data object All : FieldFilter
    data object Healthy : FieldFilter
    data object Warning : FieldFilter
    data object Critical : FieldFilter
}

data class FieldUiState(
    val fields: List<FieldDetails> = emptyList(),
    val archivedFields: List<FieldDetails> = emptyList(),
    val selectedFilter: FieldFilter = FieldFilter.All
) {
    val filteredFields: List<FieldDetails> = when(selectedFilter) {
        FieldFilter.All -> fields
        FieldFilter.Healthy -> fields.filter { it.health == FieldHealth.HEALTHY }
        FieldFilter.Critical -> fields.filter { it.health == FieldHealth.CRITICAL }
        FieldFilter.Warning -> fields.filter { it.health == FieldHealth.WARNING }
    }

    val filteredArchivedFields: List<FieldDetails> = when(selectedFilter) {
        FieldFilter.All -> archivedFields
        FieldFilter.Healthy -> archivedFields.filter { it.health == FieldHealth.HEALTHY }
        FieldFilter.Critical -> archivedFields.filter { it.health == FieldHealth.CRITICAL }
        FieldFilter.Warning -> archivedFields.filter { it.health == FieldHealth.WARNING }
    }

    val allCount: Int = fields.size + archivedFields.size
    val healthyCount: Int = fields.count { it.health == FieldHealth.HEALTHY } + archivedFields.count { it.health == FieldHealth.HEALTHY }
    val warningCount: Int = fields.count { it.health == FieldHealth.WARNING } + archivedFields.count { it.health == FieldHealth.WARNING }
    val criticalCount: Int = fields.count { it.health == FieldHealth.CRITICAL } + archivedFields.count { it.health == FieldHealth.CRITICAL }
}

class FieldViewModel(
    private val fieldDetailsOfflineRepository: FieldDetailsOfflineRepository,
    private val syncOrchestrator: SyncOrchestrator,
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {

    private val selectedFilter = MutableStateFlow<FieldFilter>(FieldFilter.All)

    val uiState: StateFlow<FieldUiState> =
        combine(
            fieldDetailsOfflineRepository.getAllFieldsStream(),
            fieldDetailsOfflineRepository.getArchivedFieldsStream(),
            selectedFilter
        ) { fields, archivedFields, filter ->
            FieldUiState(
                fields = fields,
                archivedFields = archivedFields,
                selectedFilter = filter
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FieldUiState()
        )

    fun onFilterChange(filter: FieldFilter) {
        selectedFilter.value = filter
    }

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _refreshError = MutableStateFlow<String?>(null)
    val refreshError: StateFlow<String?> = _refreshError.asStateFlow()

    fun refresh() {
        if(_isRefreshing.value) return

        viewModelScope.launch {
            _isRefreshing.value = true
            _refreshError.value = null

            val result = syncOrchestrator.syncAll(userPreferencesRepository.lastSync.first())
            Log.d("FieldSyncManager", "Result: $result")

            when(result) {
                is SyncResult.Success -> {
                    userPreferencesRepository.saveLastSync(System.currentTimeMillis())
                }
                is SyncResult.Error -> {
                    _refreshError.value = result.message
                }
                is SyncResult.PartialSuccess -> {}
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
                FieldViewModel(
                    agriStationApplication().container.fieldDetailsOfflineRepository,
                    agriStationApplication().container.syncOrchestrator,
                    agriStationApplication().userPreferencesRepository
                )
            }
        }
    }
}