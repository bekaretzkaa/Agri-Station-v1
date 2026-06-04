package com.example.agristation1.network.fieldNetwork

import javax.inject.Inject

interface NetworkFieldRepository {
    suspend fun getFields(since: Long): FieldsSyncResponseDto
}

class NetworkFieldRepositoryImpl @Inject constructor(
    private val fieldApiService: FieldApiService
) : NetworkFieldRepository {

    override suspend fun getFields(since: Long): FieldsSyncResponseDto {
        return fieldApiService.getFields(since)
    }

}