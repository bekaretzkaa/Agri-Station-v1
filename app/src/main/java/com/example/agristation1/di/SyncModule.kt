package com.example.agristation1.di

import com.example.agristation1.data.SyncOrchestrator
import com.example.agristation1.data.SyncOrchestratorImpl
import com.example.agristation1.network.alertNetwork.AlertSyncManager
import com.example.agristation1.network.alertNetwork.AlertSyncManagerImpl
import com.example.agristation1.network.fieldNetwork.FieldSyncManager
import com.example.agristation1.network.fieldNetwork.FieldSyncManagerImpl
import com.example.agristation1.network.sensorNetwork.SensorSyncManager
import com.example.agristation1.network.sensorNetwork.SensorSyncManagerImpl
import com.example.agristation1.network.taskNetwork.TaskSyncManager
import com.example.agristation1.network.taskNetwork.TaskSyncManagerImpl
import com.example.agristation1.network.userNetwork.UserFarmSyncManager
import com.example.agristation1.network.userNetwork.UserFarmSyncManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SyncModule {

    @Binds
    @Singleton
    abstract fun bindFieldSyncManager(
        fieldSyncManagerImpl: FieldSyncManagerImpl
    ): FieldSyncManager

    @Binds
    @Singleton
    abstract fun bindAlertSyncManager(
        alertSyncManagerImpl: AlertSyncManagerImpl
    ): AlertSyncManager

    @Binds
    @Singleton
    abstract fun bindTaskSyncManager(
        taskSyncManagerImpl: TaskSyncManagerImpl
    ): TaskSyncManager

    @Binds
    @Singleton
    abstract fun bindUserFarmSyncManager(
        userFarmSyncManagerImpl: UserFarmSyncManagerImpl
    ): UserFarmSyncManager

    @Binds
    @Singleton
    abstract fun bindSensorSyncManager(
        sensorSyncManagerImpl: SensorSyncManagerImpl
    ): SensorSyncManager

    @Binds
    @Singleton
    abstract fun bindSyncOrchestrator(
        syncOrchestratorImpl: SyncOrchestratorImpl
    ): SyncOrchestrator

}