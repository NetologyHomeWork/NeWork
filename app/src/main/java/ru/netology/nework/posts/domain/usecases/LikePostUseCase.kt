package ru.netology.nework.posts.domain.usecases

import ru.netology.nework.core.utils.Resource
import ru.netology.nework.posts.domain.repository.PostsRepository
import javax.inject.Inject

class LikePostUseCase @Inject constructor(
    private val repository: PostsRepository
) {

    suspend fun execute(postId: Long, isLike: Boolean): Resource<Unit> {
        val response = if (isLike) repository.dislikePost(postId)
        else repository.likePost(postId)
        return when (response) {
            is Resource.Success -> Resource.Success(Unit)
            is Resource.Error -> Resource.Error(response.cause)
        }
    }
}