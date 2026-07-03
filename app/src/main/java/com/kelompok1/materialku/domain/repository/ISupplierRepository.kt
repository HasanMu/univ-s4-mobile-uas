package com.kelompok1.materialku.domain.repository

import com.kelompok1.materialku.domain.model.Supplier
import kotlinx.coroutines.flow.Flow

interface ISupplierRepository {
    fun observeAll(): Flow<List<Supplier>>
    suspend fun listAktif(): List<Supplier>
    suspend fun findById(id: Int): Supplier?
    suspend fun insert(supplier: Supplier): Long
    suspend fun update(supplier: Supplier)
    suspend fun delete(id: Int)
}
