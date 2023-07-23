package ru.netology.nework.create.domain.models

import ru.netology.nework.core.domain.entities.Attachment
import ru.netology.nework.core.domain.entities.Coordinates
import ru.netology.nework.core.domain.entities.EventType

data class CreateEventRequest(
    val id: Long,
    val content: String,
    val datetime: String,
    val cords: Coordinates? = null,
    val type: EventType? = null,
    val attachment: Attachment? = null,
    val link: String? = null,
    val speakerIds: List<Long> = emptyList()
)
