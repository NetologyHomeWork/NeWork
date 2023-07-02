package ru.netology.nework.login.presentation.registration

import android.net.Uri
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.netology.nework.R
import ru.netology.nework.core.domain.NetworkException
import ru.netology.nework.core.domain.entities.ImageFileModel
import ru.netology.nework.core.presentation.base.BaseViewModel
import ru.netology.nework.core.presentation.base.Commands
import ru.netology.nework.core.presentation.view.ThreeStateView
import ru.netology.nework.core.utils.CoroutineDispatchers
import ru.netology.nework.core.utils.Resource
import ru.netology.nework.core.utils.ResourcesManager
import ru.netology.nework.login.domain.entities.AuthField
import ru.netology.nework.login.domain.exceptions.AccountAlreadyExistException
import ru.netology.nework.login.domain.exceptions.EmptyFieldException
import ru.netology.nework.login.domain.exceptions.PasswordMismatchException
import ru.netology.nework.login.domain.usecases.RegistrationUseCase
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val registrationUseCase: RegistrationUseCase,
    private val dispatchers: CoroutineDispatchers,
    private val resourcesManager: ResourcesManager
) : BaseViewModel<RegistrationViewModel.RegistrationCommands>() {

    private val _photoFlow = MutableStateFlow<ImageFileModel?>(null)
    val photoFlow: StateFlow<ImageFileModel?> = _photoFlow.asStateFlow()

    val threeStateFlow: StateFlow<ThreeStateView.State> = _threeStateFlow.asStateFlow()

    val commands = _commands.asSharedFlow()

    fun onRegistrationClick(
        login: String,
        name: String,
        password: String,
        repeatPassword: String
    ) {
        viewModelScope.launch(dispatchers.IO) {
            _threeStateFlow.tryEmit(ThreeStateView.State.Loading)
            delay(ThreeStateView.DELAY_LOADING)
            val resource = registrationUseCase.execute(
                login = login,
                name = name,
                password = password,
                repeatPassword = repeatPassword,
                file = photoFlow.value?.file
            )
            when (resource) {
                is Resource.Success -> {
                    _commands.tryEmit(RegistrationCommands.NavigateToMainScreen)
                }
                is Resource.Error -> {
                    _threeStateFlow.tryEmit(ThreeStateView.State.Content)
                    when (resource.cause) {
                        is NetworkException -> {
                            _commands.tryEmit(
                                RegistrationCommands.ShowAlertDialog(
                                    message = resourcesManager.getString(R.string.error_network_connection),
                                    title = resourcesManager.getString(R.string.error_connection)
                                )
                            )
                        }
                        is AccountAlreadyExistException -> {
                            _commands.tryEmit(
                                RegistrationCommands.ShowAlertDialog(
                                    message = resourcesManager.getString(R.string.error_account_already_exist)
                                )
                            )
                        }
                        is EmptyFieldException -> {
                            _commands.tryEmit(RegistrationCommands.ShowInputError(resource.cause.field))
                        }
                        is PasswordMismatchException -> {
                            _commands.tryEmit(
                                RegistrationCommands.ShowAlertDialog(
                                    resourcesManager.getString(R.string.error_password_mismatch)
                                )
                            )
                        }
                        else -> {
                            _commands.tryEmit(
                                RegistrationCommands.ShowAlertDialog(
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

    fun onPhotoUploaded(uri: Uri, file: File) {
        _photoFlow.tryEmit(
            ImageFileModel(
                uri = uri,
                file = file
            )
        )
    }

    fun onPhotoRemoved() {
        _photoFlow.tryEmit(null)
    }

    sealed interface RegistrationCommands : Commands {
        object NavigateToMainScreen : RegistrationCommands
        class ShowAlertDialog(val message: String, val title: String? = null) : RegistrationCommands
        class ShowInputError(val field: AuthField) : RegistrationCommands
    }
}