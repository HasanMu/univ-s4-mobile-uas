package com.kelompok1.materialku.presentation.stok

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kelompok1.materialku.R
import com.kelompok1.materialku.databinding.BottomSheetStokMutasiBinding
import com.kelompok1.materialku.domain.model.JenisStok
import com.kelompok1.materialku.domain.model.Material

class StokMutasiBottomSheet(
    private val materials: List<Material>,
    private val preselectMaterialId: Int? = null,
    private val onSubmit: (MutasiInput) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetStokMutasiBinding? = null
    private val binding get() = _binding!!

    private var selectedMaterialId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetStokMutasiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val labels = materials.map { "${it.nama} (stok: ${it.stokSaat})" }
        binding.actMaterial.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, labels)
        )
        binding.actMaterial.setOnItemClickListener { _, _, position, _ ->
            selectedMaterialId = materials.getOrNull(position)?.id
            updatePreview()
        }
        preselectMaterialId?.let { id ->
            val idx = materials.indexOfFirst { it.id == id }
            if (idx >= 0) {
                selectedMaterialId = id
                binding.actMaterial.setText(labels[idx], false)
            }
        }

        binding.rgJenis.setOnCheckedChangeListener { _, _ -> updatePreview() }
        binding.etQty.addTextChangedListener(
            afterTextChanged = { updatePreview() }
        )

        binding.btnSimpan.setOnClickListener {
            val jenis = if (binding.rbMasuk.isChecked) JenisStok.MASUK else JenisStok.KELUAR
            onSubmit(
                MutasiInput(
                    materialId = selectedMaterialId,
                    jenis = jenis,
                    qtyText = binding.etQty.text?.toString().orEmpty(),
                    keterangan = binding.etKeterangan.text?.toString().orEmpty()
                )
            )
            dismiss()
        }

        updatePreview()
    }

    private fun updatePreview() {
        val material = materials.firstOrNull { it.id == selectedMaterialId }
        val qty = binding.etQty.text?.toString()?.toIntOrNull() ?: 0
        if (material == null || qty <= 0) {
            binding.tvStokPreview.text = ""
            return
        }
        val jenis = if (binding.rbMasuk.isChecked) JenisStok.MASUK else JenisStok.KELUAR
        val nextStok = when (jenis) {
            JenisStok.MASUK -> material.stokSaat + qty
            JenisStok.KELUAR -> material.stokSaat - qty
        }
        binding.tvStokPreview.text = getString(R.string.stok_setelah, nextStok.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Kecil helper — TextInputEditText tidak expose extension addTextChangedListener
// dengan afterTextChanged sebagai lambda kalau tidak import androidx.core.widget
private inline fun android.widget.EditText.addTextChangedListener(
    crossinline afterTextChanged: (String) -> Unit
) {
    addTextChangedListener(object : android.text.TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: android.text.Editable?) {
            afterTextChanged(s?.toString() ?: "")
        }
    })
}
