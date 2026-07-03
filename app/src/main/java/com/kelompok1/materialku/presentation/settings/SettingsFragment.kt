package com.kelompok1.materialku.presentation.settings

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.kelompok1.materialku.R
import com.kelompok1.materialku.databinding.FragmentSettingsBinding
import com.kelompok1.materialku.domain.model.RoleEnum
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
        // Profil
        if (state.username.isNotEmpty()) {
            binding.tvUsername.text = state.username
            binding.tvAvatarInitial.text = state.username.take(1).uppercase()
        }
        state.role?.let { binding.tvUserRole.text = it.roleDisplay() }

        // Dark mode toggle
        if (!state.loaded) return
        binding.switchDarkMode.setOnCheckedChangeListener(null)
        binding.switchDarkMode.isChecked = state.darkMode
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkMode(isChecked)
        }
    }

    private fun RoleEnum.roleDisplay(): String = when (this) {
        RoleEnum.ROLE_ADMIN -> getString(R.string.role_admin)
        RoleEnum.ROLE_KASIR -> getString(R.string.role_kasir)
        RoleEnum.ROLE_GUDANG -> getString(R.string.role_gudang)
        RoleEnum.ROLE_MANAGER -> getString(R.string.role_manager)
    }
}
