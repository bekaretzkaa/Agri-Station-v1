package com.example.agristation1.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.agristation1.data.farmDetails.FarmDetails
import com.example.agristation1.data.alertDetails.AlertDetails
import com.example.agristation1.data.alertDetails.AlertDetailsDao
import com.example.agristation1.data.chatDetails.ChatDetailsDao
import com.example.agristation1.data.chatDetails.ChatEntity
import com.example.agristation1.data.chatDetails.ChatMessageEntity
import com.example.agristation1.data.farmDetails.FarmDetailsDao
import com.example.agristation1.data.fieldDetails.FieldDetails
import com.example.agristation1.data.fieldDetails.FieldDetailsDao
import com.example.agristation1.data.fieldDetails.FieldPoints
import com.example.agristation1.data.historyDetails.HistoryDetails
import com.example.agristation1.data.historyDetails.HistoryDetailsDao
import com.example.agristation1.data.sensorDetails.SensorDetails
import com.example.agristation1.data.sensorDetails.SensorDetailsDao
import com.example.agristation1.data.taskDetails.TaskDetails
import com.example.agristation1.data.taskDetails.TaskDetailsDao
import com.example.agristation1.data.userDetails.UserDetails
import com.example.agristation1.data.userDetails.UserDetailsDao
import com.example.agristation1.network.alertNetwork.AlertPendingOperation
import com.example.agristation1.network.alertNetwork.AlertPendingOperationDao
import com.example.agristation1.network.taskNetwork.TaskPendingOperation
import com.example.agristation1.network.taskNetwork.TaskPendingOperationDao

@Database(
    entities = [FarmDetails::class, FieldDetails::class, FieldPoints::class,AlertDetails::class, SensorDetails::class, TaskDetails::class, ChatEntity::class, ChatMessageEntity::class, HistoryDetails::class, AlertPendingOperation::class, TaskPendingOperation::class, UserDetails::class],
    version = 1
)
@TypeConverters(
    FieldConverters::class,
    AlertConverters::class,
    TaskConverters::class,
    TimeConverters::class,
    MessageConverters::class,
    PendingOperationConverters::class,
    SensorConverters::class
)
abstract class AgriStationDatabase : RoomDatabase() {

    abstract fun alertPendingOperationDao(): AlertPendingOperationDao

    abstract fun taskPendingOperationDao(): TaskPendingOperationDao

    abstract fun farmDetailsDao(): FarmDetailsDao

    abstract fun fieldDetailsDao(): FieldDetailsDao

    abstract fun alertDetailsDao(): AlertDetailsDao

    abstract fun taskDetailsDao(): TaskDetailsDao

    abstract fun historyDetailsDao(): HistoryDetailsDao

    abstract fun chatDetailsDao(): ChatDetailsDao

    abstract fun sensorDetailsDao(): SensorDetailsDao

    abstract fun userDetailsDao(): UserDetailsDao

    companion object {
        @Volatile
        private var INSTANCE: AgriStationDatabase? = null

        fun getDatabase(context: Context): AgriStationDatabase {
            return INSTANCE  ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AgriStationDatabase::class.java,
                    "agri_station_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }

}
