package com.example.agristation1.ui.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Agriculture
import androidx.compose.material.icons.outlined.AreaChart
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.DeviceThermostat
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Opacity
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.ThermostatAuto
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.agristation1.R
import com.example.agristation1.data.AppColors
import com.example.agristation1.data.alertDetails.AlertDetails
import com.example.agristation1.data.alertDetails.toBorderColor
import com.example.agristation1.data.alertDetails.toContainerColor
import com.example.agristation1.data.alertDetails.toContentColor
import com.example.agristation1.data.alertDetails.toStringField
import com.example.agristation1.data.fieldDetails.FieldConnectivity
import com.example.agristation1.data.fieldDetails.FieldDetails
import com.example.agristation1.data.fieldDetails.FieldHealth
import com.example.agristation1.data.fieldDetails.toBorderColor
import com.example.agristation1.data.fieldDetails.toContainerColor
import com.example.agristation1.data.fieldDetails.toContentColor
import com.example.agristation1.data.fieldDetails.toIconColor
import com.example.agristation1.data.fieldDetails.toStringField
import com.example.agristation1.data.formatRelativeTime
import com.example.agristation1.fakedata.FakeAlertData
import com.example.agristation1.fakedata.FakeFieldData
import com.example.agristation1.ui.viewmodel.FieldDetailsUiState
import com.example.agristation1.ui.viewmodel.FieldDetailsViewModel
import com.example.compose.AppTheme

@Composable
fun FieldDetailsMainScreen(
    onBack: () -> Unit = {},
    viewModel: FieldDetailsViewModel,
    onOpenAlertDetails: (Int) -> Unit = {},
    onOpenAllAlerts: () -> Unit = {},
    openStatistics: (Int) -> Unit = {}
) {
    val uiState: FieldDetailsUiState by viewModel.uiState.collectAsState()
    val fieldDetails = uiState.fieldDetails
    val alerts = uiState.filteredAlerts


    if (fieldDetails == null) {
        return
    }

    Column {
        FieldDetailsTopBar(
            fieldDetails = fieldDetails,
            onBack = onBack
        )
        FieldDetailsScreen(
            fieldDetails = fieldDetails,
            alerts = alerts,
            onOpenAlertDetails = onOpenAlertDetails,
            onOpenAllAlerts = onOpenAllAlerts,
            openStatistics = { openStatistics(fieldDetails.id) }
        )
    }
}

@Composable
fun FieldDetailsTopBar(
    fieldDetails: FieldDetails,
    onBack: () -> Unit,
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
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = fieldDetails.title ?: "",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = fieldDetails.health.toContainerColor()),
                        border = BorderStroke(1.dp, fieldDetails.health.toBorderColor()),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = fieldDetails.health.toStringField(),
                            color = fieldDetails.health.toContentColor(),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = fieldDetails.lifecycle.toContainerColor()),
                        border = BorderStroke(1.dp, fieldDetails.lifecycle.toBorderColor()),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = fieldDetails.lifecycle.toStringField(),
                            color = fieldDetails.lifecycle.toContentColor(),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.AreaChart,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${fieldDetails.area} Hectares",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Outlined.Agriculture,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Crop: ${fieldDetails.type}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (fieldDetails.connectivity == FieldConnectivity.ONLINE) Icons.Outlined.Wifi else Icons.Outlined.WifiOff,
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                tint = fieldDetails.connectivity.toIconColor()
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
    }
}

