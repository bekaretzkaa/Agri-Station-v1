package com.example.agristation1.data

import android.content.Context
import com.example.agristation1.data.alertDetails.AlertDetailsOfflineRepository
import com.example.agristation1.data.chatDetails.ChatDetailsOfflineRepository
import com.example.agristation1.data.farmDetails.FarmDetailsOfflineRepository
import com.example.agristation1.data.fieldDetails.FieldDetailsOfflineRepository
import com.example.agristation1.data.historyDetails.HistoryOfflineRepository
import com.example.agristation1.data.historyDetails.HistoryOfflineRepositoryImpl
import com.example.agristation1.data.sensorDetails.SensorDetailsOfflineRepository
import com.example.agristation1.data.taskDetails.TaskDetailsOfflineRepository
import com.example.agristation1.data.userDetails.UserDetailsOfflineRepository
import com.example.agristation1.network.alertNetwork.AlertApiService
import com.example.agristation1.network.alertNetwork.AlertPendingOperationRepository
import com.example.agristation1.network.alertNetwork.AlertPendingOperationRepositoryImpl
import com.example.agristation1.network.alertNetwork.AlertSyncManager
import com.example.agristation1.network.alertNetwork.NetworkAlertRepository
import com.example.agristation1.network.alertNetwork.NetworkAlertRepositoryImpl
import com.example.agristation1.network.fieldNetwork.FieldApiService
import com.example.agristation1.network.fieldNetwork.FieldSyncManager
import com.example.agristation1.network.fieldNetwork.NetworkFieldRepository
import com.example.agristation1.network.fieldNetwork.NetworkFieldRepositoryImpl
import com.example.agristation1.network.gemini.GeminiApiService
import com.example.agristation1.network.gemini.GeminiRepository
import com.example.agristation1.network.sensorNetwork.NetworkSensorRepository
import com.example.agristation1.network.sensorNetwork.NetworkSensorRepositoryImpl
import com.example.agristation1.network.sensorNetwork.SensorApiService
import com.example.agristation1.network.sensorNetwork.SensorSyncManager
import com.example.agristation1.network.taskNetwork.NetworkTaskRepository
import com.example.agristation1.network.taskNetwork.NetworkTaskRepositoryImpl
import com.example.agristation1.network.taskNetwork.TaskApiService
import com.example.agristation1.network.taskNetwork.TaskPendingOperationRepository
import com.example.agristation1.network.taskNetwork.TaskPendingOperationRepositoryImpl
import com.example.agristation1.network.taskNetwork.TaskSyncManager
import com.example.agristation1.network.userNetwork.NetworkUserFarmRepository
import com.example.agristation1.network.userNetwork.NetworkUserFarmRepositoryImpl
import com.example.agristation1.network.userNetwork.UserFarmApiService
import com.example.agristation1.network.userNetwork.UserFarmSyncManager
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

interface AppContainer {

    val farmDetailsOfflineRepository: FarmDetailsOfflineRepository

    val fieldDetailsOfflineRepository: FieldDetailsOfflineRepository

    val alertDetailsOfflineRepository: AlertDetailsOfflineRepository

    val taskDetailsOfflineRepository: TaskDetailsOfflineRepository

    val historyOfflineRepository: HistoryOfflineRepository

    val sensorDetailsOfflineRepository: SensorDetailsOfflineRepository

    val chatDetailsOfflineRepository: ChatDetailsOfflineRepository

    val userDetailsOfflineRepository: UserDetailsOfflineRepository

    val networkAlertRepository: NetworkAlertRepository

    val networkFieldRepository: NetworkFieldRepository

    val networkTaskRepository: NetworkTaskRepository

    val networkUserFarmRepository: NetworkUserFarmRepository

    val networkSensorRepository: NetworkSensorRepository

    val fieldSyncManager: FieldSyncManager

    val alertSyncManager: AlertSyncManager

    val taskSyncManager: TaskSyncManager

    val userFarmSyncManager: UserFarmSyncManager

    val sensorSyncManager: SensorSyncManager

    val taskPendingOperationRepository: TaskPendingOperationRepository

    val alertPendingOperationRepository: AlertPendingOperationRepository

    val syncOrchestrator: SyncOrchestrator

    val geminiRepository: GeminiRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val baseUrl = "http://10.0.2.2:3001/"
    private val geminiBaseUrl = "https://generativelanguage.googleapis.com/"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val geminiRetrofit = Retrofit.Builder()
        .baseUrl(geminiBaseUrl)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val geminiApiService: GeminiApiService by lazy {
        geminiRetrofit.create(GeminiApiService::class.java)
    }

