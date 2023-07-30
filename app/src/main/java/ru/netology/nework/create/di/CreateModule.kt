package ru.netology.nework.create.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import ru.netology.nework.create.data.network.CreateApi
import ru.netology.nework.create.data.repository.CreateRepositoryImpl
import ru.netology.nework.create.domain.repository.CreateRepository
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
class CreateModule {

    @[Provides Singleton]
    fun provideCreateRepository(impl: CreateRepositoryImpl): CreateRepository = impl

    @[Provides Singleton]
    fun provideCreateApi(retrofit: Retrofit): CreateApi = retrofit.create()
}