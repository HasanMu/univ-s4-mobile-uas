package com.kelompok1.materialku

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.NavHostFragment
import com.kelompok1.materialku.data.local.PreferencesDataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var prefs: PreferencesDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)

        // Auto-skip Login kalau session masih ada. Baca DataStore secara
        // blocking sebelum nav graph di-inflate — supaya user langsung
        // liat Dashboard tanpa flash Login. Bacaan DataStore tunggal
        // cepat (~beberapa ms), aman di UI thread startup.
        val session = runBlocking { prefs.session.first() }
        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val graph = navHost.navController.navInflater.inflate(R.navigation.nav_graph)
        if (session != null) {
            graph.setStartDestination(R.id.dashboardFragment)
        }
        navHost.navController.graph = graph
    }
}
