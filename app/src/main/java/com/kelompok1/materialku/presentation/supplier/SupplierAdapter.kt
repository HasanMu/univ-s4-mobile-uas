package com.kelompok1.materialku.presentation.supplier

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kelompok1.materialku.R
import com.kelompok1.materialku.databinding.ItemSupplierBinding
import com.kelompok1.materialku.domain.model.Supplier

class SupplierAdapter(
    private val onClick: (Supplier) -> Unit,
    private val onLongClick: (Supplier) -> Unit
) : ListAdapter<Supplier, SupplierAdapter.VH>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemSupplierBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemSupplierBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(supplier: Supplier) {
            val ctx = binding.root.context
            binding.tvNama.text = supplier.nama

            if (supplier.kontak.isBlank()) {
                binding.tvKontak.visibility = View.GONE
            } else {
                binding.tvKontak.visibility = View.VISIBLE
                binding.tvKontak.text = supplier.kontak
            }
            if (supplier.alamat.isBlank() || supplier.alamat == "-") {
                binding.tvAlamat.visibility = View.GONE
            } else {
                binding.tvAlamat.visibility = View.VISIBLE
                binding.tvAlamat.text = supplier.alamat
            }

            val aktif = supplier.aktif
            binding.tvStatus.text = ctx.getString(
                if (aktif) R.string.supplier_status_aktif else R.string.supplier_status_nonaktif
            )
            val bg = if (aktif) R.color.badge_aman_bg else R.color.badge_kritis_bg
            val fg = if (aktif) R.color.role_kasir else R.color.error
            binding.cvStatus.setCardBackgroundColor(
                ColorStateList.valueOf(ContextCompat.getColor(ctx, bg))
            )
            binding.tvStatus.setTextColor(ContextCompat.getColor(ctx, fg))

            binding.root.setOnClickListener { onClick(supplier) }
            binding.root.setOnLongClickListener {
                onLongClick(supplier)
                true
            }
        }
    }

    private object Diff : DiffUtil.ItemCallback<Supplier>() {
        override fun areItemsTheSame(a: Supplier, b: Supplier): Boolean = a.id == b.id
        override fun areContentsTheSame(a: Supplier, b: Supplier): Boolean = a == b
    }
}
