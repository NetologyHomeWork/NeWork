package ru.netology.nework.events.presentation.detail

import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
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
import ru.netology.nework.events.domain.usecases.DeleteEventUseCase
import ru.netology.nework.events.domain.usecases.GetEventByIdUseCase
import ru.netology.nework.events.domain.usecases.LikeEventUseCase
import ru.netology.nework.events.presentation.events.adaprer.EventItem
import ru.netology.nework.events.presentation.events.adaprer.EventMapper

class EventDetailViewModel @AssistedInject constructor(
    private val getEventByIdUseCase: GetEventByIdUseCase,
    private val likeEventUseCase: LikeEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
    private val dispatchers: CoroutineDispatchers,
    private val resourcesManager: ResourcesManager,
    private val mapper: EventMapper,
    @Assisted private val eventId: Long
) : BaseViewModel<EventDetailViewModel.EventDetailCommands>() {

    private val _eventFlow = MutableStateFlow<EventItem?>(null)
    val eventFlow = _eventFlow.filterNotNull()

    val threeStateFlow = _threeStateFlow.asStateFlow()
    val commands = _commands.asSharedFlow()

    init {
        viewModelScope.launch(dispatchers.IO) {
            fillEventScreen()
        }
    }

    fun onRetryClick() {
        viewModelScope.launch(dispatchers.IO) {
            fillEventScreen()
        }
    }

    private suspend fun fillEventScreen() {
        _threeStateFlow.tryEmit(ThreeStateView.State.Loading)
        delay(ThreeStateView.DELAY_LOADING)
        when (val resource = getEventByIdUseCase.execute(eventId)) {
            is Resource.Success -> {
                _eventFlow.tryEmit(mapper.mapEventToEventItem(resource.data))
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

    fun onInMapClick() {
        _commands.tryEmit(
            EventDetailCommands.OpenUrl(
                resourcesManager.getString(
                    R.string.coordinate_geo,
                    _eventFlow.value?.coords?.lat,
                    _eventFlow.value?.coords?.long
                )
            )
        )
    }

    fun onOpenMapError() {
        _commands.tryEmit(
            EventDetailCommands.ShowSnackbar(
                resourcesManager.getString(R.string.absence_app)
            )
        )
    }

    fun onLikeClick() {
        viewModelScope.launch(dispatchers.IO) {
            val isLike = _eventFlow.value?.likedByMe ?: return@launch
            val likeCount = _eventFlow.value?.likeCount ?: return@launch
            _eventFlow.tryEmit(
                _eventFlow.value?.copy(
                    likedByMe = isLike.not(),
                    likeCount = if (isLike) likeCount - 1 else likeCount + 1
                )
            )
            val resource = likeEventUseCase.execute(eventId, isLike)
            if (resource is Resource.Error) {
                _eventFlow.tryEmit(_eventFlow.value?.copy(likedByMe = isLike, likeCount = likeCount))
                when (resource.cause) {
                    is NetworkException -> {
                        _commands.tryEmit(
                            EventDetailCommands.ShowSnackbar(
                                resourcesManager.getString(R.string.error_network_connection)
                            )
                        )
                    }

                    else -> {
                        _commands.tryEmit(
                            EventDetailCommands.ShowSnackbar(
                                resourcesManager.getString(R.string.error_server_connection)
                            )
                        )
                    }
                }
            }
        }
    }

    fun onBackPressedClick() {
        val isLike = _eventFlow.value?.likedByMe ?: run {
            _commands.tryEmit(EventDetailCommands.NavigateOnBackPressed(null))
            return
        }
        val likeCount = _eventFlow.value?.likeCount ?: run {
            _commands.tryEmit(EventDetailCommands.NavigateOnBackPressed(null))
            return
        }
        _commands.tryEmit(
            EventDetailCommands.NavigateOnBackPressed(
                EventDetailFragment.LikeEventResult(
                    eventId = eventId,
                    isLike = isLike,
                    likeCount = likeCount
                )
            )
        )
    }

    fun onDeleteClick() {
        viewModelScope.launch(dispatchers.IO) {
            _threeStateFlow.tryEmit(ThreeStateView.State.Loading)
            delay(ThreeStateView.DELAY_LOADING)
            when (val resource = deleteEventUseCase.execute(eventId)) {
                is Resource.Success -> {
                    _commands.tryEmit(EventDetailCommands.NavigateUpWhenEventDeleted(eventId))
                }

                is Resource.Error -> {
                    when (resource.cause) {
                        is NetworkException -> {
                            _commands.tryEmit(
                                EventDetailCommands.ShowSnackbar(
                                    resourcesManager.getString(R.string.error_network_connection)
                                )
                            )
                        }

                        else -> {
                            _commands.tryEmit(
                                EventDetailCommands.ShowSnackbar(
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

    fun onEditClick() {
        val content = _eventFlow.value?.content ?: return
        val dataTime = _eventFlow.value?.datetime ?: return
        val type = _eventFlow.value?.type ?: return
        _commands.tryEmit(
            EventDetailCommands.NavigateToEditEvent(
                eventId = eventId,
                content = content,
                dataTime = dataTime,
                type = type
            )
        )
    }

    fun onEventUpdated(content: String) {
        val event = _eventFlow.value?.copy(content = content) ?: return
        _eventFlow.tryEmit(event)
    }


    sealed interface EventDetailCommands : Commands {
        class OpenUrl(val url: String) : EventDetailCommands
        class ShowSnackbar(val message: String) : EventDetailCommands

        class NavigateOnBackPressed(
            val likeResult: EventDetailFragment.LikeEventResult?
        ) : EventDetailCommands

        class NavigateUpWhenEventDeleted(val eventId: Long) : EventDetailCommands
        class NavigateToEditEvent(
            val eventId: Long,
            val content: String,
            val dataTime: String,
            val type: String
        ) : EventDetailCommands
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted eventId: Long
        ): EventDetailViewModel
    }
}