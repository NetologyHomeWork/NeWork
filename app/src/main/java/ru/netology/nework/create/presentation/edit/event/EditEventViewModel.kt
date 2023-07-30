package ru.netology.nework.create.presentation.edit.event

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
import ru.netology.nework.create.domain.usecases.EditEventUseCase
import ru.netology.nework.create.presentation.edit.event.EditEventViewModel.EditEventCommands
import javax.inject.Inject

@HiltViewModel
class EditEventViewModel @Inject constructor(
    private val editEventUseCase: EditEventUseCase,
    private val resourcesManager: ResourcesManager,
    private val dispatchers: CoroutineDispatchers
) : BaseViewModel<EditEventCommands>() {

    val treeStateFlow: StateFlow<ThreeStateView.State> = _threeStateFlow.asStateFlow()
    val commands: SharedFlow<EditEventCommands> = _commands.asSharedFlow()

    fun editEvent(eventId: Long, content: String, date: String) {
        viewModelScope.launch(dispatchers.IO) {
            _threeStateFlow.tryEmit(ThreeStateView.State.Loading)
            delay(ThreeStateView.DELAY_LOADING)
            when (val resource = editEventUseCase.execute(eventId, content, date, null, null)) {
                is Resource.Success -> {
                    _commands.tryEmit(EditEventCommands.NavigateToBack(content))
                }
                is Resource.Error -> {
                    when (resource.cause) {
                        is NetworkException -> {
                            _commands.tryEmit(
                                EditEventCommands.ShowAlertDialog(
                                    resourcesManager.getString(R.string.error_connection)
                                )
                            )
                        }
                        is EmptyContentException -> {
                            _commands.tryEmit(
                                EditEventCommands.ShowAlertDialog(
                                    resourcesManager.getString(R.string.empty_event_content_error)
                                )
                            )
                        }
                        is EmptyDateException -> {
                            _commands.tryEmit(
                                EditEventCommands.ShowAlertDialog(
                                    resourcesManager.getString(R.string.empty_date_error)
                                )
                            )
                        }
                        else -> {
                            _commands.tryEmit(
                                EditEventCommands.ShowAlertDialog(
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

    sealed interface EditEventCommands : Commands {
        class ShowAlertDialog(val message: String) : EditEventCommands
        class NavigateToBack(val content: String) : EditEventCommands
    }
}