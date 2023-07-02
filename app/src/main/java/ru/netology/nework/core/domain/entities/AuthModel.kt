package ru.netology.nework.core.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class AuthModel(
    val id: Long,
    val token: String
)