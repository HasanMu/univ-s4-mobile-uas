package com.kelompok1.materialku.domain.repository

import com.kelompok1.materialku.domain.model.Material
import com.kelompok1.materialku.domain.model.Transaksi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

interface IReportRepository {
    suspend fun buildLaporan(periode: LaporanPeriode): LaporanData
}

sealed class LaporanPeriode(val label: String) {
    data object HariIni : LaporanPeriode("Hari Ini")
    data object MingguIni : LaporanPeriode("Minggu Ini")
    data object BulanIni : LaporanPeriode("Bulan Ini")
    data class Custom(val from: LocalDate, val to: LocalDate) :
        LaporanPeriode(
            "${from.format(CUSTOM_FMT)} — ${to.format(CUSTOM_FMT)}"
        )

    companion object {
        private val CUSTOM_FMT: DateTimeFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
    }
}

data class LaporanData(
    val periode: LaporanPeriode,
    val from: LocalDate,
    val to: LocalDate,
    val totalTransaksi: Int,
    val pendapatan: Double,
    val unitTerjual: Int,
    val transaksiTerbaru: List<Transaksi>,
    val stokKritis: List<Material>
)
