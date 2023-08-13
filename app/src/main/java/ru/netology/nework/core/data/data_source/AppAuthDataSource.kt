package ru.netology.nework.core.data.data_source

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import ru.netology.nework.core.domain.entities.AuthModel
import ru.netology.nework.core.domain.SettingsDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuthDataSource @Inject constructor(
    @ApplicationContext context: Context
) : SettingsDataSource, OnSharedPreferenceChangeListener {

    private val prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val _authFlow = MutableStateFlow<AuthModel?>(null)

    override val authFlow: StateFlow<AuthModel?>
        get() = _authFlow.asStateFlow()

    init {
        getAuth()
    }

    override fun setAuth(authModel: AuthModel) {
        val str = Json.encodeToString(AuthModel.serializer(), authModel)
        prefs.edit { putString(AUTH_KEY, str) }
    }

    override fun getAuth(): AuthModel? {
        val json = prefs.getString(AUTH_KEY, null) ?: return null
        return try {
            val auth = Json.decodeFromString(AuthModel.serializer(), json)
            _authFlow.tryEmit(auth)
            auth
        } catch (e: Exception) {
            null
        }
    }

    override fun logout() {
        prefs.edit {
            remove(AUTH_KEY)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        _authFlow.tryEmit(getAuth())
    }

    private companion object {
        const val PREFERENCES_NAME = "PREFERENCES_NAME"
        const val AUTH_KEY = "AUTH_KEY"
    }
}