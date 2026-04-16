package com.example.agristation1.network.fieldNetwork

import retrofit2.http.GET
import retrofit2.http.Query

interface FieldApiService {

    @GET("fields/sync")
    suspend fun getFields(
        @Query("since") since: Long? = null
    ): FieldsSyncResponseDto

}