package ru.netology.nework.core.data.mappers

import ru.netology.nework.core.data.dto.*
import ru.netology.nework.core.domain.entities.*
import ru.netology.nework.core.utils.dateFormat

fun PostDto.toPostEntity(): Post {
    return Post(
        id = this.id,
        authorId = this.authorId,
        author = this.author,
        authorAvatar = this.authorAvatar,
        authorJob = this.authorJob,
        content = this.content,
        published = this.published.dateFormat(),
        coords = this.coords?.toCordsEntity(),
        link = this.link,
        likeOwnerIds = this.likeOwnerIds,
        mentionIds = this.mentionIds,
        mentionedMe = this.mentionedMe,
        likedByMe = this.likedByMe,
        attachment = this.attachment?.toAttachmentEntity(),
        ownedByMe = this.ownedByMe
    )
}

fun List<PostDto>.toListPostEntity(): List<Post> {
    return this.map { it.toPostEntity() }
}

fun CoordinatesDto?.toCordsEntity(): Coordinates? {
    if (this == null) return null
    return Coordinates(
        lat = this.lat,
        long = this.long
    )
}

fun AttachmentDto?.toAttachmentEntity(): Attachment? {
    if (this == null) return null
    return Attachment(
        url = this.url,
        type = AttachmentType.parse(this.type)
    )
}

fun UserDataDto.toUserData(): UserData {
    return UserData(
        id = this.id,
        login = this.login,
        name = this.name,
        avatar = this.avatar
    )
}

fun JobDataDto.toJobData(): JobData {
    return JobData(
        id = this.id,
        name = this.name,
        position = this.position,
        start = this.start,
        finish = this.finish,
        link = this.link
    )
}

fun JobData.toJobDataDto(): JobDataDto {
    return JobDataDto(
        id = this.id,
        name = this.name,
        position = this.position,
        start = this.start,
        finish = this.finish,
        link = this.link
    )
}

fun List<JobDataDto>.toJobList(): List<JobData> {
    return this.map { it.toJobData() }
}

fun Attachment?.toDto(): AttachmentDto? {
    if (this == null) return null
    return AttachmentDto(
        url = this.url,
        type = this.type.type
    )
}

fun Coordinates?.toDto(): CoordinatesDto? {
    if (this == null) return null
    return CoordinatesDto(
        lat = this.lat,
        long = this.long
    )
}

