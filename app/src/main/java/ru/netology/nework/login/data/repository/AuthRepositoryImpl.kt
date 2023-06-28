package ru.netology.nework.login.data.repository

import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nework.core.domain.entities.AuthModel
import ru.netology.nework.core.domain.wrapServerException
import ru.netology.nework.core.utils.Resource
import ru.netology.nework.login.data.mappers.toAuthModel
import ru.netology.nework.login.data.mappers.toAuthModelResponse
import ru.netology.nework.login.data.network.AuthApi
import ru.netology.nework.login.domain.AuthRepository
import ru.netology.nework.login.domain.entities.AuthData
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi
) : AuthRepository {

    override suspend fun authentication(body: AuthData): Resource<AuthModel> {
        return wrapServerException {
            authApi.authentication(body.toAuthModelResponse()).toAuthModel()
        }
    }

    override suspend fun simpleRegistration(
        login: String,
        password: String,
        name: String
    ): Resource<AuthModel> {
        return wrapServerException {
            authApi.simpleRegistration(
                login = login,
                password = password,
                name = name
            ).toAuthModel()
        }
    }

    override suspend fun registrationWithPhoto(
        login: String,
        password: String,
        name: String,
        file: File
    ): Resource<AuthModel> {
        return wrapServerException {
            val part = MultipartBody.Part.createFormData(
                name = FILE_NAME,
                filename = file.name,
                body = file.asRequestBody()
            )
            authApi.registrationWithPhoto(
                login = login,
                password = password,
                name = name,
                file = part
            ).toAuthModel()
        }
    }

    private companion object {
        const val FILE_NAME = "file"
    }
}