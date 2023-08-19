package ru.netology.nework.posts.presentation.detail

import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import ru.netology.nework.R
import ru.netology.nework.core.domain.NetworkException
import ru.netology.nework.core.presentation.base.BaseViewModel
import ru.netology.nework.core.presentation.base.Commands
import ru.netology.nework.core.presentation.view.ThreeStateView
import ru.netology.nework.core.utils.CoroutineDispatchers
import ru.netology.nework.core.utils.Resource
import ru.netology.nework.core.utils.ResourcesManager
import ru.netology.nework.posts.data.mappers.toPostItem
import ru.netology.nework.posts.domain.usecases.DeletePostUseCase
import ru.netology.nework.posts.domain.usecases.GetPostByIdUseCase
import ru.netology.nework.posts.domain.usecases.LikePostUseCase
import ru.netology.nework.posts.presentation.posts.adapter.PostItem

class PostDetailViewModel @AssistedInject constructor(
    private val getPostByIdUseCase: GetPostByIdUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val resourcesManager: ResourcesManager,
    private val dispatchers: CoroutineDispatchers,
    @Assisted private val postId: Long
) : BaseViewModel<PostDetailViewModel.PostDetailCommands>() {

    private val _postFlow = MutableStateFlow<PostItem?>(null)
    val postFlow = _postFlow.filterNotNull()

    val commands = _commands.asSharedFlow()

    val threeStateFlow = _threeStateFlow.asStateFlow()

    init {
        viewModelScope.launch(dispatchers.IO) {
            _threeStateFlow.tryEmit(ThreeStateView.State.Loading)
            when (val resource = getPostByIdUseCase.execute(postId)) {
                is Resource.Success -> {
                    _postFlow.tryEmit(resource.data.toPostItem())
                    _threeStateFlow.tryEmit(ThreeStateView.State.Content)
                }

                is Resource.Error -> {
                    when (resource.cause) {
                        is NetworkException -> {
                            _threeStateFlow.tryEmit(
                                ThreeStateView.State.Error(
                                    resourcesManager.getString(R.string.error_network_connection)
                                )
                            )
                        }

                        else -> {
                            _threeStateFlow.tryEmit(
                                ThreeStateView.State.Error(
                                    resourcesManager.getString(R.string.error_server_connection)
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun onInMapClick() {
        _commands.tryEmit(
            PostDetailCommands.OpenUrl(
                resourcesManager.getString(
                    R.string.coordinate_geo,
                    _postFlow.value?.coords?.lat,
                    _postFlow.value?.coords?.long
                )
            )
        )
    }

    fun onLikeClick() {
        viewModelScope.launch(dispatchers.IO) {
            val postId = _postFlow.value?.id ?: return@launch
            val isLike = _postFlow.value?.likedByMe ?: return@launch
            val likeCount = _postFlow.value?.likeCount ?: return@launch
            _postFlow.tryEmit(
                _postFlow.value?.copy(
                    likedByMe = isLike.not(),
                    likeCount = if (isLike) likeCount - 1 else likeCount + 1
                )
            )
            val resource = likePostUseCase.execute(postId, isLike)
            if (resource is Resource.Error) {
                _postFlow.tryEmit(_postFlow.value?.copy(likedByMe = isLike, likeCount = likeCount))
                when (resource.cause) {
                    is NetworkException -> {
                        _commands.tryEmit(
                            PostDetailCommands.ShowSnackbar(
                                resourcesManager.getString(R.string.error_network_connection)
                            )
                        )
                    }

                    else -> {
                        _commands.tryEmit(
                            PostDetailCommands.ShowSnackbar(
                                resourcesManager.getString(R.string.error_server_connection)
                            )
                        )
                    }
                }
            }
        }
    }

    fun onOpenMapError() {
        _commands.tryEmit(
            PostDetailCommands.ShowSnackbar(
                resourcesManager.getString(R.string.absence_app)
            )
        )
    }

    fun onBackPressedClick() {
        val postId = _postFlow.value?.id ?: return
        val isLike = _postFlow.value?.likedByMe ?: return
        val likeCount = _postFlow.value?.likeCount ?: return
        _commands.tryEmit(PostDetailCommands.NavigateOnBackPressed(postId, isLike, likeCount))
    }

    fun onEditClick() {
        val postId = _postFlow.value?.id ?: return
        val content = _postFlow.value?.content ?: return
        _commands.tryEmit(
            PostDetailCommands.NavigateToEditPost(
                postId = postId,
                content = content
            )
        )
    }

    fun onDeleteClick() {
        val postId = _postFlow.value?.id ?: return
        viewModelScope.launch(dispatchers.IO) {
            _threeStateFlow.tryEmit(ThreeStateView.State.Loading)
            when (val resource = deletePostUseCase.execute(postId)) {
                is Resource.Success -> {
                    _commands.tryEmit(PostDetailCommands.NavigateUpWhenPostDeleted(postId))
                }

                is Resource.Error -> {
                    when (resource.cause) {
                        is NetworkException -> {
                            _commands.tryEmit(
                                PostDetailCommands.ShowSnackbar(
                                    resourcesManager.getString(R.string.error_network_connection)
                                )
                            )
                        }

                        else -> {
                            _commands.tryEmit(
                                PostDetailCommands.ShowSnackbar(
                                    resourcesManager.getString(R.string.error_server_connection)
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun onPostUpdated(content: String) {
        val post = _postFlow.value?.copy(content = content) ?: return
        _postFlow.tryEmit(post)
    }

    sealed interface PostDetailCommands : Commands {
        class OpenUrl(val url: String) : PostDetailCommands
        class ShowSnackbar(val message: String) : PostDetailCommands
        class NavigateOnBackPressed(
            val postId: Long,
            val isLike: Boolean,
            val likeCount: Int
        ) : PostDetailCommands

        class NavigateToEditPost(val postId: Long, val content: String) : PostDetailCommands

        class NavigateUpWhenPostDeleted(val postId: Long) : PostDetailCommands
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted postId: Long
        ): PostDetailViewModel
    }
}