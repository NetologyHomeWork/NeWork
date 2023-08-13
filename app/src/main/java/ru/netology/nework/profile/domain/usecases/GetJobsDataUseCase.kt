package ru.netology.nework.profile.domain.usecases

import ru.netology.nework.core.domain.entities.JobData
import ru.netology.nework.core.utils.Resource
import ru.netology.nework.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class GetJobsDataUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend fun execute(): Resource<List<JobData>> {
        return repository.getJobsData()
    }
}