package ru.netology.nework.create.presentation.edit.post

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
import ru.netology.nework.create.domain.usecases.EditPostUseCase
import ru.netology.nework.create.presentation.edit.post.EditPostViewModel.EditPostCommands
import javax.inject.Inject

@HiltViewModel
class EditPostViewModel @Inject constructor(
    private val editPostUseCase: EditPostUseCase,
    private val resourcesManager: ResourcesManager,
    private val dispatchers: CoroutineDispatchers
) : BaseViewModel<EditPostCommands>() {

    val treeStateFlow: StateFlow<ThreeStateView.State> = _threeStateFlow.asStateFlow()
    val commands: SharedFlow<EditPostCommands> = _commands.asSharedFlow()

    fun editPost(postId: Long, content: String) {
        viewModelScope.launch(dispatchers.IO) {
            _threeStateFlow.tryEmit(ThreeStateView.State.Loading)
            delay(ThreeStateView.DELAY_LOADING)
            when (val resource = editPostUseCase.execute(postId, content, null)) {
                is Resource.Success -> {
                    _commands.tryEmit(EditPostCommands.NavigateToBack(content))
                }
                is Resource.Error -> {
                    when (resource.cause) {
                        is NetworkException -> {
                            _commands.tryEmit(
                                EditPostCommands.ShowAlertDialog(
                                    resourcesManager.getString(R.string.error_connection)
                                )
                            )
                        }
                        is EmptyContentException -> {
                            _commands.tryEmit(
                                EditPostCommands.ShowAlertDialog(
                                    resourcesManager.getString(R.string.empty_post_content_error)
                                )
                            )
                        }
                        else -> {
                            _commands.tryEmit(
                                EditPostCommands.ShowAlertDialog(
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

    sealed interface EditPostCommands : Commands {
        class ShowAlertDialog(val message: String) : EditPostCommands
        class NavigateToBack(val content: String) : EditPostCommands
    }
}