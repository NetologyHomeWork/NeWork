package ru.netology.nework.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDataDto(
    @SerialName("id") val id: Long,
    @SerialName("login") val login: String,
    @SerialName("name") val name: String,
    @SerialName("avatar") val avatar: String?
)