package ru.netology.nework.create.data.models

import kotlinx.serialization.Serializable
import ru.netology.nework.core.data.dto.AttachmentDto
import ru.netology.nework.core.data.dto.CoordinatesDto
import ru.netology.nework.core.data.mappers.toDto
import ru.netology.nework.core.domain.entities.EventType
import ru.netology.nework.create.domain.models.CreateEventRequest

@Serializable
data class CreateEventRequestDto(
    val id: Long,
    val content: String,
    val datetime: String,
    val cords: CoordinatesDto? = null,
    val type: EventType? = null,
    val attachment: AttachmentDto? = null,
    val link: String? = null,
    val speakerIds: List<Long> = emptyList()
)

fun CreateEventRequest.toDto(): CreateEventRequestDto {
    return CreateEventRequestDto(
        id = this.id,
        content = this.content,
        datetime = this.datetime,
        cords = this.cords.toDto(),
        type = this.type,
        attachment = this.attachment.toDto(),
        link = this.link,
        speakerIds = this.speakerIds
    )
}