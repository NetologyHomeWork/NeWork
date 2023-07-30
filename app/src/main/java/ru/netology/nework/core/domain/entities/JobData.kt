package ru.netology.nework.core.domain.entities

data class JobData(
    val id: Long,
    val name: String,
    val position: String,
    val start: String,
    val finish: String?,
    val link: String?
)
