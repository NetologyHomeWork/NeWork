package ru.netology.nework.create.presentation.option

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.netology.nework.core.presentation.view.ThreeStateView
import ru.netology.nework.core.utils.CoroutineDispatchers
import ru.netology.nework.core.utils.Resource
import ru.netology.nework.create.domain.usecases.GetUserDataUseCase
import ru.netology.nework.create.domain.usecases.GetUserJobUseCase
import javax.inject.Inject

@HiltViewModel
class OptionViewModel @Inject constructor(
    getUserDataUseCase: GetUserDataUseCase,
    getUserJobUseCase: GetUserJobUseCase,
    dispatchers: CoroutineDispatchers
) : ViewModel() {

    private val _optionScreenStateFlow = MutableStateFlow<OptionScreenState?>(null)
    val optionScreenStateFlow: StateFlow<OptionScreenState?> = _optionScreenStateFlow.asStateFlow()

    private val _threeStateFlow = MutableStateFlow<ThreeStateView.State>(ThreeStateView.State.Loading)
    val threeStateFlow: StateFlow<ThreeStateView.State> = _threeStateFlow

    init {
        viewModelScope.launch(dispatchers.IO) {
            val userDeferred = viewModelScope.async {
                getUserDataUseCase.execute()
            }
            val jobDeferred = viewModelScope.async {
                getUserJobUseCase.execute()
            }
            val userResource = userDeferred.await()
            val jobResource = jobDeferred.await()
            val state = OptionScreenState(
                userName = if (userResource is Resource.Success) userResource.data.name else null,
                userAvatar = if (userResource is Resource.Success) userResource.data.avatar else null,
                userJob = if (jobResource is Resource.Success) jobResource.data.position else null,
                isError = userResource is Resource.Success
            )
            _optionScreenStateFlow.tryEmit(state)
            _threeStateFlow.tryEmit(ThreeStateView.State.Content)
        }
    }

    data class OptionScreenState(
        val userName: String?,
        val userAvatar: String?,
        val userJob: String?,
        val isError: Boolean
    )
}