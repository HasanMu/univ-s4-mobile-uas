package com.kelompok1.materialku.presentation.material

import androidx.fragment.app.viewModels
import com.kelompok1.materialku.databinding.FragmentMaterialFormBinding
import com.kelompok1.materialku.presentation.base.BaseFragment

class MaterialFormFragment : BaseFragment<FragmentMaterialFormBinding>(
    FragmentMaterialFormBinding::inflate
) {
    private val viewModel: MaterialViewModel by viewModels()

    override fun setupViews() {
        binding.btnBack.setOnClickListener {
            // TODO: pop back
        }
        binding.btnSave.setOnClickListener {
            // TODO: validate and save
        }
    }

    override fun observeViewModel() {
        // TODO: observe save result
    }
}