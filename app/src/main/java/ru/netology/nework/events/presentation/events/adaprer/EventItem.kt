package ru.netology.nework.events.presentation.events.adaprer

import ru.netology.nework.R
import ru.netology.nework.core.domain.entities.Attachment
import ru.netology.nework.core.domain.entities.Coordinates
import ru.netology.nework.core.domain.entities.Event
import ru.netology.nework.core.utils.ResourcesManager
import javax.inject.Inject

data class EventItem(
    val id:	Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val authorJob: String,
    val content: String,
    val datetime: String,
    val published: String,
    val coords: Coordinates?,
    val type: String,
    val likeCount: Int,
    val likedByMe: Boolean,
    val attachment: Attachment?,
    val ownedByMe: Boolean,
    val inProgressToDelete: Boolean
)

class EventMapper @Inject constructor(
    private val resourcesManager: ResourcesManager
) {
    fun mapEventToEventItem(event: Event): EventItem {
        return EventItem(
            id = event.id,
            authorId = event.authorId,
            author = event.author,
            authorAvatar = event.authorAvatar,
            authorJob = event.authorJob ?: resourcesManager.getString(R.string.no_job),
            content = event.content,
            datetime = event.datetime,
            published = event.published,
            coords = event.coords,
            type = event.type.typeEvent,
            likeCount = event.likeOwnerIds.size,
            likedByMe = event.likedByMe,
            attachment = event.attachment,
            ownedByMe = event.ownedByMe,
            inProgressToDelete = false
        )
    }

    fun mapListEventToListEventItem(eventList: List<Event>): List<EventItem> {
        return eventList.map(this::mapEventToEventItem)
    }
}