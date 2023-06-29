package ru.netology.nework.core.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

fun <T> Flow<T>.observeWhenOnStarted(owner: LifecycleOwner, block: (T) -> Unit) {
    owner.repeatingJob(Lifecycle.State.STARTED) {
        collect { value ->
            block(value)
        }
    }
}

fun <T> Flow<T>.observeWhenOnCreated(owner: LifecycleOwner, block: (T) -> Unit) {
    owner.repeatingJob(Lifecycle.State.CREATED) {
        collect { value ->
            block(value)
        }
    }
}

private fun LifecycleOwner.repeatingJob(
    state: Lifecycle.State,
    block: suspend CoroutineScope.() -> Unit
): Job {
    return lifecycleScope.launch {
        repeatOnLifecycle(state, block)
    }
}