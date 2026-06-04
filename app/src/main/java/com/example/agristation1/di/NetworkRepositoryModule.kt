package com.example.agristation1.di

import com.example.agristation1.network.alertNetwork.NetworkAlertRepository
import com.example.agristation1.network.alertNetwork.NetworkAlertRepositoryImpl
import com.example.agristation1.network.fieldNetwork.NetworkFieldRepository
import com.example.agristation1.network.fieldNetwork.NetworkFieldRepositoryImpl
import com.example.agristation1.network.gemini.GeminiRepository
import com.example.agristation1.network.gemini.GeminiRepositoryImpl
import com.example.agristation1.network.sensorNetwork.NetworkSensorRepository
import com.example.agristation1.network.sensorNetwork.NetworkSensorRepositoryImpl
import com.example.agristation1.network.taskNetwork.NetworkTaskRepository
import com.example.agristation1.network.taskNetwork.NetworkTaskRepositoryImpl
import com.example.agristation1.network.userNetwork.NetworkUserFarmRepository
import com.example.agristation1.network.userNetwork.NetworkUserFarmRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGeminiRepository(
        geminiRepositoryImpl: GeminiRepositoryImpl
    ) : GeminiRepository

    @Binds
    @Singleton
    abstract fun bindNetworkAlertRepository(
        networkAlertRepositoryImpl: NetworkAlertRepositoryImpl
    ) : NetworkAlertRepository

    @Binds
    @Singleton
    abstract fun bindNetworkFieldRepository(
        networkFieldRepositoryImpl: NetworkFieldRepositoryImpl
    ) : NetworkFieldRepository

    @Binds
    @Singleton
    abstract fun bindNetworkTaskRepository(
        networkTaskRepositoryImpl: NetworkTaskRepositoryImpl
    ) : NetworkTaskRepository

    @Binds
    @Singleton
    abstract fun bindNetworkUserFarmRepository(
        networkUserFarmRepositoryImpl: NetworkUserFarmRepositoryImpl
    ) : NetworkUserFarmRepository

    @Binds
    @Singleton
    abstract fun bindNetworkSensorRepository(
        networkSensorRepositoryImpl: NetworkSensorRepositoryImpl
    ) : NetworkSensorRepository
}