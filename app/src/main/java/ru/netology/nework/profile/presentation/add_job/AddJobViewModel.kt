package ru.netology.nework.profile.presentation.add_job

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
import ru.netology.nework.profile.domain.entity.AddJobField
import ru.netology.nework.profile.domain.exceptions.CurrentJobException
import ru.netology.nework.profile.domain.exceptions.EmptyFieldException
import ru.netology.nework.profile.domain.exceptions.WrongRangeDateException
import ru.netology.nework.profile.domain.usecases.AddJobUseCase
import ru.netology.nework.profile.domain.usecases.EditJobUseCase
import ru.netology.nework.profile.presentation.profile.adapter.ProfileItems
import ru.netology.nework.profile.presentation.profile.adapter.toJobItem
import javax.inject.Inject

@HiltViewModel
class AddJobViewModel @Inject constructor(
    private val addJobUseCase: AddJobUseCase,
    private val editJobUseCase: EditJobUseCase,
    private val resourcesManager: ResourcesManager,
    private val dispatchers: CoroutineDispatchers
) : BaseViewModel<AddJobViewModel.AddJobCommands>() {

    val commands = _commands.asSharedFlow()
    val threeStateFlow = _threeStateFlow.asStateFlow()

    fun onSaveClick(
        id: Long,
        name: String,
        position: String,
        start: String,
        finish: String,
        isCurrentJob: Boolean
    ) {
        viewModelScope.launch(dispatchers.IO) {
            _threeStateFlow.tryEmit(ThreeStateView.State.Loading)
            delay(ThreeStateView.DELAY_LOADING)
            val resource = if (id == 0L) {
                addJobUseCase.execute(
                    name = name,
                    position = position,
                    start = start,
                    finish = finish,
                    isCurrentJob = isCurrentJob
                )
            } else {
                editJobUseCase.execute(
                    id = id,
                    name = name,
                    position = position,
                    start = start,
                    finish = finish,
                    isCurrentJob = isCurrentJob
                )
            }
            when (resource) {
                is Resource.Success -> _commands.tryEmit(
                    AddJobCommands.NavigateToProfile(resource.data.toJobItem(resourcesManager))
                )

                is Resource.Error -> {
                    when (resource.cause) {
                        is EmptyFieldException -> {
                            _commands.tryEmit(AddJobCommands.ShowInputError(resource.cause.field))
                        }

                        is CurrentJobException -> {
                            _commands.tryEmit(
                                AddJobCommands.ShowAlertDialog(
                                    resourcesManager.getString(
                                        R.string.error_job_finished
                                    )
                                )
                            )
                        }

                        is NetworkException -> {
                            _commands.tryEmit(
                                AddJobCommands.ShowAlertDialog(
                                    resourcesManager.getString(R.string.error_connection)
                                )
                            )
                        }

                        is WrongRangeDateException -> {
                            _commands.tryEmit(
                                AddJobCommands.ShowAlertDialog(
                                    resourcesManager.getString(R.string.error_wrong_date_range)
                                )
                            )
                        }

                        else -> {
                            _commands.tryEmit(
                                AddJobCommands.ShowAlertDialog(
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

    sealed interface AddJobCommands : Commands {
        class NavigateToProfile(val jobItem: ProfileItems.JobItem) : AddJobCommands
        class ShowAlertDialog(val message: String) : AddJobCommands
        class ShowInputError(val field: AddJobField) : AddJobCommands
    }
}