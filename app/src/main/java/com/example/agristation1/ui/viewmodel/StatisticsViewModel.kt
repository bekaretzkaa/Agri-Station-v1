package com.example.agristation1.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.agristation1.data.fieldDetails.FieldDetails
import com.example.agristation1.data.fieldDetails.FieldDetailsOfflineRepository
import com.example.agristation1.data.historyDetails.HistoryOfflineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
    val soilData: List<Double> = emptyList(),
    val airData: List<Double> = emptyList()
)

data class MoistureChartUiState(
    val filter: MoistureFilter = MoistureFilter.Both,
    val period: PeriodFilter = PeriodFilter.Month,
    val soilData: List<Double> = emptyList(),
    val airData: List<Double> = emptyList()
)

data class LuxChartUiState(
    val period: PeriodFilter = PeriodFilter.Month,
    val data: List<Double> = emptyList()
)

class StatisticsViewModel(
    savedStateHandle: SavedStateHandle,
    private val fieldDetailsOfflineRepository: FieldDetailsOfflineRepository,
    private val historyOfflineRepository: HistoryOfflineRepository
) : ViewModel() {

    private val fieldId: Long = savedStateHandle.get<String>("fieldId")?.toLongOrNull() ?: 0L

    private val temperatureFilter = MutableStateFlow<TemperatureFilter>(TemperatureFilter.Both)
    private val temperaturePeriod = MutableStateFlow<PeriodFilter>(PeriodFilter.Month)
    private val moistureFilter = MutableStateFlow<MoistureFilter>(MoistureFilter.Both)
    private val moisturePeriod = MutableStateFlow<PeriodFilter>(PeriodFilter.Month)
    private val luxPeriod = MutableStateFlow<PeriodFilter>(PeriodFilter.Month)

    init {
        viewModelScope.launch {
            historyOfflineRepository.deleteOlderThan(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000)
        }
    }

    private val temperatureChartState: StateFlow<TemperatureChartUiState> =
        combine(temperatureFilter, temperaturePeriod) { filter, period -> filter to period }
            .flatMapLatest { (filter, period) ->
                combine(
                    historyOfflineRepository.observeByPeriod(fieldId, period,
                        raw = historyOfflineRepository::observeSoilTemperature,
                        aggregated = historyOfflineRepository::observeSoilTemperatureAggregated
                    ).distinctUntilChanged(),
                    historyOfflineRepository.observeByPeriod(fieldId, period,
                        raw = historyOfflineRepository::observeAirTemperature,
                        aggregated = historyOfflineRepository::observeAirTemperatureAggregated
                    ).distinctUntilChanged()
                ) { soilData, airData ->
                    TemperatureChartUiState(
                        filter = filter,
                        period = period,
                        soilData = soilData,
                        airData = airData
                    )
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TemperatureChartUiState())


    private val moistureChartState: StateFlow<MoistureChartUiState> =
        combine(moistureFilter, moisturePeriod) { filter, period -> filter to period }
            .flatMapLatest { (filter, period) ->
                combine(
                    historyOfflineRepository.observeByPeriod(fieldId, period,
                        raw = historyOfflineRepository::observeSoilMoisture,
                        aggregated = historyOfflineRepository::observeSoilMoistureAggregated
                    ).distinctUntilChanged(),
                    historyOfflineRepository.observeByPeriod(fieldId, period,
                        raw = historyOfflineRepository::observeAirHumidity,
                        aggregated = historyOfflineRepository::observeAirHumidityAggregated
                    ).distinctUntilChanged()
                ) { soilData, airData ->
                    MoistureChartUiState(
                        filter = filter,
                        period = period,
                        soilData = soilData,
                        airData = airData
                    )
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MoistureChartUiState())


    private val luxChartState: StateFlow<LuxChartUiState> =
        luxPeriod
            .flatMapLatest { period ->
                historyOfflineRepository.observeByPeriod(fieldId, period,
                    raw = historyOfflineRepository::observeLux,
                    aggregated = historyOfflineRepository::observeLuxAggregated
                )
                    .distinctUntilChanged()
                    .map { data -> LuxChartUiState(period = period, data = data) }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LuxChartUiState())


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
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StatisticsUiState())


    fun onTemperatureFilterChange(filter: TemperatureFilter) { temperatureFilter.value = filter }
    fun onTemperaturePeriodChange(period: PeriodFilter) { temperaturePeriod.value = period }
    fun onMoistureFilterChange(filter: MoistureFilter) { moistureFilter.value = filter }
    fun onMoisturePeriodChange(period: PeriodFilter) { moisturePeriod.value = period }
    fun onLuxPeriodChange(period: PeriodFilter) { luxPeriod.value = period }


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

fun PeriodFilter.toSince(): Long {
    val now = System.currentTimeMillis()
    return when (this) {
        PeriodFilter.Day -> now - 86_400L * 1000
        PeriodFilter.Week -> now - 7 * 86_400L * 1000
        PeriodFilter.Month -> now - 30 * 86_400L * 1000
    }
}


fun PeriodFilter.toBucket(): Long? = when (this) {
    PeriodFilter.Day   -> null
    PeriodFilter.Week  -> 3 * 3_600L * 1000L
    PeriodFilter.Month -> 12 * 3_600L * 1000L
}

fun HistoryOfflineRepository.observeByPeriod(
    fieldId: Long,
    period: PeriodFilter,
    raw: (Long, Long) -> Flow<List<Double>>,
    aggregated: (Long, Long, Long) -> Flow<List<Double>>
): Flow<List<Double>> {
    val since = period.toSince()
    val bucket = period.toBucket()
    return if (bucket == null) raw(fieldId, since)
    else aggregated(fieldId, since, bucket)
}