package com.example.agristation1.ui.pages

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
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme

@Composable
fun ProfileMainScreen() {
    Column {
        ProfileTopBar()
        ProfileScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            ProfileInformation()
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            AppSettings()
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            AccountSettings()
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            About()
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            LogOut()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ProfileTopBar(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
            .statusBarsPadding()
            .padding(start = 12.dp, bottom = 16.dp, end = 12.dp)

    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun ProfileInformation(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.inversePrimary, shape = CircleShape)
                    .size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "JD",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "John Davidson",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Farm Manager",
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "GreenField Farm",
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.LightGray
            )
        }
        Column(
            modifier = Modifier.padding(horizontal = 12.dp).fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)

                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "john.davidson@greenfield.com",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Business,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)

                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "GreenField Agriculture Co.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AppSettings(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                    .padding(12.dp)
                    .height(40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "App Settings",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.LightGray
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                    .padding(horizontal = 12.dp, vertical = 20.dp)
                    .height(40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Notifications",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "Manage alert preferences",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null
                )
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.LightGray
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                    .padding(horizontal = 12.dp, vertical = 20.dp)
                    .height(40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.DarkMode,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Theme",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "Light mode",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null
                )
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.LightGray
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                    .padding(horizontal = 12.dp, vertical = 20.dp)
                    .height(40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Language,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Language & Units",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "English, Metric",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun AccountSettings(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                    .padding(12.dp)
                    .height(40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Account",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.LightGray
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                    .padding(horizontal = 12.dp, vertical = 20.dp)
                    .height(40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Account Settings",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "Update your information",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null
                )
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.LightGray
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                    .padding(horizontal = 12.dp, vertical = 20.dp)
                    .height(40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.HelpOutline,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Help & Support",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "Get assistance",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun About(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Row() {
                Text(
                    text = "Version",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "1.0.0",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row() {
                Text(
                    text = "Last Updated",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "March 2, 2026",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun LogOut(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Logout,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Log Out",
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    AppTheme() {
        ProfileMainScreen()
    }
}