package ru.netology.nework.events.domain.entity

import ru.netology.nework.core.domain.entities.Attachment
import ru.netology.nework.core.domain.entities.Coordinates

data class Event(
    val id:	Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val authorJob: String?,
    val content: String,
    val datetime: String,
    val published: String,
    val coords: Coordinates?,
    val type: EventType,
    val likeOwnerIds: List<Int>,
    val likedByMe: Boolean,
    val speakerIds: List<Int>,
    val participantsIds: List<Int>,
    val participatedByMe: Boolean,
    val attachment: Attachment?,
    val link: String?,
    val ownedByMe: Boolean
)

enum class EventType(type: String) {
    OFFLINE("OFFLINE"),
    ONLINE("ONLINE");

    companion object {
        fun parse(type: String): EventType = EventType.values().find { it.name == type } ?: OFFLINE
    }
}