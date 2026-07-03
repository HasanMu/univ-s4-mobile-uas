package com.kelompok1.materialku.presentation.settings

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.kelompok1.materialku.R
import com.kelompok1.materialku.databinding.FragmentSettingsBinding
import com.kelompok1.materialku.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>(
    FragmentSettingsBinding::inflate
) {
    private val viewModel: SettingsViewModel by viewModels()

    override fun setupViews() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        // Listener di-set di render() setelah loaded=true supaya nilai
        // awal dari DataStore gak nge-trigger toggle spuriously.
        binding.rowPrivasi.setOnClickListener {
            Toast.makeText(requireContext(), R.string.settings_soon, Toast.LENGTH_SHORT).show()
        }
        binding.rowBantuan.setOnClickListener {
            Toast.makeText(requireContext(), R.string.settings_soon, Toast.LENGTH_SHORT).show()
        }
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { render(it) }
            }
        }
    }

    private fun render(state: SettingsState) {
        if (!state.loaded) return
        // Set tanpa memicu listener — supaya initial state gak keitung
        // sebagai user action.
        binding.switchDarkMode.setOnCheckedChangeListener(null)
        binding.switchDarkMode.isChecked = state.darkMode
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkMode(isChecked)
        }
    }
}
