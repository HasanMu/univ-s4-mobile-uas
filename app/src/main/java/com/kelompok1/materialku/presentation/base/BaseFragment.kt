package com.kelompok1.materialku.presentation.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
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
        if (useStatusBarInset()) applyStatusBarInsetToRoot()
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

    /**
     * True (default) = BaseFragment auto-add tinggi status bar ke paddingTop
     * root view. Override false untuk fragment yang handle insets sendiri:
     * - LoginFragment: pakai fitsSystemWindows di NestedScrollView-nya
     * - DashboardFragment: applyStatusBarInsetToHero() ke section tertentu
     *   (bukan root) supaya hero gelap tetep extend ke atas status bar.
     */
    protected open fun useStatusBarInset(): Boolean = true

    private fun applyStatusBarInsetToRoot() {
        val root = binding.root
        val originalTop = root.paddingTop
        val originalBottom = root.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            // IME menang atas nav bar — pick yang lebih besar, supaya
            // keyboard yang buka mendorong konten (termasuk bottom bar
            // & FAB) ke atas cukup untuk tetep visible.
            val bottom = maxOf(sys.bottom, ime.bottom)
            v.updatePadding(
                top = originalTop + sys.top,
                bottom = originalBottom + bottom
            )
            insets
        }
    }

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
