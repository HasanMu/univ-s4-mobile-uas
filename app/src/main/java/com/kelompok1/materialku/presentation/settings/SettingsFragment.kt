package com.kelompok1.materialku.presentation.settings

import androidx.fragment.app.viewModels
import com.kelompok1.materialku.databinding.FragmentSettingsBinding
import com.kelompok1.materialku.presentation.base.BaseFragment

class SettingsFragment : BaseFragment<FragmentSettingsBinding>(
    FragmentSettingsBinding::inflate
) {
    private val viewModel: SettingsViewModel by viewModels()

    override fun setupViews() {
        binding.btnBack.setOnClickListener {
            // TODO: pop back
        }
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            // TODO: toggle dark mode
        }
        binding.rowPrivasi.setOnClickListener {
            // TODO: open privacy policy
        }
        binding.rowBantuan.setOnClickListener {
            // TODO: open help
        }
    }

    override fun observeViewModel() {
        // TODO: observe settings state
    }
}