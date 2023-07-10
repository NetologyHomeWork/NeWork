package ru.netology.nework.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostDto(
    @SerialName("id") val id: Long,
    @SerialName("authorId") val authorId: Long,
    @SerialName("author") val author: String,
    @SerialName("authorAvatar") val authorAvatar: String?,
    @SerialName("authorJob") val authorJob: String?,
    @SerialName("content") val content: String,
    @SerialName("published") val published: String,
    @SerialName("coords") val coords: CoordinatesDto?,
    @SerialName("link") val link: String?,
    @SerialName("likeOwnerIds") val likeOwnerIds: List<Int> = emptyList(),
    @SerialName("mentionIds") val mentionIds: List<Int> = emptyList(),
    @SerialName("mentionedMe") val mentionedMe: Boolean,
    @SerialName("likedByMe") val likedByMe: Boolean,
    @SerialName("attachment") val attachment: AttachmentDto?,
    @SerialName("ownedByMe") val ownedByMe: Boolean
)

@Serializable
data class CoordinatesDto(
    @SerialName("lat") val lat: String,
    @SerialName("long") val long: String?
)

@Serializable
data class AttachmentDto(
    @SerialName("url") val url: String,
    @SerialName("type") val type: String
)