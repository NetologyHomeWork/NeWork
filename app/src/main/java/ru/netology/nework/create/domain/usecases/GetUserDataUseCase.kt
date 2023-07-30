package ru.netology.nework.create.domain.usecases

import ru.netology.nework.core.domain.InternalUnknownError
import ru.netology.nework.core.domain.SettingsDataSource
import ru.netology.nework.core.domain.entities.UserData
import ru.netology.nework.core.utils.Resource
import ru.netology.nework.create.domain.repository.CreateRepository
import javax.inject.Inject

class GetUserDataUseCase @Inject constructor(
    private val authDataSource: SettingsDataSource,
    private val createRepository: CreateRepository
) {

    suspend fun execute(): Resource<UserData> {
        return createRepository.getUserData(
            userId = authDataSource.getAuth()?.id
                ?: return Resource.Error(
                    InternalUnknownError(IllegalArgumentException("Unfortunately get userId"))
                )
        )
    }
}