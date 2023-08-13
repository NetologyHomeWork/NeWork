package ru.netology.nework.profile.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import ru.netology.nework.profile.data.network.ProfileApi
import ru.netology.nework.profile.data.repository.ProfileRepositoryImpl
import ru.netology.nework.profile.domain.repository.ProfileRepository
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
class ProfileModule {

    @[Provides Singleton]
    fun providesProfileRepository(
        impl: ProfileRepositoryImpl
    ): ProfileRepository = impl

    @[Provides Singleton]
    fun providesProfileApi(
        retrofit: Retrofit
    ): ProfileApi = retrofit.create()
}