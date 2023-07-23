package ru.netology.nework.events.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import ru.netology.nework.events.data.network.EventsApi
import ru.netology.nework.events.data.repository.EventsRepositoryImpl
import ru.netology.nework.events.domain.repository.EventsRepository
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
class EventModule {

    @[Provides Singleton]
    fun provideEventsApi(
        retrofit: Retrofit
    ): EventsApi = retrofit.create()

    @[Provides Singleton]
    fun provide(impl: EventsRepositoryImpl): EventsRepository = impl
}