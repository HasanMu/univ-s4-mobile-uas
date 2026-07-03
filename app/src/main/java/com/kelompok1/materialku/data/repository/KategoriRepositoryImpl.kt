package com.kelompok1.materialku.data.repository

import com.kelompok1.materialku.data.local.dao.KategoriDao
import com.kelompok1.materialku.data.local.entity.KategoriEntity
import com.kelompok1.materialku.domain.model.Kategori
import com.kelompok1.materialku.domain.repository.IKategoriRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KategoriRepositoryImpl @Inject constructor(
    private val dao: KategoriDao
) : IKategoriRepository {

    override fun observeAll(): Flow<List<Kategori>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun listAktif(): List<Kategori> =
        dao.listAktif().map { it.toDomain() }

    override suspend fun findById(id: Int): Kategori? =
        dao.findById(id)?.toDomain()

    override suspend fun insert(kategori: Kategori): Long =
        dao.insert(KategoriEntity.fromDomain(kategori))

    override suspend fun update(kategori: Kategori) {
        dao.update(KategoriEntity.fromDomain(kategori))
    }

    override suspend fun delete(id: Int) {
        dao.delete(id)
    }
}
