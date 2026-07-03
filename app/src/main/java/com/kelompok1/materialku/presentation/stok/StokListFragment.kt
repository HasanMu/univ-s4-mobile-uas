package com.kelompok1.materialku.presentation.stok

import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kelompok1.materialku.R
import com.kelompok1.materialku.databinding.FragmentStokListBinding
import com.kelompok1.materialku.domain.model.Material
import com.kelompok1.materialku.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StokListFragment : BaseFragment<FragmentStokListBinding>(
    FragmentStokListBinding::inflate
) {
    private val viewModel: StokViewModel by viewModels()

    private val adapter by lazy {
        StokAdapter(onClick = { showMutasiSheet(it) })
    }

    override fun setupViews() {
        binding.rvStok.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStok.adapter = adapter

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.fabMutasi.setOnClickListener {
            showMutasiSheet(null)
        }
        binding.chipSemua.setOnClickListener { viewModel.setFilter(StokFilter.Semua) }
        binding.chipKritis.setOnClickListener { viewModel.setFilter(StokFilter.Kritis) }
        binding.chipAman.setOnClickListener { viewModel.setFilter(StokFilter.Aman) }
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

    private fun render(state: StokListState) {
        adapter.submitList(state.items)
        setChip(binding.chipSemua, state.filter == StokFilter.Semua)
        setChip(binding.chipKritis, state.filter == StokFilter.Kritis)
        setChip(binding.chipAman, state.filter == StokFilter.Aman)
    }

    private fun setChip(view: TextView, isActive: Boolean) {
        val ctx = requireContext()
        val bg = if (isActive) R.color.primary else R.color.surface
        val fg = if (isActive) R.color.on_primary else R.color.on_surface_variant
        view.setBackgroundColor(ContextCompat.getColor(ctx, bg))
        view.setTextColor(ContextCompat.getColor(ctx, fg))
    }

    private fun handleEvent(event: StokEvent) {
        when (event) {
            is StokEvent.MutasiSaved ->
                toast(getString(R.string.stok_mutasi_saved, event.stokBaru.toString()))
            is StokEvent.Error -> toast(event.message)
        }
    }

    private fun showMutasiSheet(preselect: Material?) {
        val allMaterials = viewModel.state.value.allMaterials
        if (allMaterials.isEmpty()) {
            toast(getString(R.string.stok_mutasi_no_material))
            return
        }
        StokMutasiBottomSheet(
            materials = allMaterials,
            preselectMaterialId = preselect?.id,
            onSubmit = { viewModel.catatMutasi(it) }
        ).show(parentFragmentManager, "stok-mutasi")
    }

    private fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }
}
