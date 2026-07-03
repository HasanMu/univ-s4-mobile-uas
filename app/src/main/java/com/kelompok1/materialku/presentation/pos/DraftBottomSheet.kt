package com.kelompok1.materialku.presentation.pos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kelompok1.materialku.databinding.BottomSheetDraftBinding
import com.kelompok1.materialku.databinding.ItemDraftBinding
import com.kelompok1.materialku.domain.model.Transaksi
import com.kelompok1.materialku.util.Formatter
import java.time.format.DateTimeFormatter

class DraftBottomSheet(
    private val drafts: List<Transaksi>,
    private val onPick: (Int) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetDraftBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetDraftBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = DraftAdapter(
            onClick = { id ->
                onPick(id)
                dismiss()
            }
        )
        binding.rvDraft.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDraft.adapter = adapter
        adapter.submitList(drafts)

        if (drafts.isEmpty()) {
            binding.rvDraft.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
        } else {
            binding.rvDraft.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

private class DraftAdapter(
    private val onClick: (Int) -> Unit
) : ListAdapter<Transaksi, DraftAdapter.VH>(Diff) {

    private val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemDraftBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemDraftBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(t: Transaksi) {
            binding.tvNoFaktur.text = t.noFaktur
            binding.tvTanggal.text = t.tanggal.format(dateFormat)
            binding.tvTotal.text = Formatter.rupiah(t.totalHarga)
            binding.root.setOnClickListener { onClick(t.id) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<Transaksi>() {
        override fun areItemsTheSame(a: Transaksi, b: Transaksi): Boolean = a.id == b.id
        override fun areContentsTheSame(a: Transaksi, b: Transaksi): Boolean = a == b
    }
}
