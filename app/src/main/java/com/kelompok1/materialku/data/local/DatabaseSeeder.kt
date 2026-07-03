package com.kelompok1.materialku.data.local

import com.kelompok1.materialku.data.local.entity.UserEntity
import com.kelompok1.materialku.domain.model.RoleEnum
import com.kelompok1.materialku.util.PasswordHasher

/**
 * Data awal yang di-seed saat database pertama kali dibuat (callback OnCreate).
 * Kredensial default sesuai CONTRIBUTING.md — untuk demo & pengembangan.
 */
object DatabaseSeeder {

    fun defaultUsers(hasher: PasswordHasher): List<UserEntity> = listOf(
        UserEntity(username = "admin", passwordHash = hasher.hash("admin123"), role = RoleEnum.ROLE_ADMIN),
        UserEntity(username = "kasir", passwordHash = hasher.hash("kasir123"), role = RoleEnum.ROLE_KASIR),
        UserEntity(username = "gudang", passwordHash = hasher.hash("gudang123"), role = RoleEnum.ROLE_GUDANG),
        UserEntity(username = "manager", passwordHash = hasher.hash("manager123"), role = RoleEnum.ROLE_MANAGER)
    )
}