    override val geminiRepository: GeminiRepository by lazy {
        GeminiRepository(
            fieldDetailsOfflineRepository,
            alertDetailsOfflineRepository,
            taskDetailsOfflineRepository,
            chatDetailsOfflineRepository,
            geminiApiService
        )
    }

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

    override val historyOfflineRepository: HistoryOfflineRepository by lazy {
        HistoryOfflineRepositoryImpl(
            AgriStationDatabase.getDatabase(context).historyDetailsDao()
        )
    }

    override val sensorDetailsOfflineRepository: SensorDetailsOfflineRepository by lazy {
        SensorDetailsOfflineRepository(
            AgriStationDatabase.getDatabase(context).sensorDetailsDao()
        )
    }

    override val chatDetailsOfflineRepository: ChatDetailsOfflineRepository by lazy {
        ChatDetailsOfflineRepository(
            AgriStationDatabase.getDatabase(context).chatDetailsDao()
        )
    }

    override val userDetailsOfflineRepository: UserDetailsOfflineRepository by lazy {
        UserDetailsOfflineRepository(
            AgriStationDatabase.getDatabase(context).userDetailsDao()
        )
    }

    private val alertApiService: AlertApiService by lazy {
        retrofit.create(AlertApiService::class.java)
    }
    private val fieldApiService: FieldApiService by lazy {
        retrofit.create(FieldApiService::class.java)
    }

    private val taskApiService: TaskApiService by lazy {
        retrofit.create(TaskApiService::class.java)
    }

    private val userFarmApiService: UserFarmApiService by lazy {
        retrofit.create(UserFarmApiService::class.java)
    }

    private val sensorApiService: SensorApiService by lazy {
        retrofit.create(SensorApiService::class.java)
    }

    override val networkAlertRepository: NetworkAlertRepository by lazy {
        NetworkAlertRepositoryImpl(alertApiService)
    }

    override val networkFieldRepository: NetworkFieldRepository by lazy {
        NetworkFieldRepositoryImpl(fieldApiService)
    }

    override val networkTaskRepository: NetworkTaskRepositoryImpl by lazy {
        NetworkTaskRepositoryImpl(taskApiService)
    }

    override val networkUserFarmRepository: NetworkUserFarmRepositoryImpl by lazy {
        NetworkUserFarmRepositoryImpl(userFarmApiService)
    }

    override val networkSensorRepository: NetworkSensorRepository by lazy {
        NetworkSensorRepositoryImpl(sensorApiService)
    }

    override val fieldSyncManager: FieldSyncManager by lazy {
        FieldSyncManager(
            fieldDetailsOfflineRepository,
            historyOfflineRepository,
            networkFieldRepository,
            AgriStationDatabase.getDatabase(context)
        )
    }

    override val alertSyncManager: AlertSyncManager by lazy {
        AlertSyncManager(
            alertDetailsOfflineRepository,
            taskDetailsOfflineRepository,
            AgriStationDatabase.getDatabase(context).alertPendingOperationDao(),
            networkAlertRepository
        )
    }

    override val taskSyncManager: TaskSyncManager by lazy {
        TaskSyncManager(
            taskDetailsOfflineRepository,
            AgriStationDatabase.getDatabase(context).taskPendingOperationDao(),
            networkTaskRepository
        )
    }

    override val userFarmSyncManager: UserFarmSyncManager by lazy {
        UserFarmSyncManager(
            userDetailsOfflineRepository,
            farmDetailsOfflineRepository,
            networkUserFarmRepository,
            AgriStationDatabase.getDatabase(context)
        )
    }

    override val sensorSyncManager: SensorSyncManager by lazy {
        SensorSyncManager(
            sensorDetailsOfflineRepository,
            networkSensorRepository,
            AgriStationDatabase.getDatabase(context)
        )
    }

    override val syncOrchestrator: SyncOrchestrator by lazy {
        SyncOrchestrator(
            fieldSyncManager, alertSyncManager, taskSyncManager, userFarmSyncManager, sensorSyncManager
        )
    }

    override val taskPendingOperationRepository: TaskPendingOperationRepository by lazy {
        TaskPendingOperationRepositoryImpl(
            AgriStationDatabase.getDatabase(context).taskPendingOperationDao()
        )
    }

    override val alertPendingOperationRepository: AlertPendingOperationRepository by lazy {
        AlertPendingOperationRepositoryImpl(
            AgriStationDatabase.getDatabase(context).alertPendingOperationDao()
        )
    }
}
