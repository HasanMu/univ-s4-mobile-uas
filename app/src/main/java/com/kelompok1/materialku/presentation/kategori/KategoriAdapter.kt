package com.kelompok1.materialku.presentation.kategori

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kelompok1.materialku.R
import com.kelompok1.materialku.databinding.ItemKategoriBinding
import com.kelompok1.materialku.domain.model.Kategori

class KategoriAdapter(
    private val onClick: (Kategori) -> Unit,
    private val onLongClick: (Kategori) -> Unit
) : ListAdapter<Kategori, KategoriAdapter.VH>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemKategoriBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemKategoriBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(kategori: Kategori) {
            val ctx = binding.root.context
            binding.tvNama.text = kategori.nama
            if (kategori.deskripsi.isBlank()) {
                binding.tvDeskripsi.visibility = android.view.View.GONE
            } else {
                binding.tvDeskripsi.visibility = android.view.View.VISIBLE
                binding.tvDeskripsi.text = kategori.deskripsi
            }

            val aktif = kategori.aktif
            binding.tvStatus.text = ctx.getString(
                if (aktif) R.string.kategori_status_aktif else R.string.kategori_status_nonaktif
            )
            val bg = if (aktif) R.color.badge_aman_bg else R.color.badge_kritis_bg
            val fg = if (aktif) R.color.role_kasir else R.color.error
            binding.cvStatus.setCardBackgroundColor(
                ColorStateList.valueOf(ContextCompat.getColor(ctx, bg))
            )
            binding.tvStatus.setTextColor(ContextCompat.getColor(ctx, fg))

            binding.root.setOnClickListener { onClick(kategori) }
            binding.root.setOnLongClickListener {
                onLongClick(kategori)
                true
            }
        }
    }

    private object Diff : DiffUtil.ItemCallback<Kategori>() {
        override fun areItemsTheSame(a: Kategori, b: Kategori): Boolean = a.id == b.id
        override fun areContentsTheSame(a: Kategori, b: Kategori): Boolean = a == b
    }
}
