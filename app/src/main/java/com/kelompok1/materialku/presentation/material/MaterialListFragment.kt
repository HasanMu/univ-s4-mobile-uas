package com.kelompok1.materialku.presentation.material

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kelompok1.materialku.R
import com.kelompok1.materialku.databinding.FragmentMaterialListBinding
import com.kelompok1.materialku.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MaterialListFragment : BaseFragment<FragmentMaterialListBinding>(
    FragmentMaterialListBinding::inflate
) {
    private val viewModel: MaterialViewModel by viewModels()

    private val adapter by lazy {
        MaterialAdapter(
            onClick = { row -> openForm(row.material.id) },
            onLongClick = { row -> confirmDelete(row) }
        )
    }

    override fun setupViews() {
        binding.rvMaterial.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMaterial.adapter = adapter

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.fabAdd.setOnClickListener {
            openForm(materialId = null)
        }
        binding.btnSearch.setOnClickListener {
            // TODO: implement search input; toggle chips → text field
        }

        binding.chipSemua.setOnClickListener { viewModel.setFilter(MaterialFilter.Semua) }
        binding.chipBahan.setOnClickListener { viewModel.setFilter(MaterialFilter.ByKategoriName("Bahan")) }
        binding.chipAlat.setOnClickListener { viewModel.setFilter(MaterialFilter.ByKategoriName("Alat")) }
        binding.chipLainnya.setOnClickListener { viewModel.setFilter(MaterialFilter.ByKategoriName("Lainnya")) }
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { render(it) }
            }
        }
    }

    private fun render(state: MaterialListState) {
        adapter.submitList(state.items)
        renderChips(state.filter)
    }

    private fun renderChips(active: MaterialFilter) {
        val activeName = (active as? MaterialFilter.ByKategoriName)?.name
        setChip(binding.chipSemua, active == MaterialFilter.Semua)
        setChip(binding.chipBahan, activeName == "Bahan")
        setChip(binding.chipAlat, activeName == "Alat")
        setChip(binding.chipLainnya, activeName == "Lainnya")
    }

    private fun setChip(view: android.widget.TextView, isActive: Boolean) {
        val ctx = requireContext()
        val bg = if (isActive) R.color.primary else R.color.surface
        val fg = if (isActive) R.color.on_primary else R.color.on_surface_variant
        view.setBackgroundColor(androidx.core.content.ContextCompat.getColor(ctx, bg))
        view.setTextColor(androidx.core.content.ContextCompat.getColor(ctx, fg))
    }

    private fun openForm(materialId: Int?) {
        val args = Bundle().apply { putInt("materialId", materialId ?: -1) }
        findNavController().navigate(R.id.action_material_to_form, args)
    }

    private fun confirmDelete(row: MaterialRow) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus material?")
            .setMessage("${row.material.nama} akan dihapus permanen.")
            .setPositiveButton(getString(R.string.action_hapus)) { _, _ ->
                viewModel.delete(row.material.id)
            }
            .setNegativeButton(getString(R.string.action_batal), null)
            .show()
    }
}
