package com.kelompok1.materialku.presentation.pos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kelompok1.materialku.databinding.ItemCartBinding
import com.kelompok1.materialku.util.Formatter

class CartAdapter(
    private val onIncrement: (Int) -> Unit,
    private val onDecrement: (Int) -> Unit
) : ListAdapter<CartItem, CartAdapter.VH>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {
            binding.tvNama.text = item.material.nama
            binding.tvSubtotal.text = "${item.qty} × ${Formatter.rupiah(item.material.hargaJual)} = ${Formatter.rupiah(item.subtotal)}"
            binding.tvQty.text = item.qty.toString()

            binding.btnPlus.setOnClickListener { onIncrement(item.material.id) }
            binding.btnMinus.setOnClickListener { onDecrement(item.material.id) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(a: CartItem, b: CartItem): Boolean =
            a.material.id == b.material.id
        override fun areContentsTheSame(a: CartItem, b: CartItem): Boolean = a == b
    }
}
