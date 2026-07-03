package com.kelompok1.materialku.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kelompok1.materialku.domain.model.JenisStok
import com.kelompok1.materialku.domain.model.StokLog
import java.time.LocalDate

@Entity(
    tableName = "stok_log",
    indices = [
        Index(value = ["materialId"]),
        Index(value = ["userId"]),
        Index(value = ["tanggal"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = MaterialEntity::class,
            parentColumns = ["id"],
            childColumns = ["materialId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class StokLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val materialId: Int,
    val jenis: JenisStok,
    val qty: Int,
    val tanggal: LocalDate,
    val keterangan: String = "",
    val userId: Int
) {
    fun toDomain(): StokLog = StokLog(
        id = id,
        materialId = materialId,
        jenis = jenis,
        qty = qty,
        tanggal = tanggal,
        keterangan = keterangan,
        userId = userId
    )

    companion object {
        fun fromDomain(log: StokLog): StokLogEntity = StokLogEntity(
            id = log.id,
            materialId = log.materialId,
            jenis = log.jenis,
            qty = log.qty,
            tanggal = log.tanggal,
            keterangan = log.keterangan,
            userId = log.userId
        )
    }
}
