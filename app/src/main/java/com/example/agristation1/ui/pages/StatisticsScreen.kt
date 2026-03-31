package com.example.agristation1.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.example.agristation1.data.fieldDetails.FieldDetails
import com.example.agristation1.fakedata.FakeFieldData
import com.example.agristation1.ui.viewmodel.MoistureFilter
import com.example.agristation1.ui.viewmodel.PeriodFilter
import com.example.agristation1.ui.viewmodel.StatisticsUiState
import com.example.agristation1.ui.viewmodel.StatisticsViewModel
import com.example.agristation1.ui.viewmodel.TemperatureFilter
import com.example.compose.AppTheme
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import kotlin.math.max

@Composable
fun StatisticsMainScreen(
    onBack: () -> Unit = {},
    viewModel: StatisticsViewModel,
) {
    val uiState: StatisticsUiState by viewModel.uiState.collectAsState()
    val fieldDetails = uiState.fieldDetails ?: return

    Column {
        StatisticsTopBar(
            fieldDetails = fieldDetails,
            onBack = onBack
        )
        StatisticsScreen(
            uiState = uiState,
            onBothTemperatureFilter = { viewModel.onTemperatureFilterChange(TemperatureFilter.Both) },
            onSoilTemperatureFilter = { viewModel.onTemperatureFilterChange(TemperatureFilter.Soil) },
            onAirTemperatureFilter = { viewModel.onTemperatureFilterChange(TemperatureFilter.Air) },
            onDayTemperatureFilter = { viewModel.onTemperaturePeriodFilterChange(PeriodFilter.Day) },
            onWeekTemperatureFilter = { viewModel.onTemperaturePeriodFilterChange(PeriodFilter.Week) },
            onMonthTemperatureFilter = { viewModel.onTemperaturePeriodFilterChange(PeriodFilter.Month) },

            onBothMoistureFilter = { viewModel.onMoistureFilterChange(MoistureFilter.Both) },
            onSoilMoistureFilter = { viewModel.onMoistureFilterChange(MoistureFilter.Soil) },
            onAirMoistureFilter = { viewModel.onMoistureFilterChange(MoistureFilter.Air) },
            onDayMoistureFilter = { viewModel.onMoisturePeriodFilterChange(PeriodFilter.Day) },
            onWeekMoistureFilter = { viewModel.onMoisturePeriodFilterChange(PeriodFilter.Week) },
            onMonthMoistureFilter = { viewModel.onMoisturePeriodFilterChange(PeriodFilter.Month) },

            onDayLuxFilter = { viewModel.onLuxPeriodFilterChange(PeriodFilter.Day) },
            onWeekLuxFilter = { viewModel.onLuxPeriodFilterChange(PeriodFilter.Week) },
            onMonthLuxFilter = { viewModel.onLuxPeriodFilterChange(PeriodFilter.Month) }
        )
    }
}

