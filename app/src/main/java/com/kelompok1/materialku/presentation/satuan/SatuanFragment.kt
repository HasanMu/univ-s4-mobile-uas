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

    private fun handleEvent(event: SatuanEvent) {
        when (event) {
            SatuanEvent.Saved -> toast(getString(R.string.success_save))
            SatuanEvent.Deleted -> toast(getString(R.string.success_delete))
            is SatuanEvent.ValidationError -> toast(event.message)
        }
    }

    private fun showForm(existing: Satuan?) {
        val db = DialogSatuanFormBinding.inflate(layoutInflater)
        db.etNama.setText(existing?.nama.orEmpty())
        db.etSimbol.setText(existing?.simbol.orEmpty())

        val title = if (existing == null) R.string.satuan_add_new else R.string.satuan_edit

        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(db.root)
            .setPositiveButton(R.string.action_save) { _, _ ->
                viewModel.save(
                    SatuanFormInput(
                        editingId = existing?.id,
                        nama = db.etNama.text?.toString().orEmpty(),
                        simbol = db.etSimbol.text?.toString().orEmpty()
                    )
                )
            }
            .setNegativeButton(R.string.action_batal, null)
            .show()
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
