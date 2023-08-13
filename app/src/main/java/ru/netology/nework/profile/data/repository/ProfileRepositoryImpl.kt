package ru.netology.nework.profile.data.repository

import ru.netology.nework.core.data.mappers.toJobData
import ru.netology.nework.core.data.mappers.toJobDataDto
import ru.netology.nework.core.data.mappers.toJobList
import ru.netology.nework.core.data.mappers.toUserData
import ru.netology.nework.core.domain.entities.JobData
import ru.netology.nework.core.domain.entities.UserData
import ru.netology.nework.core.domain.wrapServerException
import ru.netology.nework.core.utils.Resource
import ru.netology.nework.profile.data.network.ProfileApi
import ru.netology.nework.profile.domain.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val profileApi: ProfileApi
) : ProfileRepository {

    override suspend fun getUserData(userId: Long): Resource<UserData> {
        return wrapServerException {
            profileApi.getUserData(userId).toUserData()
        }
    }

    override suspend fun getCurrentJob(userId: Long): Resource<JobData> {
        return wrapServerException {
            profileApi.getCurrentJob(userId).toJobData()
        }
    }

    override suspend fun getJobsData(): Resource<List<JobData>> {
        return wrapServerException {
            profileApi.getJobsData().toJobList()
        }
    }

    override suspend fun addJob(body: JobData): Resource<JobData> {
        return wrapServerException {
            profileApi.addJob(body.toJobDataDto()).toJobData()
        }
    }

    override suspend fun editJob(body: JobData): Resource<JobData> {
        return wrapServerException {
            profileApi.editJob(body.toJobDataDto()).toJobData()
        }
    }

    override suspend fun deleteJob(jobId: Long): Resource<Unit> {
        return wrapServerException {
            profileApi.deleteJob(jobId)
        }
    }
}