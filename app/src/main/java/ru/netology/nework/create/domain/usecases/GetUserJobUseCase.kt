package ru.netology.nework.create.domain.usecases

import ru.netology.nework.core.domain.InternalUnknownError
import ru.netology.nework.core.domain.SettingsDataSource
import ru.netology.nework.core.domain.entities.JobData
import ru.netology.nework.core.utils.Resource
import ru.netology.nework.create.domain.repository.CreateRepository
import javax.inject.Inject

class GetUserJobUseCase @Inject constructor(
    private val authDataSource: SettingsDataSource,
    private val createRepository: CreateRepository
) {
    suspend fun execute(): Resource<JobData> {
        return createRepository.getJobData(
            userId = authDataSource.getAuth()?.id
                ?: return Resource.Error(
                    InternalUnknownError(IllegalArgumentException("Unfortunately get userId"))
                )
        )
    }
}