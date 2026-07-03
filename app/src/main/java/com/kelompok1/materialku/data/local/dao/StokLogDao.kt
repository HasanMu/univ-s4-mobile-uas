package com.kelompok1.materialku.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kelompok1.materialku.data.local.entity.StokLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StokLogDao {

    @Query("SELECT * FROM stok_log ORDER BY tanggal DESC, id DESC")
    fun observeAll(): Flow<List<StokLogEntity>>

    @Query("SELECT * FROM stok_log WHERE materialId = :materialId ORDER BY tanggal DESC, id DESC")
    fun observeByMaterial(materialId: Int): Flow<List<StokLogEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(log: StokLogEntity): Long

    @Query("SELECT COUNT(*) FROM stok_log")
    suspend fun count(): Int
}
