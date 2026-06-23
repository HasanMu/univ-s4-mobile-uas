package com.kelompok1.materialku.presentation.pos

import androidx.fragment.app.viewModels
import com.kelompok1.materialku.databinding.FragmentPosBinding
import com.kelompok1.materialku.presentation.base.BaseFragment

class PosFragment : BaseFragment<FragmentPosBinding>(
    FragmentPosBinding::inflate
) {
    private val viewModel: PosViewModel by viewModels()

    override fun setupViews() {
        binding.btnBack.setOnClickListener {
            // TODO: pop back
        }
        binding.btnCheckout.setOnClickListener {
            // TODO: show cart bottom sheet
        }
    }

    override fun observeViewModel() {
        // TODO: observe material list and cart
    }
}