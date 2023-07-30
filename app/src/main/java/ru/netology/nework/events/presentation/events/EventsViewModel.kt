package ru.netology.nework.events.presentation.events

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
import ru.netology.nework.events.domain.usecases.DeleteEventUseCase
import ru.netology.nework.events.domain.usecases.GetEventsUseCase
import ru.netology.nework.events.domain.usecases.LikeEventUseCase
import ru.netology.nework.events.presentation.events.adaprer.EventItem
import ru.netology.nework.events.presentation.events.adaprer.EventMapper
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val getEventsUseCase: GetEventsUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
    private val likeEventUseCase: LikeEventUseCase,
    private val dispatchers: CoroutineDispatchers,
    private val mapper: EventMapper,
    private val resourcesManager: ResourcesManager
) : BaseViewModel<EventsViewModel.EventsCommand>() {

    private val _eventsFlow = MutableStateFlow<List<EventItem>>(emptyList())
    val eventsFlow = _eventsFlow.asStateFlow()

    val threeStateFlow = _threeStateFlow.asStateFlow()
    val swipeRefreshingFlow = _swipeRefreshingFlow.asStateFlow()
    val commands = _commands.asSharedFlow()

    init {
        viewModelScope.launch(dispatchers.IO) {
            _threeStateFlow.tryEmit(ThreeStateView.State.Loading)
            fillEventList()
        }
    }

    fun onRetryClick() {
        viewModelScope.launch(dispatchers.IO) {
            _threeStateFlow.tryEmit(ThreeStateView.State.Loading)
            fillEventList()
        }
    }

    fun onSwipeRefresh() {
        viewModelScope.launch(dispatchers.IO) {
            _swipeRefreshingFlow.tryEmit(true)
            when (val resource = getEventsUseCase.execute()) {
                is Resource.Success -> {
                    _eventsFlow.tryEmit(
                        mapper.mapListEventToListEventItem(resource.data)
                    )
                }
                is Resource.Error -> {
                    when (resource.cause) {
                        is NetworkException -> {
                            _commands.tryEmit(
                                EventsCommand.ShowSnackbar(
                                    resourcesManager.getString(R.string.error_network_connection)
                                )
                            )
                        }
                        else -> {
                            _commands.tryEmit(
                                EventsCommand.ShowSnackbar(
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

    fun likeEvent(eventId: Long, isLike: Boolean) {
        viewModelScope.launch(dispatchers.IO) {
            updateLike(eventId)
            val resource = likeEventUseCase.execute(eventId, isLike)
            if (resource is Resource.Error) {
                updateLike(eventId)
                when (resource.cause) {
                    is NetworkException -> {
                        _commands.tryEmit(
                            EventsCommand.ShowSnackbar(
                                resourcesManager.getString(R.string.error_network_connection)
                            )
                        )
                    }
                    else -> {
                        _commands.tryEmit(
                            EventsCommand.ShowSnackbar(
                                resourcesManager.getString(R.string.error_server_connection)
                            )
                        )
                    }
                }
            }
        }
    }

    fun onDeletePostClick(eventID: Long) {
        viewModelScope.launch(dispatchers.IO) {
            updateDelete(eventID, true)
            when (val resource = deleteEventUseCase.execute(eventID)) {
                is Resource.Success -> removeEvent(eventID)
                is Resource.Error -> {
                    updateDelete(eventID, false)
                    when (resource.cause) {
                        is NetworkException -> {
                            _commands.tryEmit(
                                EventsCommand.ShowSnackbar(
                                    resourcesManager.getString(R.string.error_network_connection)
                                )
                            )
                        }
                        else -> {
                            _commands.tryEmit(
                                EventsCommand.ShowSnackbar(
                                    resourcesManager.getString(R.string.error_server_connection)
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun updateEvent(eventId: Long, content: String) {
        val mutable = mutableListOf<EventItem>().apply { addAll(eventsFlow.value) }
        val index = mutable.indexOfFirst { it.id == eventId }
        if (index == -1) return
        val temp = mutable[index]
        mutable[index] = temp.copy(content = content)
        _eventsFlow.tryEmit(mutable)
    }

    private suspend fun fillEventList() {
        val resource = getEventsUseCase.execute()
        when (resource) {
            is Resource.Success -> {
                _eventsFlow.tryEmit(
                    mapper.mapListEventToListEventItem(resource.data)
                )
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

    private fun updateLike(eventId: Long) {
        val mutable = mutableListOf<EventItem>().apply { addAll(eventsFlow.value) }
        val index = mutable.indexOfFirst { it.id == eventId }
        if (index == -1) return
        val temp = mutable[index]
        if (temp.likedByMe) {
            mutable[index] = temp.copy(likedByMe = false, likeCount = temp.likeCount - 1)
        } else {
            mutable[index] = temp.copy(likedByMe = true, likeCount = temp.likeCount + 1)
        }
        _eventsFlow.tryEmit(mutable)
    }

    private fun updateDelete(eventId: Long, inDeleteProgress: Boolean) {
        val mutable = mutableListOf<EventItem>().apply { addAll(eventsFlow.value) }
        val index = mutable.indexOfFirst { it.id == eventId }
        if (index == -1) return
        val temp = mutable[index]
        mutable[index] = temp.copy(inProgressToDelete = inDeleteProgress)
        _eventsFlow.tryEmit(mutable)
    }

    private fun removeEvent(eventId: Long) {
        val mutable = mutableListOf<EventItem>().apply { addAll(eventsFlow.value) }
        val index = mutable.indexOfFirst { it.id == eventId }
        if (index == -1) return
        mutable.removeAt(index)
        _eventsFlow.tryEmit(mutable)
    }

    sealed interface EventsCommand : Commands {
        class ShowSnackbar(val message: String) : EventsCommand
    }
}