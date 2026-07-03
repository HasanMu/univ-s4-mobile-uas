package com.kelompok1.materialku.data.repository

import com.kelompok1.materialku.data.local.dao.SupplierDao
import com.kelompok1.materialku.data.local.entity.SupplierEntity
import com.kelompok1.materialku.domain.model.Supplier
import com.kelompok1.materialku.domain.repository.ISupplierRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupplierRepositoryImpl @Inject constructor(
    private val dao: SupplierDao
) : ISupplierRepository {

    override fun observeAll(): Flow<List<Supplier>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun listAktif(): List<Supplier> =
        dao.listAktif().map { it.toDomain() }

    override suspend fun findById(id: Int): Supplier? =
        dao.findById(id)?.toDomain()

    override suspend fun insert(supplier: Supplier): Long =
        dao.insert(SupplierEntity.fromDomain(supplier))

    override suspend fun update(supplier: Supplier) {
        dao.update(SupplierEntity.fromDomain(supplier))
    }

    override suspend fun delete(id: Int) {
        dao.delete(id)
    }
}
