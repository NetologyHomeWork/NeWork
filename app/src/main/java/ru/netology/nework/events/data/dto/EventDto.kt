package ru.netology.nework.events.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.netology.nework.core.data.dto.AttachmentDto
import ru.netology.nework.core.data.dto.CoordinatesDto
import ru.netology.nework.events.domain.entity.EventType

@Serializable
data class EventDto(
    @SerialName("id") val id: Long,
    @SerialName("authorId") val authorId: Long,
    @SerialName("author") val author: String,
    @SerialName("authorAvatar") val authorAvatar: String?,
    @SerialName("authorJob") val authorJob: String?,
    @SerialName("content") val content: String,
    @SerialName("datetime") val datetime: String,
    @SerialName("published") val published: String,
    @SerialName("coords") val coords: CoordinatesDto?,
    @SerialName("type") val type: EventType,
    @SerialName("likeOwnerIds") val likeOwnerIds: List<Int>,
    @SerialName("likedByMe") val likedByMe: Boolean,
    @SerialName("speakerIds") val speakerIds: List<Int>,
    @SerialName("participantsIds") val participantsIds: List<Int>,
    @SerialName("participatedByMe") val participatedByMe: Boolean,
    @SerialName("attachment") val attachment: AttachmentDto?,
    @SerialName("link") val link: String?,
    @SerialName("ownedByMe") val ownedByMe: Boolean
)