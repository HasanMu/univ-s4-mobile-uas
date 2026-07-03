package com.kelompok1.materialku.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kelompok1.materialku.domain.model.Material

@Entity(
    tableName = "material",
    indices = [
        Index(value = ["kode"], unique = true),
        Index(value = ["kategoriId"]),
        Index(value = ["satuanId"]),
        Index(value = ["supplierId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = KategoriEntity::class,
            parentColumns = ["id"],
            childColumns = ["kategoriId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = SatuanEntity::class,
            parentColumns = ["id"],
            childColumns = ["satuanId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = SupplierEntity::class,
            parentColumns = ["id"],
            childColumns = ["supplierId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class MaterialEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val kode: String,
    val nama: String,
    val hargaJual: Double,
    val stokSaat: Int,
    val stokMin: Int,
    val kategoriId: Int,
    val satuanId: Int,
    val supplierId: Int? = null
) {
    fun toDomain(): Material = Material(
        id = id,
        kode = kode,
        nama = nama,
        hargaJual = hargaJual,
        stokSaat = stokSaat,
        stokMin = stokMin,
        kategoriId = kategoriId,
        satuanId = satuanId,
        supplierId = supplierId
    )

    companion object {
        fun fromDomain(m: Material): MaterialEntity = MaterialEntity(
            id = m.id,
            kode = m.kode,
            nama = m.nama,
            hargaJual = m.hargaJual,
            stokSaat = m.stokSaat,
            stokMin = m.stokMin,
            kategoriId = m.kategoriId,
            satuanId = m.satuanId,
            supplierId = m.supplierId
        )
    }
}
