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

    private fun handleEvent(event: KategoriEvent) {
        when (event) {
            KategoriEvent.Saved -> toast(getString(R.string.success_save))
            KategoriEvent.Deleted -> toast(getString(R.string.success_delete))
            is KategoriEvent.ValidationError -> toast(event.message)
        }
    }

    private fun showForm(existing: Kategori?) {
        val dialogBinding = DialogKategoriFormBinding.inflate(layoutInflater)
        dialogBinding.etNama.setText(existing?.nama.orEmpty())
        dialogBinding.etDeskripsi.setText(existing?.deskripsi.orEmpty())
        dialogBinding.switchAktif.isChecked = existing?.aktif ?: true

        val title = if (existing == null) R.string.kategori_add else R.string.kategori_edit

        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.action_save) { _, _ ->
                viewModel.save(
                    KategoriFormInput(
                        editingId = existing?.id,
                        nama = dialogBinding.etNama.text?.toString().orEmpty(),
                        deskripsi = dialogBinding.etDeskripsi.text?.toString().orEmpty(),
                        aktif = dialogBinding.switchAktif.isChecked
                    )
                )
            }
            .setNegativeButton(R.string.action_batal, null)
            .show()
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
