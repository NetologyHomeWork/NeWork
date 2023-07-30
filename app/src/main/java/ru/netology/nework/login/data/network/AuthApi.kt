package ru.netology.nework.login.data.network

import okhttp3.MultipartBody
import retrofit2.http.*
import ru.netology.nework.login.data.network.dto.AuthenticationModelRequest
import ru.netology.nework.login.data.network.dto.AuthenticationModelResponse

interface AuthApi {

    @POST("api/users/authentication")
    suspend fun authentication(
        @Body body: AuthenticationModelRequest
    ): AuthenticationModelResponse

    @[FormUrlEncoded POST("api/users/registration/")]
    suspend fun simpleRegistration(
        @Field("login") login: String,
        @Field("password") password: String,
        @Field("name") name: String
    ): AuthenticationModelResponse

    @[Multipart POST("api/users/registration/")]
    suspend fun registrationWithPhoto(
        @Part("login") login: String,
        @Part("password") password: String,
        @Part("name") name: String,
        @Part file: MultipartBody.Part
    ): AuthenticationModelResponse
}