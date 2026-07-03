package com.kelompok1.materialku.data.repository

import androidx.room.withTransaction
import com.kelompok1.materialku.data.local.MaterialKuDatabase
import com.kelompok1.materialku.data.local.dao.MaterialDao
import com.kelompok1.materialku.data.local.dao.StokLogDao
import com.kelompok1.materialku.data.local.entity.StokLogEntity
import com.kelompok1.materialku.domain.model.JenisStok
import com.kelompok1.materialku.domain.model.StokLog
import com.kelompok1.materialku.domain.repository.IStokRepository
import com.kelompok1.materialku.domain.repository.MutasiError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StokRepositoryImpl @Inject constructor(
    private val db: MaterialKuDatabase,
    private val stokLogDao: StokLogDao,
    private val materialDao: MaterialDao
) : IStokRepository {

    override fun observeLog(): Flow<List<StokLog>> =
        stokLogDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeLogByMaterial(materialId: Int): Flow<List<StokLog>> =
        stokLogDao.observeByMaterial(materialId).map { list -> list.map { it.toDomain() } }

    override suspend fun catatMutasi(
        materialId: Int,
        jenis: JenisStok,
        qty: Int,
        keterangan: String,
        userId: Int
    ): Result<Int> {
        if (qty <= 0) return Result.failure(MutasiError.InvalidQty)

        return db.withTransaction {
            val material = materialDao.findById(materialId)
                ?: return@withTransaction Result.failure(MutasiError.MaterialNotFound)

            val stokBaru = when (jenis) {
                JenisStok.MASUK -> material.stokSaat + qty
                JenisStok.KELUAR -> {
                    if (qty > material.stokSaat) {
                        return@withTransaction Result.failure(
                            MutasiError.InsufficientStock(material.stokSaat, qty)
                        )
                    }
                    material.stokSaat - qty
                }
            }

            materialDao.update(material.copy(stokSaat = stokBaru))
            stokLogDao.insert(
                StokLogEntity(
                    materialId = materialId,
                    jenis = jenis,
                    qty = qty,
                    tanggal = LocalDate.now(),
                    keterangan = keterangan,
                    userId = userId
                )
            )
            Result.success(stokBaru)
        }
    }
}
