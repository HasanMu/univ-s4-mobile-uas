package com.kelompok1.materialku.presentation.satuan

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
import com.kelompok1.materialku.databinding.DialogSatuanFormBinding
import com.kelompok1.materialku.databinding.FragmentSatuanBinding
import com.kelompok1.materialku.domain.model.Satuan
import com.kelompok1.materialku.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SatuanFragment : BaseFragment<FragmentSatuanBinding>(
    FragmentSatuanBinding::inflate
) {
    private val viewModel: SatuanViewModel by viewModels()

    private val adapter by lazy {
        SatuanAdapter(
            onClick = { showForm(it) },
            onLongClick = { confirmDelete(it) }
        )
    }

    override fun setupViews() {
        binding.rvSatuan.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSatuan.adapter = adapter

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

    private fun render(state: SatuanListState) {
        adapter.submitList(state.items)
        val empty = state.items.isEmpty()
        binding.emptyState.visibility = if (empty) View.VISIBLE else View.GONE
        binding.rvSatuan.visibility = if (empty) View.GONE else View.VISIBLE
    }

    private var openDialog: androidx.appcompat.app.AlertDialog? = null
    private var openDialogBinding: DialogSatuanFormBinding? = null

    private fun handleEvent(event: SatuanEvent) {
        when (event) {
            SatuanEvent.Saved -> {
                openDialog?.dismiss()
                toast(getString(R.string.success_save))
            }
            SatuanEvent.Deleted -> toast(getString(R.string.success_delete))
            is SatuanEvent.ValidationError -> when (event.field) {
                SatuanField.NAMA -> openDialogBinding?.tilNama?.error = event.message
                SatuanField.SIMBOL -> openDialogBinding?.tilSimbol?.error = event.message
            }
        }
    }

    private fun showForm(existing: Satuan?) {
        val db = DialogSatuanFormBinding.inflate(layoutInflater)
        db.etNama.setText(existing?.nama.orEmpty())
        db.etSimbol.setText(existing?.simbol.orEmpty())

        db.etNama.addTextChangedListener(clearOn { db.tilNama.error = null })
        db.etSimbol.addTextChangedListener(clearOn { db.tilSimbol.error = null })

        val title = if (existing == null) R.string.satuan_add_new else R.string.satuan_edit

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(db.root)
            .setPositiveButton(R.string.action_save, null)
            .setNegativeButton(R.string.action_batal, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                db.tilNama.error = null
                db.tilSimbol.error = null
                viewModel.save(
                    SatuanFormInput(
                        editingId = existing?.id,
                        nama = db.etNama.text?.toString().orEmpty(),
                        simbol = db.etSimbol.text?.toString().orEmpty()
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

    private fun confirmDelete(satuan: Satuan) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.satuan_delete_title)
            .setMessage(getString(R.string.satuan_delete_message, satuan.nama))
            .setPositiveButton(R.string.action_hapus) { _, _ ->
                viewModel.delete(satuan.id)
            }
            .setNegativeButton(R.string.action_batal, null)
            .show()
    }

    private fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }
}
