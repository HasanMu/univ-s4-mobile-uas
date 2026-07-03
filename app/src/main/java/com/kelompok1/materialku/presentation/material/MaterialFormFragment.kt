package com.kelompok1.materialku.presentation.material

import android.view.View
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.kelompok1.materialku.R
import com.kelompok1.materialku.databinding.FragmentMaterialFormBinding
import com.kelompok1.materialku.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MaterialFormFragment : BaseFragment<FragmentMaterialFormBinding>(
    FragmentMaterialFormBinding::inflate
) {
    private val viewModel: MaterialViewModel by viewModels()
    private var lastSnapshot: MaterialFormState? = null

    private val etKode by lazy { binding.tilKode.editText!! }
    private val etNama by lazy { binding.tilNama.editText!! }
    private val etHarga by lazy { binding.tilHarga.editText!! }
    private val etStokAwal by lazy { binding.tilStokAwal.editText!! }
    private val etStokMin by lazy { binding.tilStokMin.editText!! }
    private val ddKategori by lazy { binding.tilKategori.editText as MaterialAutoCompleteTextView }
    private val ddSatuan by lazy { binding.tilSatuan.editText as MaterialAutoCompleteTextView }
    private val ddSupplier by lazy { binding.tilSupplier.editText as MaterialAutoCompleteTextView }

    override fun setupViews() {
        val materialId = arguments?.getInt("materialId", -1)?.takeIf { it > 0 }
        viewModel.loadFormData(materialId)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnSave.setOnClickListener {
            hideKeyboard()
            viewModel.save()
        }

        etKode.doAfterTextChanged { t ->
            viewModel.updateForm { copy(kode = t?.toString().orEmpty(), errorMessage = null) }
        }
        etNama.doAfterTextChanged { t ->
            viewModel.updateForm { copy(nama = t?.toString().orEmpty(), errorMessage = null) }
        }
        etHarga.doAfterTextChanged { t ->
            viewModel.updateForm { copy(hargaText = t?.toString().orEmpty(), errorMessage = null) }
        }
        etStokAwal.doAfterTextChanged { t ->
            viewModel.updateForm { copy(stokAwalText = t?.toString().orEmpty(), errorMessage = null) }
        }
        etStokMin.doAfterTextChanged { t ->
            viewModel.updateForm { copy(stokMinText = t?.toString().orEmpty(), errorMessage = null) }
        }
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.formState.collect { render(it) }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { evt ->
                    if (evt is MaterialEvent.Saved) findNavController().popBackStack()
                }
            }
        }
    }

    private fun render(state: MaterialFormState) {
        if (!state.loaded) return

        // Hanya bind field text kalau isi berubah, biar cursor nggak lompat.
        setTextIfDifferent(etKode, state.kode)
        setTextIfDifferent(etNama, state.nama)
        setTextIfDifferent(etHarga, state.hargaText)
        setTextIfDifferent(etStokAwal, state.stokAwalText)
        setTextIfDifferent(etStokMin, state.stokMinText)

        // Setup dropdown sekali saat pertama kali loaded.
        if (lastSnapshot?.loaded != true) {
            setupDropdown(
                ddKategori,
                state.kategoris.map { it.nama },
                state.kategoris.indexOfFirst { it.id == state.kategoriId }
            ) { idx ->
                viewModel.updateForm {
                    copy(kategoriId = state.kategoris.getOrNull(idx)?.id, errorMessage = null)
                }
            }
            setupDropdown(
                ddSatuan,
                state.satuans.map { "${it.nama} (${it.simbol})" },
                state.satuans.indexOfFirst { it.id == state.satuanId }
            ) { idx ->
                viewModel.updateForm {
                    copy(satuanId = state.satuans.getOrNull(idx)?.id, errorMessage = null)
                }
            }
            val supplierOptions = listOf("— Tanpa supplier —") + state.suppliers.map { it.nama }
            val supplierIndex = if (state.supplierId == null) 0
            else 1 + state.suppliers.indexOfFirst { it.id == state.supplierId }.coerceAtLeast(0)
            setupDropdown(ddSupplier, supplierOptions, supplierIndex) { idx ->
                val chosen = if (idx == 0) null else state.suppliers.getOrNull(idx - 1)?.id
                viewModel.updateForm { copy(supplierId = chosen, errorMessage = null) }
            }
        }

        binding.tvError.visibility = if (state.errorMessage.isNullOrBlank()) View.GONE else View.VISIBLE
        binding.tvError.text = state.errorMessage.orEmpty()

        lastSnapshot = state
    }

    private fun setTextIfDifferent(et: android.widget.EditText, value: String) {
        if (et.text?.toString() != value) et.setText(value)
    }

    private fun setupDropdown(
        view: MaterialAutoCompleteTextView,
        options: List<String>,
        selectedIndex: Int,
        onSelected: (Int) -> Unit
    ) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            options
        )
        view.setAdapter(adapter)
        if (selectedIndex in options.indices) {
            view.setText(options[selectedIndex], false)
        }
        view.setOnItemClickListener { _, _, position, _ -> onSelected(position) }
    }
}
