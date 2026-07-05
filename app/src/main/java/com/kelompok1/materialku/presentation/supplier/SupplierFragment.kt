package com.kelompok1.materialku.presentation.supplier

import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kelompok1.materialku.R
import com.kelompok1.materialku.databinding.DialogSupplierFormBinding
import com.kelompok1.materialku.databinding.FragmentSupplierBinding
import com.kelompok1.materialku.domain.model.Supplier
import com.kelompok1.materialku.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SupplierFragment : BaseFragment<FragmentSupplierBinding>(
    FragmentSupplierBinding::inflate
) {
    private val viewModel: SupplierViewModel by viewModels()

    private val adapter by lazy {
        SupplierAdapter(
            onClick = { showForm(it) },
            onLongClick = { confirmDelete(it) }
        )
    }

    override fun setupViews() {
        binding.rvSupplier.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSupplier.adapter = adapter

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.fabAdd.setOnClickListener {
            showForm(null)
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

    private fun render(state: SupplierListState) {
        adapter.submitList(state.items)
        val empty = state.items.isEmpty()
        binding.emptyState.visibility = if (empty) View.VISIBLE else View.GONE
        binding.rvSupplier.visibility = if (empty) View.GONE else View.VISIBLE
    }

    private var openDialog: AlertDialog? = null
    private var openDialogBinding: DialogSupplierFormBinding? = null

    private fun handleEvent(event: SupplierEvent) {
        when (event) {
            SupplierEvent.Saved -> {
                openDialog?.dismiss()
                toast(getString(R.string.success_save))
            }
            SupplierEvent.Deleted -> toast(getString(R.string.success_delete))
            is SupplierEvent.ValidationError -> when (event.field) {
                SupplierField.NAMA -> openDialogBinding?.tilNama?.error = event.message
            }
        }
    }

    private fun showForm(existing: Supplier?) {
        val db = DialogSupplierFormBinding.inflate(layoutInflater)
        db.etNama.setText(existing?.nama.orEmpty())
        db.etKontak.setText(existing?.kontak.orEmpty())
        db.etAlamat.setText(existing?.alamat.orEmpty())
        db.switchAktif.isChecked = existing?.aktif ?: true

        db.etNama.addTextChangedListener(clearOn { db.tilNama.error = null })

        val title = if (existing == null) R.string.supplier_add else R.string.supplier_edit

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(db.root)
            .setPositiveButton(R.string.action_save, null)
            .setNegativeButton(R.string.action_batal, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                db.tilNama.error = null
                viewModel.save(
                    SupplierFormInput(
                        editingId = existing?.id,
                        nama = db.etNama.text?.toString().orEmpty(),
                        kontak = db.etKontak.text?.toString().orEmpty(),
                        alamat = db.etAlamat.text?.toString().orEmpty(),
                        aktif = db.switchAktif.isChecked
                    )
                )
            }
        }

        openDialog = dialog
        openDialogBinding = db
        dialog.setOnDismissListener {
            openDialog = null
            openDialogBinding = null
        }
        dialog.show()
    }

    private fun clearOn(action: () -> Unit) = object : android.text.TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: android.text.Editable?) { action() }
    }

    private fun confirmDelete(supplier: Supplier) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.supplier_delete_title)
            .setMessage(getString(R.string.supplier_delete_message, supplier.nama))
            .setPositiveButton(R.string.action_hapus) { _, _ ->
                viewModel.delete(supplier.id)
            }
            .setNegativeButton(R.string.action_batal, null)
            .show()
    }

    private fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }
}
