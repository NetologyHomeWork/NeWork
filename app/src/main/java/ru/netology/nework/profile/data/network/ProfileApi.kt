package ru.netology.nework.profile.data.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.netology.nework.core.data.dto.JobDataDto
import ru.netology.nework.core.data.dto.UserDataDto

interface ProfileApi {

    @GET("api/users/{user_id}/")
    suspend fun getUserData(
        @Path("user_id") userId: Long
    ): UserDataDto

    @GET("api/{user_id}/jobs/")
    suspend fun getCurrentJob(
        @Path("user_id") userId: Long
    ): JobDataDto

    @GET("api/my/jobs/")
    suspend fun getJobsData(): List<JobDataDto>

    @POST("api/my/jobs/")
    suspend fun addJob(
        @Body body: JobDataDto
    ): JobDataDto

    @POST("api/my/jobs/")
    suspend fun editJob(
        @Body body: JobDataDto
    ): JobDataDto

    @DELETE("/api/my/jobs/{job_id}/")
    suspend fun deleteJob(
        @Path("job_id") jobId: Long
    )
}