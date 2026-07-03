package com.kelompok1.materialku.presentation.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
) : Fragment() {

    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        applyStatusBarAppearance()
    }

    abstract fun setupViews()

    abstract fun observeViewModel()

    /**
     * True kalau background di area status bar berwarna gelap
     * (jadi icon status bar harus terang biar kebaca).
     * Default false = bg terang, icon gelap. Override di fragment berbg gelap.
     */
    protected open fun useLightStatusBarIcons(): Boolean = false

    protected fun hideKeyboard() {
        val view = activity?.currentFocus ?: binding.root
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
        binding.root.requestFocus()
    }

    private fun applyStatusBarAppearance() {
        val window = activity?.window ?: return
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = !useLightStatusBarIcons()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