@Composable
fun StatisticsTopBar(
    fieldDetails: FieldDetails,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
            .statusBarsPadding()
            .padding(start = 12.dp, bottom = 12.dp, end = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBack,
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                )
            }
            Text(
                text = "Back",
                style = MaterialTheme.typography.titleLarge
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${fieldDetails.title} Statistics",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            Card(colors = CardDefaults.cardColors()) {
//                Text(
//                    text = fieldDetails.color.toStringField(),
//                    style = MaterialTheme.typography.bodySmall,
//                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
//                    color = fieldDetails.color.toContentColor(),
//                    fontWeight = FontWeight.Bold
//                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
fun StatisticsScreen(
    uiState: StatisticsUiState,

    onBothTemperatureFilter: () -> Unit,
    onSoilTemperatureFilter: () -> Unit,
    onAirTemperatureFilter: () -> Unit,

    onDayTemperatureFilter: () -> Unit,
    onWeekTemperatureFilter: () -> Unit,
    onMonthTemperatureFilter: () -> Unit,

    onBothMoistureFilter: () -> Unit,
    onSoilMoistureFilter: () -> Unit,
    onAirMoistureFilter: () -> Unit,
    
    onDayMoistureFilter: () -> Unit,
    onWeekMoistureFilter: () -> Unit,
    onMonthMoistureFilter: () -> Unit,

    onDayLuxFilter: () -> Unit,
    onWeekLuxFilter: () -> Unit,
    onMonthLuxFilter: () -> Unit,
) {
    val temperatureChartData = remember(
        uiState.temperatureChart.soilData,
        uiState.temperatureChart.airData,
        uiState.temperatureChart.filter
    ) {
        val soilLine = Line(
            label = "Soil Temperature",
            values = uiState.temperatureChart.soilData.mapNotNull { it.value?.toDouble() ?: null },
            color = SolidColor(Color.Green),
            curvedEdges = true,
            drawStyle = DrawStyle.Stroke(width = 4.dp)
        )
        val airLine = Line(
            label = "Air Temperature",
            values = uiState.temperatureChart.airData.mapNotNull { it.value?.toDouble() ?: null },
            color = SolidColor(Color.Blue),
            curvedEdges = true
        )

        when (uiState.temperatureChart.filter) {
            TemperatureFilter.Both -> listOf(soilLine, airLine)
            TemperatureFilter.Soil -> listOf(soilLine)
            TemperatureFilter.Air -> listOf(airLine)
        }
    }
    val moistureChartData = remember(
        uiState.moistureChart.soilData,
        uiState.moistureChart.airData,
        uiState.moistureChart.filter
    ) {
        val soilLine = Line(
            label = "Soil Moisture",
            values = uiState.moistureChart.soilData.mapNotNull { it.value?.toDouble() ?: null },
            color = SolidColor(Color.Green),
            curvedEdges = true,
            drawStyle = DrawStyle.Stroke(width = 4.dp)
        )
        val airLine = Line(
            label = "Air Humidity",
            values = uiState.moistureChart.airData.mapNotNull { it.value?.toDouble() ?: null },
            color = SolidColor(Color.Blue),
            curvedEdges = true
        )

        when (uiState.moistureChart.filter) {
            MoistureFilter.Both -> listOf(soilLine, airLine)
            MoistureFilter.Soil -> listOf(soilLine)
            MoistureFilter.Air -> listOf(airLine)
        }
    }
    val luxChartData = remember(
        uiState.luxChart.data,
        uiState.luxChart.period
    ) {
        listOf(
            Line(
                label = "Light Intensity",
                values = uiState.luxChart.data.mapNotNull { it.value?.toDouble() ?: null },
                color = SolidColor(Color.Green),
                curvedEdges = true,
                drawStyle = DrawStyle.Stroke(width = 4.dp)
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(12.dp)
    ) {
        item {
            Card(
                onClick = {},
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    TemperatureFilterSegmentedButton(
                        uiState,
                        onBothTemperatureFilter,
                        onSoilTemperatureFilter,
                        onAirTemperatureFilter,
                        onDayTemperatureFilter,
                        onWeekTemperatureFilter,
                        onMonthTemperatureFilter
                    )

                    val scrollState = rememberScrollState()
                    val widthPerPoint = 10.dp
                    val chartWidth = max(360.dp, widthPerPoint * max(uiState.temperatureChart.soilData.size, uiState.temperatureChart.airData.size))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState)
                    ) {
                        LineChart(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .width(chartWidth),
                            data = temperatureChartData,
                            labelProperties = LabelProperties(
                                enabled = true,
                                labels = when(uiState.temperatureChart.period) {
                                    PeriodFilter.Day -> listOf("6:00", "12:00", "18:00", "00:00")
                                    PeriodFilter.Week -> listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
                                    PeriodFilter.Month -> listOf("Week 1", "Week 2", "Week 3", "Week 4")
                                }),
                            animationMode = AnimationMode.Together(delayBuilder = {
                                it * 500L
                            }),
                            gridProperties = GridProperties(
                                enabled = true,
                                yAxisProperties = GridProperties.AxisProperties()
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Card(
                onClick = {},
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    MoistureFilterSegmentedButton(
                        uiState,
                        onBothMoistureFilter,
                        onSoilMoistureFilter,
                        onAirMoistureFilter,
                        onDayMoistureFilter,
                        onWeekMoistureFilter,
                        onMonthMoistureFilter
                    )

                    val scrollState = rememberScrollState()
                    val widthPerPoint = 10.dp
                    val chartWidth = max(360.dp, widthPerPoint * max(uiState.moistureChart.soilData.size, uiState.moistureChart.airData.size))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState)
                    ) {
                        LineChart(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .width(chartWidth),
                            data = moistureChartData,
                            labelProperties = LabelProperties(
                                enabled = true,
                                labels = when(uiState.moistureChart.period) {
                                    PeriodFilter.Day -> listOf("6:00", "12:00", "18:00", "00:00")
                                    PeriodFilter.Week -> listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
                                    PeriodFilter.Month -> listOf("Week 1", "Week 2", "Week 3", "Week 4")
                                }),
                            animationMode = AnimationMode.Together(delayBuilder = {
                                it * 500L
                            }),
                            gridProperties = GridProperties(
                                enabled = true,
                                yAxisProperties = GridProperties.AxisProperties()
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Card(
                onClick = {},
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    LuxFilterSegmentedButton(
                        uiState,
                        onDayLuxFilter,
                        onWeekLuxFilter,
                        onMonthLuxFilter
                    )

                    val scrollState = rememberScrollState()
                    val widthPerPoint = 10.dp
                    val chartWidth = max(360.dp, widthPerPoint * uiState.luxChart.data.size)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState)
                    ) {
                        LineChart(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .width(chartWidth),
                            data = luxChartData,
                            labelProperties = LabelProperties(
                                enabled = true,
                                labels = when(uiState.luxChart.period) {
                                    PeriodFilter.Day -> listOf("6:00", "12:00", "18:00", "00:00")
                                    PeriodFilter.Week -> listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
                                    PeriodFilter.Month -> listOf("Week 1", "Week 2", "Week 3", "Week 4")
                                }),
                            animationMode = AnimationMode.Together(delayBuilder = {
                                it * 500L
                            }),
                            gridProperties = GridProperties(
                                enabled = true,
                                yAxisProperties = GridProperties.AxisProperties()
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun TemperatureFilterSegmentedButton(
    uiState: StatisticsUiState,

    onBothTemperatureFilter: () -> Unit,
    onSoilTemperatureFilter: () -> Unit,
    onAirTemperatureFilter: () -> Unit,

    onDayTemperatureFilter: () -> Unit,
    onWeekTemperatureFilter: () -> Unit,
    onMonthTemperatureFilter: () -> Unit,
) {
    val temperatureOptions = listOf(
        "Both" to TemperatureFilter.Both,
        "Soil" to TemperatureFilter.Soil,
        "Air" to TemperatureFilter.Air,
    )

    val periodOptions = listOf(
        "Day" to PeriodFilter.Day,
        "Week" to PeriodFilter.Week,
        "Month" to PeriodFilter.Month,
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SingleChoiceSegmentedButtonRow{
            temperatureOptions.forEachIndexed { index, (text, filter) ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = temperatureOptions.size
                    ),
                    onClick = when(filter) {
                        TemperatureFilter.Both -> onBothTemperatureFilter
                        TemperatureFilter.Soil -> onSoilTemperatureFilter
                        TemperatureFilter.Air -> onAirTemperatureFilter
                    },
                    selected = uiState.temperatureChart.filter == filter,
                    label = { Text(text = text) },
                    contentPadding = PaddingValues(),
                    icon = {},
                    modifier = Modifier.height(30.dp).width(50.dp),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.primary,
                        activeContentColor = MaterialTheme.colorScheme.onPrimary,
                        inactiveContainerColor = MaterialTheme.colorScheme.onPrimary,
                        inactiveContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
        SingleChoiceSegmentedButtonRow{
            periodOptions.forEachIndexed { index, (text, filter) ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = periodOptions.size
                    ),
                    onClick = when(filter) {
                        PeriodFilter.Day -> onDayTemperatureFilter
                        PeriodFilter.Week -> onWeekTemperatureFilter
                        PeriodFilter.Month -> onMonthTemperatureFilter
                    },
                    selected = uiState.temperatureChart.period == filter,
                    label = { Text(text = text) },
                    contentPadding = PaddingValues(),
                    icon = {},
                    modifier = Modifier.height(30.dp).width(50.dp),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.primary,
                        activeContentColor = MaterialTheme.colorScheme.onPrimary,
                        inactiveContainerColor = MaterialTheme.colorScheme.onPrimary,
                        inactiveContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@Composable
fun MoistureFilterSegmentedButton(
    uiState: StatisticsUiState,

    onBothMoistureFilter: () -> Unit,
    onSoilMoistureFilter: () -> Unit,
    onAirMoistureFilter: () -> Unit,

    onDayMoistureFilter: () -> Unit,
    onWeekMoistureFilter: () -> Unit,
    onMonthMoistureFilter: () -> Unit,
) {
    val moistureOptions = listOf(
        "Both" to MoistureFilter.Both,
        "Soil" to MoistureFilter.Soil,
        "Air" to MoistureFilter.Air,
    )

    val periodOptions = listOf(
        "Day" to PeriodFilter.Day,
        "Week" to PeriodFilter.Week,
        "Month" to PeriodFilter.Month,
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SingleChoiceSegmentedButtonRow{
            moistureOptions.forEachIndexed { index, (text, filter) ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = moistureOptions.size
                    ),
                    onClick = when(filter) {
                        MoistureFilter.Both -> onBothMoistureFilter
                        MoistureFilter.Soil -> onSoilMoistureFilter
                        MoistureFilter.Air -> onAirMoistureFilter
                    },
                    selected = uiState.moistureChart.filter == filter,
                    label = { Text(text = text) },
                    contentPadding = PaddingValues(),
                    icon = {},
                    modifier = Modifier.height(30.dp).width(50.dp),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.primary,
                        activeContentColor = MaterialTheme.colorScheme.onPrimary,
                        inactiveContainerColor = MaterialTheme.colorScheme.onPrimary,
                        inactiveContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
        SingleChoiceSegmentedButtonRow{
            periodOptions.forEachIndexed { index, (text, filter) ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = periodOptions.size
                    ),
                    onClick = when(filter) {
                        PeriodFilter.Day -> onDayMoistureFilter
                        PeriodFilter.Week -> onWeekMoistureFilter
                        PeriodFilter.Month -> onMonthMoistureFilter
                    },
                    selected = uiState.moistureChart.period == filter,
                    label = { Text(text = text) },
                    contentPadding = PaddingValues(),
                    icon = {},
                    modifier = Modifier.height(30.dp).width(50.dp),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.primary,
                        activeContentColor = MaterialTheme.colorScheme.onPrimary,
                        inactiveContainerColor = MaterialTheme.colorScheme.onPrimary,
                        inactiveContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@Composable
fun LuxFilterSegmentedButton(
    uiState: StatisticsUiState,

    onDayLuxFilter: () -> Unit,
    onWeekLuxFilter: () -> Unit,
    onMonthLuxFilter: () -> Unit,
) {
    val periodOptions = listOf(
        "Day" to PeriodFilter.Day,
        "Week" to PeriodFilter.Week,
        "Month" to PeriodFilter.Month,
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        SingleChoiceSegmentedButtonRow{
            periodOptions.forEachIndexed { index, (text, filter) ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = periodOptions.size
                    ),
                    onClick = when(filter) {
                        PeriodFilter.Day -> onDayLuxFilter
                        PeriodFilter.Week -> onWeekLuxFilter
                        PeriodFilter.Month -> onMonthLuxFilter
                    },
                    selected = uiState.luxChart.period == filter,
                    label = { Text(text = text) },
                    contentPadding = PaddingValues(),
                    icon = {},
                    modifier = Modifier.height(30.dp).width(50.dp),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.primary,
                        activeContentColor = MaterialTheme.colorScheme.onPrimary,
                        inactiveContainerColor = MaterialTheme.colorScheme.onPrimary,
                        inactiveContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewStatisticsMainScreen() {
    AppTheme {

        val uiState = StatisticsUiState(
            fieldDetails = FakeFieldData.fields[2],
        )
        val fieldDetails = uiState.fieldDetails

        Column {
            StatisticsTopBar(
                fieldDetails = fieldDetails!!,
                onBack = {}
            )
            StatisticsScreen(
                uiState = uiState,
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {}
            )
        }
    }
}
