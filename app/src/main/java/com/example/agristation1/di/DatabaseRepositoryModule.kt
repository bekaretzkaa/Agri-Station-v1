package com.example.agristation1.di

import com.example.agristation1.data.alertDetails.AlertDetailsOfflineRepository
import com.example.agristation1.data.alertDetails.AlertDetailsRepository
import com.example.agristation1.data.chatDetails.ChatDetailsOfflineRepository
import com.example.agristation1.data.chatDetails.ChatDetailsRepository
import com.example.agristation1.data.farmDetails.FarmDetailsOfflineRepository
import com.example.agristation1.data.farmDetails.FarmDetailsRepository
import com.example.agristation1.data.fieldDetails.FieldDetailsOfflineRepository
import com.example.agristation1.data.fieldDetails.FieldDetailsRepository
import com.example.agristation1.data.historyDetails.HistoryOfflineRepository
import com.example.agristation1.data.historyDetails.HistoryOfflineRepositoryImpl
import com.example.agristation1.data.sensorDetails.SensorDetailsOfflineRepository
import com.example.agristation1.data.sensorDetails.SensorDetailsRepository
import com.example.agristation1.data.taskDetails.TaskDetailsOfflineRepository
import com.example.agristation1.data.taskDetails.TaskDetailsRepository
import com.example.agristation1.data.userDetails.UserDetailsOfflineRepository
import com.example.agristation1.data.userDetails.UserDetailsRepository
import com.example.agristation1.network.alertNetwork.AlertPendingOperationRepository
import com.example.agristation1.network.alertNetwork.AlertPendingOperationRepositoryImpl
import com.example.agristation1.network.taskNetwork.TaskPendingOperationRepository
import com.example.agristation1.network.taskNetwork.TaskPendingOperationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFarmDetailsRepository(
        farmDetailsOfflineRepository: FarmDetailsOfflineRepository
    ): FarmDetailsRepository

    @Binds
    @Singleton
    abstract fun bindFieldDetailsRepository(
        fieldDetailsOfflineRepository: FieldDetailsOfflineRepository
    ): FieldDetailsRepository

    @Binds
    @Singleton
    abstract fun bindAlertDetailsRepository(
        alertDetailsOfflineRepository: AlertDetailsOfflineRepository
    ): AlertDetailsRepository

    @Binds
    @Singleton
    abstract fun bindTaskDetailsRepository(
        taskDetailsOfflineRepository: TaskDetailsOfflineRepository
    ): TaskDetailsRepository

    @Binds
    @Singleton
    abstract fun bindHistoryOfflineRepository(
        historyOfflineRepositoryImpl: HistoryOfflineRepositoryImpl
    ): HistoryOfflineRepository

    @Binds
    @Singleton
    abstract fun bindSensorDetailsRepository(
        sensorDetailsOfflineRepository: SensorDetailsOfflineRepository
    ): SensorDetailsRepository

    @Binds
    @Singleton
    abstract fun bindChatDetailsRepository(
        chatDetailsOfflineRepository: ChatDetailsOfflineRepository
    ): ChatDetailsRepository

    @Binds
    @Singleton
    abstract fun bindUserDetailsRepository(
        userDetailsOfflineRepository: UserDetailsOfflineRepository
    ): UserDetailsRepository


    @Binds
    @Singleton
    abstract fun bindAlertPendingOperationRepository(
        alertPendingOperationRepositoryImpl: AlertPendingOperationRepositoryImpl
    ): AlertPendingOperationRepository

    @Binds
    @Singleton
    abstract fun bindTaskPendingOperationRepository(
        taskPendingOperationRepositoryImpl: TaskPendingOperationRepositoryImpl
    ): TaskPendingOperationRepository
}