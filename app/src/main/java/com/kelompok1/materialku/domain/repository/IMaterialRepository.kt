package com.kelompok1.materialku.domain.repository

import com.kelompok1.materialku.domain.model.Material
import kotlinx.coroutines.flow.Flow

interface IMaterialRepository {
    fun observeAll(): Flow<List<Material>>
    fun observeByKategori(kategoriId: Int): Flow<List<Material>>
    suspend fun findById(id: Int): Material?
    suspend fun findByKode(kode: String): Material?
    suspend fun insert(material: Material): Long
    suspend fun update(material: Material)
    suspend fun delete(id: Int)
    suspend fun count(): Int
    suspend fun countKritis(): Int
}
