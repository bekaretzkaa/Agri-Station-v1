package com.example.agristation1.network.sensorNetwork

import androidx.room.withTransaction
import com.example.agristation1.data.AgriStationDatabase
import com.example.agristation1.data.SyncResult
import com.example.agristation1.data.sensorDetails.SensorBattery
import com.example.agristation1.data.sensorDetails.SensorDetails
import com.example.agristation1.data.sensorDetails.SensorDetailsOfflineRepository
import com.example.agristation1.data.sensorDetails.SensorState
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

class SensorSyncManager(
    private val sensorDetailsOfflineRepository: SensorDetailsOfflineRepository,
    private val sensorRepository: NetworkSensorRepository,
    private val db: AgriStationDatabase
) {

    suspend fun sync(): SyncResult {
        return try {

            val response = sensorRepository.getSensors()

            if(response.sensors != null) {
                db.withTransaction {
                    sensorDetailsOfflineRepository.upsertSensors(response.sensors.toEntityList())
                }
            }

            SyncResult.Success
        } catch (e: IOException) {
            SyncResult.Error("No connection with server")
        } catch (e: HttpException) {
            SyncResult.Error("Server error: ${e.code()}")
        } catch (e: Exception) {
            SyncResult.Error("Unknown error: ${e.message}")
        }
    }

}

fun List<SensorDetailsNetwork>.toEntityList(): List<SensorDetails> =
    map { it.toEntity() }

fun SensorDetailsNetwork.toEntity(): SensorDetails {
    return SensorDetails(
        id = id,
        fieldId = fieldId,
        name = name,
        latitude = latitude,
        longitude = longitude,
        battery = SensorBattery.fromCode(battery),
        state = SensorState.fromCode(state)
    )
}