package ru.netology.nework.login.data.mappers

import ru.netology.nework.core.domain.entities.AuthModel
import ru.netology.nework.login.data.network.dto.AuthenticationModelRequest
import ru.netology.nework.login.data.network.dto.AuthenticationModelResponse
import ru.netology.nework.login.domain.entities.AuthData

fun AuthData.toAuthModelResponse(): AuthenticationModelRequest {
    return AuthenticationModelRequest(
        login = this.login,
        password = password
    )
}

fun AuthenticationModelResponse.toAuthModel(): AuthModel {
    return AuthModel(
        id = this.id,
        token = this.token
    )
}
