package ru.netology.nework.events.data.repository

import ru.netology.nework.core.domain.wrapServerException
import ru.netology.nework.core.utils.Resource
import ru.netology.nework.events.data.mapper.toEvent
import ru.netology.nework.events.data.mapper.toEventList
import ru.netology.nework.events.data.network.EventsApi
import ru.netology.nework.events.domain.entity.Event
import ru.netology.nework.events.domain.repository.EventsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventsRepositoryImpl @Inject constructor(
    private val eventsApi: EventsApi
) : EventsRepository {

    override suspend fun getEvents(): Resource<List<Event>> {
        return wrapServerException {
            eventsApi.getEvents().toEventList()
        }
    }

    override suspend fun likeEvent(eventId: Long): Resource<Event> {
        return wrapServerException {
            eventsApi.likeEvent(eventId).toEvent()
        }
    }

    override suspend fun dislikeEvent(eventId: Long): Resource<Event> {
        return wrapServerException {
            eventsApi.dislikeEvent(eventId).toEvent()
        }
    }

    override suspend fun deletePost(eventId: Long): Resource<Unit> {
        return wrapServerException {
            eventsApi.deleteEvent(eventId)
        }
    }
}