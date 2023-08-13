package ru.netology.nework.core.domain

import kotlinx.coroutines.flow.StateFlow
import ru.netology.nework.core.domain.entities.AuthModel

interface SettingsDataSource {

    val authFlow: StateFlow<AuthModel?>

    fun setAuth(authModel: AuthModel)

    fun getAuth(): AuthModel?

    fun logout()
}