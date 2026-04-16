package com.example.agristation1.network.fieldNetwork

import androidx.room.withTransaction
import com.example.agristation1.data.AgriStationDatabase
import com.example.agristation1.data.SyncResult
import com.example.agristation1.data.fieldDetails.FieldConnectivity
import com.example.agristation1.data.fieldDetails.FieldDetails
import com.example.agristation1.data.fieldDetails.FieldDetailsOfflineRepository
import com.example.agristation1.data.fieldDetails.FieldHealth
import com.example.agristation1.data.fieldDetails.FieldLifecycle
import com.example.agristation1.data.fieldDetails.FieldPoints
import com.example.agristation1.data.historyDetails.HistoryDetails
import com.example.agristation1.data.historyDetails.HistoryOfflineRepository
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import java.time.Instant

class FieldSyncManager(
    private val fieldDetailsRepository: FieldDetailsOfflineRepository,
    private val historyOfflineRepository: HistoryOfflineRepository,
    private val fieldRepository: NetworkFieldRepository,
    private val db: AgriStationDatabase
) {

    suspend fun sync(since: Long): SyncResult {
        return try {

            val response = fieldRepository.getFields(since)

            db.withTransaction {
                response.fields.forEach { fieldDto ->
                    val existing = fieldDetailsRepository.getFieldById(fieldDto.id)

                    fieldDetailsRepository.upsert(fieldDto.toEntity(existing))

                    historyOfflineRepository.insertAll(fieldDto.history.toEntities(fieldDto.id))
                }

                fieldDetailsRepository.upsertFieldPoints(response.fieldPoints.toEntities())
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

fun FieldHistoryPointDto.toEntity(fieldId: Long): HistoryDetails = HistoryDetails(
    fieldId = fieldId,
    recordedAt = Instant.ofEpochSecond(recordedAt),
    soilMoisture = soilMoisture,
    soilTemperature = soilTemperature,
    airTemperature = airTemperature,
    airHumidity = airHumidity,
    lux = lux,
)

fun List<FieldHistoryPointDto>.toEntities(fieldId: Long): List<HistoryDetails> =
    map { it.toEntity(fieldId) }

fun FieldDto.toEntity(existing: FieldDetails?): FieldDetails {
    val s = snapshot

    return FieldDetails(
        id = id,
        farmId = farmId,
        title = title,
        area = area ?: existing?.area,
        type = type ?: existing?.type,

        soilMoisture = s.soilMoisture,
        soilTemperature = s.soilTemperature,
        airTemperature = s.airTemperature,
        airHumidity = s.airHumidity,
        lux = s.lux,

        lastValidSoilMoisture = existing?.soilMoisture ?: existing?.lastValidSoilMoisture,
        lastValidSoilTemperature = existing?.soilTemperature ?: existing?.lastValidSoilTemperature,
        lastValidAirTemperature = existing?.airTemperature ?: existing?.lastValidAirTemperature,
        lastValidAirHumidity = existing?.airHumidity ?: existing?.lastValidAirHumidity,
        lastValidLux = existing?.lux ?: existing?.lastValidLux,

        totalSensors = s.totalSensors,
        activeSensors = s.activeSensors,

        health = FieldHealth.fromCode(s.health),
        connectivity = FieldConnectivity.fromCode(s.connectivity),
        lifecycle = FieldLifecycle.fromCode(s.lifecycle)
    )
}

fun FieldPointDto.toEntity(): FieldPoints = FieldPoints(
    id = id,
    fieldId = fieldId,
    pointOrder = pointOrder,
    latitude = latitude,
    longitude = longitude
)

fun List<FieldPointDto>.toEntities(): List<FieldPoints> = map { it.toEntity() }