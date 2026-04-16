package com.example.agristation1.network.userNetwork

import kotlinx.serialization.Serializable

@Serializable
data class UserDetailsNetwork(
    val id: Long,
    val farmId: Long?,
    val role: String,
    val name: String,
    val surname: String,
    val email: String,
    val company: String
)