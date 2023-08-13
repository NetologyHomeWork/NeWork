package ru.netology.nework.profile.presentation.profile

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.netology.nework.R
import ru.netology.nework.core.domain.NetworkException
import ru.netology.nework.core.domain.entities.JobData
import ru.netology.nework.core.presentation.base.BaseViewModel
import ru.netology.nework.core.presentation.base.Commands
import ru.netology.nework.core.presentation.view.ThreeStateView
import ru.netology.nework.core.utils.CoroutineDispatchers
import ru.netology.nework.core.utils.Resource
import ru.netology.nework.core.utils.ResourcesManager
import ru.netology.nework.profile.domain.usecases.ProfileViewModelUseCaseInteractor
import ru.netology.nework.profile.presentation.profile.adapter.ProfileItems
import ru.netology.nework.profile.presentation.profile.adapter.toJobItemList
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val interactor: ProfileViewModelUseCaseInteractor,
    private val resourcesManager: ResourcesManager,
    private val dispatchers: CoroutineDispatchers
) : BaseViewModel<ProfileViewModel.ProfileCommands>() {

    private val _screenStateFlow = MutableStateFlow<ProfileScreenState?>(null)
    val screenStateFlow = _screenStateFlow.asStateFlow()

    val commands = _commands.asSharedFlow()
    val threeStateFlow = _threeStateFlow.asStateFlow()

    init {
        viewModelScope.launch(dispatchers.IO) {
            fillProfile()
        }
    }

    fun onLogoutClick() {
        interactor.logout()
    }

    fun onRetryClick() {
        viewModelScope.launch(dispatchers.IO) {
            fillProfile()
        }
    }

    fun onDeleteClick(jobId: Long) {
        viewModelScope.launch(dispatchers.IO) {
            updateProgressToDelete(jobId, true)
            when (val resource = interactor.deleteJob(jobId)) {
                is Resource.Success -> deleteItem(jobId)
                is Resource.Error -> {
                    updateProgressToDelete(jobId, false)
                    when (resource.cause) {
                        is NetworkException -> {
                            _commands.tryEmit(
                                ProfileCommands.ShowSnackbar(
                                    resourcesManager.getString(R.string.error_network_connection)
                                )
                            )
                        }

                        else -> {
                            _commands.tryEmit(
                                ProfileCommands.ShowSnackbar(
                                    resourcesManager.getString(R.string.error_server_connection)
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun fillProfile() {
        _threeStateFlow.tryEmit(ThreeStateView.State.Loading)
        delay(ThreeStateView.DELAY_LOADING)
        val userDataDeferred = viewModelScope.async(dispatchers.IO) {
            interactor.getUserData()
        }

        val jobListDeferred = viewModelScope.async(dispatchers.IO) {
            interactor.getAllJobs()
        }

        when (val userData = userDataDeferred.await()) {
            is Resource.Error -> {
                jobListDeferred.cancel()
                when (userData.cause) {
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

            is Resource.Success -> {
                val currentJob = jobListDeferred.await()

                _screenStateFlow.tryEmit(
                    ProfileScreenState(
                        name = userData.data.name,
                        avatar = userData.data.avatar,
                        currentJob = getCurrentJob(currentJob),
                        jobList = transformJobList(currentJob)
                    )
                )

                _threeStateFlow.tryEmit(ThreeStateView.State.Content)
            }
        }
    }

    private fun transformJobList(resource: Resource<List<JobData>>): List<ProfileItems> {
        val mutable = mutableListOf<ProfileItems>()
        if (resource is Resource.Success) {
            if (resource.data.isNotEmpty()) {
                mutable.addAll(resource.data.toJobItemList(resourcesManager))
                mutable.add(ProfileItems.AddJobItem)
            } else {
                mutable.add(ProfileItems.EmptyJobList(resourcesManager.getString(R.string.absence_job_data)))
                mutable.add(ProfileItems.AddJobItem)
            }
        } else {
            mutable.add(ProfileItems.EmptyJobList(resourcesManager.getString(R.string.unfortunately_load_job)))
        }
        return mutable
    }

    private fun getCurrentJob(resource: Resource<List<JobData>>): String {
        return if (resource is Resource.Success) {
            if (resource.data.isNotEmpty()) {
                resource.data.first { it.finish == null }.name
            } else {
                resourcesManager.getString(R.string.no_job)
            }
        } else {
            resourcesManager.getString(R.string.unfortunately_load_job)
        }
    }

    private fun updateProgressToDelete(jobId: Long, inDeleteProgress: Boolean) {
        val mutable = screenStateFlow.value?.jobList
            ?.toMutableList() ?: mutableListOf()

        val index = mutable.indexOfFirst { it is ProfileItems.JobItem && it.id == jobId }
        if (index == -1) return
        val temp = mutable[index] as ProfileItems.JobItem
        mutable[index] = temp.copy(deleteInProgress = inDeleteProgress)
        val newState = screenStateFlow.value?.copy(jobList = mutable)
        _screenStateFlow.tryEmit(newState)
    }

    private fun deleteItem(jobId: Long) {
        val mutable = screenStateFlow.value?.jobList
            ?.toMutableList() ?: mutableListOf()

        val index = mutable.indexOfFirst { it is ProfileItems.JobItem && it.id == jobId }
        if (index == -1) return
        mutable.removeAt(index)
        val newState = if (mutable.any { it is ProfileItems.JobItem }.not()) {
            mutable.add(
                0,
                ProfileItems.EmptyJobList(resourcesManager.getString(R.string.absence_job_data))
            )
            screenStateFlow.value?.copy(
                currentJob = resourcesManager.getString(R.string.no_job),
                jobList = mutable
            )
        } else {
            screenStateFlow.value?.copy(jobList = mutable)
        }
        _screenStateFlow.tryEmit(newState)
    }

    fun onJobListUpdated(jobItem: ProfileItems.JobItem) {
        val mutable = screenStateFlow.value?.jobList
            ?.toMutableList() ?: mutableListOf()

        val index = mutable.indexOfFirst { it is ProfileItems.JobItem && it.id == jobItem.id }
        if (index == -1) mutable.add(jobItem)
        mutable[index] = jobItem
        val newState = screenStateFlow.value?.copy(jobList = mutable)
        _screenStateFlow.tryEmit(newState)
    }

    sealed interface ProfileCommands : Commands {
        class ShowSnackbar(val message: String) : ProfileCommands
    }

    data class ProfileScreenState(
        val name: String,
        val avatar: String?,
        val currentJob: String?,
        val jobList: List<ProfileItems>
    )
}