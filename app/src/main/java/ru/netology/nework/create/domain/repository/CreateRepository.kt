package ru.netology.nework.create.domain.repository

import ru.netology.nework.core.domain.entities.Event
import ru.netology.nework.core.domain.entities.JobData
import ru.netology.nework.core.domain.entities.Post
import ru.netology.nework.core.domain.entities.UserData
import ru.netology.nework.core.utils.Resource
import ru.netology.nework.create.domain.models.CreateEventRequest
import ru.netology.nework.create.domain.models.CreatePostRequest

interface CreateRepository {
    suspend fun savePost(body: CreatePostRequest): Resource<Post>
    suspend fun saveEvent(body: CreateEventRequest): Resource<Event>
    suspend fun getUserData(userId: Long): Resource<UserData>
    suspend fun getJobData(userId: Long): Resource<JobData>
}