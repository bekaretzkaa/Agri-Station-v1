package com.example.agristation1.data.farmDetails

import kotlinx.coroutines.flow.Flow

interface FarmDetailsRepository {
    fun getFarmDetailsStream(): Flow<FarmDetails?>

    suspend fun upsertFarmDetails(farmDetails: FarmDetails)
}

class FarmDetailsOfflineRepository(
    private val farmDetailsDao: FarmDetailsDao
) : FarmDetailsRepository {

    override fun getFarmDetailsStream(): Flow<FarmDetails?> {
        return farmDetailsDao.getFarmDetails()
    }

    override suspend fun upsertFarmDetails(farmDetails: FarmDetails) {
        farmDetailsDao.upsertFarmDetails(farmDetails)
    }

}