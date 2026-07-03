package com.kelompok1.materialku.presentation.satuan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kelompok1.materialku.databinding.ItemSatuanBinding
import com.kelompok1.materialku.domain.model.Satuan

class SatuanAdapter(
    private val onClick: (Satuan) -> Unit,
    private val onLongClick: (Satuan) -> Unit
) : ListAdapter<Satuan, SatuanAdapter.VH>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemSatuanBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemSatuanBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(satuan: Satuan) {
            binding.tvNama.text = satuan.nama
            binding.tvSimbol.text = satuan.simbol
            binding.root.setOnClickListener { onClick(satuan) }
            binding.root.setOnLongClickListener {
                onLongClick(satuan)
                true
            }
        }
    }

    private object Diff : DiffUtil.ItemCallback<Satuan>() {
        override fun areItemsTheSame(a: Satuan, b: Satuan): Boolean = a.id == b.id
        override fun areContentsTheSame(a: Satuan, b: Satuan): Boolean = a == b
    }
}
