package com.kelompok1.materialku.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kelompok1.materialku.data.local.entity.KategoriEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KategoriDao {

    @Query("SELECT * FROM kategori ORDER BY nama ASC")
    fun observeAll(): Flow<List<KategoriEntity>>

    @Query("SELECT * FROM kategori WHERE aktif = 1 ORDER BY nama ASC")
    suspend fun listAktif(): List<KategoriEntity>

    @Query("SELECT * FROM kategori WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): KategoriEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(kategori: KategoriEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<KategoriEntity>): List<Long>

    @Update
    suspend fun update(kategori: KategoriEntity)

    @Query("DELETE FROM kategori WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT COUNT(*) FROM kategori")
    suspend fun count(): Int
}
