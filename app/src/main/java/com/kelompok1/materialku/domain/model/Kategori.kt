package com.kelompok1.materialku.domain.model

import java.time.LocalDate

data class Kategori(
    val id: Int = 0,
    val nama: String,
    val deskripsi: String = "",
    val createdAt: LocalDate,
    val aktif: Boolean = true
)
