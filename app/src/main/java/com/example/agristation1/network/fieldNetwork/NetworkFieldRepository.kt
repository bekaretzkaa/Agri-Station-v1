package com.example.agristation1.network.fieldNetwork

interface NetworkFieldRepository {
    suspend fun getFields(since: Long): FieldsSyncResponseDto
}

class NetworkFieldRepositoryImpl(
    private val fieldApiService: FieldApiService
) : NetworkFieldRepository {

    override suspend fun getFields(since: Long): FieldsSyncResponseDto {
        return fieldApiService.getFields(since)
    }

}