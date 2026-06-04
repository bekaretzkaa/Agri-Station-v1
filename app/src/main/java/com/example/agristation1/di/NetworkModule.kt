package com.example.agristation1.di

import com.example.agristation1.network.alertNetwork.AlertApiService
import com.example.agristation1.network.fieldNetwork.FieldApiService
import com.example.agristation1.network.gemini.GeminiApiService
import com.example.agristation1.network.sensorNetwork.SensorApiService
import com.example.agristation1.network.taskNetwork.TaskApiService
import com.example.agristation1.network.userNetwork.UserFarmApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val baseUrl = "http://10.0.2.2:3001/"
    private val geminiBaseUrl = "https://generativelanguage.googleapis.com/"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideGeminiApi(): GeminiApiService {
        return Retrofit.Builder()
            .baseUrl(geminiBaseUrl)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(GeminiApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideLocalRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideAlertApi(retrofit: Retrofit): AlertApiService {
        return retrofit.create(AlertApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideFieldApi(retrofit: Retrofit): FieldApiService {
        return retrofit.create(FieldApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideTaskApi(retrofit: Retrofit): TaskApiService {
        return retrofit.create(TaskApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideUserFarmApi(retrofit: Retrofit): UserFarmApiService {
        return retrofit.create(UserFarmApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideSensorApi(retrofit: Retrofit): SensorApiService {
        return retrofit.create(SensorApiService::class.java)
    }

}