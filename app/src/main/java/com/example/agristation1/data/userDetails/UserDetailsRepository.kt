package com.example.agristation1.data.userDetails

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface UserDetailsRepository {

    fun getUserDetailsById(id: Long): Flow<UserDetails?>

    suspend fun upsertUserDetails(userDetails: UserDetails)

}

class UserDetailsOfflineRepository @Inject constructor(
    private val userDetailsDao: UserDetailsDao
) : UserDetailsRepository {

    override fun getUserDetailsById(id: Long): Flow<UserDetails?> =
        userDetailsDao.getUserDetailsById(id)

    override suspend fun upsertUserDetails(userDetails: UserDetails) {
        userDetailsDao.upsertUserDetails(userDetails)
    }

}