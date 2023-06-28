package ru.netology.nework.login.domain.usecases

import kotlinx.serialization.json.Json
import ru.netology.nework.core.domain.BackendException
import ru.netology.nework.core.domain.InternalUnknownError
import ru.netology.nework.core.domain.SettingsDataSource
import ru.netology.nework.core.domain.entities.ErrorResponseBody
import ru.netology.nework.core.utils.Resource
import ru.netology.nework.login.domain.AuthRepository
import ru.netology.nework.login.domain.entities.AuthData
import ru.netology.nework.login.domain.entities.AuthField
import ru.netology.nework.login.domain.exceptions.EmptyFieldException
import ru.netology.nework.login.domain.exceptions.IncorrectPasswordException
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val settingsDataSource: SettingsDataSource
) {

    suspend fun execute(
        login: String,
        password: String
    ): Resource<Unit> {
        if (login.isBlank()) return Resource.Error(EmptyFieldException(AuthField.LOGIN))
        if (password.isBlank()) return Resource.Error(EmptyFieldException(AuthField.PASSWORD))
        val resource = authRepository.authentication(
            AuthData(
                login = login,
                password = password
            )
        )
        return when (resource) {
            is Resource.Success -> {
                settingsDataSource.setAuth(resource.data)
                Resource.Success(Unit)
            }
            is Resource.Error -> {
                when (resource.cause) {
                    is BackendException -> {
                        val message = resource.cause.message ?: return Resource.Error(resource.cause)
                        return try {
                            val errorBody = Json.decodeFromString(ErrorResponseBody.serializer(), message).reason
                            if (errorBody == INCORRECT_PASSWORD_MESSAGE) Resource.Error(
                                IncorrectPasswordException()
                            )
                            else Resource.Error(resource.cause)
                        } catch (e: Exception) {
                            Resource.Error(InternalUnknownError(e))
                        }
                    }
                }
                Resource.Error(resource.cause)
            }
        }
    }

    private companion object {
        const val INCORRECT_PASSWORD_MESSAGE = "Incorrect password"
    }
}