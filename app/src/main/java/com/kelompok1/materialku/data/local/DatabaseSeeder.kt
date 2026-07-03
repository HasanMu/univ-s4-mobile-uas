package com.kelompok1.materialku.data.local

import com.kelompok1.materialku.data.local.entity.KategoriEntity
import com.kelompok1.materialku.data.local.entity.SatuanEntity
import com.kelompok1.materialku.data.local.entity.SupplierEntity
import com.kelompok1.materialku.data.local.entity.UserEntity
import com.kelompok1.materialku.domain.model.RoleEnum
import com.kelompok1.materialku.util.PasswordHasher
import java.time.LocalDate

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

    fun defaultKategoris(): List<KategoriEntity> {
        val today = LocalDate.now()
        return listOf(
            KategoriEntity(nama = "Bahan", deskripsi = "Bahan bangunan", createdAt = today),
            KategoriEntity(nama = "Alat", deskripsi = "Peralatan tukang", createdAt = today),
            KategoriEntity(nama = "Lainnya", deskripsi = "Kategori lainnya", createdAt = today)
        )
    }

    fun defaultSatuans(): List<SatuanEntity> = listOf(
        SatuanEntity(nama = "sak", simbol = "sak"),
        SatuanEntity(nama = "kaleng", simbol = "kaleng"),
        SatuanEntity(nama = "unit", simbol = "unit"),
        SatuanEntity(nama = "lembar", simbol = "lbr"),
        SatuanEntity(nama = "batang", simbol = "btg"),
        SatuanEntity(nama = "meter", simbol = "m"),
        SatuanEntity(nama = "kilogram", simbol = "kg"),
        SatuanEntity(nama = "liter", simbol = "L"),
        SatuanEntity(nama = "pieces", simbol = "pcs")
    )

    fun defaultSuppliers(): List<SupplierEntity> = listOf(
        SupplierEntity(nama = "Toko Maju", kontak = "081234567890", alamat = "Jl. Merdeka No. 1"),
        SupplierEntity(nama = "Sumber Teknik", kontak = "081234567891", alamat = "Jl. Sudirman No. 5"),
        SupplierEntity(nama = "Umum", kontak = "", alamat = "-")
    )
}
