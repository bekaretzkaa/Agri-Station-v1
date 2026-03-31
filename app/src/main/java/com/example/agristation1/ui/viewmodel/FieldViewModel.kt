package com.example.agristation1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.agristation1.data.fieldDetails.FieldDetailsOfflineRepository
import com.example.agristation1.data.fieldDetails.FieldDetails
import com.example.agristation1.data.alertDetails.AlertDetailsOfflineRepository
import com.example.agristation1.data.fieldDetails.FieldHealth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.count
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

    val allCount: Int = fields.size
    val healthyCount: Int = fields.count { it.health == FieldHealth.HEALTHY }
    val warningCount: Int = fields.count { it.health == FieldHealth.WARNING }
    val criticalCount: Int = fields.count { it.health == FieldHealth.CRITICAL }
}

class FieldViewModel(
    private val fieldDetailsOfflineRepository: FieldDetailsOfflineRepository,
    private val alertDetailsOfflineRepository: AlertDetailsOfflineRepository
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
                FieldViewModel(
                    agriStationApplication().container.fieldDetailsOfflineRepository,
                    agriStationApplication().container.alertDetailsOfflineRepository
                )
            }
        }
    }
}