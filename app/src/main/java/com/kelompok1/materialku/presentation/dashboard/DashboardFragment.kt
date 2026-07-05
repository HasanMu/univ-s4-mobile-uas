package com.kelompok1.materialku.presentation.dashboard

import android.content.res.ColorStateList
import android.view.View
import android.widget.LinearLayout
import android.widget.Space
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView
import com.kelompok1.materialku.R
import com.kelompok1.materialku.databinding.FragmentDashboardBinding
import com.kelompok1.materialku.domain.model.RoleEnum
import com.kelompok1.materialku.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding>(
    FragmentDashboardBinding::inflate
) {
    private val viewModel: DashboardViewModel by viewModels()

    override fun useLightStatusBarIcons(): Boolean = true

    // Hero (bg gelap) di-extend ke atas status bar via applyStatusBarInsetToHero
    // — supaya inset masuk ke hero, bukan ke root (yang bg-nya
    // berbeda + akan bikin gap terang di atas hero). BaseFragment
    // gak perlu apply ke root.
    override fun useStatusBarInset(): Boolean = false

    override fun setupViews() {
        applyStatusBarInsetToHero()
        binding.cardMaterial.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_material)
        }
        binding.cardKategori.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_kategori)
        }
        binding.cardPos.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_pos)
        }
        binding.cardLaporan.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_laporan)
        }
        binding.cardStok.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_stok)
        }
        binding.cardUser.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_user)
        }
        binding.cardSupplier.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_supplier)
        }
        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_settings)
        }
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }
    }

    /**
     * Tambah top inset (tinggi status bar) ke padding hero — bukan replace.
     * Hero jadi punya paddingTop = spacing_xl + status_bar_height, sementara
     * paddingHorizontal & paddingBottom tetap spacing_xl.
     */
    private fun applyStatusBarInsetToHero() {
        val hero = binding.heroSection
        val originalPaddingTop = hero.paddingTop
        ViewCompat.setOnApplyWindowInsetsListener(hero) { v, insets ->
            val topInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            v.updatePadding(top = originalPaddingTop + topInset)
            insets
        }
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { render(it) }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { handleEvent(it) }
            }
        }
    }

    private fun render(state: DashboardState) {
        binding.tvUserName.text = state.userName
        binding.tvStatMaterial.text = state.statMaterial.toString()
        binding.tvStatTransaksi.text = state.statTransaksi.toString()
        binding.tvStatKritis.text = state.statKritis.toString()

        val role = state.role
        if (role != null) {
            binding.tvRoleLabel.text = role.displayName().uppercase()
            binding.cvRoleBadge.setCardBackgroundColor(
                ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), role.badgeColorRes())
                )
            )
            applyRoleVisibility(role)
        }
    }

    private fun handleEvent(event: DashboardEvent) {
        when (event) {
            DashboardEvent.LoggedOut,
            DashboardEvent.SessionExpired -> {
                findNavController().navigate(R.id.action_dashboard_to_login)
            }
        }
    }

    /**
     * Role access matrix — sesuai CONTRIBUTING.md.
     * Screen yang tidak visible di-hide (GONE) untuk menghilangkan celah spacing.
     */
    /**
     * Kartu yang boleh dilihat per role. Urutan di list = urutan tampil di grid
     * (kiri-atas ke bawah). Re-pack 2-per-baris supaya nggak ada gap kosong.
     */
    private fun cardsForRole(role: RoleEnum): List<MaterialCardView> = when (role) {
        RoleEnum.ROLE_ADMIN -> listOf(
            binding.cardMaterial, binding.cardKategori,
            binding.cardPos, binding.cardLaporan,
            binding.cardStok, binding.cardUser,
            binding.cardSupplier
        )
        RoleEnum.ROLE_KASIR -> listOf(binding.cardPos)
        RoleEnum.ROLE_GUDANG -> listOf(
            binding.cardMaterial, binding.cardKategori,
            binding.cardStok, binding.cardSupplier
        )
        RoleEnum.ROLE_MANAGER -> listOf(
            binding.cardMaterial, binding.cardKategori,
            binding.cardStok, binding.cardLaporan,
            binding.cardSupplier
        )
    }

    private fun applyRoleVisibility(role: RoleEnum) {
        val visible = cardsForRole(role)
        val rows = listOf(binding.rowMenu1, binding.rowMenu2, binding.rowMenu3, binding.rowMenu4)

        // Detach semua card dari row asalnya biar bisa disusun ulang.
        rows.forEach { it.removeAllViews() }

        val spacingMd = resources.getDimensionPixelSize(R.dimen.spacing_md)
        val cardHeight = resources.getDimensionPixelSize(R.dimen.height_menu_card)

        // Isi row 2-per-baris.
        visible.chunked(2).forEachIndexed { rowIdx, pair ->
            val row = rows.getOrNull(rowIdx) ?: return@forEachIndexed
            pair.forEachIndexed { colIdx, card ->
                card.visibility = View.VISIBLE
                val lp = LinearLayout.LayoutParams(0, cardHeight, 1f)
                if (colIdx == 0 && pair.size == 2) lp.marginEnd = spacingMd
                row.addView(card, lp)
            }
            // Ganjil di kanan: tambah Space filler biar card tetap 50% lebar.
            if (pair.size == 1) {
                val filler = Space(requireContext())
                val fillerLp = LinearLayout.LayoutParams(0, cardHeight, 1f)
                // Card kiri butuh margin end supaya jarak ke filler konsisten.
                (row.getChildAt(0).layoutParams as LinearLayout.LayoutParams).marginEnd = spacingMd
                row.addView(filler, fillerLp)
            }
            row.visibility = View.VISIBLE
        }
        // Row sisa yang nggak kepakai: sembunyikan.
        for (i in visible.chunked(2).size until rows.size) {
            rows[i].visibility = View.GONE
        }
    }
}

private fun RoleEnum.badgeColorRes(): Int = when (this) {
    RoleEnum.ROLE_ADMIN -> R.color.role_admin
    RoleEnum.ROLE_KASIR -> R.color.role_kasir
    RoleEnum.ROLE_GUDANG -> R.color.role_gudang
    RoleEnum.ROLE_MANAGER -> R.color.role_manager
}
