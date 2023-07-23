package ru.netology.nework.create.data.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.netology.nework.core.data.dto.EventDto
import ru.netology.nework.core.data.dto.JobDataDto
import ru.netology.nework.core.data.dto.PostDto
import ru.netology.nework.core.data.dto.UserDataDto
import ru.netology.nework.create.data.models.CreateEventRequestDto
import ru.netology.nework.create.data.models.CreatePostRequestDto

interface CreateApi {

    @POST("api/posts/")
    suspend fun savePost(
        @Body body: CreatePostRequestDto
    ): PostDto

    @POST("api/events/")
    suspend fun saveEvent(
        @Body body: CreateEventRequestDto
    ): EventDto

    @GET("api/users/{user_id}/")
    suspend fun getUserData(
        @Path("user_id") userId: Long
    ): UserDataDto

    @GET("api/{user_id}/jobs/")
    suspend fun getJobData(
        @Path("user_id") userId: Long
    ): JobDataDto
}