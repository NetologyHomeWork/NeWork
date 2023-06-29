package ru.netology.nework.core.utils

open class AppExceptions(
    message: String = "",
    cause: Throwable? = null
) : Exception(message, cause)