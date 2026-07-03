package com.kelompok1.materialku.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kelompok1.materialku.domain.model.StatusTransaksi
import com.kelompok1.materialku.domain.model.Transaksi
import java.time.LocalDateTime

@Entity(
    tableName = "transaksi",
    indices = [
        Index(value = ["noFaktur"], unique = true),
        Index(value = ["userId"]),
        Index(value = ["tanggal"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class TransaksiEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val noFaktur: String,
    val tanggal: LocalDateTime,
    val totalHarga: Double,
    val status: StatusTransaksi,
    val userId: Int
) {
    fun toDomain(): Transaksi = Transaksi(
        id = id,
        noFaktur = noFaktur,
        tanggal = tanggal,
        totalHarga = totalHarga,
        status = status,
        userId = userId
    )

    companion object {
        fun fromDomain(t: Transaksi): TransaksiEntity = TransaksiEntity(
            id = t.id,
            noFaktur = t.noFaktur,
            tanggal = t.tanggal,
            totalHarga = t.totalHarga,
            status = t.status,
            userId = t.userId
        )
    }
}
