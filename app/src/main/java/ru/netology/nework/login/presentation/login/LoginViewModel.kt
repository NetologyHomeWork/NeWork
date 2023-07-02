package ru.netology.nework.login.presentation.login

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.netology.nework.R
import ru.netology.nework.core.domain.NetworkException
import ru.netology.nework.core.presentation.base.BaseViewModel
import ru.netology.nework.core.presentation.base.Commands
import ru.netology.nework.core.presentation.view.ThreeStateView
import ru.netology.nework.core.utils.CoroutineDispatchers
import ru.netology.nework.core.utils.Resource
import ru.netology.nework.core.utils.ResourcesManager
import ru.netology.nework.login.domain.entities.AuthField
import ru.netology.nework.login.domain.exceptions.EmptyFieldException
import ru.netology.nework.login.domain.exceptions.IncorrectPasswordException
import ru.netology.nework.login.domain.usecases.AuthUseCase
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val dispatchers: CoroutineDispatchers,
    private val resourcesManager: ResourcesManager
) : BaseViewModel<LoginViewModel.LoginCommands>() {

    val threeStateFlow: StateFlow<ThreeStateView.State> = _threeStateFlow.asStateFlow()

    val commands = _commands.asSharedFlow()

    fun onLoginClick(
        login: String,
        password: String
    ) {
        viewModelScope.launch(dispatchers.IO) {
            _threeStateFlow.tryEmit(ThreeStateView.State.Loading)
            delay(ThreeStateView.DELAY_LOADING)
            val resource = authUseCase.execute(
                login = login,
                password = password
            )
            when (resource) {
                is Resource.Success -> {
                    _commands.tryEmit(LoginCommands.NavigateToMainScreen)
                }
                is Resource.Error -> {
                    _threeStateFlow.tryEmit(ThreeStateView.State.Content)
                    when (resource.cause) {
                        is NetworkException -> {
                            _commands.tryEmit(
                                LoginCommands.ShowAlertDialog(
                                    message = resourcesManager.getString(R.string.error_network_connection),
                                    title = resourcesManager.getString(R.string.error_connection)
                                )
                            )
                        }
                        is IncorrectPasswordException -> {
                            _commands.tryEmit(
                                LoginCommands.ShowAlertDialog(
                                    resourcesManager.getString(R.string.error_incorrect_password)
                                )
                            )
                        }
                        is EmptyFieldException -> {
                            _commands.tryEmit(LoginCommands.ShowInputError(resource.cause.field))
                        }
                        else -> {
                            _commands.tryEmit(
                                LoginCommands.ShowAlertDialog(
                                    resourcesManager.getString(R.string.error_server_connection),
                                    title = resourcesManager.getString(R.string.error_connection)
                                )
                            )
                        }
                    }
                }
            }
        }

    }

    sealed interface LoginCommands : Commands {
        object NavigateToMainScreen : LoginCommands
        class ShowAlertDialog(val message: String, val title: String? = null) : LoginCommands
        class ShowInputError(val field: AuthField) : LoginCommands
    }
}