package ru.netology.nework.create.domain.usecases

import ru.netology.nework.core.domain.entities.Attachment
import ru.netology.nework.core.domain.entities.EventType
import ru.netology.nework.core.utils.Resource
import ru.netology.nework.core.utils.toIso8601
import ru.netology.nework.create.domain.exceptions.EmptyContentException
import ru.netology.nework.create.domain.exceptions.EmptyDateException
import ru.netology.nework.create.domain.models.CreateEventRequest
import ru.netology.nework.create.domain.repository.CreateRepository
import javax.inject.Inject

class CreateEventUseCase @Inject constructor(
    private val createRepository: CreateRepository
) {
    suspend fun execute(
        content: String,
        dataTime: String,
        eventType: EventType?,
        attachment: Attachment?
    ): Resource<Unit> {

        if (content.isBlank()) return Resource.Error(EmptyContentException())
        if (dataTime.isBlank()) return Resource.Error(EmptyDateException())

        val resource = createRepository.saveEvent(
            CreateEventRequest(
                id = ID_FOR_CREATE_EVENT,
                content = content,
                datetime = dataTime.toIso8601(),
                type = eventType,
                attachment = attachment
            )
        )
        return when (resource) {
            is Resource.Success -> Resource.Success(Unit)
            is Resource.Error -> Resource.Error(resource.cause)
        }
    }

    private companion object {
        private const val ID_FOR_CREATE_EVENT = 0L
    }
}