package com.kelompok1.materialku.presentation.material

import androidx.fragment.app.viewModels
import com.kelompok1.materialku.databinding.FragmentMaterialListBinding
import com.kelompok1.materialku.presentation.base.BaseFragment

class MaterialListFragment : BaseFragment<FragmentMaterialListBinding>(
    FragmentMaterialListBinding::inflate
) {
    private val viewModel: MaterialViewModel by viewModels()

    override fun setupViews() {
        binding.btnBack.setOnClickListener {
            // TODO: pop back
        }
        binding.fabAdd.setOnClickListener {
            // TODO: navigate to form
        }
        binding.btnSearch.setOnClickListener {
            // TODO: toggle search
        }
    }

    override fun observeViewModel() {
        // TODO: observe material list
    }
}