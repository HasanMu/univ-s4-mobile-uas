package com.kelompok1.materialku.data.repository

import com.kelompok1.materialku.data.local.dao.ItemTransaksiDao
import com.kelompok1.materialku.data.local.dao.MaterialDao
import com.kelompok1.materialku.data.local.dao.TransaksiDao
import com.kelompok1.materialku.data.local.dao.UserDao
import com.kelompok1.materialku.domain.model.StatusTransaksi
import com.kelompok1.materialku.domain.repository.IReportRepository
import com.kelompok1.materialku.domain.repository.LaporanData
import com.kelompok1.materialku.domain.repository.LaporanPeriode
import com.kelompok1.materialku.domain.repository.TrxDetail
import com.kelompok1.materialku.domain.repository.TrxDetailItem
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepositoryImpl @Inject constructor(
    private val transaksiDao: TransaksiDao,
    private val itemTransaksiDao: ItemTransaksiDao,
    private val materialDao: MaterialDao,
    private val userDao: UserDao
) : IReportRepository {

    override suspend fun getTrxDetail(transaksiId: Int): TrxDetail? {
        val trx = transaksiDao.findById(transaksiId)?.toDomain() ?: return null
        val items = itemTransaksiDao.findByTransaksi(transaksiId).map { e ->
            val mat = materialDao.findById(e.materialId)
            TrxDetailItem(
                namaMaterial = mat?.nama ?: "(material dihapus)",
                qty = e.qty,
                hargaSatuan = e.hargaSatuan,
                subtotal = e.subtotal
            )
        }
        val kasir = userDao.findById(trx.userId)?.username ?: "(user dihapus)"
        return TrxDetail(transaksi = trx, kasir = kasir, items = items)
    }

    override suspend fun buildLaporan(periode: LaporanPeriode): LaporanData {
        val (from, to) = resolveRange(periode)
        val fromDt = from.atStartOfDay()
        // `to` inclusive-end → cast ke start of next day supaya query < to
        // menangkap semua transaksi di tanggal `to`.
        val toDt = to.plusDays(1).atStartOfDay()

        val total = transaksiDao.countInRange(StatusTransaksi.SELESAI, fromDt, toDt)
        val pendapatan = transaksiDao.sumTotalInRange(StatusTransaksi.SELESAI, fromDt, toDt)
        val terjual = itemTransaksiDao.sumQtyInRange(fromDt, toDt)
        val recent = transaksiDao.listSelesaiInRange(StatusTransaksi.SELESAI, fromDt, toDt)
            .take(10)
            .map { it.toDomain() }

        // Stok kritis snapshot — semua material yang stokSaat <= stokMin,
        // gak tergantung periode laporan.
        val stokKritis = materialDao.listKritis().map { it.toDomain() }

        return LaporanData(
            periode = periode,
            from = from,
            to = to,
            totalTransaksi = total,
            pendapatan = pendapatan,
            unitTerjual = terjual,
            transaksiTerbaru = recent,
            stokKritis = stokKritis
        )
    }

    private fun resolveRange(periode: LaporanPeriode): Pair<LocalDate, LocalDate> {
        val today = LocalDate.now()
        return when (periode) {
            LaporanPeriode.HariIni -> today to today
            LaporanPeriode.MingguIni -> {
                val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                monday to today
            }
            LaporanPeriode.BulanIni -> today.withDayOfMonth(1) to today
            is LaporanPeriode.Custom -> periode.from to periode.to
        }
    }
}
