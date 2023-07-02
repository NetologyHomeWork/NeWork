package ru.netology.nework.login.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthenticationModelResponse(
    @SerialName("id") val id: Long,
    @SerialName("token") val token: String
)