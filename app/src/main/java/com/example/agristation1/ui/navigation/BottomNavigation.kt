package com.example.agristation1.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val badgeCount: Int? = null
) {
    object Home : BottomBarScreen(
        route = "home",
        title = "Home",
        icon = Icons.Outlined.Home,
    )
    object Fields : BottomBarScreen(
        route = "fields",
        title = "Fields",
        icon = Icons.Outlined.Layers,
    )
    object Alerts : BottomBarScreen(
        route = "alerts",
        title = "Alerts",
        icon = Icons.Outlined.WarningAmber,
    )
    object Tasks : BottomBarScreen(
        route = "tasks",
        title = "Tasks",
        icon = Icons.Outlined.CheckBox,
    )
    object Profile : BottomBarScreen(
        route = "profile",
        title = "Profile",
        icon = Icons.Outlined.Person,
    )
}