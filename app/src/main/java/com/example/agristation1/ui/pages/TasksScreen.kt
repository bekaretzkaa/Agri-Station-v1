package com.example.agristation1.ui.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.TableRows
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.agristation1.data.AppColors
import com.example.agristation1.data.alertDetails.AlertLifecycle
import com.example.agristation1.data.alertDetails.toBorderColor
import com.example.agristation1.data.alertDetails.toContainerColor
import com.example.agristation1.data.formatRelativeTime
import com.example.agristation1.data.taskDetails.TaskDetails
import com.example.agristation1.data.taskDetails.TaskPriority
import com.example.agristation1.data.taskDetails.TaskStatus
import com.example.agristation1.data.taskDetails.TaskType
import com.example.agristation1.data.taskDetails.toBorderColor
import com.example.agristation1.data.taskDetails.toContainerColor
import com.example.agristation1.data.taskDetails.toContentColor
import com.example.agristation1.data.taskDetails.toStringField
import com.example.agristation1.fakedata.FakeFieldData
import com.example.agristation1.fakedata.FakeTaskData
import com.example.agristation1.ui.viewmodel.TaskFilter
import com.example.agristation1.ui.viewmodel.TaskFormState
import com.example.agristation1.ui.viewmodel.TaskUiState
import com.example.agristation1.ui.viewmodel.TaskViewModel
import com.example.compose.AppTheme
import okhttp3.internal.concurrent.Task
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksMainScreen(
    viewModel: TaskViewModel,
    onTaskClick: (Long) -> Unit = {}
) {

    LaunchedEffect(Unit) {
        viewModel.checkAndMarkAsOverdueTask()
    }

    val uiState: TaskUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val refreshError by viewModel.refreshError.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheetAddTask by remember { mutableStateOf(false) }

    if (showBottomSheetAddTask) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheetAddTask = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ) {
            AddTaskSheet(
                uiState = uiState,
                formState = viewModel.state,
                onTitleChange = viewModel::onTitleChange,
                onDescriptionChange = viewModel::onDescriptionChange,
                onFieldChange = viewModel::onFieldChange,
                onPriorityChange = viewModel::onPriorityChange,
                onDueDateChange = viewModel::onDateChange,
                onDismiss = { showBottomSheetAddTask = false },
                onAddTask = {
                    viewModel.addTask()
                    showBottomSheetAddTask = false
                },
                onTypeChange = {
                    viewModel.onTypeChange(it)
                }
            )
        }
    }

    LaunchedEffect(refreshError) {
        refreshError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearRefreshError()
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            TasksTopBar(
                uiState = uiState,
                onFilterChange = { viewModel.onFilterChange(it) },
                onOpenNewTask = { showBottomSheetAddTask = true }
            )
            TasksScreen(
                uiState = uiState,
                onTaskClick = onTaskClick,
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.refresh() }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
fun TasksTopBar(
    uiState: TaskUiState,
    onFilterChange: (TaskFilter) -> Unit,
    onOpenNewTask: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
            .statusBarsPadding()
            .padding(start = 12.dp, bottom = 12.dp, end = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Tasks",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onOpenNewTask,
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "New Task")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            item {
                Button(
                    onClick = { onFilterChange(TaskFilter.All) },
                    modifier = Modifier.height(35.dp),
                    contentPadding = PaddingValues(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.selectedFilter == TaskFilter.All) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary,
                        contentColor = if (uiState.selectedFilter == TaskFilter.All) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
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
                    onClick = { onFilterChange(TaskFilter.High) },
                    modifier = Modifier.height(35.dp),
                    contentPadding = PaddingValues(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.selectedFilter == TaskFilter.High) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary,
                        contentColor = if (uiState.selectedFilter == TaskFilter.High) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "High (${uiState.highCount})",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            item {
                Button(
                    onClick = { onFilterChange(TaskFilter.Medium) },
                    modifier = Modifier.height(35.dp),
                    contentPadding = PaddingValues(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.selectedFilter == TaskFilter.Medium) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary,
                        contentColor = if (uiState.selectedFilter == TaskFilter.Medium) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Medium (${uiState.mediumCount})",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            item {
                Button(
                    onClick = { onFilterChange(TaskFilter.Low) },
                    modifier = Modifier.height(35.dp),
                    contentPadding = PaddingValues(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.selectedFilter == TaskFilter.Low) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary,
                        contentColor = if (uiState.selectedFilter == TaskFilter.Low) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Low (${uiState.lowCount})",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(0.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    uiState: TaskUiState,
    onTaskClick: (Long) -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh
    ) {

        if(uiState.filteredTasks.isEmpty() && uiState.archivedTasks.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No tasks",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surface),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
            ) {
                items(uiState.filteredTasks) { item ->
                    TaskInformationCard(
                        item,
                        onTaskClick,
                        uiState
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                }

                if(uiState.archivedTasks.isNotEmpty()) {
                    item {
                        Text(
                            text = "Archived Tasks",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    items(uiState.archivedTasks) { item ->
                        TaskInformationCard(
                            item,
                            onTaskClick,
                            uiState
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TaskInformationCard(
    item: TaskDetails,
    onTaskClick: (Long) -> Unit,
    uiState: TaskUiState,
) {
    val stripeWidth = 6.dp
    val cardShape = RoundedCornerShape(16.dp)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = if(item.status != TaskStatus.CANCELLED && item.status != TaskStatus.COMPLETED) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            )
        } else {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                contentColor = Color.Gray
            )
        },
        onClick = { onTaskClick(item.id) },
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
                        .background(color = when(item.status) {
                            TaskStatus.COMPLETED -> AppColors.green.c200
                            TaskStatus.CANCELLED -> AppColors.gray.c200
                            else -> item.status.toBorderColor()
                        })
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
                Column {
                    Row {
                        Column {
                            Row {
                                Spacer(modifier = Modifier.height(4.dp))
                                Icon(
                                    imageVector = when (item.status) {
                                        TaskStatus.COMPLETED -> Icons.Outlined.CheckCircle
                                        TaskStatus.IN_PROGRESS -> Icons.Outlined.AccessTime
                                        TaskStatus.OPEN -> Icons.Outlined.Circle
                                        TaskStatus.OVERDUE -> Icons.Outlined.ErrorOutline
                                        TaskStatus.CANCELLED -> Icons.Outlined.Cancel
                                    },
                                    contentDescription = null,
                                    tint = when (item.status) {
                                        TaskStatus.COMPLETED -> AppColors.green.c600
                                        TaskStatus.IN_PROGRESS -> AppColors.blue.c600
                                        TaskStatus.CANCELLED -> AppColors.gray.c600
                                        TaskStatus.OVERDUE -> AppColors.red.c600
                                        TaskStatus.OPEN -> AppColors.gray.c600
                                    }
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.fillMaxWidth()) {

                                    // 1 line: title + priority
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.Top
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
                                                containerColor = if (
                                                    item.status != TaskStatus.COMPLETED &&
                                                    item.status != TaskStatus.CANCELLED
                                                ) item.priority.toContainerColor() else AppColors.gray.c100
                                            ),
                                            border = BorderStroke(
                                                1.dp,
                                                if (
                                                    item.status != TaskStatus.COMPLETED &&
                                                    item.status != TaskStatus.CANCELLED
                                                ) item.priority.toBorderColor() else AppColors.gray.c200
                                            ),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = item.priority.toStringField(),
                                                color = if (
                                                    item.status != TaskStatus.COMPLETED &&
                                                    item.status != TaskStatus.CANCELLED
                                                ) item.priority.toContentColor() else AppColors.gray.c800,
                                                style = MaterialTheme.typography.bodySmall,
                                                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // 2 line: field + from alert + status
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = uiState.fields.find { it.id == item.fieldId }?.title ?: "Unknown Field",
                                                style = MaterialTheme.typography.bodyLarge,
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = item.status.toContainerColor()),
                                            border = BorderStroke(1.dp, item.status.toBorderColor()),
                                            shape = RoundedCornerShape(6.dp),
                                        ) {
                                            Text(
                                                text = item.status.toStringField(),
                                                color = item.status.toContentColor(),
                                                style = MaterialTheme.typography.bodySmall,
                                                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(26.dp))
                    Row(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .fillMaxWidth(0.8f)
                    ) {
                        Text(
                            text = item.description ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Due ${formatRelativeTime(item.timeDue)}",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "from alert",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddTaskSheet(
    uiState: TaskUiState,
    formState: TaskFormState,
    onDismiss: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onFieldChange: (Long) -> Unit,
    onPriorityChange: (TaskPriority) -> Unit,
    onDueDateChange: (Instant) -> Unit,
    onAddTask: () -> Unit,
    onTypeChange: (TaskType) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLowest
            ),
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

        FieldDropDown(
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
                onClick = onAddTask,
                enabled = !(formState.title?.isBlank() ?: false || formState.description?.isBlank() ?: false || formState.fieldId == -1L),
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
fun FieldDropDown(
    uiState: TaskUiState,
    formState: TaskFormState,
    onFieldChange: (Long) -> Unit
) {
    val options = uiState.fields
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options.find { it.id == formState.fieldId }) }

    Column(
        modifier = Modifier.padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
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
@Composable
fun TaskTypeDropDown(
    formState: TaskFormState,
    onTypeChange: (TaskType) -> Unit
) {
    val options = TaskType.entries.toTypedArray()
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options.find { it.code == formState.type.code }) }

    Column(
        modifier = Modifier.padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.TableRows,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Type *",
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedOption?.toStringField() ?: "",
                onValueChange = { },
                readOnly = true,
                placeholder = { Text("Select a type") },
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
                        text = { Text(option.toStringField()?: "") },
                        onClick = {
                            selectedOption = option
                            onTypeChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DueDateFieldWithDialog(
    formState: TaskFormState,
    onDueDateChange: (Instant) -> Unit
) {

    var showDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Column(
        modifier = Modifier.padding(12.dp)
    ) {
        Row {
            Icon(
                imageVector = Icons.Outlined.CalendarToday,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Due Date",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            OutlinedTextField(
                value = formState.timeDue.toString() ?: "no date",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(text = "dd.mm.yyyy") },
                trailingIcon = {
                    Icon(Icons.Outlined.DateRange, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showDialog = true }
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.AccessTime,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Optional - Leave blank if no specific due date",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            onDueDateChange(Instant.ofEpochMilli(millis))
                        }
                        showDialog = false
                    }
                ) {
                    Text(text = "OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text(text = "Cancel")
                }
            },
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }
}

@Preview
@Composable
fun TasksScreenPreview() {
    AppTheme {
        val newTasks =
            FakeTaskData.tasks.filter { it.status != TaskStatus.CANCELLED && it.status != TaskStatus.COMPLETED }

        val uiState = TaskUiState(
            tasks = FakeTaskData.tasks,
            filteredTasks = newTasks.take(2),
            fields = FakeFieldData.fields,
            selectedFilter = TaskFilter.All,
            archivedTasks = FakeTaskData.tasks.filter { it.status == TaskStatus.COMPLETED || it.status == TaskStatus.CANCELLED }
        )

        Column {
            TasksTopBar(
                uiState = uiState,
                onFilterChange = { }
            )
            TasksScreen(
                uiState = uiState,
                onTaskClick = { },
                isRefreshing = false,
                onRefresh = { }
            )
        }
    }
}

@Preview
@Composable
fun AddTaskSheetPreview() {
    AppTheme {
        AddTaskSheet(
            uiState = TaskUiState(
                tasks = FakeTaskData.tasks,
                fields = FakeFieldData.fields,
                selectedFilter = TaskFilter.All
            ),
            formState = TaskFormState(),
            onTitleChange = {},
            onDescriptionChange = {},
            onFieldChange = {},
            onPriorityChange = {},
            onDueDateChange = {},
            onDismiss = {},
            onAddTask = {},
            onTypeChange = {}
        )
    }
}

private fun Long.toRuDate(): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale("ru"))
    return formatter.format(Date(this))
}