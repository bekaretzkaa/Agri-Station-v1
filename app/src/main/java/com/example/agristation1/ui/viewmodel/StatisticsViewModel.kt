package com.example.agristation1.ui.viewmodel

import androidx.compose.runtime.State
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.agristation1.data.fieldDetails.FieldDetails
import com.example.agristation1.data.fieldDetails.FieldDetailsOfflineRepository
import com.example.agristation1.data.history.AirHumidity
import com.example.agristation1.data.history.AirTemperature
import com.example.agristation1.data.history.HistoryOfflineRepository
import com.example.agristation1.data.history.Lux
import com.example.agristation1.data.history.SoilMoisture
import com.example.agristation1.data.history.SoilTemperature
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import java.time.Period

sealed interface TemperatureFilter {
    data object Both : TemperatureFilter
    data object Soil : TemperatureFilter
    data object Air : TemperatureFilter
}

sealed interface MoistureFilter {
    data object Both : MoistureFilter
    data object Soil : MoistureFilter
    data object Air : MoistureFilter
}

sealed interface PeriodFilter {
    data object Day : PeriodFilter
    data object Week : PeriodFilter
    data object Month : PeriodFilter
}

data class StatisticsUiState(
    val fieldDetails: FieldDetails? = null,
    val temperatureChart: TemperatureChartUiState = TemperatureChartUiState(),
    val moistureChart: MoistureChartUiState = MoistureChartUiState(),
    val luxChart: LuxChartUiState = LuxChartUiState()
)

data class TemperatureChartUiState(
    val filter: TemperatureFilter = TemperatureFilter.Both,
    val period: PeriodFilter = PeriodFilter.Month,
    val soilData: List<SoilTemperature> = emptyList(),
    val airData: List<AirTemperature> = emptyList()
)

data class MoistureChartUiState(
    val filter: MoistureFilter = MoistureFilter.Both,
    val period: PeriodFilter = PeriodFilter.Month,
    val soilData: List<SoilMoisture> = emptyList(),
    val airData: List<AirHumidity> = emptyList()
)

data class LuxChartUiState(
    val period: PeriodFilter = PeriodFilter.Month,
    val data: List<Lux> = emptyList()
)

class StatisticsViewModel(
    savedStateHandle: SavedStateHandle,
    private val fieldDetailsOfflineRepository: FieldDetailsOfflineRepository,
    private val historyOfflineRepository: HistoryOfflineRepository
) : ViewModel() {

    private val fieldId: Int = savedStateHandle.get<String>("fieldId")?.toIntOrNull() ?: 0

    // Temperature filters
    private val temperatureFilter = MutableStateFlow<TemperatureFilter>(TemperatureFilter.Both)
    private val temperaturePeriodFilter = MutableStateFlow<PeriodFilter>(PeriodFilter.Month)

    private val temperatureChartState: StateFlow<TemperatureChartUiState> =
        combine(
            historyOfflineRepository.getSoilTemperatureByFieldIdStream(fieldId),
            historyOfflineRepository.getAirTemperatureByFieldIdStream(fieldId),
            temperatureFilter,
            temperaturePeriodFilter
        ) { soilTemperature, airTemperature, filter, period ->
            TemperatureChartUiState(
                filter = filter,
                period = period,
                soilData = soilTemperature.take(period.toPointLimit()),
                airData = airTemperature.take(period.toPointLimit())
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TemperatureChartUiState()
        )

    private val moistureFilter = MutableStateFlow<MoistureFilter>(MoistureFilter.Both)
    private val moisturePeriodFilter = MutableStateFlow<PeriodFilter>(PeriodFilter.Month)

    private val moistureChartState: StateFlow<MoistureChartUiState> =
        combine(
            historyOfflineRepository.getSoilMoistureByFieldIdStream(fieldId),
            historyOfflineRepository.getAirHumidityByFieldIdStream(fieldId),
            moistureFilter,
            moisturePeriodFilter
        ) { soilMoisture, airHumidity, filter, period ->
            MoistureChartUiState(
                filter = filter,
                period = period,
                soilData = soilMoisture.take(period.toPointLimit()),
                airData = airHumidity.take(period.toPointLimit())
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MoistureChartUiState()
        )

    private val luxPeriodFilter = MutableStateFlow<PeriodFilter>(PeriodFilter.Month)

    private val luxChartState: StateFlow<LuxChartUiState> =
        combine(
            historyOfflineRepository.getLuxByFieldIdStream(fieldId),
            luxPeriodFilter
        ) { lux, period ->
            LuxChartUiState(
                period = period,
                data = lux.take(period.toPointLimit())
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LuxChartUiState()
        )

    val uiState: StateFlow<StatisticsUiState> =
        combine(
            fieldDetailsOfflineRepository.getFieldByIdStream(fieldId),
            temperatureChartState,
            moistureChartState,
            luxChartState
        ) { fieldDetails, temperatureChart, moistureChart, luxChart ->
            StatisticsUiState(
                fieldDetails = fieldDetails,
                temperatureChart = temperatureChart,
                moistureChart = moistureChart,
                luxChart = luxChart
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StatisticsUiState()
        )

    fun onTemperatureFilterChange(filter: TemperatureFilter) {
        temperatureFilter.value = filter
    }

    fun onTemperaturePeriodFilterChange(filter: PeriodFilter) {
        temperaturePeriodFilter.value = filter
    }

    fun onMoistureFilterChange(filter: MoistureFilter) {
        moistureFilter.value = filter
    }

    fun onMoisturePeriodFilterChange(filter: PeriodFilter) {
        moisturePeriodFilter.value = filter
    }

    fun onLuxPeriodFilterChange(filter: PeriodFilter) {
        luxPeriodFilter.value = filter
    }

    private fun PeriodFilter.toPointLimit(): Int {
        return when (this) {
            PeriodFilter.Day -> 4
            PeriodFilter.Week -> 7 * 4
            PeriodFilter.Month -> 30 * 4
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                StatisticsViewModel(
                    this.createSavedStateHandle(),
                    agriStationApplication().container.fieldDetailsOfflineRepository,
                    agriStationApplication().container.historyOfflineRepository
                )
            }
        }
    }

}