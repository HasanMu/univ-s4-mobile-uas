package com.kelompok1.materialku.presentation.pos

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kelompok1.materialku.R
import com.kelompok1.materialku.databinding.FragmentPosBinding
import com.kelompok1.materialku.domain.model.StatusTransaksi
import com.kelompok1.materialku.presentation.base.BaseFragment
import com.kelompok1.materialku.util.Formatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PosFragment : BaseFragment<FragmentPosBinding>(
    FragmentPosBinding::inflate
) {
    private val viewModel: PosViewModel by viewModels()

    private val adapter by lazy {
        PosMaterialAdapter(
            onAdd = { viewModel.addToCart(it) },
            onIncrement = { viewModel.addToCart(it) },
            onDecrement = { viewModel.removeFromCart(it) }
        )
    }

    override fun setupViews() {
        binding.rvPosMaterial.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPosMaterial.adapter = adapter

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.chipSemua.setOnClickListener { viewModel.setFilter(PosFilter.Semua) }
        binding.chipBahan.setOnClickListener { viewModel.setFilter(PosFilter.ByKategoriName("Bahan")) }
        binding.chipAlat.setOnClickListener { viewModel.setFilter(PosFilter.ByKategoriName("Alat")) }
        binding.chipLainnya.setOnClickListener { viewModel.setFilter(PosFilter.ByKategoriName("Lainnya")) }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.setSearch(s?.toString().orEmpty())
            }
        })

        binding.btnCheckout.setOnClickListener {
            val cart = viewModel.state.value.cart
            if (cart.isEmpty()) {
                toast(getString(R.string.pos_empty_cart))
                return@setOnClickListener
            }
            showCartSheet()
        }

        binding.btnDraftList.setOnClickListener { showDraftSheet() }
    }

    private fun showDraftSheet() {
        DraftBottomSheet(
            drafts = viewModel.drafts(),
            onPick = { transaksiId -> viewModel.openDraft(transaksiId) }
        ).show(parentFragmentManager, "draft-list")
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

    private fun render(state: PosState) {
        adapter.submitList(state.items)
        binding.tvCartCount.text = getString(R.string.pos_items, state.cartCount)
        binding.tvTotal.text = Formatter.rupiah(state.total)
        renderChips(state.filter)

        binding.btnDraftList.text = if (state.draftCount == 0) getString(R.string.pos_draft_button)
        else getString(R.string.pos_draft_button_count, state.draftCount)
        binding.btnDraftList.isEnabled = state.draftCount > 0
    }

    private fun renderChips(active: PosFilter) {
        val activeName = (active as? PosFilter.ByKategoriName)?.name
        setChip(binding.chipSemua, active == PosFilter.Semua)
        setChip(binding.chipBahan, activeName == "Bahan")
        setChip(binding.chipAlat, activeName == "Alat")
        setChip(binding.chipLainnya, activeName == "Lainnya")
    }

    private fun setChip(view: TextView, isActive: Boolean) {
        val ctx = requireContext()
        val bg = if (isActive) R.color.primary else R.color.surface
        val fg = if (isActive) R.color.on_primary else R.color.on_surface_variant
        view.setBackgroundColor(ContextCompat.getColor(ctx, bg))
        view.setTextColor(ContextCompat.getColor(ctx, fg))
    }

    private fun handleEvent(event: PosEvent) {
        when (event) {
            is PosEvent.CheckoutSuccess -> showSuccessDialog(event)
            is PosEvent.DraftLoaded -> toast(getString(R.string.pos_draft_loaded, event.itemCount))
            is PosEvent.Error -> toast(event.message)
        }
    }

    private fun showCartSheet() {
        val state = viewModel.state.value
        CartBottomSheet(
            items = state.cart,
            total = state.total,
            onIncrement = { viewModel.addToCart(it) },
            onDecrement = { viewModel.removeFromCart(it) },
            onSubmit = { status -> viewModel.checkout(status) }
        ).show(parentFragmentManager, "cart")
    }

    private fun showSuccessDialog(event: PosEvent.CheckoutSuccess) {
        val statusLabel = when (event.status) {
            StatusTransaksi.SELESAI -> getString(R.string.status_selesai)
            StatusTransaksi.DRAFT -> getString(R.string.status_draft)
            StatusTransaksi.BATAL -> "BATAL"
        }
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.pos_success_title, statusLabel))
            .setMessage(
                getString(
                    R.string.pos_success_message,
                    event.result.noFaktur,
                    Formatter.rupiah(event.result.totalHarga)
                )
            )
            .setPositiveButton("OK", null)
            .show()
    }

    private fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }
}