@Composable
fun FieldDetailsScreen(
    fieldDetails: FieldDetails,
    alerts: List<AlertDetails>,
    onOpenAlertDetails: (Int) -> Unit,
    onOpenAllAlerts: () -> Unit,
    openStatistics: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp)
    ) {
        item {
            FieldDetailsMainDetails(
                fieldDetails = fieldDetails,
                openStatistics = openStatistics
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            FieldDetailsMap()
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (!alerts.isEmpty()) {
            item {
                FieldDetailsActiveAlertsHeader(
                    fieldDetails = fieldDetails,
                    alertsSize = alerts.size,
                    onOpenAllAlerts = onOpenAllAlerts
                )
            }

            itemsIndexed(alerts) { index, alert ->
                val isLastIndex = index == alerts.lastIndex
                FieldDetailsActiveAlertsInformationCard(
                    alertDetails = alert,
                    isLastItem = isLastIndex,
                    onOpenAlertDetails = onOpenAlertDetails
                )
            }
        }
    }
}

@Composable
fun FieldDetailsMainDetails(
    fieldDetails: FieldDetails,
    openStatistics: () -> Unit
) {
    Column(
        modifier = Modifier.clickable(
            onClick = openStatistics
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.WaterDrop,
                        contentDescription = null,
                        tint = AppColors.blue.c600
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Soil Moisture",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${fieldDetails.soilMoisture ?: fieldDetails.lastValidSoilMoisture}%",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (fieldDetails.soilMoisture == null) Color.Gray else Color.Black
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Thermostat,
                        contentDescription = null,
                        tint = AppColors.orange.c600
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Soil Temp",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${fieldDetails.soilTemperature ?: fieldDetails.lastValidSoilTemperature}°C",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (fieldDetails.soilTemperature == null) Color.Gray else Color.Black
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Cloud,
                        contentDescription = null,
                        tint = AppColors.red.c600
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Air Temp",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${fieldDetails.airTemperature ?: fieldDetails.lastValidAirTemperature}°C",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (fieldDetails.airTemperature == null) Color.Gray else Color.Black
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(12.dp))


        Row(verticalAlignment = Alignment.CenterVertically) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Opacity,
                        contentDescription = null,
                        tint = AppColors.blue.c800
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Humidity",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${fieldDetails.airHumidity ?: fieldDetails.lastValidAirHumidity}%",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (fieldDetails.airHumidity == null) Color.Gray else Color.Black
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.WbSunny,
                        contentDescription = null,
                        tint = AppColors.yellow.c600
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Lux",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${fieldDetails.lux?.div(1000) ?: fieldDetails.lastValidLux?.div(1000)}k lx",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (fieldDetails.lux == null) Color.Gray else Color.Black
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Sensors,
                        contentDescription = null,
                        tint = AppColors.green.c600
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Active Sensors",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${fieldDetails.activeSensors}/${fieldDetails.totalSensors}",
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }
        }
    }
}

@Composable
fun FieldDetailsMap() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            )
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Place,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Field Map",
                style = MaterialTheme.typography.titleMedium
            )
        }
        Box(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(R.drawable.field_picture),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
            )
        }
    }
}

@Composable
fun FieldDetailsActiveAlertsHeader(
    fieldDetails: FieldDetails,
    alertsSize: Int,
    onOpenAllAlerts: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if(fieldDetails.health == FieldHealth.HEALTHY && alertsSize > 0) AppColors.yellow.c100 else fieldDetails.health.toContainerColor(),
            contentColor = if(fieldDetails.health == FieldHealth.HEALTHY && alertsSize > 0) AppColors.yellow.c800 else fieldDetails.health.toContentColor()
        ),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .height(40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.WarningAmber,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Active Alerts ($alertsSize)",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = onOpenAllAlerts,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if(fieldDetails.health == FieldHealth.HEALTHY && alertsSize > 0) AppColors.yellow.c800 else fieldDetails.health.toContentColor()
                    )
                ) {
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

            }
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun FieldDetailsActiveAlertsInformationCard(
    alertDetails: AlertDetails,
    isLastItem: Boolean,
    onOpenAlertDetails: (Int) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        shape = if (isLastItem) RoundedCornerShape(
            bottomStart = 12.dp,
            bottomEnd = 12.dp
        ) else RectangleShape,
        onClick = { onOpenAlertDetails(alertDetails.id) },
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.weight(0.9f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = alertDetails.title ?: "",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = alertDetails.severity.toContainerColor()),
                        border = BorderStroke(1.dp, alertDetails.severity.toBorderColor()),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = alertDetails.severity.toStringField(),
                            color = alertDetails.severity.toContentColor(),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = alertDetails.description ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = formatRelativeTime(alertDetails.detectedAt),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
            Box(modifier = Modifier.weight(0.1f)) {
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
    if (!isLastItem) {
        HorizontalDivider(
            thickness = 1.dp,
            color = Color.LightGray
        )
    }
}

@Preview
@Composable
fun FieldDetailsScreenPreview() {
    AppTheme {
        val uiState = FieldDetailsUiState(
            fieldDetails = FakeFieldData.fields[2],
            filteredAlerts = FakeAlertData.alerts.filter { it.fieldId == FakeFieldData.fields[2].id }
        )
        val fieldDetails = uiState.fieldDetails
        val alerts = uiState.filteredAlerts


        Column {
            FieldDetailsTopBar(
                fieldDetails = fieldDetails!!,
                onBack = {}
            )
            FieldDetailsScreen(
                fieldDetails = fieldDetails,
                alerts = alerts,
                onOpenAlertDetails = {},
                onOpenAllAlerts = {},
                openStatistics = {}
            )
        }
    }
}