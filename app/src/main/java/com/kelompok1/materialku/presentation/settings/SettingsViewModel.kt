package com.kelompok1.materialku.presentation.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.viewModelScope
import com.kelompok1.materialku.data.local.PreferencesDataStore
import com.kelompok1.materialku.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: PreferencesDataStore
) : BaseViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        prefs.darkMode
            .onEach { enabled ->
                _state.value = _state.value.copy(darkMode = enabled, loaded = true)
            }
            .launchIn(viewModelScope)
    }

    fun setDarkMode(enabled: Boolean) {
        launchWithError {
            prefs.setDarkMode(enabled)
            // Apply immediately supaya user langsung liat efeknya — DataStore
            // flow di init akan tetep update state, tapi setDefaultNightMode
            // di sini bikin recreate Activity terjadi seketika.
            AppCompatDelegate.setDefaultNightMode(
                if (enabled) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }
}

data class SettingsState(
    val darkMode: Boolean = false,
    val loaded: Boolean = false
)
