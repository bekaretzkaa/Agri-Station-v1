package com.example.agristation1.ui.pages

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Start
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.outlined.WaterDrop
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.agristation1.data.AppColors
import com.example.agristation1.data.alertDetails.toContainerColor
import com.example.agristation1.data.alertDetails.toContentColor
import com.example.agristation1.data.alertDetails.toStringField
import com.example.agristation1.data.taskDetails.TaskDetails
import com.example.agristation1.data.taskNotes.TaskNotes
import com.example.agristation1.data.formatRelativeTime
import com.example.agristation1.data.taskDetails.TaskPriority
import com.example.agristation1.data.taskDetails.TaskStatus
import com.example.agristation1.data.taskDetails.TaskType
import com.example.agristation1.data.taskDetails.toBorderColor
import com.example.agristation1.data.taskDetails.toContainerColor
import com.example.agristation1.data.taskDetails.toContentColor
import com.example.agristation1.data.taskDetails.toStringField
import com.example.agristation1.data.toUiDueDate
import com.example.agristation1.fakedata.FakeAlertData
import com.example.agristation1.fakedata.FakeFieldData
import com.example.agristation1.fakedata.FakeTaskData
import com.example.agristation1.fakedata.FakeTaskNotesData
import com.example.agristation1.ui.viewmodel.TaskDetailsUiState
import com.example.agristation1.ui.viewmodel.TaskDetailsViewModel
import com.example.agristation1.ui.viewmodel.TaskFilter
import com.example.agristation1.ui.viewmodel.TaskFormState
import com.example.agristation1.ui.viewmodel.TaskUiState
import com.example.compose.AppTheme
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsMainScreen(
    onBack: () -> Unit = {},
    viewModel: TaskDetailsViewModel,
    onOpenFieldDetails: (Int) -> Unit = {},
    onOpenAlertDetails: (Int) -> Unit = {},
    onDeleteTask: (Int) -> Unit = {}
) {
    val uiState: TaskDetailsUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val taskDetails = uiState.taskDetails ?: return

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheetAddNote by remember { mutableStateOf(false) }
    var showBottomSheetUpdateNote by remember { mutableStateOf(false) }
    var showAlertSheetDeleteNote by remember { mutableStateOf(false) }
    var showAlertSheetDeleteTaskDetails by remember { mutableStateOf(false) }

    var showBottomSheetUpdateTask by remember { mutableStateOf(false) }

    var note by remember { mutableStateOf("") }
    var noteId by remember { mutableStateOf(0) }

    if (showBottomSheetAddNote) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheetAddNote = false },
            sheetState = sheetState
        ) {
            AddNoteSheet(
                onDismiss = { showBottomSheetAddNote = false },
                onAddNote = {
                    viewModel.insertTaskNote(taskDetails.id, it)
                    showBottomSheetAddNote = false
                }
            )
        }
    } else if(showBottomSheetUpdateNote) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheetUpdateNote = false },
            sheetState = sheetState
        ) {
            UpdateNoteSheet(
                onDismiss = { showBottomSheetUpdateNote = false },
                onUpdateNote = {
                    viewModel.updateTaskNote(noteId, it)
                    showBottomSheetUpdateNote = false
                },
                taskNote = note
            )
        }
    } else if(showAlertSheetDeleteNote) {
        DeleteNoteSheet(
            onDismiss = { showAlertSheetDeleteNote = false },
            onDeleteNote = {
                viewModel.deleteTaskNote(noteId)
                showAlertSheetDeleteNote = false
            }
        )
    } else if(showAlertSheetDeleteTaskDetails) {
        DeleteTaskDetailsSheet(
            onDismiss = { showAlertSheetDeleteTaskDetails = false },
            onDeleteTask = {
                onDeleteTask(taskDetails.id)
                showAlertSheetDeleteTaskDetails = false
            }
        )
    } else if(showBottomSheetUpdateTask) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheetUpdateTask = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        ) {
            UpdateTaskSheet(
                uiState = uiState,
                formState = viewModel.state,
                onTitleChange = viewModel::onTitleChange,
                onDescriptionChange = viewModel::onDescriptionChange,
                onFieldChange = viewModel::onFieldChange,
                onPriorityChange = viewModel::onPriorityChange,
                onDueDateChange = viewModel::onDateChange,
                onDismiss = { showBottomSheetUpdateTask = false },
                onSaveTask = {
                    viewModel.updateTask()
                    showBottomSheetUpdateTask = false
                },
                onTypeChange = {
                    viewModel.onTypeChange(it)
                }
            )
        }
    }

    Column {
        TaskDetailsTopBar(
            taskDetails = taskDetails,
            onBack = onBack
        )
        TaskDetailsScreen(
            taskDetails = taskDetails,
            uiState = uiState,
            onOpenFieldDetails = onOpenFieldDetails,
            onOpenAlertDetails = onOpenAlertDetails,
            onMarkAsCompleted = { viewModel.markTaskAsCompleted(taskDetails.id) },
            onDeleteTaskDetails = { showAlertSheetDeleteTaskDetails = true },
            onAddNote = { showBottomSheetAddNote = true },
            onUnMarkAsCompleted = { viewModel.unMarkTaskAsCompleted(taskDetails.id) },
            onDeleteTaskNote = {
                noteId = it
                showAlertSheetDeleteNote = true
                               },
            onUpdateTaskNote = {
                note = it.note
                noteId = it.id
                showBottomSheetUpdateNote = true
            },
            onEditTask = {
                viewModel.initializeState()
                showBottomSheetUpdateTask = true
            },
            onStartTask = { viewModel.markTaskAsStarted(taskDetails.id) }
        )
    }
}

