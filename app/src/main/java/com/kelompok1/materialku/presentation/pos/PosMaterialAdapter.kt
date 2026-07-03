package com.kelompok1.materialku.presentation.pos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kelompok1.materialku.databinding.ItemPosMaterialBinding
import com.kelompok1.materialku.util.Formatter

class PosMaterialAdapter(
    private val onAdd: (Int) -> Unit,
    private val onIncrement: (Int) -> Unit,
    private val onDecrement: (Int) -> Unit
) : ListAdapter<PosMaterialRow, PosMaterialAdapter.VH>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemPosMaterialBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemPosMaterialBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(row: PosMaterialRow) {
            val m = row.material
            binding.tvNama.text = m.nama
            binding.tvKategori.text = "${row.kategoriNama} • Stok: ${m.stokSaat}"
            binding.tvHarga.text = Formatter.rupiah(m.hargaJual)

            if (row.qtyInCart > 0) {
                binding.btnAdd.visibility = View.GONE
                binding.qtyControls.visibility = View.VISIBLE
                binding.tvQty.text = row.qtyInCart.toString()
            } else {
                binding.btnAdd.visibility = View.VISIBLE
                binding.qtyControls.visibility = View.GONE
            }

            binding.btnAdd.setOnClickListener { onAdd(m.id) }
            binding.btnPlus.setOnClickListener { onIncrement(m.id) }
            binding.btnMinus.setOnClickListener { onDecrement(m.id) }
            binding.root.setOnClickListener { onAdd(m.id) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<PosMaterialRow>() {
        override fun areItemsTheSame(a: PosMaterialRow, b: PosMaterialRow): Boolean =
            a.material.id == b.material.id
        override fun areContentsTheSame(a: PosMaterialRow, b: PosMaterialRow): Boolean = a == b
    }
}
