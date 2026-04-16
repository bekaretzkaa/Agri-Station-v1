package com.example.agristation1.ui.navigation

sealed class AppRoute(val route: String) {
    data object Home : AppRoute("home")
    data object Fields : AppRoute("fields")
    data object Alerts : AppRoute("alerts")
    data object Tasks : AppRoute("tasks")
    data object Profile : AppRoute("profile")

    data object Chat : AppRoute("chat")

    data object FieldDetails : AppRoute("field_details/{fieldId}") {
        fun createRoute(fieldId: Long) = "field_details/$fieldId"
    }

    data object AlertDetails : AppRoute("alert_details/{alertId}") {
        fun createRoute(alertId: Long) = "alert_details/$alertId"
    }

    data object TaskDetails : AppRoute("task_details/{taskId}") {
        fun createRoute(taskId: Long) = "task_details/$taskId"
    }

    data object Statistics : AppRoute("statistics/{fieldId}") {
        fun createRoute(fieldId: Long) = "statistics/$fieldId"
    }
}