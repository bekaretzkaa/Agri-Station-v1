package com.example.agristation1.network.userNetwork

import retrofit2.http.GET

interface UserFarmApiService {
    @GET("user/farm")
    suspend fun getFarmDetails(): FarmDetailsNetwork

    @GET("user/details")
    suspend fun getUserDetails(): UserDetailsNetwork
}