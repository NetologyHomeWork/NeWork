package ru.netology.nework.core.data.mappers

import ru.netology.nework.core.data.dto.AttachmentDto
import ru.netology.nework.core.data.dto.CoordinatesDto
import ru.netology.nework.core.data.dto.PostDto
import ru.netology.nework.core.domain.entities.Attachment
import ru.netology.nework.core.domain.entities.AttachmentType
import ru.netology.nework.core.domain.entities.Coordinates
import ru.netology.nework.core.domain.entities.Post
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

private fun CoordinatesDto.toCordsEntity(): Coordinates {
    return Coordinates(
        lat = this.lat,
        long = this.long
    )
}

private fun AttachmentDto.toAttachmentEntity(): Attachment {
    return Attachment(
        url = this.url,
        type = AttachmentType.parse(this.type)
    )
}
