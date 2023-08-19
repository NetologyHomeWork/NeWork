package ru.netology.nework.posts.domain.repository

import ru.netology.nework.core.domain.entities.Post
import ru.netology.nework.core.utils.Resource

interface PostsRepository {
    suspend fun getPosts(): Resource<List<Post>>
    suspend fun likePost(postId: Long): Resource<Post>
    suspend fun dislikePost(postId: Long): Resource<Post>
    suspend fun deletePost(postId: Long): Resource<Unit>
    suspend fun getPostById(postId: Long): Resource<Post>
}