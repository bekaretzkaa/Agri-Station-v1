package com.example.agristation1.data

import android.content.Context
import com.example.agristation1.data.alertDetails.AlertDetailsOfflineRepository
import com.example.agristation1.data.chatDetails.ChatDetailsOfflineRepository
import com.example.agristation1.data.farmDetails.FarmDetailsOfflineRepository
import com.example.agristation1.data.fieldDetails.FieldDetailsOfflineRepository
import com.example.agristation1.data.history.HistoryOfflineRepository
import com.example.agristation1.data.taskDetails.TaskDetailsOfflineRepository
import com.example.agristation1.data.taskNotes.TaskNotesOfflineRepository

interface AppContainer {

    val farmDetailsOfflineRepository: FarmDetailsOfflineRepository

    val fieldDetailsOfflineRepository: FieldDetailsOfflineRepository

    val alertDetailsOfflineRepository: AlertDetailsOfflineRepository

    val taskDetailsOfflineRepository: TaskDetailsOfflineRepository

    val taskNotesOfflineRepository: TaskNotesOfflineRepository

    val historyOfflineRepository: HistoryOfflineRepository

    val chatDetailsOfflineRepository: ChatDetailsOfflineRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    override val farmDetailsOfflineRepository: FarmDetailsOfflineRepository by lazy {
        FarmDetailsOfflineRepository(
            AgriStationDatabase.getDatabase(context).farmDetailsDao()
        )
    }

    override val fieldDetailsOfflineRepository: FieldDetailsOfflineRepository by lazy {
        FieldDetailsOfflineRepository(
            AgriStationDatabase.getDatabase(context).fieldDetailsDao()
        )
    }

    override val alertDetailsOfflineRepository: AlertDetailsOfflineRepository by lazy {
        AlertDetailsOfflineRepository(
            AgriStationDatabase.getDatabase(context).alertDetailsDao()
        )
    }

    override val taskDetailsOfflineRepository: TaskDetailsOfflineRepository by lazy {
        TaskDetailsOfflineRepository(
            AgriStationDatabase.getDatabase(context).taskDetailsDao()
        )
    }

    override val taskNotesOfflineRepository: TaskNotesOfflineRepository by lazy {
        TaskNotesOfflineRepository(
            AgriStationDatabase.getDatabase(context).taskNotesDao()
        )
    }

    override val historyOfflineRepository: HistoryOfflineRepository by lazy {
        HistoryOfflineRepository(
            AgriStationDatabase.getDatabase(context).historyDao()
        )
    }

    override val chatDetailsOfflineRepository: ChatDetailsOfflineRepository by lazy {
        ChatDetailsOfflineRepository(
            AgriStationDatabase.getDatabase(context).chatDetailsDao()
        )
    }

}