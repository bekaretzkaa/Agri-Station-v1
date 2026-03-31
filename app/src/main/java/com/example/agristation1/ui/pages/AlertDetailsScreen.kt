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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Opacity
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.PestControl
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import com.example.agristation1.data.formatRelativeTime
import com.example.agristation1.data.taskDetails.TaskDetails
import com.example.agristation1.data.taskDetails.TaskPriority
import com.example.agristation1.data.taskDetails.TaskType
import com.example.agristation1.data.taskDetails.toBorderColor
import com.example.agristation1.data.taskDetails.toContainerColor
import com.example.agristation1.data.taskDetails.toContentColor
import com.example.agristation1.fakedata.FakeAlertData
import com.example.agristation1.fakedata.FakeFieldData
import com.example.agristation1.fakedata.FakeTaskData
import com.example.agristation1.ui.viewmodel.AlertDetailsUiState
import com.example.agristation1.ui.viewmodel.AlertDetailsViewModel
import com.example.agristation1.ui.viewmodel.TaskFormState
import com.example.compose.AppTheme
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDetailsMainScreen(
    onBack: () -> Unit = {},
    viewModel: AlertDetailsViewModel,
    onOpenFieldDetails: (Int) -> Unit = {},
    onOpenTaskDetails: (Int) -> Unit = {},
    onDeleteAlert: (Int) -> Unit
) {
    val uiState: AlertDetailsUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val alertDetails = uiState.alertDetails ?: return

    val sheetState = rememberModalBottomSheetState()
    var showAddTaskFromAlert by remember { mutableStateOf(false) }
    var showDeleteAlert by remember { mutableStateOf(false) }

    if(showAddTaskFromAlert) {
        ModalBottomSheet(
            onDismissRequest = { showAddTaskFromAlert = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ) {
            AddTaskSheetFromAlert(
                uiState = uiState,
                formState = viewModel.state,
                onTitleChange = { viewModel.onTitleChange(it) },
                onDescriptionChange = { viewModel.onDescriptionChange(it) },
                onFieldChange = { viewModel.onFieldChange(it) },
                onPriorityChange = { viewModel.onPriorityChange(it) },
                onDueDateChange = { viewModel.onDateChange(it) },
                onDismiss = { showAddTaskFromAlert = false },
                onSaveTask = {
                    viewModel.addTask()
                    showAddTaskFromAlert = false
                },
                onTypeChange = { viewModel.onTypeChange(it) }
            )
        }
    } else if(showDeleteAlert) {
        DeleteAlertDetailsSheet (
            onDismiss = { showDeleteAlert = false },
            onDeleteAlert = {
                onDeleteAlert(alertDetails.id)
                showDeleteAlert = false
            }
        )
    }

    Column {
        AlertDetailsTopBar(
            alertDetails = alertDetails,
            onBack = onBack
        )
        AlertDetailsScreen(
            uiState = uiState,
            alertDetails = alertDetails,
            onMarkAsViewed = { viewModel.markAlertAsAcknowledged() },
            onUnMarkAsViewed = { viewModel.unMarkAlertAsAcknowledged() },
            onMarkAsResolved = { viewModel.markAlertAsResolved() },
            onMarkAsDismissed = { viewModel.markAlertAsDismissed() },
            onCreateTask = {
                viewModel.initializeState()
                showAddTaskFromAlert = true
            },
            onOpenTaskDetails = onOpenTaskDetails,
            onOpenFieldDetails = onOpenFieldDetails,
            onDeleteAlert = {
                showDeleteAlert = true
            }
        )
    }
}

@Composable
fun AlertDetailsTopBar(
    alertDetails: AlertDetails,
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
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        color = alertDetails.severity.toContainerColor(),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = when (alertDetails.type) {
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
                    tint = alertDetails.severity.toIconColor()
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = alertDetails.title ?: "",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
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
                    Spacer(modifier = Modifier.width(4.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = alertDetails.lifecycle.toContainerColor()),
                        border = BorderStroke(1.dp, alertDetails.lifecycle.toBorderColor()),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = alertDetails.lifecycle.toStringField(),
                            color = alertDetails.lifecycle.toContentColor(),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
fun AlertDetailsScreen(
    uiState: AlertDetailsUiState,
    alertDetails: AlertDetails,
    onMarkAsViewed: () -> Unit,
    onUnMarkAsViewed: () -> Unit,
    onMarkAsResolved: () -> Unit,
    onMarkAsDismissed: () -> Unit,
    onCreateTask: () -> Unit,
    onOpenTaskDetails: (Int) -> Unit,
    onOpenFieldDetails: (Int) -> Unit,
    onDeleteAlert: () -> Unit
) {
    val relatedTask = uiState.task

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp)
    ) {
        item {
            AlertDetailsInformation(
                uiState = uiState,
                alertDetails = alertDetails,
                onOpenFieldDetails = onOpenFieldDetails,
                onOpenTaskDetails = onOpenTaskDetails,
                relatedTask = relatedTask
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            AlertDetailsCurrentReading(
                alertDetails = alertDetails
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            AlertDetailsRecommendedAction(
                alertDetails = alertDetails
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            AlertDetailsButtons(
                uiState = uiState,
                alertDetails = alertDetails,
                onMarkAsViewed = onMarkAsViewed,
                onUnMarkAsViewed = onUnMarkAsViewed,
                relatedTask = relatedTask,
                onMarkAsResolved = onMarkAsResolved,
                onMarkAsDismissed = onMarkAsDismissed,
                onCreateTask = onCreateTask,
                onDeleteAlert = onDeleteAlert
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AlertDetailsInformation(
    uiState: AlertDetailsUiState,
    alertDetails: AlertDetails,
    onOpenFieldDetails: (Int) -> Unit,
    onOpenTaskDetails: (Int) -> Unit,
    relatedTask: TaskDetails?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
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
                Text(
                    text = "Alert Information",
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.LightGray
            )
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = alertDetails.description ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Detected",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.AccessTime,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = formatRelativeTime(alertDetails.detectedAt),
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column {
                        Text(
                            text = "Type",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = alertDetails.type.toStringField()
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Related Field",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    onClick = { onOpenFieldDetails(alertDetails.fieldId) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Place,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = uiState.field?.title ?: "Something Wrong",
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Outlined.OpenInNew,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if(relatedTask != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Related Task",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = relatedTask.status.toContainerColor()),
                        onClick = { onOpenTaskDetails(relatedTask.id) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CheckBox,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = relatedTask.title ?: "",
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Outlined.OpenInNew,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                if(alertDetails.type == AlertType.SENSOR) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Related Sensor",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "S4",
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun AlertDetailsCurrentReading(
    alertDetails: AlertDetails
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
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
                Text(
                    text = "Current Readings",
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.LightGray
            )
            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                when(alertDetails.type) {
                    AlertType.UNKNOWN, AlertType.GATE, AlertType.SENSOR -> {

                    }
                    else -> {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = alertDetails.severity.toContainerColor()),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Current value",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "${alertDetails.currentValue} ${alertDetails.unit}",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = alertDetails.lifecycle.toContentColor()
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Threshold",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "${alertDetails.threshold} ${alertDetails.unit}",
                                        style = MaterialTheme.typography.titleLarge,
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    ),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                    )
                                ) {
                                    append("Issue: ")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.Normal,
                                    )
                                ) {
                                    append("Current value is below the recommended threshold")
                                }
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun AlertDetailsRecommendedAction(
    alertDetails: AlertDetails
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = alertDetails.severity.toContainerColor()),
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
                Text(
                    text = "Recommended Action",
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.LightGray,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Immediate irrigation required. Prioritize affected zones in the northwest sector.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun AlertDetailsButtons(
    uiState: AlertDetailsUiState,
    alertDetails: AlertDetails,
    onMarkAsViewed: () -> Unit,
    onUnMarkAsViewed: () -> Unit,
    relatedTask: TaskDetails?,
    onMarkAsResolved: () -> Unit,
    onMarkAsDismissed: () -> Unit,
    onCreateTask: () -> Unit,
    onDeleteAlert: () -> Unit
) {

    if(relatedTask == null) {
        Card(
            colors = CardDefaults.cardColors(containerColor = AppColors.yellow.c200),
            onClick = onCreateTask
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = AppColors.yellow.c800
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Create Task from Alert",
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppColors.yellow.c800,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }

    if(alertDetails.lifecycle == AlertLifecycle.OPEN) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = AppColors.blue.c100
            ),
            onClick = { if(!uiState.isUpdating) onMarkAsViewed() }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircleOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Mark as Acknowledged",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    } else if (alertDetails.lifecycle == AlertLifecycle.ACKNOWLEDGED){
        Card(
            colors = CardDefaults.cardColors(
                containerColor = AppColors.blue.c100
            ),
            onClick = { if(!uiState.isUpdating) onUnMarkAsViewed() }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Circle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Mark as not Acknowledged",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }

    when(alertDetails.lifecycle) {
        AlertLifecycle.OPEN, AlertLifecycle.ACKNOWLEDGED -> {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                onClick = { onMarkAsResolved() }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircleOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mark as Resolved",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceDim),
                onClick = { onMarkAsDismissed() }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mark as Dismissed",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
        AlertLifecycle.DISMISSED -> {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceDim),
                onClick = { onUnMarkAsViewed() }
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceDim),
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Alert Dismissed",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "This alert has been marked as dismissed. Great work!",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceDim),
                onClick = { onUnMarkAsViewed() }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Circle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mark as not Dismissed",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
        AlertLifecycle.RESOLVED -> {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircleOutline,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Alert Resolved",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "This alert has been marked as resolved. Great work!",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = AppColors.green.c200),
                onClick = { onUnMarkAsViewed() }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Circle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mark as not Resolved",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        onClick = { onDeleteAlert() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Delete Alert",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AddTaskSheetFromAlert(
    uiState: AlertDetailsUiState,
    formState: TaskFormState,
    onDismiss: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onFieldChange: (Int) -> Unit,
    onPriorityChange: (TaskPriority) -> Unit,
    onDueDateChange: (LocalDate) -> Unit,
    onSaveTask: () -> Unit,
    onTypeChange: (TaskType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .background(color = MaterialTheme.colorScheme.surfaceContainerLowest),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "New Task",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = onDismiss
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                )
            }
        }
        HorizontalDivider(
            thickness = 1.dp,
            color = Color.LightGray
        )
        Card(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = AppColors.red.c100,
                contentColor = AppColors.red.c800
            )
        ) {
            Row(
                modifier = Modifier.padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.WarningAmber,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Task created from alert",
                        color = Color.Black
                    )
                    Text(
                        text = "The task details have been pre-filled based on the alert. You can modify them as needed.",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "Task Title *",
                style = MaterialTheme.typography.bodyLarge,
            )

            OutlinedTextField(
                value = formState.title ?: "",
                onValueChange = { onTitleChange(it) },
                label = { Text("Enter task title") },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "Description *",
                style = MaterialTheme.typography.bodyLarge,
            )

            OutlinedTextField(
                value = formState.description ?: "",
                onValueChange = { onDescriptionChange(it) },
                label = { Text("Describe the task in detail") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(12.dp)
            )
        }
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Flag,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Priority *",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = { onPriorityChange(TaskPriority.HIGH) },
                    shape = RoundedCornerShape(12.dp),
                    colors = if(formState.priority == TaskPriority.HIGH) ButtonDefaults.buttonColors(
                        containerColor = formState.priority.toContainerColor(),
                        contentColor = formState.priority.toContentColor(),
                    ) else ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceBright,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(1.dp, if(formState.priority == TaskPriority.HIGH) formState.priority.toBorderColor() else Color.LightGray),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Flag,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "High"
                        )
                    }
                }
                OutlinedButton(
                    onClick = { onPriorityChange(TaskPriority.MEDIUM) },
                    shape = RoundedCornerShape(12.dp),
                    colors = if(formState.priority == TaskPriority.MEDIUM) ButtonDefaults.buttonColors(
                        containerColor = formState.priority.toContainerColor(),
                        contentColor = formState.priority.toContentColor(),
                    ) else ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceBright,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(1.dp, if(formState.priority == TaskPriority.MEDIUM) formState.priority.toBorderColor() else Color.LightGray),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Flag,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Medium"
                        )
                    }
                }
                OutlinedButton(
                    onClick = { onPriorityChange(TaskPriority.LOW) },
                    shape = RoundedCornerShape(12.dp),
                    colors = if(formState.priority == TaskPriority.LOW) ButtonDefaults.buttonColors(
                        containerColor = formState.priority.toContainerColor(),
                        contentColor = formState.priority.toContentColor(),
                    ) else ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceBright,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(1.dp, if(formState.priority == TaskPriority.LOW) formState.priority.toBorderColor() else Color.LightGray),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Flag,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Low"
                        )
                    }
                }
            }
        }

        FieldDropDownAddFromAlert(
            uiState = uiState,
            formState = formState,
            onFieldChange = onFieldChange
        )

        TaskTypeDropDown(
            formState = formState,
            onTypeChange = onTypeChange
        )

        DueDateFieldWithDialog(
            formState = formState,
            onDueDateChange = onDueDateChange
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = Color.LightGray
        )
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                onClick = onSaveTask,
                enabled = true,
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Save,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Create Task",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldDropDownAddFromAlert(
    uiState: AlertDetailsUiState,
    formState: TaskFormState,
    onFieldChange: (Int) -> Unit
) {
    val options = uiState.fields
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(uiState.fields.find { it.id == formState.fieldId }) }

    Column(
        modifier = Modifier.padding(12.dp)
    ) {
        Row {
            Icon(
                imageVector = Icons.Outlined.Place,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Related Field *",
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedOption?.title ?: "",
                onValueChange = { },
                readOnly = true,
                placeholder = { Text("Select a field") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.title ?: "") },
                        onClick = {
                            selectedOption = option
                            onFieldChange(option.id)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteAlertDetailsSheet(
    onDismiss: () -> Unit,
    onDeleteAlert: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f).background(
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    shape = RoundedCornerShape(12.dp)
                ),
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Delete Alert",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = onDismiss
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                    )
                }
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.LightGray
            )
            Column(
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 20.dp).height(100.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(horizontalArrangement = Arrangement.Center) {
                    Box(
                        modifier = Modifier.background(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.errorContainer
                        ).size(50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.WarningAmber,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Are you sure you want to delete this alert? This action cannot be undone.",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.LightGray
            )
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    onClick = onDeleteAlert,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Delete",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview
@Composable
fun AlertDetailsScreenPreview() {
    AppTheme {
        val uiState = AlertDetailsUiState(
            alertDetails = FakeAlertData.alerts[0],
            field = FakeFieldData.fields.firstOrNull { it.id == FakeAlertData.alerts[0].fieldId},
            task = FakeTaskData.tasks.firstOrNull { it.alertId == FakeAlertData.alerts[0].id }
        )
        val alertDetails = uiState.alertDetails

        Column {
            AlertDetailsTopBar(
                alertDetails = alertDetails!!,
                onBack = {}
            )
            AlertDetailsScreen(
                uiState = uiState,
                alertDetails = alertDetails,
                onMarkAsViewed = {},
                onUnMarkAsViewed = {},
                onMarkAsResolved = {},
                onMarkAsDismissed = {},
                onCreateTask = {},
                onOpenFieldDetails = {},
                onOpenTaskDetails = {},
                onDeleteAlert = {}
            )
        }
    }
}

@Preview
@Composable
fun AddFromAlertTaskSheetPreview() {
    AppTheme {
        AddTaskSheetFromAlert(
            uiState = AlertDetailsUiState(
                alertDetails = FakeAlertData.alerts[0],
                field = FakeFieldData.fields[2],
                task = FakeTaskData.tasks[0],
                fields = FakeFieldData.fields
            ),
            formState = TaskFormState(),
            onTitleChange = {},
            onDescriptionChange = {},
            onFieldChange = {},
            onPriorityChange = {},
            onDueDateChange = {},
            onDismiss = {},
            onSaveTask = {},
            onTypeChange = {}
        )
    }
}

@Preview
@Composable
fun DeleteAlertDetailsSheetPreview() {
    AppTheme {
        DeleteAlertDetailsSheet(
            onDismiss = {},
            onDeleteAlert = {}
        )
    }
}