package com.example.agristation1.ui.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.DeviceThermostat
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.MonitorHeart
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.agristation1.data.AppColors
import com.example.agristation1.data.alertDetails.AlertDetails
import com.example.agristation1.data.alertDetails.AlertType
import com.example.agristation1.data.alertDetails.toBorderColor
import com.example.agristation1.data.alertDetails.toContainerColor
import com.example.agristation1.data.alertDetails.toContentColor
import com.example.agristation1.data.alertDetails.toIconColor
import com.example.agristation1.data.alertDetails.toStringField
import com.example.agristation1.data.formatRelativeTime
import com.example.agristation1.data.taskDetails.TaskStatus
import com.example.agristation1.data.taskDetails.toBorderColor
import com.example.agristation1.data.taskDetails.toContainerColor
import com.example.agristation1.data.taskDetails.toContentColor
import com.example.agristation1.data.taskDetails.toStringField
import com.example.agristation1.data.toUiDueDate
import com.example.agristation1.fakedata.FakeAlertData
import com.example.agristation1.fakedata.FakeFieldData
import com.example.agristation1.ui.viewmodel.AlertFilter
import com.example.agristation1.ui.viewmodel.AlertUiState
import com.example.agristation1.ui.viewmodel.AlertViewModel
import com.example.compose.AppTheme

@Composable
fun AlertsMainScreen(
    onAlertClick: (Int) -> Unit = {},
    viewModel: AlertViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column {
        AlertsTopBar(
            uiState = uiState,
            onFilterSelected = { viewModel.onFilterChange(it) }
        )
        AlertsScreen(
            uiState = uiState,
            onAlertClick = onAlertClick
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
                    onClick = { onFilterSelected(AlertFilter.Resolved) },
                    modifier = Modifier.height(35.dp),
                    contentPadding = PaddingValues(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.selectedFilter == AlertFilter.Resolved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary,
                        contentColor = if (uiState.selectedFilter == AlertFilter.Resolved) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Resolved (${uiState.resolvedCount})",
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
    onAlertClick: (Int) -> Unit
) {
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
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AlertInformationCard(
    item: AlertDetails,
    onClick: (Int) -> Unit,
    uiState: AlertUiState
) {
    val stripeWidth = 6.dp
    val cardShape = RoundedCornerShape(16.dp)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
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
                        .background(item.severity.toBorderColor())
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
                                color = item.severity.toContainerColor(),
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
                            tint = item.severity.toIconColor()
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
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = item.severity.toContainerColor()
                                ),
                                border = BorderStroke(1.dp, item.severity.toBorderColor()),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = item.severity.toStringField(),
                                    color = item.severity.toContentColor(),
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

                Text(
                    text = item.description ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    modifier = Modifier.padding(start = 8.dp)
                )

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
        val uiState = AlertUiState(
            alerts = FakeAlertData.alerts,
            fields = FakeFieldData.fields
        )

        Column {
            AlertsTopBar(
                uiState = uiState,
                onFilterSelected = { }
            )
            AlertsScreen(
                uiState = uiState,
                onAlertClick = {}
            )
        }
    }
}