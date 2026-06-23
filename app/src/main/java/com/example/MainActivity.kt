package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.data.database.AppDatabase
import com.example.data.repository.MaterialRepositoryImpl
import com.example.data.repository.StokRepositoryImpl
import com.example.data.repository.TransaksiRepositoryImpl
import com.example.data.repository.UserRepositoryImpl
import com.example.ui.screens.AppContent
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Local Room Database manual context injection
        val database = AppDatabase.getDatabase(applicationContext)

        val userRepository = UserRepositoryImpl(database.userDao())
        val materialRepository = MaterialRepositoryImpl(
            database.materialDao(),
            database.kategoriDao(),
            database.satuanDao(),
            database.supplierDao()
        )
        val transaksiRepository = TransaksiRepositoryImpl(
            database.transaksiDao(),
            database.itemTransaksiDao()
        )
        val stokRepository = StokRepositoryImpl(database.stokLogDao())

        // Build Factory standard provider
        val factory = MainViewModelFactory(
            userRepository,
            materialRepository,
            transaksiRepository,
            stokRepository
        )
        val viewModel: MainViewModel by viewModels { factory }

        setContent {
            MyApplicationTheme {
                AppContent(viewModel = viewModel)
            }
        }
    }
}
