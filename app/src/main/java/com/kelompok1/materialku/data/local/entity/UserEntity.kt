package com.kelompok1.materialku.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kelompok1.materialku.domain.model.RoleEnum
import com.kelompok1.materialku.domain.model.User
import java.time.LocalDateTime

@Entity(
    tableName = "user",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val passwordHash: String,
    val role: RoleEnum,
    val aktif: Boolean = true,
    val lastLogin: LocalDateTime? = null
) {
    fun toDomain(): User = User(
        id = id,
        username = username,
        passwordHash = passwordHash,
        role = role,
        aktif = aktif,
        lastLogin = lastLogin
    )

    companion object {
        fun fromDomain(user: User): UserEntity = UserEntity(
            id = user.id,
            username = user.username,
            passwordHash = user.passwordHash,
            role = user.role,
            aktif = user.aktif,
            lastLogin = user.lastLogin
        )
    }
}
