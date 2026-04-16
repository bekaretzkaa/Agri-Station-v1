package com.example.agristation1.ui.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.agristation1.data.AppColors
import com.example.agristation1.data.fieldDetails.FieldConnectivity
import com.example.agristation1.data.fieldDetails.FieldDetails
import com.example.agristation1.data.fieldDetails.FieldLifecycle
import com.example.agristation1.data.fieldDetails.toBorderColor
import com.example.agristation1.data.fieldDetails.toContainerColor
import com.example.agristation1.data.fieldDetails.toContentColor
import com.example.agristation1.data.fieldDetails.toIconColor
import com.example.agristation1.data.fieldDetails.toStringField
import com.example.agristation1.fakedata.FakeFieldData
import com.example.agristation1.ui.viewmodel.FieldFilter
import com.example.agristation1.ui.viewmodel.FieldUiState
import com.example.agristation1.ui.viewmodel.FieldViewModel
import com.example.compose.AppTheme

@Composable
fun FieldsMainScreen(
    onFieldClick: (Long) -> Unit = {},
    viewModel: FieldViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val refreshError by viewModel.refreshError.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(refreshError) {
        refreshError?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearRefreshError()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            FieldsTopBar(
                uiState = uiState,
                onFilterSelected = { viewModel.onFilterChange(it) }
            )
            FieldsScreen(
                uiState = uiState,
                onFieldClick = onFieldClick,
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.refresh() }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

@Composable
fun FieldsTopBar(
    uiState: FieldUiState,
    onFilterSelected: (FieldFilter) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
            .statusBarsPadding()
            .padding(start = 12.dp, bottom = 12.dp, end = 12.dp)
    ) {
        Text(
            text = "Fields",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            item {
                Button(
                    onClick = { onFilterSelected(FieldFilter.All) },
                    modifier = Modifier.height(35.dp),
                    contentPadding = PaddingValues(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.selectedFilter == FieldFilter.All) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary,
                        contentColor = if (uiState.selectedFilter == FieldFilter.All) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "All (${uiState.allCount})",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            item {
                Button(
                    onClick = { onFilterSelected(FieldFilter.Healthy) },
                    modifier = Modifier.height(35.dp),
                    contentPadding = PaddingValues(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.selectedFilter == FieldFilter.Healthy) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary,
                        contentColor = if (uiState.selectedFilter == FieldFilter.Healthy) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Healthy (${uiState.healthyCount})",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            item {
                Button(
                    onClick = { onFilterSelected(FieldFilter.Warning) },
                    modifier = Modifier.height(35.dp),
                    contentPadding = PaddingValues(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.selectedFilter == FieldFilter.Warning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary,
                        contentColor = if (uiState.selectedFilter == FieldFilter.Warning) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Warning (${uiState.warningCount})",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            item {
                Button(
                    onClick = { onFilterSelected(FieldFilter.Critical) },
                    modifier = Modifier.height(35.dp),
                    contentPadding = PaddingValues(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.selectedFilter == FieldFilter.Critical) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary,
                        contentColor = if (uiState.selectedFilter == FieldFilter.Critical) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Critical (${uiState.criticalCount})",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(0.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldsScreen(
    uiState: FieldUiState,
    onFieldClick: (Long) -> Unit,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {}
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh
    ) {
        if(uiState.filteredFields.isEmpty() && uiState.filteredArchivedFields.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No fields",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surface),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                items(uiState.filteredFields) { item ->
                    FieldsInformationCard(
                        item = item,
                        onClick = onFieldClick,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                if(uiState.filteredArchivedFields.isNotEmpty()) {
                    item {
                        Text(
                            text = "Archived Fields",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    items(uiState.filteredArchivedFields) { item ->
                        FieldsInformationCard(
                            item = item,
                            onClick = onFieldClick
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FieldsInformationCard(
    item: FieldDetails,
    onClick: (Long) -> Unit,
) {
    val stripeWidth = 6.dp
    val cardShape = RoundedCornerShape(16.dp)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = if(item.lifecycle != FieldLifecycle.ARCHIVED) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            )
        } else {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                contentColor = Color.Gray
            )
        },
        onClick = { onClick(item.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        shape = cardShape
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(cardShape)
            ) {
                Box(
                    modifier = Modifier
                        .width(stripeWidth)
                        .fillMaxHeight()
                        .background(if (item.lifecycle != FieldLifecycle.ARCHIVED) item.health.toBorderColor() else item.lifecycle.toBorderColor())
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp + stripeWidth,
                        end = 16.dp,
                        top = 24.dp,
                        bottom = 24.dp
                    )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.title ?: "",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = if (item.connectivity == FieldConnectivity.ONLINE) Icons.Outlined.Wifi else Icons.Outlined.WifiOff,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = if(item.lifecycle != FieldLifecycle.ARCHIVED) item.connectivity.toIconColor() else Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${item.area} ha",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(
                                color = Color.Gray,
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.type ?: "",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = item.health.toContainerColor()),
                        border = BorderStroke(1.dp, item.health.toBorderColor()),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = item.health.toStringField(),
                            color = item.health.toContentColor(),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = item.lifecycle.toContainerColor()),
                        border = BorderStroke(1.dp, item.lifecycle.toBorderColor()),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = item.lifecycle.toStringField(),
                            color = item.lifecycle.toContentColor(),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(0.5f)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.WaterDrop,
                            contentDescription = null,
                            tint = AppColors.blue.c600
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = "Soil Moisture",
                                style = MaterialTheme.typography.bodySmall,
                            )
                            Text(
                                text = (item.soilMoisture ?: item.lastValidSoilMoisture)?.let { "$it%" } ?: "N/A",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (item.soilMoisture == null) Color.Gray else LocalContentColor.current
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Thermostat,
                            contentDescription = null,
                            tint = AppColors.red.c600
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = "Temperature",
                                style = MaterialTheme.typography.bodySmall,
                            )
                            Text(
                                text = (item.airTemperature ?: item.lastValidAirTemperature)?.let { "$it°C" } ?: "N/A",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (item.airTemperature == null) Color.Gray else LocalContentColor.current
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(0.5f)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LightMode,
                            contentDescription = null,
                            tint = AppColors.yellow.c600
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = "Light",
                                style = MaterialTheme.typography.bodySmall,
                            )
                            Text(
                                text = (item.lux ?: item.lastValidLux)?.let { "${it / 1000}k lx" } ?: "N/A",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (item.lux == null) Color.Gray else LocalContentColor.current
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Sensors,
                            contentDescription = null,
                            tint = AppColors.green.c600
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = "Sensors",
                                style = MaterialTheme.typography.bodySmall,
                            )
                            Text(
                                text = if (item.totalSensors != null && item.activeSensors != null) {
                                    "${item.activeSensors}/${item.totalSensors}"
                                } else {
                                    "N/A"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = if (item.totalSensors == null || item.activeSensors == null) {
                                    Color.Gray
                                } else {
                                    LocalContentColor.current
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun FieldsScreenPreview() {
    AppTheme {
        val uiState = FieldUiState(
            fields = FakeFieldData.fields.filter { it.lifecycle != FieldLifecycle.ARCHIVED }.take(2),
            archivedFields = FakeFieldData.fields.filter { it.lifecycle == FieldLifecycle.ARCHIVED },
            selectedFilter = FieldFilter.All
        )

        Column {
            FieldsTopBar(
                uiState = uiState,
                onFilterSelected = { }
            )
            FieldsScreen(
                uiState = uiState,
                onFieldClick = { },
            )
        }
    }
}