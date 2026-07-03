package com.kelompok1.materialku.presentation.dashboard

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.kelompok1.materialku.R
import com.kelompok1.materialku.databinding.FragmentDashboardBinding
import com.kelompok1.materialku.domain.model.RoleEnum
import com.kelompok1.materialku.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding>(
    FragmentDashboardBinding::inflate
) {
    private val viewModel: DashboardViewModel by viewModels()

    override fun setupViews() {
        binding.cardMaterial.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_material)
        }
        binding.cardKategori.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_kategori)
        }
        binding.cardPos.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_pos)
        }
        binding.cardLaporan.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_laporan)
        }
        binding.cardStok.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_stok)
        }
        binding.cardUser.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_user)
        }
        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_settings)
        }
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { render(it) }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { handleEvent(it) }
            }
        }
    }

    private fun render(state: DashboardState) {
        binding.tvUserName.text = state.userName
        binding.tvStatMaterial.text = state.statMaterial.toString()
        binding.tvStatTransaksi.text = state.statTransaksi.toString()
        binding.tvStatKritis.text = state.statKritis.toString()

        val role = state.role
        if (role != null) {
            binding.tvRoleLabel.text = role.displayName().uppercase()
            binding.cvRoleBadge.setCardBackgroundColor(
                ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), role.badgeColorRes())
                )
            )
            applyRoleVisibility(role)
        }
    }

    private fun handleEvent(event: DashboardEvent) {
        when (event) {
            DashboardEvent.LoggedOut,
            DashboardEvent.SessionExpired -> {
                findNavController().navigate(R.id.action_dashboard_to_login)
            }
        }
    }

    /**
     * Role access matrix — sesuai CONTRIBUTING.md.
     * Screen yang tidak visible di-hide (GONE) untuk menghilangkan celah spacing.
     */
    private fun applyRoleVisibility(role: RoleEnum) = when (role) {
        RoleEnum.ROLE_ADMIN -> {
            // Semua card visible
            showAll()
        }
        RoleEnum.ROLE_KASIR -> {
            hideAll()
            binding.cardPos.visibility = View.VISIBLE
        }
        RoleEnum.ROLE_GUDANG -> {
            hideAll()
            binding.cardMaterial.visibility = View.VISIBLE
            binding.cardKategori.visibility = View.VISIBLE
            binding.cardStok.visibility = View.VISIBLE
        }
        RoleEnum.ROLE_MANAGER -> {
            hideAll()
            binding.cardMaterial.visibility = View.VISIBLE
            binding.cardKategori.visibility = View.VISIBLE
            binding.cardStok.visibility = View.VISIBLE
            binding.cardLaporan.visibility = View.VISIBLE
        }
    }

    private fun showAll() {
        binding.cardMaterial.visibility = View.VISIBLE
        binding.cardKategori.visibility = View.VISIBLE
        binding.cardPos.visibility = View.VISIBLE
        binding.cardLaporan.visibility = View.VISIBLE
        binding.cardStok.visibility = View.VISIBLE
        binding.cardUser.visibility = View.VISIBLE
    }

    private fun hideAll() {
        binding.cardMaterial.visibility = View.INVISIBLE
        binding.cardKategori.visibility = View.INVISIBLE
        binding.cardPos.visibility = View.INVISIBLE
        binding.cardLaporan.visibility = View.INVISIBLE
        binding.cardStok.visibility = View.INVISIBLE
        binding.cardUser.visibility = View.INVISIBLE
    }
}

private fun RoleEnum.badgeColorRes(): Int = when (this) {
    RoleEnum.ROLE_ADMIN -> R.color.role_admin
    RoleEnum.ROLE_KASIR -> R.color.role_kasir
    RoleEnum.ROLE_GUDANG -> R.color.role_gudang
    RoleEnum.ROLE_MANAGER -> R.color.role_manager
}
