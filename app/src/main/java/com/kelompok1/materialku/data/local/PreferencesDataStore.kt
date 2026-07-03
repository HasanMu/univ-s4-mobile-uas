package com.kelompok1.materialku.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kelompok1.materialku.domain.model.RoleEnum
import com.kelompok1.materialku.domain.repository.SessionSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "materialku_prefs")

@Singleton
class PreferencesDataStore @Inject constructor(
    private val context: Context
) {

    private val store: DataStore<Preferences> get() = context.dataStore

    val session: Flow<SessionSnapshot?> = store.data.map { prefs ->
        val id = prefs[KEY_USER_ID] ?: return@map null
        val username = prefs[KEY_USERNAME] ?: return@map null
        val roleName = prefs[KEY_ROLE] ?: return@map null
        SessionSnapshot(id, username, RoleEnum.valueOf(roleName))
    }

    suspend fun saveSession(userId: Int, username: String, role: RoleEnum) {
        store.edit { prefs ->
            prefs[KEY_USER_ID] = userId
            prefs[KEY_USERNAME] = username
            prefs[KEY_ROLE] = role.name
        }
    }

    suspend fun clearSession() {
        store.edit { prefs ->
            prefs.remove(KEY_USER_ID)
            prefs.remove(KEY_USERNAME)
            prefs.remove(KEY_ROLE)
        }
    }

    val darkMode: Flow<Boolean> = store.data.map { prefs ->
        prefs[KEY_DARK_MODE] ?: false
    }

    suspend fun setDarkMode(enabled: Boolean) {
        store.edit { prefs -> prefs[KEY_DARK_MODE] = enabled }
    }

    companion object {
        private val KEY_USER_ID = intPreferencesKey("session_user_id")
        private val KEY_USERNAME = stringPreferencesKey("session_username")
        private val KEY_ROLE = stringPreferencesKey("session_role")
        private val KEY_DARK_MODE = booleanPreferencesKey("pref_dark_mode")
    }
}