@Composable
fun TaskDetailsTopBar(
    taskDetails: TaskDetails,
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
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = when (taskDetails.status) {
                    TaskStatus.COMPLETED -> Icons.Outlined.CheckCircle
                    TaskStatus.IN_PROGRESS -> Icons.Outlined.AccessTime
                    TaskStatus.OPEN -> Icons.Outlined.Circle
                    TaskStatus.OVERDUE -> Icons.Outlined.ErrorOutline
                    TaskStatus.CANCELLED -> Icons.Outlined.Cancel
                },
                contentDescription = null,
                tint = when (taskDetails.status) {
                    TaskStatus.COMPLETED -> AppColors.green.c600
                    TaskStatus.IN_PROGRESS -> AppColors.blue.c600
                    TaskStatus.CANCELLED -> AppColors.gray.c600
                    TaskStatus.OVERDUE -> AppColors.red.c600
                    TaskStatus.OPEN -> AppColors.gray.c600
                }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = taskDetails.title ?: "",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (taskDetails.status == TaskStatus.COMPLETED)
                        Color.Gray
                    else
                        Color.Black,
                    modifier = if (taskDetails.status == TaskStatus.COMPLETED)
                        Modifier.drawBehind {
                            val strokeWidth = 2.dp.toPx()
                            val y = size.height / 2

                            drawLine(
                                color = Color.Black,
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = strokeWidth
                            )
                        }
                    else
                        Modifier
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = taskDetails.priority.toContainerColor()),
                        border = BorderStroke(1.dp, taskDetails.priority.toBorderColor()),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = taskDetails.priority.toStringField(),
                            color = taskDetails.priority.toContentColor(),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = taskDetails.status.toContainerColor()),
                        border = BorderStroke(1.dp, taskDetails.status.toBorderColor()),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = taskDetails.status.toStringField(),
                            color = taskDetails.status.toContentColor(),
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
fun TaskDetailsScreen(
    taskDetails: TaskDetails,
    uiState: TaskDetailsUiState,
    onOpenFieldDetails: (Int) -> Unit,
    onOpenAlertDetails: (Int) -> Unit,
    onMarkAsCompleted: () -> Unit,
    onDeleteTaskDetails: () -> Unit,
    onAddNote: () -> Unit,
    onUnMarkAsCompleted: () -> Unit,
    onDeleteTaskNote: (Int) -> Unit,
    onUpdateTaskNote: (TaskNotes) -> Unit,
    onEditTask: () -> Unit,
    onStartTask: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp)
    ) {
        item {
            TaskDetailsInformation(
                taskDetails = taskDetails,
                uiState = uiState,
                onOpenFieldDetails = onOpenFieldDetails,
                onOpenAlertDetails = onOpenAlertDetails
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (taskDetails.status == TaskStatus.COMPLETED) {
            item {
                TaskDetailsCompleted()
            }
        } else {
            item {
                TaskDetailsButtons(
                    taskDetails = taskDetails,
                    onMarkAsCompleted = onMarkAsCompleted,
                    onEditTask = onEditTask,
                    onStartTask = onStartTask
                )
            }
        }

        item {
            TaskDetailsOtherButtons(
                taskDetails = taskDetails,
                onDeleteTaskDetails = onDeleteTaskDetails,
                onUnMarkAsCompleted = onUnMarkAsCompleted
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            TaskDetailsNotes(
                uiState = uiState,
                onAddNote = onAddNote,
                onDeleteTaskNote = onDeleteTaskNote,
                onUpdateTaskNote = onUpdateTaskNote
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TaskDetailsInformation(
    taskDetails: TaskDetails,
    uiState: TaskDetailsUiState,
    onOpenFieldDetails: (Int) -> Unit,
    onOpenAlertDetails: (Int) -> Unit
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
                    text = "Task Details",
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.weight(1f))
                if(taskDetails.type != TaskType.UNKNOWN) {
                    Text(
                        text = "Type: ${taskDetails.type.toStringField()}",
                        style = MaterialTheme.typography.bodyLarge
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
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = taskDetails.description ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Created",
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
                                text = formatRelativeTime(taskDetails.timeCreated),
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column {
                        Text(
                            text = "Due Date",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = taskDetails.timeDue?.toUiDueDate() ?: "No date",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
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
                    onClick = { onOpenFieldDetails(taskDetails.fieldId) }
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
                            text = uiState.field?.title ?: "Unknown field",
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
                if (uiState.alert != null && taskDetails.status != TaskStatus.COMPLETED) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Related Alert",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = uiState.alert.severity.toContainerColor()),
                        onClick = { onOpenAlertDetails(uiState.alert.id) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.WarningAmber,
                                contentDescription = null,
                                tint = uiState.alert.severity.toContentColor()
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = uiState.alert.title ?: "",
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

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun TaskDetailsButtons(
    taskDetails: TaskDetails,
    onMarkAsCompleted: () -> Unit,
    onEditTask: () -> Unit,
    onStartTask: () -> Unit
) {
    if(taskDetails.status == TaskStatus.OPEN) {
        Card(
            colors = CardDefaults.cardColors(containerColor = AppColors.blue.c200),
            onClick = onStartTask,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Start Task",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        onClick = onMarkAsCompleted
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
                text = "Mark as Completed",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
    Card(
        colors = CardDefaults.cardColors(containerColor = AppColors.yellow.c200),
        onClick = onEditTask
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Edit Task",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TaskDetailsOtherButtons(
    taskDetails: TaskDetails,
    onDeleteTaskDetails: () -> Unit,
    onUnMarkAsCompleted: () -> Unit
) {
    if (taskDetails.status == TaskStatus.COMPLETED) {
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = AppColors.green.c200),
            onClick = onUnMarkAsCompleted
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Mark as not Completed",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        onClick = { onDeleteTaskDetails() }
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
                text = "Delete Task",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TaskDetailsCompleted() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
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
                    text = "Task Completed",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "This task has been marked as completed. Great work!",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskDetailsNotes(
    uiState: TaskDetailsUiState,
    onAddNote: () -> Unit,
    onDeleteTaskNote: (Int) -> Unit,
    onUpdateTaskNote: (TaskNotes) -> Unit
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
                    text = "Notes & Comments",
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(
                            color = AppColors.blue.c200,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.notes.size.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = AppColors.blue.c800
                    )
                }

            }
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.LightGray
            )
            if (uiState.notes.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                ) {
                    items(uiState.notes) { note ->
                        TaskDetailsNotesCard(
                            taskNote = note,
                            onDeleteTaskNote = onDeleteTaskNote,
                            onUpdateTaskNote = onUpdateTaskNote
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No notes yet",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
            Button(
                modifier = Modifier
                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                onClick = onAddNote
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add Note",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun TaskDetailsNotesCard(
    taskNote: TaskNotes,
    onDeleteTaskNote: (Int) -> Unit,
    onUpdateTaskNote: (TaskNotes) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = Modifier.padding(12.dp)
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = taskNote.note,
                    style = MaterialTheme.typography.bodyLarge
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatRelativeTime(taskNote.createdAt),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        modifier = Modifier.size(26.dp),
                        onClick = { onUpdateTaskNote(taskNote) }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        modifier = Modifier.size(26.dp),
                        onClick = { onDeleteTaskNote(taskNote.id) }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddNoteSheet(
    onDismiss: () -> Unit,
    onAddNote: (String) -> Unit
) {
    var note by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Add Note",
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
            modifier = Modifier.padding(12.dp)
        ) {
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Type your note or comment here...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(12.dp)
            )
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
                onClick = { onAddNote(note) },
                enabled = note.isNotBlank()
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Send,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Post Note",
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

@Composable
fun UpdateNoteSheet(
    onDismiss: () -> Unit,
    onUpdateNote: (String) -> Unit,
    taskNote: String
) {
    var note by remember { mutableStateOf(taskNote) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Edit Note",
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
            modifier = Modifier.padding(12.dp)
        ) {
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Type your note or comment here...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(12.dp)
            )
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
                onClick = { onUpdateNote(note) },
                enabled = note.isNotBlank()
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Send,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Update Note",
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

@Composable
fun DeleteNoteSheet(
    onDismiss: () -> Unit,
    onDeleteNote: () -> Unit
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
                    text = "Delete Note",
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
                        text = "Are you sure you want to delete this note? This action cannot be undone.",
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
                    onClick = onDeleteNote,
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

@Composable
fun DeleteTaskDetailsSheet(
    onDismiss: () -> Unit,
    onDeleteTask: () -> Unit
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
                    text = "Delete Task",
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
                        text = "Are you sure you want to delete this task? This action cannot be undone.",
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
                    onClick = onDeleteTask,
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

@Composable
fun UpdateTaskSheet(
    uiState: TaskDetailsUiState,
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
                text = "Edit Task",
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
        if(uiState.taskDetails?.alertId != null) {
            Card(
                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.red.c100,
                    contentColor = AppColors.red.c800
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.WarningAmber,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Task created from alert",
                        color = Color.Black
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

        if(uiState.taskDetails?.alertId == null) {
            FieldDropDownUpdate(
                uiState = uiState,
                formState = formState,
                onFieldChange = onFieldChange
            )
        }

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
                enabled = !(formState.title?.isBlank() ?: false || formState.description?.isBlank() ?: false || formState.fieldId == -1),
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
                        text = "Save Changes",
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
                    containerColor = MaterialTheme.colorScheme.surfaceDim,
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
fun FieldDropDownUpdate(
    uiState: TaskDetailsUiState,
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
                onValueChange = {},
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TaskDetailsScreenPreview() {
    AppTheme {
        val uiState = TaskDetailsUiState(
            taskDetails = FakeTaskData.tasks[1],
            field = FakeFieldData.fields[2],
            alert = FakeAlertData.alerts[2],
            notes = FakeTaskNotesData.taskNotes.filter { it.taskId == 1 }
        )

        Column {
            TaskDetailsTopBar(
                taskDetails = uiState.taskDetails!!,
                onBack = {}
            )
            TaskDetailsScreen(
                taskDetails = uiState.taskDetails,
                uiState = uiState,
                onOpenFieldDetails = {},
                onOpenAlertDetails = {},
                onMarkAsCompleted = {},
                onDeleteTaskDetails = {},
                onAddNote = {},
                onUnMarkAsCompleted = {},
                onDeleteTaskNote = {},
                onUpdateTaskNote = {},
                onEditTask = {},
                onStartTask = {}
            )
        }
    }
}

@Preview
@Composable
fun AddNoteSheetPreview() {
    AppTheme {
        AddNoteSheet(
            onDismiss = {},
            onAddNote = {}
        )
    }
}

@Preview
@Composable
fun UpdateNoteSheetPreview() {
    AppTheme {
        UpdateNoteSheet(
            onDismiss = {},
            onUpdateNote = {},
            taskNote = FakeTaskNotesData.taskNotes[0].note
        )
    }
}

@Preview
@Composable
fun DeleteNoteSheetPreview() {
    AppTheme {
        DeleteNoteSheet(
            onDismiss = {},
            onDeleteNote = {}
        )
    }
}

@Preview
@Composable
fun DeleteTaskDetailsSheetPreview() {
    AppTheme {
        DeleteTaskDetailsSheet(
            onDismiss = {},
            onDeleteTask = {}
        )
    }
}

@Preview
@Composable
fun UpdateTaskSheetPreview() {
    AppTheme {
        UpdateTaskSheet(
            uiState = TaskDetailsUiState(
                taskDetails = FakeTaskData.tasks[1],
                field = FakeFieldData.fields[2],
                alert = FakeAlertData.alerts[2],
                notes = FakeTaskNotesData.taskNotes,
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