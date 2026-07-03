package com.kelompok1.materialku.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kelompok1.materialku.domain.model.ItemTransaksi

@Entity(
    tableName = "item_transaksi",
    indices = [
        Index(value = ["transaksiId"]),
        Index(value = ["materialId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = TransaksiEntity::class,
            parentColumns = ["id"],
            childColumns = ["transaksiId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MaterialEntity::class,
            parentColumns = ["id"],
            childColumns = ["materialId"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class ItemTransaksiEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val transaksiId: Int,
    val materialId: Int,
    val qty: Int,
    val hargaSatuan: Double,
    val subtotal: Double
) {
    fun toDomain(): ItemTransaksi = ItemTransaksi(
        id = id,
        transaksiId = transaksiId,
        materialId = materialId,
        qty = qty,
        hargaSatuan = hargaSatuan,
        subtotal = subtotal
    )
}
