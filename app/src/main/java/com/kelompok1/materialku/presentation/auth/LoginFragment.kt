package com.kelompok1.materialku.presentation.auth

import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.kelompok1.materialku.R
import com.kelompok1.materialku.databinding.FragmentLoginBinding
import com.kelompok1.materialku.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(
    FragmentLoginBinding::inflate
) {
    private val viewModel: LoginViewModel by viewModels()

    override fun useLightStatusBarIcons(): Boolean = true

    // Root layout pakai fitsSystemWindows di NestedScrollView — biar
    // dia yang consume top inset. BaseFragment gak perlu tambah lagi.
    override fun useStatusBarInset(): Boolean = false

    override fun setupViews() {
        binding.btnLogin.setOnClickListener { submit() }
        binding.etUsername.doAfterTextChanged { viewModel.resetError() }
        binding.etPassword.doAfterTextChanged { viewModel.resetError() }
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { render(it) }
            }
        }
    }

    private fun submit() {
        // Tutup keyboard dulu supaya error tidak ketutup keyboard,
        // dan supaya user jelas lihat progress spinner + tombol disable.
        hideKeyboard()
        val username = binding.etUsername.text?.toString().orEmpty()
        val password = binding.etPassword.text?.toString().orEmpty()
        viewModel.login(username, password)
    }

    private fun render(state: LoginState) {
        binding.progress.visibility = if (state is LoginState.Loading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = state !is LoginState.Loading
        binding.etUsername.isEnabled = state !is LoginState.Loading
        binding.etPassword.isEnabled = state !is LoginState.Loading

        when (state) {
            is LoginState.Error -> {
                binding.tvError.text = state.message
                binding.tvError.visibility = View.VISIBLE
            }
            is LoginState.Success -> {
                binding.tvError.visibility = View.GONE
                findNavController().navigate(R.id.action_login_to_dashboard)
            }
            else -> {
                binding.tvError.visibility = View.GONE
            }
        }
    }
}
