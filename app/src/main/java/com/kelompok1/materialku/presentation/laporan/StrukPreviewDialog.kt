package com.kelompok1.materialku.presentation.laporan

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.kelompok1.materialku.R
import com.kelompok1.materialku.databinding.DialogStrukPreviewBinding
import com.kelompok1.materialku.domain.repository.IReportRepository
import com.kelompok1.materialku.util.StrukFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StrukPreviewDialog : DialogFragment() {

    @Inject lateinit var reportRepo: IReportRepository

    private var _binding: DialogStrukPreviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogStrukPreviewBinding.inflate(layoutInflater)
        val transaksiId = arguments?.getInt(ARG_TRX_ID, -1) ?: -1

        binding.tvStruk.text = getString(R.string.struk_preview_loading)
        binding.btnTutup.setOnClickListener { dismiss() }
        binding.btnShare.isEnabled = false
        binding.btnShare.setOnClickListener {
            val text = binding.tvStruk.text.toString()
            val send = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
                putExtra(Intent.EXTRA_SUBJECT, "Struk MaterialKu")
            }
            startActivity(Intent.createChooser(send, getString(R.string.struk_preview_share)))
        }

        if (transaksiId > 0) loadDetail(transaksiId)

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    private fun loadDetail(transaksiId: Int) {
        lifecycleScope.launch {
            runCatching { reportRepo.getTrxDetail(transaksiId) }
                .onSuccess { detail ->
                    if (detail == null) {
                        binding.tvStruk.text = getString(R.string.struk_preview_notfound)
                        return@onSuccess
                    }
                    binding.tvStruk.text = StrukFormatter.format(detail)
                    binding.btnShare.isEnabled = true
                }
                .onFailure {
                    binding.tvStruk.text = it.message ?: getString(R.string.error_generic)
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TRX_ID = "transaksiId"
        private const val TAG = "struk_preview"

        fun show(fm: FragmentManager, transaksiId: Int) {
            StrukPreviewDialog().apply {
                arguments = Bundle().apply { putInt(ARG_TRX_ID, transaksiId) }
            }.show(fm, TAG)
        }
    }
}
