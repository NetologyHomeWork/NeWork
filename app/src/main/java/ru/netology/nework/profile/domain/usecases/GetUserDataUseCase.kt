package ru.netology.nework.profile.domain.usecases

import ru.netology.nework.core.domain.InternalUnknownError
import ru.netology.nework.core.domain.SettingsDataSource
import ru.netology.nework.core.domain.entities.UserData
import ru.netology.nework.core.utils.Resource
import ru.netology.nework.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class GetUserDataUseCase @Inject constructor(
    private val repository: ProfileRepository,
    private val settings: SettingsDataSource
) {
    suspend fun execute(): Resource<UserData> {
        val userId = settings.getAuth()?.id ?: return Resource.Error(
            InternalUnknownError(
                IllegalArgumentException("Unfortunately get userId")
            )
        )
        return repository.getUserData(userId)
    }
}