package ru.netology.nework.events.domain.repository

import ru.netology.nework.core.domain.entities.Event
import ru.netology.nework.core.utils.Resource

interface EventsRepository {
    suspend fun getEvents(): Resource<List<Event>>
    suspend fun likeEvent(eventId: Long): Resource<Event>
    suspend fun dislikeEvent(eventId: Long): Resource<Event>
    suspend fun deleteEvent(eventId: Long): Resource<Unit>
    suspend fun getEventById(eventId: Long): Resource<Event>
}