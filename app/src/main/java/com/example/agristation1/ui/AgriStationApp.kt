package com.example.agristation1.ui

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.agristation1.ui.navigation.BottomBarScreen
import com.example.agristation1.ui.navigation.BottomNavGraph
import com.example.compose.AppTheme

@Composable
fun AgriStationApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        BottomNavGraph(navController, innerPadding.calculateBottomPadding())
    }
}

@Composable
fun BottomBar(
    navController: NavHostController
) {
    val screens = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.Fields,
        BottomBarScreen.Alerts,
        BottomBarScreen.Tasks,
        BottomBarScreen.Profile
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        screens.forEach { screen ->
            AddItem(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    NavigationBarItem(
        label = {
            Text(
                text = screen.title
            )
        },
        icon = {
            Icon(imageVector = screen.icon, contentDescription = "Navigation Icon")
        },
        selected = currentDestination?.hierarchy?.any {
            it.route == screen.route
        } == true,
        onClick = {
            navController.navigate(screen.route) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
                restoreState = true
            }
        }
    )
}

@Preview
@Composable
fun AgriStationAppPreview() {
    AppTheme() {
        AgriStationApp()
    }
}

@Composable
operator fun PaddingValues.plus(other: PaddingValues): PaddingValues {
    val ld = LocalLayoutDirection.current
    return PaddingValues(
        start = this.calculateStartPadding(ld) + other.calculateStartPadding(ld),
        top = this.calculateTopPadding() + other.calculateTopPadding(),
        end = this.calculateEndPadding(ld) + other.calculateEndPadding(ld),
        bottom = this.calculateBottomPadding() + other.calculateBottomPadding()
    )
}