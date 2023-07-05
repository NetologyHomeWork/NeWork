package ru.netology.nework.posts.data.network

import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.netology.nework.core.data.dto.PostDto

interface PostsApi {

    @GET("api/posts/")
    suspend fun getPosts(): List<PostDto>

    @POST("api/posts/{post_id}/likes/")
    suspend fun likePost(
        @Path("post_id") postId: Long
    ): PostDto

    @DELETE("api/posts/{post_id}/likes/")
    suspend fun dislikePost(
        @Path("post_id") postId: Long
    ): PostDto

    @DELETE("api/posts/{post_id}/")
    suspend fun deletePost(
        @Path("post_id") postId: Long
    )
}