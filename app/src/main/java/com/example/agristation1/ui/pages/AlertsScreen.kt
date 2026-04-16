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
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Opacity
import androidx.compose.material.icons.outlined.PestControl
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.agristation1.data.alertDetails.AlertDetails
import com.example.agristation1.data.alertDetails.AlertLifecycle
import com.example.agristation1.data.alertDetails.AlertType
import com.example.agristation1.data.alertDetails.toBorderColor
import com.example.agristation1.data.alertDetails.toContainerColor
import com.example.agristation1.data.alertDetails.toContentColor
import com.example.agristation1.data.alertDetails.toIconColor
import com.example.agristation1.data.alertDetails.toStringField
import com.example.agristation1.data.fieldDetails.FieldLifecycle
import com.example.agristation1.data.formatRelativeTime
import com.example.agristation1.fakedata.FakeAlertData
import com.example.agristation1.fakedata.FakeFieldData
import com.example.agristation1.ui.viewmodel.AlertFilter
import com.example.agristation1.ui.viewmodel.AlertUiState
import com.example.agristation1.ui.viewmodel.AlertViewModel
import com.example.compose.AppTheme

@Composable
fun AlertsMainScreen(
    onAlertClick: (Long) -> Unit = {},
    viewModel: AlertViewModel,
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
            AlertsTopBar(
                uiState = uiState,
                onFilterSelected = { viewModel.onFilterChange(it) }
            )
            AlertsScreen(
                uiState = uiState,
                onAlertClick = onAlertClick,
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
fun AlertsTopBar(
    uiState: AlertUiState,
    onFilterSelected: (AlertFilter) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
            .statusBarsPadding()
            .padding(start = 12.dp, bottom = 12.dp, end = 12.dp)
    ) {
        Text(
            text = "Alerts",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            item {
                Button(
                    onClick = { onFilterSelected(AlertFilter.All) },
                    modifier = Modifier.height(35.dp),
                    contentPadding = PaddingValues(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.selectedFilter == AlertFilter.All) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary,
                        contentColor = if (uiState.selectedFilter == AlertFilter.All) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
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
                    onClick = { onFilterSelected(AlertFilter.Open) },
                    modifier = Modifier.height(35.dp),
                    contentPadding = PaddingValues(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.selectedFilter == AlertFilter.Open) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary,
                        contentColor = if (uiState.selectedFilter == AlertFilter.Open) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Active (${uiState.openCount})",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            item {
                Button(
                    onClick = { onFilterSelected(AlertFilter.Critical) },
                    modifier = Modifier.height(35.dp),
                    contentPadding = PaddingValues(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.selectedFilter == AlertFilter.Critical) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary,
                        contentColor = if (uiState.selectedFilter == AlertFilter.Critical) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Critical (${uiState.criticalCount})",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            item {
                Button(
                    onClick = { onFilterSelected(AlertFilter.Warning) },
                    modifier = Modifier.height(35.dp),
                    contentPadding = PaddingValues(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.selectedFilter == AlertFilter.Warning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary,
                        contentColor = if (uiState.selectedFilter == AlertFilter.Warning) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
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
                    onClick = { onFilterSelected(AlertFilter.Archived) },
                    modifier = Modifier.height(35.dp),
                    contentPadding = PaddingValues(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.selectedFilter == AlertFilter.Archived) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary,
                        contentColor = if (uiState.selectedFilter == AlertFilter.Archived) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Archived (${uiState.archivedCount})",
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
fun AlertsScreen(
    uiState: AlertUiState,
    onAlertClick: (Long) -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
    ) {

        if(uiState.filteredAlerts.isEmpty() && uiState.archivedAlerts.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No alerts",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surface),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp)
            ) {
                items(
                    items = uiState.filteredAlerts,
                    key = { it.id }
                ) { item ->
                    AlertInformationCard(
                        item = item,
                        onClick = onAlertClick,
                        uiState = uiState
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                if(uiState.archivedAlerts.isNotEmpty()) {
                    item {
                        Text(
                            text = "Archived Alerts",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    items(uiState.archivedAlerts) { item ->
                        AlertInformationCard(
                            item = item,
                            onClick = onAlertClick,
                            uiState = uiState
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AlertInformationCard(
    item: AlertDetails,
    onClick: (Long) -> Unit,
    uiState: AlertUiState
) {
    val stripeWidth = 6.dp
    val cardShape = RoundedCornerShape(16.dp)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = if(item.lifecycle != AlertLifecycle.DISMISSED && item.lifecycle != AlertLifecycle.RESOLVED) {
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
                        .background(
                            color = when (item.lifecycle) {
                                AlertLifecycle.DISMISSED -> AppColors.gray.c200
                                AlertLifecycle.RESOLVED -> AppColors.green.c200
                                else -> item.severity.toBorderColor()
                            }
                        )
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
                Row {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                color = when (item.lifecycle) {
                                    AlertLifecycle.DISMISSED -> AppColors.gray.c100
                                    AlertLifecycle.RESOLVED -> AppColors.green.c100
                                    else -> item.severity.toContainerColor()
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = when (item.type) {
                                AlertType.SOIL_MOISTURE -> Icons.Outlined.WaterDrop
                                AlertType.SOIL_TEMPERATURE -> Icons.Outlined.Thermostat
                                AlertType.AIR_TEMPERATURE -> Icons.Outlined.Cloud
                                AlertType.AIR_HUMIDITY -> Icons.Outlined.Opacity
                                AlertType.LUX -> Icons.Outlined.WbSunny
                                AlertType.SENSOR -> Icons.Outlined.Sensors
                                AlertType.GATE -> Icons.Outlined.PestControl
                                AlertType.UNKNOWN -> Icons.Outlined.WarningAmber
                            },
                            contentDescription = null,
                            tint = when(item.lifecycle) {
                                AlertLifecycle.DISMISSED -> AppColors.gray.c600
                                AlertLifecycle.RESOLVED -> AppColors.green.c600
                                else -> item.severity.toIconColor()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = item.title ?: "",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f),
                                maxLines = 2
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if(item.lifecycle == AlertLifecycle.DISMISSED || item.lifecycle == AlertLifecycle.RESOLVED) AppColors.gray.c100 else item.severity.toContainerColor()
                                ),
                                border = BorderStroke(1.dp, if(item.lifecycle == AlertLifecycle.DISMISSED || item.lifecycle == AlertLifecycle.RESOLVED) AppColors.gray.c200 else item.severity.toBorderColor()),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = item.severity.toStringField(),
                                    color = if(item.lifecycle == AlertLifecycle.DISMISSED || item.lifecycle == AlertLifecycle.RESOLVED) AppColors.gray.c800 else item.severity.toContentColor(),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(
                                        vertical = 4.dp,
                                        horizontal = 8.dp
                                    ),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = uiState.fields.find { it.id == item.fieldId }?.title
                                    ?: "Unknown Field",
                                style = MaterialTheme.typography.bodyLarge,
                            )

                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = item.lifecycle.toContainerColor()
                                ),
                                border = BorderStroke(1.dp, item.lifecycle.toBorderColor()),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = item.lifecycle.toStringField(),
                                    color = item.lifecycle.toContentColor(),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(
                                        vertical = 4.dp,
                                        horizontal = 8.dp
                                    ),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                Row(modifier = Modifier.fillMaxWidth(0.85f)) {
                    Text(
                        text = item.description ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = "Type: ${item.type.toStringField()}",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatRelativeTime(item.detectedAt),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun AlertsScreenPreview() {
    AppTheme {
        val newAlerts =
            FakeAlertData.alerts.filter { it.lifecycle != AlertLifecycle.RESOLVED && it.lifecycle != AlertLifecycle.DISMISSED }

        val uiState = AlertUiState(
            alerts = FakeAlertData.alerts,
            filteredAlerts = newAlerts.take(2),
            fields = FakeFieldData.fields,
            selectedFilter = AlertFilter.All,
            archivedAlerts = FakeAlertData.alerts.filter { it.lifecycle == AlertLifecycle.DISMISSED || it.lifecycle == AlertLifecycle.RESOLVED },
        )

        Column {
            AlertsTopBar(
                uiState = uiState,
                onFilterSelected = { }
            )
            AlertsScreen(
                uiState = uiState,
                onAlertClick = {},
                isRefreshing = false,
                onRefresh = {}
            )
        }
    }
}