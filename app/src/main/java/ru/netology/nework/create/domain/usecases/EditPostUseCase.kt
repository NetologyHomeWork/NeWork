package ru.netology.nework.create.domain.usecases

import ru.netology.nework.core.domain.entities.Attachment
import ru.netology.nework.core.utils.Resource
import ru.netology.nework.create.domain.exceptions.EmptyContentException
import ru.netology.nework.create.domain.models.CreatePostRequest
import ru.netology.nework.create.domain.repository.CreateRepository
import javax.inject.Inject

class EditPostUseCase @Inject constructor(
    private val createRepository: CreateRepository
) {
    suspend fun execute(
        postId: Long,
        content: String,
        attachment: Attachment? = null
    ): Resource<Unit> {
        if (content.isBlank()) return Resource.Error(EmptyContentException())
        val resource = createRepository.savePost(
            CreatePostRequest(
                id = postId,
                content = content,
                attachment = attachment
            )
        )
        return when (resource) {
            is Resource.Success -> Resource.Success(Unit)
            is Resource.Error -> Resource.Error(resource.cause)
        }
    }
}