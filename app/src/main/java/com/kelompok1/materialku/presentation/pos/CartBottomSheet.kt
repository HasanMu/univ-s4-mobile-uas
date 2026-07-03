package com.kelompok1.materialku.presentation.pos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kelompok1.materialku.databinding.BottomSheetCartBinding
import com.kelompok1.materialku.domain.model.StatusTransaksi
import com.kelompok1.materialku.util.Formatter

class CartBottomSheet(
    private val items: List<CartItem>,
    private val total: Double,
    private val onIncrement: (Int) -> Unit,
    private val onDecrement: (Int) -> Unit,
    private val onSubmit: (StatusTransaksi) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetCartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = CartAdapter(
            onIncrement = onIncrement,
            onDecrement = onDecrement
        )
        binding.rvCart.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCart.adapter = adapter
        adapter.submitList(items)

        binding.tvTotalSheet.text = Formatter.rupiah(total)

        binding.btnBayar.setOnClickListener {
            onSubmit(StatusTransaksi.SELESAI)
            dismiss()
        }
        binding.btnDraft.setOnClickListener {
            onSubmit(StatusTransaksi.DRAFT)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
