package com.example.agristation1.ui.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.agristation1.data.chatDetails.ChatMessageEntity
import com.example.agristation1.data.chatDetails.MessageRole
import com.example.agristation1.data.formatRelativeTime
import com.example.agristation1.fakedata.FakeChatData
import com.example.agristation1.ui.viewmodel.ChatUiState
import com.example.agristation1.ui.viewmodel.ChatViewModel
import com.example.compose.AppTheme
import kotlinx.coroutines.launch

@Composable
fun ChatMainScreen(
    viewModel: ChatViewModel,
    onBack: () -> Unit = {}
) {
    val uiState: ChatUiState by viewModel.uiState.collectAsState()
    ChatMainContent(
        uiState = uiState,
        onBack = onBack,
        onChatClick = { viewModel.onChatClick(it) },
        onNewChatClick = {
            viewModel.onNewChatClick()
        },
        onInputChange = { viewModel.onInputChange(it) },
        onSendQuery = { viewModel.onSendQuery() },
        onDeleteChat = {
            viewModel.onDeleteChat()
        }
    )
}

@Composable
fun ChatMainContent(
    uiState: ChatUiState,
    onBack: () -> Unit = {},
    onChatClick: (Int) -> Unit = {},
    onNewChatClick: () -> Unit = {},
    onInputChange: (String) -> Unit = {},
    onSendQuery: () -> Unit = {},
    onDeleteChat: () -> Unit = {},

    initialDrawerValue: DrawerValue = DrawerValue.Closed
) {
    val drawerState = rememberDrawerState(initialValue = initialDrawerValue)
    val scope = rememberCoroutineScope()

    var showDeleteChat by remember { mutableStateOf(false) }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val drawerWidth = screenWidth * 0.75f

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(drawerWidth),
                drawerState = drawerState
            ) {
                Row {
                    Text(
                        text = "Chat History",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = { scope.launch { drawerState.close() } }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null
                        )
                    }
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                LazyColumn(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.chatHistory) { chat ->
                        NavigationDrawerItem(
                            label = {
                                Card {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(6.dp)
                                    ) {
                                        Text(
                                            text = chat.title ?: "",
                                        )
                                        Row {
                                            Spacer(modifier = Modifier.weight(1f))
                                            Text(
                                                text = formatRelativeTime(chat.updatedAt)
                                            )
                                        }
                                    }
                                }
                            },
                            onClick = {
                                onChatClick(chat.id)
                                scope.launch { drawerState.close() }
                            },
                            selected = false
                        )
                    }
                }
            }
        }
    ) {
        if(showDeleteChat) DeleteChat(
            onDismiss = { showDeleteChat = false },
            onDeleteChat = {
                onDeleteChat()
                showDeleteChat = false
            }
        )

        Column {
            ChatTopBar(
                onBack = onBack,
                onHistoryClick = { scope.launch { drawerState.open() } },
                onNewChatClick = onNewChatClick,
                onOpenDeleteChat = { showDeleteChat = true }
            )
            ChatScreen(
                messages = uiState.messages,
                uiState = uiState,
                onInputChange = onInputChange,
                onSendQuery = onSendQuery
            )
        }
    }
}

@Composable
fun ChatTopBar(
    onBack: () -> Unit,
    onHistoryClick: () -> Unit,
    onNewChatClick: () -> Unit,
    onOpenDeleteChat: () -> Unit
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
                text = "AI Assistant",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = null
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(
                onClick = onHistoryClick,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSecondaryContainer),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 6.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.History,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "History",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = onNewChatClick,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Create,
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
                onClick = onOpenDeleteChat,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun ChatScreen(
    messages: List<ChatMessageEntity>,
    uiState: ChatUiState,
    onInputChange: (String) -> Unit = {},
    onSendQuery: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            items(messages) { message ->
                Row {
                    if (message.role == MessageRole.USER) Spacer(modifier = Modifier.weight(1f))
                    Card(
                        modifier = Modifier.width(300.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (message.role == MessageRole.USER) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Text(
                            text = message.text,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    if (message.role == MessageRole.ASSISTANT) Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            OutlinedTextField(
                value = uiState.input,
                onValueChange = { onInputChange(it) },
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 56.dp),
                enabled = !uiState.isSending,
                placeholder = {
                    Text("Введите запрос...")
                },
                singleLine = false,
                maxLines = 5,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        onSendQuery
                    }
                ),
                trailingIcon = {
                    IconButton(
                        onClick = onSendQuery,
                        enabled = uiState.input.isNotBlank() && !uiState.isSending,
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Send,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun DeleteChat(
    onDismiss: () -> Unit,
    onDeleteChat: () -> Unit
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
                    text = "Delete Chat",
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
                        text = "Are you sure you want to delete this chat? This action cannot be undone.",
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
                    onClick = onDeleteChat,
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

@Preview(showBackground = true)
@Composable
fun ChatMainScreenPreview() {
    AppTheme {
        ChatMainContent(
            uiState = ChatUiState(
                chatHistory = FakeChatData.chats,
                messages = FakeChatData.messages.filter { it.chatId == 1 }
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatDrawerPreview() {
    AppTheme {
        ChatMainContent(
            uiState = ChatUiState(
                chatHistory = FakeChatData.chats,
                messages = FakeChatData.messages.filter { it.chatId == 1 }
            ),
            initialDrawerValue = DrawerValue.Open
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatDeletePreview() {
    AppTheme {
        DeleteChat(
            onDismiss = {},
            onDeleteChat = {}
        )
    }
}
