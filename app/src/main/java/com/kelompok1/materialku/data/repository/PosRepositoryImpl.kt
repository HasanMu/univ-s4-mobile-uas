package com.kelompok1.materialku.data.repository

import androidx.room.withTransaction
import com.kelompok1.materialku.data.local.MaterialKuDatabase
import com.kelompok1.materialku.data.local.dao.ItemTransaksiDao
import com.kelompok1.materialku.data.local.dao.MaterialDao
import com.kelompok1.materialku.data.local.dao.StokLogDao
import com.kelompok1.materialku.data.local.dao.TransaksiDao
import com.kelompok1.materialku.data.local.entity.ItemTransaksiEntity
import com.kelompok1.materialku.data.local.entity.StokLogEntity
import com.kelompok1.materialku.data.local.entity.TransaksiEntity
import com.kelompok1.materialku.domain.model.JenisStok
import com.kelompok1.materialku.domain.model.StatusTransaksi
import com.kelompok1.materialku.domain.model.Transaksi
import com.kelompok1.materialku.domain.repository.CartLine
import com.kelompok1.materialku.domain.repository.CheckoutError
import com.kelompok1.materialku.domain.repository.CheckoutResult
import com.kelompok1.materialku.domain.repository.IPosRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PosRepositoryImpl @Inject constructor(
    private val db: MaterialKuDatabase,
    private val transaksiDao: TransaksiDao,
    private val itemTransaksiDao: ItemTransaksiDao,
    private val materialDao: MaterialDao,
    private val stokLogDao: StokLogDao
) : IPosRepository {

    override fun observeAll(): Flow<List<Transaksi>> =
        transaksiDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeByStatus(status: StatusTransaksi): Flow<List<Transaksi>> =
        transaksiDao.observeByStatus(status).map { list -> list.map { it.toDomain() } }

    override suspend fun findById(id: Int): Transaksi? =
        transaksiDao.findById(id)?.toDomain()

    override suspend fun checkout(
        items: List<CartLine>,
        status: StatusTransaksi,
        userId: Int
    ): Result<CheckoutResult> {
        if (items.isEmpty()) return Result.failure(CheckoutError.EmptyCart)
        if (items.any { it.qty <= 0 }) return Result.failure(CheckoutError.InvalidQty)

        return db.withTransaction {
            // Fase 1: validasi stok semua item DULU, sebelum menulis apapun.
            // Kalau salah satu gagal, seluruh transaksi rollback dan gak ada
            // side-effect ke Material.stokSaat.
            for (line in items) {
                val material = materialDao.findById(line.materialId)
                    ?: return@withTransaction Result.failure(
                        CheckoutError.MaterialNotFound(line.materialId)
                    )
                if (status == StatusTransaksi.SELESAI && line.qty > material.stokSaat) {
                    return@withTransaction Result.failure(
                        CheckoutError.InsufficientStock(
                            materialId = material.id,
                            nama = material.nama,
                            stokSaat = material.stokSaat,
                            requested = line.qty
                        )
                    )
                }
            }

            // Fase 2: insert Transaksi header.
            val now = LocalDateTime.now()
            val noFaktur = generateNoFaktur(now, transaksiDao.count() + 1)
            val totalHarga = items.sumOf { it.qty * it.hargaSatuan }
            val transaksiId = transaksiDao.insert(
                TransaksiEntity(
                    noFaktur = noFaktur,
                    tanggal = now,
                    totalHarga = totalHarga,
                    status = status,
                    userId = userId
                )
            ).toInt()

            // Fase 3: insert semua ItemTransaksi.
            itemTransaksiDao.insertAll(
                items.map { line ->
                    ItemTransaksiEntity(
                        transaksiId = transaksiId,
                        materialId = line.materialId,
                        qty = line.qty,
                        hargaSatuan = line.hargaSatuan,
                        subtotal = line.qty * line.hargaSatuan
                    )
                }
            )

            // Fase 4: kalau SELESAI, decrement stok + insert StokLog KELUAR
            // per item. DRAFT tidak mempengaruhi stok (bisa di-checkout nanti).
            if (status == StatusTransaksi.SELESAI) {
                val today = LocalDate.now()
                for (line in items) {
                    val material = materialDao.findById(line.materialId)!!
                    materialDao.update(material.copy(stokSaat = material.stokSaat - line.qty))
                    stokLogDao.insert(
                        StokLogEntity(
                            materialId = line.materialId,
                            jenis = JenisStok.KELUAR,
                            qty = line.qty,
                            tanggal = today,
                            keterangan = "Penjualan $noFaktur",
                            userId = userId
                        )
                    )
                }
            }

            Result.success(
                CheckoutResult(
                    transaksiId = transaksiId,
                    noFaktur = noFaktur,
                    totalHarga = totalHarga
                )
            )
        }
    }

    private fun generateNoFaktur(now: LocalDateTime, seq: Int): String {
        // Format: TRX-YYYYMMDD-NNNN (running count total transaksi).
        val date = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        return "TRX-$date-%04d".format(seq)
    }
}
