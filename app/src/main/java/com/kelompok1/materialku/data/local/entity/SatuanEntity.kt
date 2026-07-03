package com.kelompok1.materialku.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kelompok1.materialku.domain.model.Satuan

@Entity(
    tableName = "satuan",
    indices = [Index(value = ["nama"], unique = true)]
)
data class SatuanEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nama: String,
    val simbol: String
) {
    fun toDomain(): Satuan = Satuan(id, nama, simbol)

    companion object {
        fun fromDomain(s: Satuan): SatuanEntity = SatuanEntity(s.id, s.nama, s.simbol)
    }
}
