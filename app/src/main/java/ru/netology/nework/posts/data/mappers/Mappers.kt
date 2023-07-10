package ru.netology.nework.posts.data.mappers

import ru.netology.nework.core.domain.entities.Post
import ru.netology.nework.posts.presentation.posts.adapter.PostItem

fun Post.toPostItem(): PostItem {
    return PostItem(
        id = this.id,
        authorId = this.authorId,
        author = this.author,
        authorAvatar = this.authorAvatar,
        content = this.content,
        published = this.published,
        likeCount = this.likeOwnerIds.size,
        likedByMe = this.likedByMe,
        attachment = this.attachment,
        ownedByMe = this.ownedByMe,
        inProgressToDelete = false
    )
}

fun List<Post>.toListPostItem(): List<PostItem> = this.map(Post::toPostItem)