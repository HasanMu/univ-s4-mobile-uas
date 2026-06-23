package com.kelompok1.materialku.presentation.user

import androidx.fragment.app.viewModels
import com.kelompok1.materialku.databinding.FragmentUserBinding
import com.kelompok1.materialku.presentation.base.BaseFragment

class UserFragment : BaseFragment<FragmentUserBinding>(
    FragmentUserBinding::inflate
) {
    private val viewModel: UserViewModel by viewModels()

    override fun setupViews() {
        binding.btnBack.setOnClickListener {
            // TODO: pop back
        }
        binding.fabAdd.setOnClickListener {
            // TODO: show add user dialog
        }
    }

    override fun observeViewModel() {
        // TODO: observe user list
    }
}