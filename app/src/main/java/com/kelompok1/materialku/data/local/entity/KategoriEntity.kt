package com.kelompok1.materialku.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kelompok1.materialku.domain.model.Kategori
import java.time.LocalDate

@Entity(
    tableName = "kategori",
    indices = [Index(value = ["nama"], unique = true)]
)
data class KategoriEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nama: String,
    val deskripsi: String = "",
    val createdAt: LocalDate,
    val aktif: Boolean = true
) {
    fun toDomain(): Kategori = Kategori(id, nama, deskripsi, createdAt, aktif)

    companion object {
        fun fromDomain(k: Kategori): KategoriEntity =
            KategoriEntity(k.id, k.nama, k.deskripsi, k.createdAt, k.aktif)
    }
}
