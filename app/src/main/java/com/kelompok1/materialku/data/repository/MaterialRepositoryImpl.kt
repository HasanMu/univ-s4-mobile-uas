package com.kelompok1.materialku.data.repository

import com.kelompok1.materialku.data.local.dao.MaterialDao
import com.kelompok1.materialku.data.local.entity.MaterialEntity
import com.kelompok1.materialku.domain.model.Material
import com.kelompok1.materialku.domain.repository.IMaterialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MaterialRepositoryImpl @Inject constructor(
    private val dao: MaterialDao
) : IMaterialRepository {

    override fun observeAll(): Flow<List<Material>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeByKategori(kategoriId: Int): Flow<List<Material>> =
        dao.observeByKategori(kategoriId).map { list -> list.map { it.toDomain() } }

    override suspend fun findById(id: Int): Material? =
        dao.findById(id)?.toDomain()

    override suspend fun findByKode(kode: String): Material? =
        dao.findByKode(kode)?.toDomain()

    override suspend fun insert(material: Material): Long =
        dao.insert(MaterialEntity.fromDomain(material))

    override suspend fun update(material: Material) {
        dao.update(MaterialEntity.fromDomain(material))
    }

    override suspend fun delete(id: Int) {
        dao.delete(id)
    }

    override suspend fun count(): Int = dao.count()

    override suspend fun countKritis(): Int = dao.countKritis()
}
