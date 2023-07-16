package ru.netology.nework.events.domain.repository

import ru.netology.nework.core.utils.Resource
import ru.netology.nework.events.domain.entity.Event

interface EventsRepository {
    suspend fun getEvents(): Resource<List<Event>>
    suspend fun likeEvent(eventId: Long): Resource<Event>
    suspend fun dislikeEvent(eventId: Long): Resource<Event>
    suspend fun deletePost(eventId: Long): Resource<Unit>
}