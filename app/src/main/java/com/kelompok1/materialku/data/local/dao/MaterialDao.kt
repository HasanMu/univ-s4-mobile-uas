package com.kelompok1.materialku.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kelompok1.materialku.data.local.entity.MaterialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialDao {

    @Query("SELECT * FROM material ORDER BY nama ASC")
    fun observeAll(): Flow<List<MaterialEntity>>

    @Query("SELECT * FROM material WHERE kategoriId = :kategoriId ORDER BY nama ASC")
    fun observeByKategori(kategoriId: Int): Flow<List<MaterialEntity>>

    @Query("SELECT * FROM material WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): MaterialEntity?

    @Query("SELECT * FROM material WHERE kode = :kode LIMIT 1")
    suspend fun findByKode(kode: String): MaterialEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(material: MaterialEntity): Long

    @Update
    suspend fun update(material: MaterialEntity)

    @Query("DELETE FROM material WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT COUNT(*) FROM material")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM material WHERE stokSaat <= stokMin")
    suspend fun countKritis(): Int
}
