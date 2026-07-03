package com.kelompok1.materialku.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kelompok1.materialku.data.local.entity.ItemTransaksiEntity

@Dao
interface ItemTransaksiDao {

    @Query("SELECT * FROM item_transaksi WHERE transaksiId = :transaksiId ORDER BY id ASC")
    suspend fun findByTransaksi(transaksiId: Int): List<ItemTransaksiEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(items: List<ItemTransaksiEntity>): List<Long>

    @Query("SELECT COUNT(*) FROM item_transaksi WHERE transaksiId = :transaksiId")
    suspend fun countByTransaksi(transaksiId: Int): Int
}
