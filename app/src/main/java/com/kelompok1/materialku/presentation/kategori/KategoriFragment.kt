package com.kelompok1.materialku.presentation.kategori

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
import com.kelompok1.materialku.databinding.DialogKategoriFormBinding
import com.kelompok1.materialku.databinding.FragmentKategoriBinding
import com.kelompok1.materialku.domain.model.Kategori
import com.kelompok1.materialku.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class KategoriFragment : BaseFragment<FragmentKategoriBinding>(
    FragmentKategoriBinding::inflate
) {
    private val viewModel: KategoriViewModel by viewModels()

    private val adapter by lazy {
        KategoriAdapter(
            onClick = { showForm(it) },
            onLongClick = { confirmDelete(it) }
        )
    }

    override fun setupViews() {
        binding.rvKategori.layoutManager = LinearLayoutManager(requireContext())
        binding.rvKategori.adapter = adapter

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.fabAdd.setOnClickListener {
            showForm(null)
        }
        binding.btnKelolaSatuan.setOnClickListener {
            findNavController().navigate(R.id.action_kategori_to_satuan)
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

    private fun render(state: KategoriListState) {
        adapter.submitList(state.items)
        val empty = state.items.isEmpty()
        binding.emptyState.visibility = if (empty) View.VISIBLE else View.GONE
        binding.rvKategori.visibility = if (empty) View.GONE else View.VISIBLE
    }

    private var openDialog: androidx.appcompat.app.AlertDialog? = null
    private var openDialogBinding: DialogKategoriFormBinding? = null

    private fun handleEvent(event: KategoriEvent) {
        when (event) {
            KategoriEvent.Saved -> {
                openDialog?.dismiss()
                toast(getString(R.string.success_save))
            }
            KategoriEvent.Deleted -> toast(getString(R.string.success_delete))
            is KategoriEvent.ValidationError -> when (event.field) {
                KategoriField.NAMA -> openDialogBinding?.tilNama?.error = event.message
            }
        }
    }

    private fun showForm(existing: Kategori?) {
        val db = DialogKategoriFormBinding.inflate(layoutInflater)
        db.etNama.setText(existing?.nama.orEmpty())
        db.etDeskripsi.setText(existing?.deskripsi.orEmpty())
        db.switchAktif.isChecked = existing?.aktif ?: true

        // Clear error otomatis begitu user edit.
        db.etNama.addTextChangedListener(clearOn { db.tilNama.error = null })

        val title = if (existing == null) R.string.kategori_add else R.string.kategori_edit

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(db.root)
            .setPositiveButton(R.string.action_save, null) // override di bawah supaya nggak auto-dismiss
            .setNegativeButton(R.string.action_batal, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                db.tilNama.error = null
                viewModel.save(
                    KategoriFormInput(
                        editingId = existing?.id,
                        nama = db.etNama.text?.toString().orEmpty(),
                        deskripsi = db.etDeskripsi.text?.toString().orEmpty(),
                        aktif = db.switchAktif.isChecked
                    )
                )
                // Dialog di-dismiss di handleEvent(Saved).
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

    private fun confirmDelete(kategori: Kategori) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.kategori_delete_title)
            .setMessage(getString(R.string.kategori_delete_message, kategori.nama))
            .setPositiveButton(R.string.action_hapus) { _, _ ->
                viewModel.delete(kategori.id)
            }
            .setNegativeButton(R.string.action_batal, null)
            .show()
    }

    private fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }
}
