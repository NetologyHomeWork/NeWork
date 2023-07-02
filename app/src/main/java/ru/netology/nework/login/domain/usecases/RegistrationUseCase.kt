package ru.netology.nework.login.domain.usecases

import kotlinx.serialization.json.Json
import ru.netology.nework.core.domain.BackendException
import ru.netology.nework.core.domain.InternalUnknownError
import ru.netology.nework.core.domain.SettingsDataSource
import ru.netology.nework.core.domain.entities.ErrorResponseBody
import ru.netology.nework.core.utils.Resource
import ru.netology.nework.login.domain.AuthRepository
import ru.netology.nework.login.domain.entities.AuthField
import ru.netology.nework.login.domain.exceptions.AccountAlreadyExistException
import ru.netology.nework.login.domain.exceptions.EmptyFieldException
import ru.netology.nework.login.domain.exceptions.PasswordMismatchException
import java.io.File
import javax.inject.Inject

class RegistrationUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val settingsDataSource: SettingsDataSource
) {

    suspend fun execute(
        login: String,
        name: String,
        password: String,
        repeatPassword: String,
        file: File? = null
    ): Resource<Unit> {
        if (name.isBlank()) return Resource.Error(EmptyFieldException(AuthField.NAME))
        if (login.isBlank()) return Resource.Error(EmptyFieldException(AuthField.LOGIN))
        if (password.isBlank()) return Resource.Error(EmptyFieldException(AuthField.PASSWORD))
        if (repeatPassword.isBlank()) return Resource.Error(EmptyFieldException(AuthField.REPEAT_PASSWORD))
        if (password != repeatPassword) return Resource.Error(PasswordMismatchException())
        val resource = if (file == null) authRepository.simpleRegistration(login, password, name)
        else authRepository.registrationWithPhoto(login, password, name, file)
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
                            if (errorBody == USER_ALREADY_EXIST_MESSAGE) Resource.Error(AccountAlreadyExistException())
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
        const val USER_ALREADY_EXIST_MESSAGE = "User already registered"
    }
}