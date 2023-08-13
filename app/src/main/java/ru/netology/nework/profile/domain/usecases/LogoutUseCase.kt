package ru.netology.nework.profile.domain.usecases

import ru.netology.nework.core.domain.SettingsDataSource
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val settings: SettingsDataSource
) {
    fun execute() {
        settings.logout()
    }
}