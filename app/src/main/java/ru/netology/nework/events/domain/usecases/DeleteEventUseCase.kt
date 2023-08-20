package ru.netology.nework.events.domain.usecases

import ru.netology.nework.core.utils.Resource
import ru.netology.nework.events.domain.repository.EventsRepository
import javax.inject.Inject

class DeleteEventUseCase @Inject constructor(
    private val eventsRepository: EventsRepository
) {
    suspend fun execute(eventId: Long): Resource<Unit> {
        return eventsRepository.deleteEvent(eventId)
    }
}