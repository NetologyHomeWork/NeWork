package ru.netology.nework.posts.domain.usecases

import ru.netology.nework.core.utils.Resource
import ru.netology.nework.posts.domain.repository.PostsRepository
import javax.inject.Inject

class DeletePostUseCase @Inject constructor(
    private val repository: PostsRepository
) {
    suspend fun execute(postId: Long): Resource<Unit> {
        return repository.deletePost(postId)
    }
}