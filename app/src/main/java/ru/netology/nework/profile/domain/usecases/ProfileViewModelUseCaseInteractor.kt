package ru.netology.nework.profile.domain.usecases

import ru.netology.nework.core.domain.entities.JobData
import ru.netology.nework.core.domain.entities.UserData
import ru.netology.nework.core.utils.Resource
import javax.inject.Inject

class ProfileViewModelUseCaseInteractor @Inject constructor(
    private val getUserDataUseCase: GetUserDataUseCase,
    private val getJobsDataUseCase: GetJobsDataUseCase,
    private val deleteJobUseCase: DeleteJobUseCase,
    private val logoutUseCase: LogoutUseCase
) {

    suspend fun getUserData(): Resource<UserData> {
        return getUserDataUseCase.execute()
    }

    suspend fun getAllJobs(): Resource<List<JobData>> {
        return getJobsDataUseCase.execute()
    }

    suspend fun deleteJob(jobId: Long): Resource<Unit> {
        return deleteJobUseCase.execute(jobId)
    }

    fun logout() {
        logoutUseCase.execute()
    }
}