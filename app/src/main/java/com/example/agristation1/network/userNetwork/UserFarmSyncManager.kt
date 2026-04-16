package com.example.agristation1.network.userNetwork

import androidx.room.withTransaction
import com.example.agristation1.data.AgriStationDatabase
import com.example.agristation1.data.SyncResult
import com.example.agristation1.data.farmDetails.FarmDetails
import com.example.agristation1.data.farmDetails.FarmDetailsOfflineRepository
import com.example.agristation1.data.userDetails.UserDetails
import com.example.agristation1.data.userDetails.UserDetailsDao
import com.example.agristation1.data.userDetails.UserDetailsOfflineRepository
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

class UserFarmSyncManager(
    private val userDetailsOfflineRepository: UserDetailsOfflineRepository,
    private val farmDetailsOfflineRepository: FarmDetailsOfflineRepository,
    private val userFarmRepository: NetworkUserFarmRepository,
    private val db: AgriStationDatabase
) {

    suspend fun sync(): SyncResult {
        return try {
            val farmResponse = userFarmRepository.getFarmDetails()
            val userResponse = userFarmRepository.getUserDetails()

            db.withTransaction {
                userDetailsOfflineRepository.upsertUserDetails(userResponse.toEntity())
                farmDetailsOfflineRepository.upsertFarmDetails(farmResponse.toEntity())
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

fun FarmDetailsNetwork.toEntity(): FarmDetails {
    return FarmDetails(
        id = id,
        farmName = farmName,
        activeSensors = activeSensors,
        totalSensors = totalSensors
    )
}

fun UserDetailsNetwork.toEntity(): UserDetails {
    return UserDetails(
        id = id,
        farmId = farmId,
        role = role,
        name = name,
        surname = surname,
        email = email,
        company = company
    )
}

