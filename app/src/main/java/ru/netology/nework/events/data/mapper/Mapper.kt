package ru.netology.nework.events.data.mapper

import ru.netology.nework.core.data.mappers.toAttachmentEntity
import ru.netology.nework.core.data.mappers.toCordsEntity
import ru.netology.nework.events.data.dto.EventDto
import ru.netology.nework.events.domain.entity.Event

fun EventDto.toEvent(): Event {
    return Event(
        id = this.id,
        authorId = this.authorId,
        author = this.author,
        authorAvatar = this.authorAvatar,
        authorJob = this.authorJob,
        content = this.content,
        datetime = this.datetime,
        published = this.published,
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