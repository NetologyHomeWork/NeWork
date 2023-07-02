package ru.netology.nework.core.domain.entities

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ErrorResponseBody(
    @SerialName("reason") val reason: String
)