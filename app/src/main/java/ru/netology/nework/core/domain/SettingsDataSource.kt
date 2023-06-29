package ru.netology.nework.core.domain

import kotlinx.coroutines.flow.StateFlow

interface SettingsDataSource {

    val authFlow: StateFlow<AuthModel?>

    fun setAuth(authModel: AuthModel)

    fun getAuth(): AuthModel?
}