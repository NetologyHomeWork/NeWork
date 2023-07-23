package ru.netology.nework.create.presentation.create.post

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
import ru.netology.nework.create.domain.usecases.CreatePostUseCase
import ru.netology.nework.create.presentation.create.post.CreatePostViewModel.CreatePostCommands
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
   private val createPostUseCase: CreatePostUseCase,
   private val resourcesManager: ResourcesManager,
   private val dispatchers: CoroutineDispatchers
) : BaseViewModel<CreatePostCommands>() {

    val treeStateFlow: StateFlow<ThreeStateView.State> = _threeStateFlow.asStateFlow()
    val commands: SharedFlow<CreatePostCommands> = _commands.asSharedFlow()

    fun createPost(content: String) {
        viewModelScope.launch(dispatchers.IO) {
            _threeStateFlow.tryEmit(ThreeStateView.State.Loading)
            delay(ThreeStateView.DELAY_LOADING)
            when (val resource = createPostUseCase.execute(content, null)) {
                is Resource.Success -> {
                    _commands.tryEmit(CreatePostCommands.NavigateToSuccess)
                }
                is Resource.Error -> {
                    when (resource.cause) {
                        is NetworkException -> {
                            _commands.tryEmit(
                                CreatePostCommands.ShowAlertDialog(
                                    resourcesManager.getString(R.string.error_connection)
                                )
                            )
                        }
                        is EmptyContentException -> {
                            _commands.tryEmit(
                                CreatePostCommands.ShowAlertDialog(
                                    resourcesManager.getString(R.string.empty_post_content_error)
                                )
                            )
                        }
                        else -> {
                            _commands.tryEmit(
                                CreatePostCommands.ShowAlertDialog(
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

    sealed interface CreatePostCommands : Commands {
        class ShowAlertDialog(val message: String) : CreatePostCommands
        object NavigateToSuccess : CreatePostCommands
    }
}