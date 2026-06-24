package com.kelompok1.materialku.presentation.stok

import androidx.fragment.app.viewModels
import com.kelompok1.materialku.databinding.FragmentStokListBinding
import com.kelompok1.materialku.presentation.base.BaseFragment

class StokListFragment : BaseFragment<FragmentStokListBinding>(
    FragmentStokListBinding::inflate
) {
    private val viewModel: StokViewModel by viewModels()

    override fun setupViews() {
        binding.btnBack.setOnClickListener {
            // TODO: pop back
        }
        binding.btnSearch.setOnClickListener {
            // TODO: toggle search
        }
    }

    override fun observeViewModel() {
        // TODO: observe stok list
    }
}