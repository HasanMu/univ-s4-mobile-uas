package com.kelompok1.materialku.presentation.dashboard

import androidx.fragment.app.viewModels
import com.kelompok1.materialku.databinding.FragmentDashboardBinding
import com.kelompok1.materialku.presentation.base.BaseFragment

class DashboardFragment : BaseFragment<FragmentDashboardBinding>(
    FragmentDashboardBinding::inflate
) {
    private val viewModel: DashboardViewModel by viewModels()

    override fun setupViews() {
        binding.cardMaterial.setOnClickListener {
            // TODO: navigate to materialList
        }
        binding.cardKategori.setOnClickListener {
            // TODO: navigate to kategori
        }
        binding.cardPos.setOnClickListener {
            // TODO: navigate to pos
        }
        binding.cardLaporan.setOnClickListener {
            // TODO: navigate to laporan
        }
        binding.cardStok.setOnClickListener {
            // TODO: navigate to stok
        }
        binding.cardUser.setOnClickListener {
            // TODO: navigate to user
        }
        binding.btnLogout.setOnClickListener {
            // TODO: logout and navigate to login
        }
    }

    override fun observeViewModel() {
        // TODO: observe dashboard stats
    }
}