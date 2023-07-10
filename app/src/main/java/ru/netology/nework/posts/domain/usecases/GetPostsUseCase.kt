package ru.netology.nework.posts.domain.usecases

import ru.netology.nework.core.domain.entities.Post
import ru.netology.nework.core.utils.Resource
import ru.netology.nework.posts.domain.repository.PostsRepository
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(
    private val postsRepository: PostsRepository
) {
    suspend fun execute(): Resource<List<Post>> {
        return postsRepository.getPosts()
    }
}