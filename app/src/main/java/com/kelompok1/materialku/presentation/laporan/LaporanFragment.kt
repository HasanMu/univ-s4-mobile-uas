package com.kelompok1.materialku.presentation.laporan

import androidx.fragment.app.viewModels
import com.kelompok1.materialku.databinding.FragmentLaporanBinding
import com.kelompok1.materialku.presentation.base.BaseFragment

class LaporanFragment : BaseFragment<FragmentLaporanBinding>(
    FragmentLaporanBinding::inflate
) {
    private val viewModel: LaporanViewModel by viewModels()

    override fun setupViews() {
        binding.btnBack.setOnClickListener {
            // TODO: pop back
        }
        binding.btnExport.setOnClickListener {
            // TODO: export PDF
        }
    }

    override fun observeViewModel() {
        // TODO: observe laporan data
    }
}