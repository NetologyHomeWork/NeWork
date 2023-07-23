package ru.netology.nework.posts.presentation.posts

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
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
import ru.netology.nework.posts.data.mappers.toListPostItem
import ru.netology.nework.posts.domain.usecases.DeletePostUseCase
import ru.netology.nework.posts.domain.usecases.GetPostsUseCase
import ru.netology.nework.posts.domain.usecases.LikePostUseCase
import ru.netology.nework.posts.presentation.posts.adapter.PostItem
import javax.inject.Inject

@HiltViewModel
class PostsViewModel @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val dispatchers: CoroutineDispatchers,
    private val resourcesManager: ResourcesManager
) : BaseViewModel<PostsViewModel.PostsCommands>() {

    private val _postsFlow = MutableStateFlow<List<PostItem>>(emptyList())
    val postsFlow = _postsFlow.asStateFlow()

    val threeStateFlow = _threeStateFlow.asStateFlow()
    val swipeRefreshingFlow = _swipeRefreshingFlow.asStateFlow()
    val commands = _commands.asSharedFlow()

    init {
        viewModelScope.launch(dispatchers.IO) {
            _threeStateFlow.tryEmit(ThreeStateView.State.Loading)
            fillPostList()
        }
    }

    fun onRetryClick() {
        viewModelScope.launch(dispatchers.IO) {
            _threeStateFlow.tryEmit(ThreeStateView.State.Loading)
            fillPostList()
        }
    }

    fun onSwipeRefresh() {
        viewModelScope.launch(dispatchers.IO) {
            _swipeRefreshingFlow.tryEmit(true)
            when (val resource = getPostsUseCase.execute()) {
                is Resource.Success -> {
                    _postsFlow.tryEmit(resource.data.toListPostItem())
                }
                is Resource.Error -> {
                    when (resource.cause) {
                        is NetworkException -> {
                            _commands.tryEmit(
                                PostsCommands.ShowSnackbar(
                                    resourcesManager.getString(R.string.error_network_connection)
                                )
                            )
                        }
                        else -> {
                            _commands.tryEmit(
                                PostsCommands.ShowSnackbar(
                                    resourcesManager.getString(R.string.error_server_connection)
                                )
                            )
                        }
                    }
                }
            }
            _swipeRefreshingFlow.tryEmit(false)
        }
    }

    fun likePost(postId: Long, isLike: Boolean) {
        viewModelScope.launch(dispatchers.IO) {
            updateLike(postId)
            val resource = likePostUseCase.execute(postId, isLike)
            if (resource is Resource.Error) {
                updateLike(postId)
                when (resource.cause) {
                    is NetworkException -> {
                        _commands.tryEmit(
                            PostsCommands.ShowSnackbar(
                                resourcesManager.getString(R.string.error_network_connection)
                            )
                        )
                    }
                    else -> {
                        _commands.tryEmit(
                            PostsCommands.ShowSnackbar(
                                resourcesManager.getString(R.string.error_server_connection)
                            )
                        )
                    }
                }
            }
        }
    }

    fun onDeletePostClick(postId: Long) {
        viewModelScope.launch(dispatchers.IO) {
            updateDelete(postId, true)
            when (val resource = deletePostUseCase.execute(postId)) {
                is Resource.Success -> removePost(postId)
                is Resource.Error -> {
                    updateDelete(postId, false)
                    when (resource.cause) {
                        is NetworkException -> {
                            _commands.tryEmit(
                                PostsCommands.ShowSnackbar(
                                    resourcesManager.getString(R.string.error_network_connection)
                                )
                            )
                        }
                        else -> {
                            _commands.tryEmit(
                                PostsCommands.ShowSnackbar(
                                    resourcesManager.getString(R.string.error_server_connection)
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun onPostUpdated(postId: Long, content: String) {
        val mutable = mutableListOf<PostItem>().apply { addAll(postsFlow.value) }
        val index = mutable.indexOfFirst { it.id == postId }
        if (index == -1) return
        val temp = mutable[index]
        mutable[index] = temp.copy(content = content)
        _postsFlow.tryEmit(mutable)
    }

    private suspend fun fillPostList() {
        delay(ThreeStateView.DELAY_LOADING)
        when (val resource = getPostsUseCase.execute()) {
            is Resource.Success -> {
                _postsFlow.tryEmit(resource.data.toListPostItem())
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

    private fun updateLike(postId: Long) {
        val mutable = mutableListOf<PostItem>().apply { addAll(postsFlow.value) }
        val index = mutable.indexOfFirst { it.id == postId }
        if (index == -1) return
        val temp = mutable[index]
        if (temp.likedByMe) {
            mutable[index] = temp.copy(likedByMe = false, likeCount = temp.likeCount - 1)
        } else {
            mutable[index] = temp.copy(likedByMe = true, likeCount = temp.likeCount + 1)
        }
        _postsFlow.tryEmit(mutable)
    }

    private fun updateDelete(postId: Long, inDeleteProgress: Boolean) {
        val mutable = mutableListOf<PostItem>().apply { addAll(postsFlow.value) }
        val index = mutable.indexOfFirst { it.id == postId }
        if (index == -1) return
        val temp = mutable[index]
        mutable[index] = temp.copy(inProgressToDelete = inDeleteProgress)
        _postsFlow.tryEmit(mutable)
    }

    private fun removePost(postId: Long) {
        val mutable = mutableListOf<PostItem>().apply { addAll(postsFlow.value) }
        val index = mutable.indexOfFirst { it.id == postId }
        if (index == -1) return
        mutable.removeAt(index)
        _postsFlow.tryEmit(mutable)
    }

    sealed interface PostsCommands : Commands {
        class ShowSnackbar(val message: String) : PostsCommands
    }
}