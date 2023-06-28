package ru.netology.nework.login.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthenticationModelRequest(
    @SerialName("login") val login: String,
    @SerialName("password") val password: String
)