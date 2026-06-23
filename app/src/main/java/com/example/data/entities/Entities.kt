package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val passwordHash: String,
    val passwordPlain: String = "",
    val role: String, // ROLE_ADMIN, ROLE_KASIR, ROLE_GUDANG, ROLE_MANAGER
    val aktif: Boolean = true,
    val lastLogin: String = ""
)

@Entity(tableName = "kategori")
data class Kategori(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nama: String,
    val deskripsi: String,
    val createdAt: String,
    val aktif: Boolean = true
)

@Entity(tableName = "satuan")
data class Satuan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nama: String,
    val simbol: String
)

@Entity(tableName = "supplier")
data class Supplier(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nama: String,
    val kontak: String,
    val alamat: String,
    val aktif: Boolean = true
)

@Entity(tableName = "material")
data class Material(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val kode: String, // e.g. MTR-011
    val nama: String,
    val hargaJual: Double,
    val stokSaat: Int,
    val minStok: Int,
    val kategoriId: Int,
    val satuanId: Int,
    val supplierId: Int
) {
    fun isStokKritis(): Boolean = stokSaat <= minStok
}

@Entity(tableName = "transaksi")
data class Transaksi(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val noFaktur: String,
    val tanggal: String,
    val totalHarga: Double,
    val status: String, // DRAFT, SELESAI, BATAL
    val userId: Int
)

@Entity(tableName = "item_transaksi")
data class ItemTransaksi(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val transaksiId: Int,
    val materialId: Int,
    val qty: Int,
    val hargaSatuan: Double,
    val subtotal: Double
)

@Entity(tableName = "stok_log")
data class StokLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val materialId: Int,
    val jenis: String, // MASUK, KELUAR
    val qty: Int,
    val tanggal: String,
    val keterangan: String
)
