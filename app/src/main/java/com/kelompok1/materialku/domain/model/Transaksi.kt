package com.kelompok1.materialku.domain.model

import java.time.LocalDateTime

data class Transaksi(
    val id: Int = 0,
    val noFaktur: String,
    val tanggal: LocalDateTime,
    val totalHarga: Double,
    val status: StatusTransaksi,
    val userId: Int
)
