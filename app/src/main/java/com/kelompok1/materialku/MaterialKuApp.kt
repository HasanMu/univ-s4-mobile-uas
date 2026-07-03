package com.kelompok1.materialku

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.kelompok1.materialku.data.local.PreferencesDataStore
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltAndroidApp
class MaterialKuApp : Application() {

    @Inject lateinit var prefs: PreferencesDataStore

    override fun onCreate() {
        super.onCreate()
        // Baca preferensi dark mode secara blocking supaya theme
        // ke-apply SEBELUM Activity attach — kalau nunggu Flow di
        // Application scope, akan terjadi flash light→dark saat launch.
        val darkMode = runBlocking { prefs.darkMode.first() }
        AppCompatDelegate.setDefaultNightMode(
            if (darkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
