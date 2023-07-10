package ru.netology.nework.posts.data.repository

import ru.netology.nework.core.data.mappers.toListPostEntity
import ru.netology.nework.core.data.mappers.toPostEntity
import ru.netology.nework.core.domain.entities.Post
import ru.netology.nework.core.domain.wrapServerException
import ru.netology.nework.core.utils.Resource
import ru.netology.nework.posts.data.network.PostsApi
import ru.netology.nework.posts.domain.repository.PostsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostsRepositoryImpl @Inject constructor(
    private val postsApi: PostsApi
) : PostsRepository {

    override suspend fun getPosts(): Resource<List<Post>> {
        return wrapServerException {
            postsApi.getPosts().toListPostEntity()
        }
    }

    override suspend fun likePost(postId: Long): Resource<Post> {
        return wrapServerException {
            postsApi.likePost(postId).toPostEntity()
        }
    }

    override suspend fun dislikePost(postId: Long): Resource<Post> {
        return wrapServerException {
            postsApi.dislikePost(postId).toPostEntity()
        }
    }

    override suspend fun deletePost(postId: Long): Resource<Unit> {
        return wrapServerException {
            postsApi.deletePost(postId)
        }
    }
}