package ru.netology.nework.core.presentation

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import ru.netology.nework.core.domain.SettingsDataSource
import ru.netology.nework.core.presentation.base.BaseViewModel
import ru.netology.nework.core.presentation.base.Commands
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    settingsDataSource: SettingsDataSource
) : BaseViewModel<MainViewModel.MainViewModelCommands>() {

    val commands = _commands.shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    init {
        settingsDataSource.authFlow.onEach { auth ->
            if (auth == null || auth.token.isBlank()) {
                _commands.tryEmit(MainViewModelCommands.NavigateToLoginScreen)
            } else {
                _commands.tryEmit(MainViewModelCommands.NavigateToHomeScreen)
            }
        }.launchIn(viewModelScope)
    }

    sealed interface MainViewModelCommands : Commands {
        object NavigateToLoginScreen : MainViewModelCommands
        object NavigateToHomeScreen : MainViewModelCommands
    }
}