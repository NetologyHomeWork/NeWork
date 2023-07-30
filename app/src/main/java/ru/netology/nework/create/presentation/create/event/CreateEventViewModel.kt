package ru.netology.nework.create.presentation.create.event

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
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
import ru.netology.nework.create.domain.exceptions.EmptyContentException
import ru.netology.nework.create.domain.exceptions.EmptyDateException
import ru.netology.nework.create.domain.usecases.CreateEventUseCase
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val createEventUseCase: CreateEventUseCase,
    private val resourcesManager: ResourcesManager,
    private val dispatchers: CoroutineDispatchers
) : BaseViewModel<CreateEventViewModel.CreateEventCommands>() {

    val treeStateFlow: StateFlow<ThreeStateView.State> = _threeStateFlow.asStateFlow()
    val commands: SharedFlow<CreateEventCommands> = _commands.asSharedFlow()

    fun createEvent(content: String, date: String) {
        viewModelScope.launch(dispatchers.IO) {
            _threeStateFlow.tryEmit(ThreeStateView.State.Loading)
            delay(ThreeStateView.DELAY_LOADING)
            when (val resource = createEventUseCase.execute(content, date, null, null)) {
                is Resource.Success -> {
                    _commands.tryEmit(CreateEventCommands.NavigateToSuccess)
                }
                is Resource.Error -> {
                    when (resource.cause) {
                        is NetworkException -> {
                            _commands.tryEmit(
                                CreateEventCommands.ShowAlertDialog(
                                    resourcesManager.getString(R.string.error_connection)
                                )
                            )
                        }
                        is EmptyContentException -> {
                            _commands.tryEmit(
                                CreateEventCommands.ShowAlertDialog(
                                    resourcesManager.getString(R.string.empty_event_content_error)
                                )
                            )
                        }
                        is EmptyDateException -> {
                            _commands.tryEmit(
                                CreateEventCommands.ShowAlertDialog(
                                    resourcesManager.getString(R.string.empty_date_error)
                                )
                            )
                        }
                        else -> {
                            _commands.tryEmit(
                                CreateEventCommands.ShowAlertDialog(
                                    resourcesManager.getString(R.string.error_server_connection)
                                )
                            )
                        }
                    }
                }
            }
            _threeStateFlow.tryEmit(ThreeStateView.State.Content)
        }
    }

    sealed interface CreateEventCommands : Commands {
        class ShowAlertDialog(val message: String) : CreateEventCommands
        object NavigateToSuccess : CreateEventCommands
    }
}