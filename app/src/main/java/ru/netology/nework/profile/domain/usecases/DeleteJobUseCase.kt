package ru.netology.nework.profile.domain.usecases

import ru.netology.nework.core.utils.Resource
import ru.netology.nework.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class DeleteJobUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend fun execute(jobId: Long): Resource<Unit> {
        return repository.deleteJob(jobId)
    }
}