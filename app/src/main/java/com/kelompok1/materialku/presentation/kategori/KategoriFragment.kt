package com.kelompok1.materialku.presentation.kategori

import androidx.fragment.app.viewModels
import com.kelompok1.materialku.databinding.FragmentKategoriBinding
import com.kelompok1.materialku.presentation.base.BaseFragment

class KategoriFragment : BaseFragment<FragmentKategoriBinding>(
    FragmentKategoriBinding::inflate
) {
    private val viewModel: KategoriViewModel by viewModels()

    override fun setupViews() {
        binding.btnBack.setOnClickListener {
            // TODO: pop back
        }
        binding.btnAddKategori.setOnClickListener {
            // TODO: show add dialog
        }
        binding.btnAddSatuan.setOnClickListener {
            // TODO: show add dialog
        }
    }

    override fun observeViewModel() {
        // TODO: observe kategori and satuan lists
    }
}