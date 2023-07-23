package ru.netology.nework.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.netology.nework.core.data.mappers.toAttachmentEntity
import ru.netology.nework.core.data.mappers.toCordsEntity
import ru.netology.nework.core.domain.entities.Event
import ru.netology.nework.core.domain.entities.EventType
import ru.netology.nework.core.utils.dateFormat

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

fun EventDto.toEvent(): Event {
    return Event(
        id = this.id,
        authorId = this.authorId,
        author = this.author,
        authorAvatar = this.authorAvatar,
        authorJob = this.authorJob,
        content = this.content,
        datetime = this.datetime.dateFormat(),
        published = this.published.dateFormat(),
        coords = this.coords.toCordsEntity(),
        type = this.type,
        likeOwnerIds = this.likeOwnerIds,
        likedByMe = this.likedByMe,
        speakerIds = this.speakerIds,
        participantsIds = this.participantsIds,
        participatedByMe = this.participatedByMe,
        attachment = this.attachment.toAttachmentEntity(),
        link = this.link,
        ownedByMe = this.ownedByMe
    )
}

fun List<EventDto>.toEventList(): List<Event> {
    return this.map { it.toEvent() }
}