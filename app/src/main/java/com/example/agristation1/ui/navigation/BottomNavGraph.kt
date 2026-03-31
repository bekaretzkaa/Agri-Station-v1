package com.example.agristation1.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.agristation1.ui.pages.AlertDetailsMainScreen
import com.example.agristation1.ui.pages.AlertsMainScreen
import com.example.agristation1.ui.pages.AlertsScreen
import com.example.agristation1.ui.pages.ChatMainScreen
import com.example.agristation1.ui.pages.FieldDetailsMainScreen
import com.example.agristation1.ui.pages.FieldsMainScreen
import com.example.agristation1.ui.pages.HomeMainScreen
import com.example.agristation1.ui.pages.HomeScreen
import com.example.agristation1.ui.pages.ProfileMainScreen
import com.example.agristation1.ui.pages.ProfileScreen
import com.example.agristation1.ui.pages.StatisticsMainScreen
import com.example.agristation1.ui.pages.TaskDetailsMainScreen
import com.example.agristation1.ui.pages.TasksMainScreen
import com.example.agristation1.ui.viewmodel.AlertDetailsViewModel
import com.example.agristation1.ui.viewmodel.AlertViewModel
import com.example.agristation1.ui.viewmodel.ChatViewModel
import com.example.agristation1.ui.viewmodel.FieldDetailsViewModel
import com.example.agristation1.ui.viewmodel.FieldViewModel
import com.example.agristation1.ui.viewmodel.HomeViewModel
import com.example.agristation1.ui.viewmodel.StatisticsViewModel
import com.example.agristation1.ui.viewmodel.TaskDetailsViewModel
import com.example.agristation1.ui.viewmodel.TaskViewModel

@Composable
fun BottomNavGraph(navController: NavHostController, padding: Dp) {
    val alertViewModel: AlertViewModel = viewModel(factory = AlertViewModel.factory)
    val fieldViewModel: FieldViewModel = viewModel(factory = FieldViewModel.factory)
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.factory)
    val taskViewModel: TaskViewModel = viewModel(factory = TaskViewModel.factory)
    val chatViewModel: ChatViewModel = viewModel(factory = ChatViewModel.factory)

    NavHost(
        navController = navController,
        startDestination = AppRoute.Home.route,
        modifier = Modifier.padding(bottom = padding)
    ) {
        composable(route = AppRoute.Home.route) {
            HomeMainScreen(
                viewModel = homeViewModel,
                onFieldClick = { fieldId ->
                    navController.navigate(AppRoute.FieldDetails.createRoute(fieldId))
                },
                onOpenAllFields = { navController.navigate(AppRoute.Fields.route) },
                onChatClick = { navController.navigate(AppRoute.Chat.route) }
            )
        }
        composable(route = AppRoute.Fields.route) {
            FieldsMainScreen(
                onFieldClick = { fieldId ->
                    navController.navigate(AppRoute.FieldDetails.createRoute(fieldId))
                },
                viewModel = fieldViewModel
            )
        }
        composable(route = AppRoute.Alerts.route) {
            AlertsMainScreen(
                onAlertClick = { alertId ->
                    navController.navigate(AppRoute.AlertDetails.createRoute(alertId))
                },
                viewModel = alertViewModel
            )
        }
        composable(route = AppRoute.Tasks.route) {
            TasksMainScreen(
                viewModel = taskViewModel,
                onTaskClick = { taskId ->
                    navController.navigate(AppRoute.TaskDetails.createRoute(taskId))
                }
            )
        }
        composable(route = AppRoute.Profile.route) {
            ProfileMainScreen()
        }

        composable(route = AppRoute.Chat.route) {
            ChatMainScreen(
                viewModel = chatViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(route = AppRoute.FieldDetails.route) { backStackEntry ->
            val fieldDetailsViewModel: FieldDetailsViewModel = viewModel(
                backStackEntry,
                factory = FieldDetailsViewModel.factory
            )
            FieldDetailsMainScreen(
                onBack = { navController.popBackStack() },
                viewModel = fieldDetailsViewModel,
                onOpenAlertDetails = { alertId ->
                    navController.navigate(AppRoute.AlertDetails.createRoute(alertId))
                },
                onOpenAllAlerts = { navController.navigate(AppRoute.Alerts.route) },
                openStatistics = { fieldId ->
                    navController.navigate(AppRoute.Statistics.createRoute(fieldId))
                }
            )
        }
        composable(route = AppRoute.AlertDetails.route) { backStackEntry ->
            val alertDetailsViewModel: AlertDetailsViewModel = viewModel(
                backStackEntry,
                factory = AlertDetailsViewModel.factory
            )
            AlertDetailsMainScreen(
                onBack = { navController.popBackStack() },
                onOpenFieldDetails = { fieldId ->
                    navController.navigate(AppRoute.FieldDetails.createRoute(fieldId))
                },
                onOpenTaskDetails = { taskId ->
                    navController.navigate(AppRoute.TaskDetails.createRoute(taskId))
                },
                viewModel = alertDetailsViewModel,
                onDeleteAlert = {
                    navController.popBackStack()
                    alertDetailsViewModel.deleteAlert(it)
                }
            )
        }
        composable(route = AppRoute.TaskDetails.route) { backStackEntry ->
            val taskDetailsViewModel: TaskDetailsViewModel = viewModel(
                backStackEntry,
                factory = TaskDetailsViewModel.factory
                )

            TaskDetailsMainScreen(
                onBack = { navController.popBackStack() },
                viewModel = taskDetailsViewModel,
                onOpenFieldDetails = { fieldId ->
                    navController.navigate(AppRoute.FieldDetails.createRoute(fieldId))
                },
                onOpenAlertDetails = { alertId ->
                    navController.navigate(AppRoute.AlertDetails.createRoute(alertId))
                },
                onDeleteTask = {
                    navController.popBackStack()
                    taskDetailsViewModel.deleteTask(it)
                }
            )
        }
        composable(route = AppRoute.Statistics.route) { backStackEntry ->
            val statisticsViewModel: StatisticsViewModel = viewModel(
                backStackEntry,
                factory = StatisticsViewModel.factory
            )

            StatisticsMainScreen(
                onBack = { navController.popBackStack() },
                viewModel = statisticsViewModel
            )
        }
    }
}