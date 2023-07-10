package ru.netology.nework.posts.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import ru.netology.nework.posts.data.network.PostsApi
import ru.netology.nework.posts.data.repository.PostsRepositoryImpl
import ru.netology.nework.posts.domain.repository.PostsRepository
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
class PostsModule {

    @[Provides Singleton]
    fun providesPostsApi(
        retrofit: Retrofit
    ): PostsApi = retrofit.create()

    @[Provides Singleton]
    fun providePostsRepository(
        impl: PostsRepositoryImpl
    ): PostsRepository = impl
}