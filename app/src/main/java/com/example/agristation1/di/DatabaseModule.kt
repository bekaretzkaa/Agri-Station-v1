package com.example.agristation1.di

import android.content.Context
import androidx.room.Room
import com.example.agristation1.data.AgriStationDatabase
import com.example.agristation1.data.alertDetails.AlertDetailsDao
import com.example.agristation1.data.chatDetails.ChatDetailsDao
import com.example.agristation1.data.farmDetails.FarmDetailsDao
import com.example.agristation1.data.fieldDetails.FieldDetailsDao
import com.example.agristation1.data.historyDetails.HistoryDetailsDao
import com.example.agristation1.data.sensorDetails.SensorDetailsDao
import com.example.agristation1.data.taskDetails.TaskDetailsDao
import com.example.agristation1.data.userDetails.UserDetailsDao
import com.example.agristation1.network.alertNetwork.AlertPendingOperationDao
import com.example.agristation1.network.taskNetwork.TaskPendingOperationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAgriStationDatabase(
        @ApplicationContext context: Context
    ): AgriStationDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AgriStationDatabase::class.java,
            "agri_station_database"
        ).build()
    }


    @Provides
    fun provideAlertPendingOperationDao(db: AgriStationDatabase) : AlertPendingOperationDao {
        return db.alertPendingOperationDao()
    }
    @Provides
    fun provideTaskPendingOperationDao(db: AgriStationDatabase) : TaskPendingOperationDao {
        return db.taskPendingOperationDao()
    }


    @Provides
    fun provideFarmDetailsDao(db: AgriStationDatabase) : FarmDetailsDao {
        return db.farmDetailsDao()
    }
    @Provides
    fun provideFieldDetailsDao(db: AgriStationDatabase) : FieldDetailsDao {
        return db.fieldDetailsDao()
    }
    @Provides
    fun provideAlertDetailsDao(db: AgriStationDatabase) : AlertDetailsDao {
        return db.alertDetailsDao()
    }
    @Provides
    fun provideTaskDetailsDao(db: AgriStationDatabase) : TaskDetailsDao {
        return db.taskDetailsDao()
    }
    @Provides
    fun provideHistoryDetailsDao(db: AgriStationDatabase) : HistoryDetailsDao {
        return db.historyDetailsDao()
    }
    @Provides
    fun provideChatDetailsDao(db: AgriStationDatabase) : ChatDetailsDao {
        return db.chatDetailsDao()
    }
    @Provides
    fun provideSensorDetailsDao(db: AgriStationDatabase) : SensorDetailsDao {
        return db.sensorDetailsDao()
    }
    @Provides
    fun provideUserDetailsDao(db: AgriStationDatabase) : UserDetailsDao {
        return db.userDetailsDao()
    }

}