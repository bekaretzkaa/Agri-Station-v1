package com.example.agristation1.data.userDetails

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDetailsDao {

    @Query("SELECT * FROM user_details WHERE id = :id")
    fun getUserDetailsById(id: Long): Flow<UserDetails?>

    @Upsert
    suspend fun upsertUserDetails(userDetails: UserDetails)

}