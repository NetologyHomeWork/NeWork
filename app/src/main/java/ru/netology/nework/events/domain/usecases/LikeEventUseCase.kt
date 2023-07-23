package ru.netology.nework.events.domain.usecases

import ru.netology.nework.core.utils.Resource
import ru.netology.nework.events.domain.repository.EventsRepository
import javax.inject.Inject

class LikeEventUseCase @Inject constructor(
    private val eventsRepository: EventsRepository
) {
    suspend fun execute(eventId: Long, isLike: Boolean): Resource<Unit> {
        val response = if (isLike) eventsRepository.dislikeEvent(eventId)
        else eventsRepository.likeEvent(eventId)
        return when (response) {
            is Resource.Success -> Resource.Success(Unit)
            is Resource.Error -> Resource.Error(response.cause)
        }
    }
}