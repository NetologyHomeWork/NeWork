package ru.netology.nework.create.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.netology.nework.core.data.dto.AttachmentDto
import ru.netology.nework.core.data.dto.CoordinatesDto
import ru.netology.nework.core.data.mappers.toDto
import ru.netology.nework.create.domain.models.CreatePostRequest

@Serializable
data class CreatePostRequestDto(
    @SerialName("id") val id: Long,
    @SerialName("content") val content: String,
    @SerialName("cords") val cords: CoordinatesDto? = null,
    @SerialName("link") val link: String? = null,
    @SerialName("attachment") val attachment: AttachmentDto? = null,
    @SerialName("mentionIds") val mentionIds: List<Long> = emptyList()
)

fun CreatePostRequest.toDto(): CreatePostRequestDto {
    return CreatePostRequestDto(
        id = this.id,
        content = this.content,
        cords = this.cords.toDto(),
        link = this.link,
        attachment = this.attachment.toDto(),
        mentionIds = this.mentionIds
    )
}