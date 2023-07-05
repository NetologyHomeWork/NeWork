package ru.netology.nework.core.domain.entities

data class Post(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val authorJob: String?,
    val content: String,
    val published: String,
    val coords: Coordinates?,
    val link: String?,
    val likeOwnerIds: List<Int>,
    val mentionIds: List<Int>,
    val mentionedMe: Boolean,
    val likedByMe: Boolean,
    val attachment: Attachment?,
    val ownedByMe: Boolean
)

data class Coordinates(
    val lat: String,
    val long: String?
)

data class Attachment(
    val url: String,
    val type: AttachmentType
)

enum class AttachmentType(type: String) {
    IMAGE("IMAGE"),
    VIDEO("VIDEO"),
    AUDIO("AUDIO");

    companion object {
        fun parse(type: String): AttachmentType = values().find { it.name == type } ?: IMAGE
    }
}