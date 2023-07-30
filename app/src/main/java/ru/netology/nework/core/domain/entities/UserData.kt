package ru.netology.nework.core.domain.entities

data class UserData(
    val id: Long,
    val login: String,
    val name: String,
    val avatar: String?
)
