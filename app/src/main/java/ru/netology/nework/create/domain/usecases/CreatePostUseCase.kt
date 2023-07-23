package ru.netology.nework.create.domain.usecases

import ru.netology.nework.core.domain.entities.Attachment
import ru.netology.nework.core.utils.Resource
import ru.netology.nework.create.domain.exceptions.EmptyContentException
import ru.netology.nework.create.domain.models.CreatePostRequest
import ru.netology.nework.create.domain.repository.CreateRepository
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(
    private val createRepository: CreateRepository
) {
    suspend fun execute(
        content: String,
        attachment: Attachment?
    ): Resource<Unit> {

        if (content.isBlank()) return Resource.Error(EmptyContentException())
        val resource = createRepository.savePost(
            CreatePostRequest(
                id = ID_FOR_CREATE_POST,
                content = content,
                attachment = attachment
            )
        )
         return when (resource) {
            is Resource.Success -> Resource.Success(Unit)
            is Resource.Error -> Resource.Error(resource.cause)
        }
    }

    private companion object {
        private const val ID_FOR_CREATE_POST = 0L
    }
}