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
import com.example.agristation1.data.alertDetails.AlertDetailsRepository
import com.example.agristation1.data.fieldDetails.FieldDetails
import com.example.agristation1.data.fieldDetails.FieldDetailsOfflineRepository
import com.example.agristation1.data.fieldDetails.FieldDetailsRepository
import com.example.agristation1.data.fieldDetails.toLatLngList
import com.example.agristation1.data.sensorDetails.SensorDetails
import com.example.agristation1.data.sensorDetails.SensorDetailsOfflineRepository
import com.example.agristation1.data.sensorDetails.SensorDetailsRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class FieldDetailsUiState(
    val fieldDetails: FieldDetails? = null,
    val fieldPoints: List<LatLng> = emptyList(),
    val filteredAlerts: List<AlertDetails> = emptyList(),
    val sensorDetails: List<SensorDetails> = emptyList()
)

@HiltViewModel
class FieldDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val fieldDetailsOfflineRepository: FieldDetailsRepository,
    private val alertDetailsOfflineRepository: AlertDetailsRepository,
    private val sensorDetailsOfflineRepository: SensorDetailsRepository
): ViewModel() {

    private val fieldId: Long = savedStateHandle.get<String>("fieldId")?.toLongOrNull() ?: 0L

    val uiState: StateFlow<FieldDetailsUiState> =
        combine(
            fieldDetailsOfflineRepository.getFieldByIdStream(fieldId),
            alertDetailsOfflineRepository.getAlertsByFieldIdStream(fieldId),
            sensorDetailsOfflineRepository.getSensorDetailsByFieldId(fieldId)
        ) { fields, filteredAlerts, sensors ->

            val points = fieldDetailsOfflineRepository.getFieldWithPointsById(fieldId).toLatLngList()

            FieldDetailsUiState(
                fieldDetails = fields,
                fieldPoints = points,
                filteredAlerts = filteredAlerts,
                sensorDetails = sensors
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FieldDetailsUiState()
        )
}

fun CreationExtras.agriStationApplication(): AgriStationApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as AgriStationApplication)