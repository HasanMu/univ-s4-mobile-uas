package com.kelompok1.materialku.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kelompok1.materialku.data.local.entity.SupplierEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SupplierDao {

    @Query("SELECT * FROM supplier ORDER BY nama ASC")
    fun observeAll(): Flow<List<SupplierEntity>>

    @Query("SELECT * FROM supplier WHERE aktif = 1 ORDER BY nama ASC")
    suspend fun listAktif(): List<SupplierEntity>

    @Query("SELECT * FROM supplier WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): SupplierEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(supplier: SupplierEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<SupplierEntity>): List<Long>

    @Update
    suspend fun update(supplier: SupplierEntity)

    @Query("DELETE FROM supplier WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT COUNT(*) FROM supplier")
    suspend fun count(): Int
}
