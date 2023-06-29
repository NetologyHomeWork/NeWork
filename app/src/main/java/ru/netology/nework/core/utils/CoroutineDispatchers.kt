package ru.netology.nework.core.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class CoroutineDispatchers(
    val default: CoroutineDispatcher,
    val IO: CoroutineDispatcher,
    val main: CoroutineDispatcher
) {
    companion object {
        val Default = CoroutineDispatchers(
            default = Dispatchers.Default,
            IO = Dispatchers.IO,
            main = Dispatchers.Main
        )
    }
}