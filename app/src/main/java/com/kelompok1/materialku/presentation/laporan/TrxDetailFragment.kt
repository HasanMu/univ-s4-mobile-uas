package com.kelompok1.materialku.presentation.laporan

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.kelompok1.materialku.R
import com.kelompok1.materialku.databinding.FragmentTrxDetailBinding
import com.kelompok1.materialku.databinding.ItemTrxDetailBinding
import com.kelompok1.materialku.domain.repository.TrxDetail
import com.kelompok1.materialku.presentation.base.BaseFragment
import com.kelompok1.materialku.util.Formatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class TrxDetailFragment : BaseFragment<FragmentTrxDetailBinding>(
    FragmentTrxDetailBinding::inflate
) {
    private val viewModel: TrxDetailViewModel by viewModels()
    private val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    override fun setupViews() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        val transaksiId = arguments?.getInt("transaksiId", -1) ?: -1
        if (transaksiId > 0) viewModel.load(transaksiId)
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { render(it) }
            }
        }
    }

    private fun render(state: TrxDetailState) {
        val detail = state.detail ?: return
        renderDetail(detail)
    }

    private fun renderDetail(d: TrxDetail) {
        binding.tvNoFaktur.text = d.transaksi.noFaktur
        binding.tvStatus.text = d.transaksi.status.name
        binding.tvTanggal.text = d.transaksi.tanggal.format(dateFormat)
        binding.tvKasir.text = d.kasir
        binding.tvTotal.text = Formatter.rupiah(d.transaksi.totalHarga)

        binding.rvItems.removeAllViews()
        if (d.items.isEmpty()) {
            val tv = android.widget.TextView(requireContext()).apply {
                text = getString(R.string.trx_detail_items_kosong)
                setTextAppearance(R.style.TextAppearance_Materialku_Caption)
                setTextColor(
                    androidx.core.content.ContextCompat.getColor(
                        requireContext(), R.color.on_surface_variant
                    )
                )
            }
            binding.rvItems.addView(tv)
            return
        }
        for (item in d.items) {
            val ib = ItemTrxDetailBinding.inflate(layoutInflater, binding.rvItems, false)
            ib.tvNama.text = item.namaMaterial
            ib.tvHargaQty.text = getString(
                R.string.trx_detail_qty_harga,
                item.qty,
                Formatter.rupiah(item.hargaSatuan)
            )
            ib.tvSubtotal.text = Formatter.rupiah(item.subtotal)
            binding.rvItems.addView(ib.root)
        }
    }
}
