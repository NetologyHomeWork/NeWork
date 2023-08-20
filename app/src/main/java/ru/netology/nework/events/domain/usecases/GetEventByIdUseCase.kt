package ru.netology.nework.events.domain.usecases

import ru.netology.nework.core.domain.entities.Event
import ru.netology.nework.core.utils.Resource
import ru.netology.nework.events.domain.repository.EventsRepository
import javax.inject.Inject

class GetEventByIdUseCase @Inject constructor(
    private val repository: EventsRepository
) {
    suspend fun execute(eventId: Long): Resource<Event> {
        return repository.getEventById(eventId)
    }
}