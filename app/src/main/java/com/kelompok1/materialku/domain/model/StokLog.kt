package com.kelompok1.materialku.domain.model

import java.time.LocalDate

data class StokLog(
    val id: Int = 0,
    val materialId: Int,
    val jenis: JenisStok,
    val qty: Int,
    val tanggal: LocalDate,
    val keterangan: String = "",
    val userId: Int
)
