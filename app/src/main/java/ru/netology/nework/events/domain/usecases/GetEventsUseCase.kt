package ru.netology.nework.events.domain.usecases

import ru.netology.nework.core.domain.entities.Event
import ru.netology.nework.core.utils.Resource
import ru.netology.nework.events.domain.repository.EventsRepository
import javax.inject.Inject

class GetEventsUseCase @Inject constructor(
    private val eventsRepository: EventsRepository
) {
    suspend fun execute(): Resource<List<Event>> {
        return eventsRepository.getEvents()
    }
}