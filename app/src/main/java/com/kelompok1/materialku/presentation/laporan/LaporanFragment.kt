package com.kelompok1.materialku.presentation.laporan

import android.content.Intent
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.kelompok1.materialku.R
import com.kelompok1.materialku.databinding.FragmentLaporanBinding
import com.kelompok1.materialku.databinding.ItemLaporanKritisBinding
import com.kelompok1.materialku.databinding.ItemLaporanTrxBinding
import com.kelompok1.materialku.domain.repository.LaporanData
import com.kelompok1.materialku.domain.repository.LaporanPeriode
import com.kelompok1.materialku.presentation.base.BaseFragment
import com.kelompok1.materialku.util.Formatter
import com.kelompok1.materialku.util.PdfExporter
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class LaporanFragment : BaseFragment<FragmentLaporanBinding>(
    FragmentLaporanBinding::inflate
) {
    private val viewModel: LaporanViewModel by viewModels()

    // PdfExporter tidak punya dependency — instansiasi langsung, gak
    // perlu jalur Hilt di sini (menghindari KSP timing issue dengan
    // ViewBinding generic parameter).
    private val pdfExporter = PdfExporter()

    private val trxDateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    override fun setupViews() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.periodePicker.setOnClickListener { showPeriodeMenu(it) }
        binding.btnExport.setOnClickListener { viewModel.requestExport() }
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

    private fun render(state: LaporanState) {
        binding.tvPeriodeLabel.text = state.periode.label
        val data = state.data ?: return
        binding.tvTotalTrx.text = data.totalTransaksi.toString()
        binding.tvPendapatan.text = Formatter.rupiah(data.pendapatan)
        binding.tvTerjual.text = getString(R.string.laporan_terjual_value, data.unitTerjual)

        binding.rvTrx.removeAllViews()
        if (data.transaksiTerbaru.isEmpty()) {
            val empty = android.widget.TextView(requireContext()).apply {
                text = getString(R.string.laporan_trx_kosong)
                setTextAppearance(R.style.TextAppearance_Materialku_Caption)
                setTextColor(
                    androidx.core.content.ContextCompat.getColor(
                        requireContext(), R.color.on_surface_variant
                    )
                )
            }
            binding.rvTrx.addView(empty)
        } else {
            for (t in data.transaksiTerbaru) {
                val itemBinding = ItemLaporanTrxBinding.inflate(layoutInflater, binding.rvTrx, false)
                itemBinding.tvNoFaktur.text = t.noFaktur
                itemBinding.tvTanggal.text = t.tanggal.format(trxDateFormat)
                itemBinding.tvHarga.text = Formatter.rupiah(t.totalHarga)
                binding.rvTrx.addView(itemBinding.root)
            }
        }

        binding.rvKritis.removeAllViews()
        if (data.stokKritis.isEmpty()) {
            binding.rvKritis.visibility = View.GONE
            binding.tvKritisEmpty.visibility = View.VISIBLE
        } else {
            binding.rvKritis.visibility = View.VISIBLE
            binding.tvKritisEmpty.visibility = View.GONE
            for (m in data.stokKritis) {
                val ib = ItemLaporanKritisBinding.inflate(layoutInflater, binding.rvKritis, false)
                ib.tvNama.text = m.nama
                ib.tvStok.text = getString(R.string.laporan_kritis_stok, m.stokSaat, m.stokMin)
                binding.rvKritis.addView(ib.root)
            }
        }
    }

    private fun handleEvent(event: LaporanEvent) {
        when (event) {
            is LaporanEvent.ExportRequested -> exportPdf(event.data)
            is LaporanEvent.Error -> toast(event.message)
        }
    }

    private fun showPeriodeMenu(anchor: View) {
        val menu = PopupMenu(requireContext(), anchor)
        menu.menu.add(0, 0, 0, R.string.laporan_periode_hari)
        menu.menu.add(0, 1, 1, R.string.laporan_periode_minggu)
        menu.menu.add(0, 2, 2, R.string.laporan_periode_bulan)
        menu.menu.add(0, 3, 3, R.string.laporan_periode_custom)
        menu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                0 -> viewModel.load(LaporanPeriode.HariIni)
                1 -> viewModel.load(LaporanPeriode.MingguIni)
                2 -> viewModel.load(LaporanPeriode.BulanIni)
                3 -> showCustomRangePicker()
            }
            true
        }
        menu.show()
    }

    private fun showCustomRangePicker() {
        val picker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(R.string.laporan_periode_custom_title)
            .build()
        picker.addOnPositiveButtonClickListener { selection ->
            val fromMs = selection.first ?: return@addOnPositiveButtonClickListener
            val toMs = selection.second ?: return@addOnPositiveButtonClickListener
            // MaterialDatePicker returns UTC epoch millis at 00:00 UTC — convert
            // to LocalDate in UTC agar tanggal yang ditap = tanggal yang dipakai.
            val from = Instant.ofEpochMilli(fromMs).atZone(ZoneOffset.UTC).toLocalDate()
            val to = Instant.ofEpochMilli(toMs).atZone(ZoneOffset.UTC).toLocalDate()
            viewModel.load(LaporanPeriode.Custom(from, to))
        }
        picker.show(parentFragmentManager, "laporan_custom_range")
    }

    private fun exportPdf(data: LaporanData) {
        runCatching {
            val uri = pdfExporter.export(requireContext(), data)
            val send = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Laporan MaterialKu — ${data.periode.label}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(send, getString(R.string.laporan_export)))
        }.onFailure {
            toast(it.message ?: getString(R.string.error_generic))
        }
    }

    private fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }
}
