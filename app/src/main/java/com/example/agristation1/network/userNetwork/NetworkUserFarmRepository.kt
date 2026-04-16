package com.example.agristation1.network.userNetwork

interface NetworkUserFarmRepository {

    suspend fun getFarmDetails(): FarmDetailsNetwork

    suspend fun getUserDetails(): UserDetailsNetwork

}

class NetworkUserFarmRepositoryImpl (
    private val userFarmApiService: UserFarmApiService
) : NetworkUserFarmRepository {

    override suspend fun getFarmDetails(): FarmDetailsNetwork {
        return userFarmApiService.getFarmDetails()
    }

    override suspend fun getUserDetails(): UserDetailsNetwork {
        return userFarmApiService.getUserDetails()
    }

}