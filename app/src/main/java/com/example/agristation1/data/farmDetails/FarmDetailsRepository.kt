package com.example.agristation1.data.farmDetails

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface FarmDetailsRepository {
    fun getFarmDetailsStream(): Flow<FarmDetails?>

    suspend fun upsertFarmDetails(farmDetails: FarmDetails)
}

class FarmDetailsOfflineRepository @Inject constructor(
    private val farmDetailsDao: FarmDetailsDao
) : FarmDetailsRepository {

    override fun getFarmDetailsStream(): Flow<FarmDetails?> {
        return farmDetailsDao.getFarmDetails()
    }

    override suspend fun upsertFarmDetails(farmDetails: FarmDetails) {
        farmDetailsDao.upsertFarmDetails(farmDetails)
    }

}