package ru.netology.nework.core.presentation.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.netology.nework.core.presentation.view.ThreeStateView

abstract class BaseViewModel<T : Commands> : ViewModel() {

    protected val _commands = MutableSharedFlow<T>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    protected val _threeStateFlow = MutableStateFlow<ThreeStateView.State>(
        ThreeStateView.State.Content
    )

    protected val _swipeRefreshingFlow = MutableStateFlow(false)
}