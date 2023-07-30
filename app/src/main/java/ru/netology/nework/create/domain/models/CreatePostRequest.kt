package ru.netology.nework.create.domain.models

import ru.netology.nework.core.domain.entities.Attachment
import ru.netology.nework.core.domain.entities.Coordinates

data class CreatePostRequest(
    val id: Long,
    val content: String,
    val cords: Coordinates? = null,
    val link: String? = null,
    val attachment: Attachment? = null,
    val mentionIds: List<Long> = emptyList()
)