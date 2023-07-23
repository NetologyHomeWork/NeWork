package ru.netology.nework.core.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import ru.netology.nework.BuildConfig
import ru.netology.nework.core.data.data_source.AppAuthDataSource
import ru.netology.nework.core.domain.SettingsDataSource
import ru.netology.nework.core.utils.CoroutineDispatchers
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
class CommonModule {

    @[Provides Singleton]
    fun providesSettingDataSource(
        dataSource: AppAuthDataSource
    ) : SettingsDataSource {
        return dataSource
    }

    @[Provides Singleton]
    fun providesJson(): Json {
        return Json(Json.Default) {
            ignoreUnknownKeys = true
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    @[Provides Singleton]
    fun provideJsonConverterFactory(
        json: Json
    ): Converter.Factory {
        return json.asConverterFactory("application/json".toMediaType())
    }

    @[Provides Singleton LoggingInterceptor]
    fun providesLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @[Provides Singleton AuthInterceptor]
    fun providesAuthInterceptor(
        dataSource: SettingsDataSource
    ): Interceptor {
        return Interceptor { chain ->
            val request = dataSource.authFlow.value?.token?.let { token ->
                chain.request()
                    .newBuilder()
                    .addHeader("Authorization", token)
                    .build()
            } ?: chain.request()
            chain.proceed(request)
        }
    }

    @[Provides Singleton]
    fun providesOkHttpClient(
        @LoggingInterceptor logging: Interceptor,
        @AuthInterceptor authInterceptor: Interceptor
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(30L, TimeUnit.SECONDS)
            .readTimeout(30L, TimeUnit.SECONDS)
            .writeTimeout(30L, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)

        if (BuildConfig.IS_LOG_ENABLED) {
            builder.addInterceptor(logging)
        }

        return builder.build()
    }

    @[Provides Singleton]
    fun providesRetrofitClient(
        factory: Converter.Factory,
        client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(factory)
            .client(client)
            .build()
    }

    @[Provides Singleton]
    fun providesDispatchers(): CoroutineDispatchers {
        return CoroutineDispatchers.Default
    }
}

@[Qualifier Retention(AnnotationRetention.RUNTIME)]
annotation class LoggingInterceptor

@[Qualifier Retention(AnnotationRetention.RUNTIME)]
annotation class AuthInterceptor

