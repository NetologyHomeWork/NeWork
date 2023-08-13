package ru.netology.nework.profile.domain.repository

import ru.netology.nework.core.domain.entities.JobData
import ru.netology.nework.core.domain.entities.UserData
import ru.netology.nework.core.utils.Resource

interface ProfileRepository {
    suspend fun getUserData(userId: Long): Resource<UserData>
    suspend fun getCurrentJob(userId: Long): Resource<JobData>
    suspend fun getJobsData(): Resource<List<JobData>>
    suspend fun addJob(body: JobData): Resource<JobData>
    suspend fun editJob(body: JobData): Resource<JobData>
    suspend fun deleteJob(jobId: Long): Resource<Unit>
}