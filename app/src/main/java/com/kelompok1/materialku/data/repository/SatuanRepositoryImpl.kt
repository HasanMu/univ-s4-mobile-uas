package com.kelompok1.materialku.data.repository

import com.kelompok1.materialku.data.local.dao.SatuanDao
import com.kelompok1.materialku.data.local.entity.SatuanEntity
import com.kelompok1.materialku.domain.model.Satuan
import com.kelompok1.materialku.domain.repository.ISatuanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SatuanRepositoryImpl @Inject constructor(
    private val dao: SatuanDao
) : ISatuanRepository {

    override fun observeAll(): Flow<List<Satuan>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun listAll(): List<Satuan> =
        dao.listAll().map { it.toDomain() }

    override suspend fun findById(id: Int): Satuan? =
        dao.findById(id)?.toDomain()

    override suspend fun insert(satuan: Satuan): Long =
        dao.insert(SatuanEntity.fromDomain(satuan))

    override suspend fun update(satuan: Satuan) {
        dao.update(SatuanEntity.fromDomain(satuan))
    }

    override suspend fun delete(id: Int) {
        dao.delete(id)
    }
}
