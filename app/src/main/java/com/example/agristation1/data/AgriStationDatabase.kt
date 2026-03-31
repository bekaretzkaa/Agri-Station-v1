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
import com.example.agristation1.data.history.AirHumidity
import com.example.agristation1.data.history.AirTemperature
import com.example.agristation1.data.history.HistoryDao
import com.example.agristation1.data.history.Lux
import com.example.agristation1.data.history.SoilMoisture
import com.example.agristation1.data.history.SoilTemperature
import com.example.agristation1.data.taskDetails.TaskDetails
import com.example.agristation1.data.taskDetails.TaskDetailsDao
import com.example.agristation1.data.taskNotes.TaskNotes
import com.example.agristation1.data.taskNotes.TaskNotesDao

@Database(
    entities = [FarmDetails::class, FieldDetails::class, AlertDetails::class, TaskDetails::class, TaskNotes::class, ChatEntity::class, ChatMessageEntity::class, SoilMoisture::class, SoilTemperature::class, AirTemperature::class, AirHumidity::class, Lux::class],
    version = 1
)
@TypeConverters(
    FieldConverters::class,
    AlertConverters::class,
    TaskConverters::class,
    TimeConverters::class,
    MessageConverters::class,
    HistoryConverters::class
)
abstract class AgriStationDatabase : RoomDatabase() {

    abstract fun farmDetailsDao(): FarmDetailsDao

    abstract fun fieldDetailsDao(): FieldDetailsDao

    abstract fun alertDetailsDao(): AlertDetailsDao

    abstract fun taskDetailsDao(): TaskDetailsDao

    abstract fun taskNotesDao(): TaskNotesDao

    abstract fun historyDao(): HistoryDao

    abstract fun chatDetailsDao(): ChatDetailsDao

    companion object {
        @Volatile
        private var Instance: AgriStationDatabase? = null

        fun getDatabase(context: Context): AgriStationDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    AgriStationDatabase::class.java,
                    "agri_station_database"
                )
                    .createFromAsset("database/agri_station_database.db")
                    .build()
                    .also { Instance = it }
            }
        }
    }

}
