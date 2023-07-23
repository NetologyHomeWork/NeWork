package ru.netology.nework.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JobDataDto(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("position") val position: String,
    @SerialName("start") val start: String,
    @SerialName("finish") val finish: String?,
    @SerialName("link") val link: String?
)