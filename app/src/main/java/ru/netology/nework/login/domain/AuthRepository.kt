package ru.netology.nework.login.domain

import ru.netology.nework.core.domain.entities.AuthModel
import ru.netology.nework.core.utils.Resource
import ru.netology.nework.login.domain.entities.AuthData
import java.io.File

interface AuthRepository {

    suspend fun authentication(body: AuthData): Resource<AuthModel>

    suspend fun simpleRegistration(
        login: String,
        password: String,
        name: String
    ): Resource<AuthModel>

    suspend fun registrationWithPhoto(
        login: String,
        password: String,
        name: String,
        file: File
    ): Resource<AuthModel>
}