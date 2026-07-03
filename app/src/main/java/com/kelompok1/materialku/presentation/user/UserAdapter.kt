package com.kelompok1.materialku.presentation.user

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kelompok1.materialku.R
import com.kelompok1.materialku.databinding.ItemUserBinding
import com.kelompok1.materialku.domain.model.RoleEnum
import com.kelompok1.materialku.domain.model.User

class UserAdapter(
    private val onClick: (User) -> Unit,
    private val onLongClick: (User) -> Unit
) : ListAdapter<User, UserAdapter.VH>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            val ctx = binding.root.context
            binding.tvUsername.text = user.username
            binding.tvRole.text = user.role.displayName().uppercase()
            binding.cvRole.setCardBackgroundColor(
                ColorStateList.valueOf(ContextCompat.getColor(ctx, user.role.badgeColorRes()))
            )
            binding.tvStatus.text = ctx.getString(
                if (user.aktif) R.string.user_status_aktif else R.string.user_status_nonaktif
            )
            binding.tvStatus.setTextColor(
                ContextCompat.getColor(
                    ctx,
                    if (user.aktif) R.color.on_surface_variant else R.color.error
                )
            )

            binding.root.setOnClickListener { onClick(user) }
            binding.root.setOnLongClickListener {
                onLongClick(user)
                true
            }
        }
    }

    private object Diff : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(a: User, b: User): Boolean = a.id == b.id
        override fun areContentsTheSame(a: User, b: User): Boolean = a == b
    }
}

private fun RoleEnum.badgeColorRes(): Int = when (this) {
    RoleEnum.ROLE_ADMIN -> R.color.role_admin
    RoleEnum.ROLE_KASIR -> R.color.role_kasir
    RoleEnum.ROLE_GUDANG -> R.color.role_gudang
    RoleEnum.ROLE_MANAGER -> R.color.role_manager
}
