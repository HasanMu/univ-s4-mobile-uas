package com.kelompok1.materialku.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kelompok1.materialku.domain.model.Supplier

@Entity(tableName = "supplier")
data class SupplierEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nama: String,
    val kontak: String = "",
    val alamat: String = "",
    val aktif: Boolean = true
) {
    fun toDomain(): Supplier = Supplier(id, nama, kontak, alamat, aktif)

    companion object {
        fun fromDomain(s: Supplier): SupplierEntity =
            SupplierEntity(s.id, s.nama, s.kontak, s.alamat, s.aktif)
    }
}
