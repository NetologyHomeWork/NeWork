package ru.netology.nework.posts.presentation.posts.adapter

import ru.netology.nework.core.domain.entities.Attachment
import ru.netology.nework.core.domain.entities.Coordinates

data class PostItem(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val content: String,
    val published: String,
    val coords: Coordinates?,
    val likeCount: Int,
    val likedByMe: Boolean,
    val attachment: Attachment?,
    val ownedByMe: Boolean,
    val inProgressToDelete: Boolean
)
