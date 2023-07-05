package ru.netology.nework.core.domain.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponseBody(
    @SerialName("reason") val reason: String
)