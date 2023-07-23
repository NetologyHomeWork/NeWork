package ru.netology.nework.create.data.repository

import ru.netology.nework.core.data.dto.toEvent
import ru.netology.nework.core.data.mappers.toJobData
import ru.netology.nework.core.data.mappers.toPostEntity
import ru.netology.nework.core.data.mappers.toUserData
import ru.netology.nework.core.domain.entities.Event
import ru.netology.nework.core.domain.entities.JobData
import ru.netology.nework.core.domain.entities.Post
import ru.netology.nework.core.domain.entities.UserData
import ru.netology.nework.core.domain.wrapServerException
import ru.netology.nework.core.utils.Resource
import ru.netology.nework.create.data.models.toDto
import ru.netology.nework.create.data.network.CreateApi
import ru.netology.nework.create.domain.models.CreateEventRequest
import ru.netology.nework.create.domain.models.CreatePostRequest
import ru.netology.nework.create.domain.repository.CreateRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateRepositoryImpl @Inject constructor(
    private val createApi: CreateApi
) : CreateRepository {

    override suspend fun savePost(body: CreatePostRequest): Resource<Post> {
        return wrapServerException {
            createApi.savePost(body.toDto()).toPostEntity()
        }
    }

    override suspend fun saveEvent(body: CreateEventRequest): Resource<Event> {
        return wrapServerException {
            createApi.saveEvent(body.toDto()).toEvent()
        }
    }

    override suspend fun getUserData(userId: Long): Resource<UserData> {
        return wrapServerException {
            createApi.getUserData(userId).toUserData()
        }
    }

    override suspend fun getJobData(userId: Long): Resource<JobData> {
        return wrapServerException {
            createApi.getJobData(userId).toJobData()
        }
    }
}