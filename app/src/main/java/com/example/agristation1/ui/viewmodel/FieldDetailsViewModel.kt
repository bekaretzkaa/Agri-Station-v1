package com.example.agristation1.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.agristation1.AgriStationApplication
import com.example.agristation1.data.alertDetails.AlertDetails
import com.example.agristation1.data.alertDetails.AlertDetailsOfflineRepository
import com.example.agristation1.data.fieldDetails.FieldDetails
import com.example.agristation1.data.fieldDetails.FieldDetailsOfflineRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class FieldDetailsUiState(
    val fieldDetails: FieldDetails? = null,
    val filteredAlerts: List<AlertDetails> = emptyList()
)

class FieldDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val fieldDetailsOfflineRepository: FieldDetailsOfflineRepository,
    private val alertDetailsOfflineRepository: AlertDetailsOfflineRepository
): ViewModel() {

    private val fieldId: Int = savedStateHandle.get<String>("fieldId")?.toIntOrNull() ?: 0

    val uiState: StateFlow<FieldDetailsUiState> =
        combine(
            fieldDetailsOfflineRepository.getFieldByIdStream(fieldId),
            alertDetailsOfflineRepository.getAlertsByFieldIdStream(fieldId)
        ) { fields, filteredAlerts ->
            FieldDetailsUiState(
                fieldDetails = fields,
                filteredAlerts = filteredAlerts
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FieldDetailsUiState()
        )

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                FieldDetailsViewModel(
                    this.createSavedStateHandle(),
                    agriStationApplication().container.fieldDetailsOfflineRepository,
                    agriStationApplication().container.alertDetailsOfflineRepository
                )
            }
        }
    }
}

fun CreationExtras.agriStationApplication(): AgriStationApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as AgriStationApplication)