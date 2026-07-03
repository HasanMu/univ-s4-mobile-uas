package com.kelompok1.materialku.presentation.stok

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kelompok1.materialku.R
import com.kelompok1.materialku.databinding.ItemStokBinding
import com.kelompok1.materialku.domain.model.Material

class StokAdapter(
    private val onClick: (Material) -> Unit
) : ListAdapter<Material, StokAdapter.VH>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemStokBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemStokBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(m: Material) {
            val ctx = binding.root.context
            binding.tvNama.text = m.nama
            binding.tvKode.text = m.kode
            binding.tvStok.text = m.stokSaat.toString()

            val kritis = m.isStokKritis()
            binding.tvBadge.text = ctx.getString(
                if (kritis) R.string.badge_kritis else R.string.badge_aman
            )
            val bg = if (kritis) R.color.badge_kritis_bg else R.color.badge_aman_bg
            val fg = if (kritis) R.color.error else R.color.role_kasir
            binding.cvBadge.setCardBackgroundColor(
                ColorStateList.valueOf(ContextCompat.getColor(ctx, bg))
            )
            binding.tvBadge.setTextColor(ContextCompat.getColor(ctx, fg))
            binding.tvStok.setTextColor(
                ContextCompat.getColor(ctx, if (kritis) R.color.error else R.color.on_surface)
            )

            binding.root.setOnClickListener { onClick(m) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<Material>() {
        override fun areItemsTheSame(a: Material, b: Material): Boolean = a.id == b.id
        override fun areContentsTheSame(a: Material, b: Material): Boolean = a == b
    }
}
