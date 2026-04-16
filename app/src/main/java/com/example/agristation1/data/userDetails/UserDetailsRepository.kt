package com.example.agristation1.data.userDetails

import kotlinx.coroutines.flow.Flow

interface UserDetailsRepository {

    fun getUserDetailsById(id: Long): Flow<UserDetails?>

    suspend fun upsertUserDetails(userDetails: UserDetails)

}

class UserDetailsOfflineRepository(
    private val userDetailsDao: UserDetailsDao
) : UserDetailsRepository {

    override fun getUserDetailsById(id: Long): Flow<UserDetails?> =
        userDetailsDao.getUserDetailsById(id)

    override suspend fun upsertUserDetails(userDetails: UserDetails) {
        userDetailsDao.upsertUserDetails(userDetails)
    }

}