package com.kelompok1.materialku.domain.repository

import com.kelompok1.materialku.domain.model.Satuan
import kotlinx.coroutines.flow.Flow

interface ISatuanRepository {
    fun observeAll(): Flow<List<Satuan>>
    suspend fun listAll(): List<Satuan>
    suspend fun findById(id: Int): Satuan?
    suspend fun insert(satuan: Satuan): Long
    suspend fun update(satuan: Satuan)
    suspend fun delete(id: Int)
}
