package com.example.agristation1.ui.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.agristation1.data.fieldDetails.FieldDetails
import com.example.agristation1.data.fieldDetails.FieldHealth
import com.example.agristation1.data.formatRelativeTime
import com.example.agristation1.fakedata.FakeAlertData
import com.example.agristation1.fakedata.FakeFarmData
import com.example.agristation1.fakedata.FakeFieldData
import com.example.agristation1.fakedata.FakeTaskData
import com.example.agristation1.ui.viewmodel.HomeUiState
import com.example.agristation1.ui.viewmodel.HomeViewModel
import com.example.compose.AppTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeMainScreen(
    viewModel: HomeViewModel,
    onFieldClick: (Int) -> Unit = {},
    onOpenAllFields: () -> Unit = {},
    onChatClick: () -> Unit = {}
) {
    val uiState: HomeUiState by viewModel.uiState.collectAsState()

    Column {
        HomeTopBar(
            uiState = uiState,
            onChatClick = onChatClick
        )
        HomeScreen(
            uiState = uiState,
            onFieldClick = onFieldClick,
            onOpenAllFields = onOpenAllFields,
            getTotalAlerts = { uiState.alerts.count { alertDetails -> alertDetails.fieldId == it } }
        )
    }
}

@Composable
fun HomeTopBar(
    uiState: HomeUiState,
    onChatClick: () -> Unit
) {

    val currentDate = Instant.now()
        .atZone(ZoneId.systemDefault())
        .format(
            DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale("en"))
        )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
            .statusBarsPadding()
            .padding(start = 12.dp, bottom = 12.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = uiState.farmDetails?.farmName ?: "",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = currentDate, style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Last updated: ${
                    if (uiState.farmDetails?.lastUpdate != null) formatRelativeTime(
                        uiState.farmDetails.lastUpdate
                    ) else ""
                }", style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = onChatClick,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.ChatBubbleOutline,
                contentDescription = null
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onFieldClick: (Int) -> Unit,
    onOpenAllFields: () -> Unit,
    getTotalAlerts: (Int) -> Int,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
    ) {
        item {
            OverallInformation(
                uiState = uiState
            )
            Spacer(modifier = Modifier.height(16.dp))
            ImmediateAttentionHeader()
        }
        itemsIndexed(uiState.attentionFields) { index, item ->
            val isLast = index == uiState.attentionFields.lastIndex
            ImmediateAttentionInformationCard(item, isLast, onFieldClick, getTotalAlerts(item.id))
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            FieldsOverviewHeader(
                onOpenAllFields = onOpenAllFields
            )
        }
        itemsIndexed(uiState.fields) { index, item ->
            val isLast = index == uiState.fields.lastIndex
            FieldsOverviewInformationCard(item, isLast, onFieldClick, getTotalAlerts(item.id))
        }
    }
}

@Composable
fun OverallInformation(
    uiState: HomeUiState, modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            InformationCard(
                title = "Fields",
                icon = Icons.Outlined.Layers,
                iconColor = Color(0xFF6367FF),
                value = uiState.fields.size,
                text = {
                    Text(
                        text = "${uiState.attentionFields.size} need attention",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.size(16.dp))
            InformationCard(
                title = "Active Sensors",
                icon = Icons.Outlined.MonitorHeart,
                iconColor = Color.Green,
                value = uiState.farmDetails?.activeSensors ?: 0,
                text = {
                    Text(
                        text = "of ${uiState.farmDetails?.totalSensors ?: 0} total",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            InformationCard(
                title = "Active Tasks",
                icon = Icons.Outlined.CheckBox,
                iconColor = Color(0xFF6367FF),
                value = uiState.tasks.size,
                text = {
                    Text(
                        text = "${uiState.tasks.size}} need attention",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.size(16.dp))
            InformationCard(
                title = "Alerts",
                icon = Icons.Outlined.WarningAmber,
                iconColor = Color.Yellow,
                value = uiState.alerts.size,
                text = {
                    Text(
                        text = "${uiState.alerts.size} critical",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun InformationCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    value: Int,
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = BorderStroke(1.dp, Color.LightGray)
        ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row {
                Text(text = title)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = icon, contentDescription = null, tint = iconColor
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value.toString(), style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            text()
        }
    }
}

@Composable
fun ImmediateAttentionHeader(
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.errorContainer)
                .padding(12.dp)
                .height(30.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = "warning",
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Requires Immediate Attention",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
fun ImmediateAttentionInformationCard(
    item: FieldDetails,
    isLastItem: Boolean,
    onFieldClick: (Int) -> Unit,
    totalAlerts: Int,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        shape = if (isLastItem) RoundedCornerShape(
            bottomStart = 12.dp, bottomEnd = 12.dp
        ) else RectangleShape,
        onClick = { onFieldClick(item.id) },
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.title ?: "", style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Card(colors = CardDefaults.cardColors()) {
//                    Text(
//                        text = item.color.toStringField(),
//                        color = item.color.toContentColor(),
//                        style = MaterialTheme.typography.bodySmall,
//                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
//                        fontWeight = FontWeight.Bold
//                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.WaterDrop,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(20.dp)
                )
                Text(
                    text = "${item.soilMoisture}%", style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "$totalAlerts alerts", style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
    if (!isLastItem) {
        HorizontalDivider(
            thickness = 1.dp, color = Color.LightGray
        )
    }
}

@Composable
fun FieldsOverviewHeader(
    onOpenAllFields: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .height(40.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fields Overview",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = onOpenAllFields
                ) {
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

            }
            HorizontalDivider(
                thickness = 1.dp, color = Color.LightGray
            )
        }
    }
}

@Composable
fun FieldsOverviewInformationCard(
    item: FieldDetails,
    isLastItem: Boolean,
    onFieldClick: (Int) -> Unit,
    totalAlerts: Int,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        shape = if (isLastItem) RoundedCornerShape(
            bottomStart = 12.dp, bottomEnd = 12.dp
        ) else RectangleShape,
        onClick = { onFieldClick(item.id) },
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.title ?: "", style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${item.area} ha",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.WaterDrop,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(20.dp)
                )
                Text(
                    text = "${item.soilMoisture}%", style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = item.type ?: "", style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = if (totalAlerts == 0) "" else "$totalAlerts alerts",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
    if (!isLastItem) {
        HorizontalDivider(
            thickness = 1.dp, color = Color.LightGray
        )
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    AppTheme {
        val uiState = HomeUiState(
            farmDetails = FakeFarmData.farmDetails,
            fields = FakeFieldData.fields,
            alerts = FakeAlertData.alerts,
            attentionFields = FakeFieldData.fields.filter { it.health == FieldHealth.CRITICAL || it.health == FieldHealth.WARNING },
            tasks = FakeTaskData.tasks
        )

        Column {
            HomeTopBar(
                uiState = uiState,
                {}
            )
            HomeScreen(
                uiState = uiState,
                onFieldClick = {},
                onOpenAllFields = {},
                getTotalAlerts = { FakeAlertData.alerts.filter { alertDetails -> alertDetails.fieldId == it }.size }
            )
        }
    }
}