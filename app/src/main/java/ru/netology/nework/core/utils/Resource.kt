package ru.netology.nework.core.utils

import ru.netology.nework.core.domain.AppExceptions

sealed class Resource<T> {
    class Success<T>(
        val data: T
    ) : Resource<T>()

    class Error<T>(
        val cause: AppExceptions
    ) : Resource<T>()
}
