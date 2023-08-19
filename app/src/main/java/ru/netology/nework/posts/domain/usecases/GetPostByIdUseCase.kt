package ru.netology.nework.posts.domain.usecases

import ru.netology.nework.core.domain.entities.Post
import ru.netology.nework.core.utils.Resource
import ru.netology.nework.posts.domain.repository.PostsRepository
import javax.inject.Inject

class GetPostByIdUseCase @Inject constructor(
    private val postsRepository: PostsRepository
) {
    suspend fun execute(postId: Long): Resource<Post> {
        return postsRepository.getPostById(postId)
    }
}