package com.kelompok1.materialku.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kelompok1.materialku.data.local.entity.SatuanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SatuanDao {

    @Query("SELECT * FROM satuan ORDER BY nama ASC")
    fun observeAll(): Flow<List<SatuanEntity>>

    @Query("SELECT * FROM satuan ORDER BY nama ASC")
    suspend fun listAll(): List<SatuanEntity>

    @Query("SELECT * FROM satuan WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): SatuanEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(satuan: SatuanEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<SatuanEntity>): List<Long>

    @Update
    suspend fun update(satuan: SatuanEntity)

    @Query("DELETE FROM satuan WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT COUNT(*) FROM satuan")
    suspend fun count(): Int
}
