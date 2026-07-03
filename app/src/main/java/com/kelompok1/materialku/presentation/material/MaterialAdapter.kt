package com.kelompok1.materialku.presentation.material

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kelompok1.materialku.R
import com.kelompok1.materialku.databinding.ItemMaterialBinding
import com.kelompok1.materialku.util.Formatter

class MaterialAdapter(
    private val onClick: (MaterialRow) -> Unit,
    private val onLongClick: (MaterialRow) -> Unit
) : ListAdapter<MaterialRow, MaterialAdapter.VH>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemMaterialBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemMaterialBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(row: MaterialRow) {
            val m = row.material
            val ctx = binding.root.context
            binding.tvNama.text = m.nama
            binding.tvKategori.text = "${row.kategoriNama} • ${m.kode}"

            val kritis = m.isStokKritis()
            binding.tvBadge.text = if (kritis) {
                ctx.getString(R.string.badge_kritis)
            } else {
                ctx.getString(R.string.badge_aman)
            }
            val badgeBg = if (kritis) R.color.badge_kritis_bg else R.color.badge_aman_bg
            val badgeFg = if (kritis) R.color.error else R.color.role_kasir
            binding.cvBadge.setCardBackgroundColor(
                ColorStateList.valueOf(ContextCompat.getColor(ctx, badgeBg))
            )
            binding.tvBadge.setTextColor(ContextCompat.getColor(ctx, badgeFg))

            binding.tvStok.text = "Stok: ${m.stokSaat}"
            binding.tvStok.setTextColor(
                ContextCompat.getColor(ctx, if (kritis) R.color.error else R.color.on_surface)
            )
            binding.tvHarga.text = Formatter.rupiah(m.hargaJual)

            binding.root.setOnClickListener { onClick(row) }
            binding.root.setOnLongClickListener {
                onLongClick(row)
                true
            }
        }
    }

    private object Diff : DiffUtil.ItemCallback<MaterialRow>() {
        override fun areItemsTheSame(a: MaterialRow, b: MaterialRow): Boolean =
            a.material.id == b.material.id

        override fun areContentsTheSame(a: MaterialRow, b: MaterialRow): Boolean = a == b
    }
}
