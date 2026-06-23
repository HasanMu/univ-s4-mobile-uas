package com.kelompok1.materialku.presentation.auth

import androidx.fragment.app.viewModels
import com.kelompok1.materialku.databinding.FragmentLoginBinding
import com.kelompok1.materialku.presentation.base.BaseFragment

class LoginFragment : BaseFragment<FragmentLoginBinding>(
    FragmentLoginBinding::inflate
) {
    private val viewModel: LoginViewModel by viewModels()

    override fun setupViews() {
        binding.btnLogin.setOnClickListener {
            // TODO: validate and call viewModel.login()
        }
    }

    override fun observeViewModel() {
        // TODO: observe login state
    }
}