package com.kelompok1.materialku.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kelompok1.materialku.data.local.entity.TransaksiEntity
import com.kelompok1.materialku.domain.model.StatusTransaksi
import kotlinx.coroutines.flow.Flow

@Dao
interface TransaksiDao {

    @Query("SELECT * FROM transaksi ORDER BY tanggal DESC, id DESC")
    fun observeAll(): Flow<List<TransaksiEntity>>

    @Query("SELECT * FROM transaksi WHERE status = :status ORDER BY tanggal DESC, id DESC")
    fun observeByStatus(status: StatusTransaksi): Flow<List<TransaksiEntity>>

    @Query("SELECT * FROM transaksi WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): TransaksiEntity?

    @Query("SELECT * FROM transaksi WHERE noFaktur = :noFaktur LIMIT 1")
    suspend fun findByNoFaktur(noFaktur: String): TransaksiEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(transaksi: TransaksiEntity): Long

    @Query("UPDATE transaksi SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: StatusTransaksi)

    @Query("SELECT COUNT(*) FROM transaksi WHERE status = :status")
    suspend fun countByStatus(status: StatusTransaksi): Int

    @Query("SELECT COUNT(*) FROM transaksi")
    suspend fun count(): Int
}
