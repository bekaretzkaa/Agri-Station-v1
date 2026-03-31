package com.example.agristation1.data.farmDetails

import kotlinx.coroutines.flow.Flow

interface FarmDetailsRepository {
    fun getFarmDetailsStream(): Flow<FarmDetails>
}

class FarmDetailsOfflineRepository(
    private val farmDetailsDao: FarmDetailsDao
) : FarmDetailsRepository {

    override fun getFarmDetailsStream(): Flow<FarmDetails> {
        return farmDetailsDao.getFarmDetails()
    }

}