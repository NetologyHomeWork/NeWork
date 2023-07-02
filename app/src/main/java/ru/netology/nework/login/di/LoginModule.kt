package ru.netology.nework.login.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import ru.netology.nework.login.data.network.AuthApi
import ru.netology.nework.login.data.repository.AuthRepositoryImpl
import ru.netology.nework.login.domain.AuthRepository
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
class LoginModule {

    @[Provides Singleton]
    fun providesAuthApi(
        retrofit: Retrofit
    ): AuthApi {
        return retrofit.create()
    }

    @[Provides Singleton]
    fun providesAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository {
        return impl
    }
}