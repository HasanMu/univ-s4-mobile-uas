package com.kelompok1.materialku.domain.repository

import com.kelompok1.materialku.domain.model.Kategori
import kotlinx.coroutines.flow.Flow

interface IKategoriRepository {
    fun observeAll(): Flow<List<Kategori>>
    suspend fun listAktif(): List<Kategori>
    suspend fun findById(id: Int): Kategori?
    suspend fun insert(kategori: Kategori): Long
    suspend fun update(kategori: Kategori)
    suspend fun delete(id: Int)
}
