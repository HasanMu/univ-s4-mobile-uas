package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users ORDER BY id ASC")
    fun getAllUsers(): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)
}

@Dao
interface KategoriDao {
    @Query("SELECT * FROM kategori ORDER BY nama ASC")
    fun getAllKategori(): Flow<List<Kategori>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKategori(kategori: Kategori): Long

    @Update
    suspend fun updateKategori(kategori: Kategori)

    @Delete
    suspend fun deleteKategori(kategori: Kategori)
}

@Dao
interface SatuanDao {
    @Query("SELECT * FROM satuan ORDER BY nama ASC")
    fun getAllSatuan(): Flow<List<Satuan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSatuan(satuan: Satuan): Long

    @Delete
    suspend fun deleteSatuan(satuan: Satuan)
}

@Dao
interface SupplierDao {
    @Query("SELECT * FROM supplier ORDER BY nama ASC")
    fun getAllSupplier(): Flow<List<Supplier>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplier(supplier: Supplier): Long

    @Update
    suspend fun updateSupplier(supplier: Supplier)

    @Delete
    suspend fun deleteSupplier(supplier: Supplier)
}

@Dao
interface MaterialDao {
    @Query("SELECT * FROM material ORDER BY nama ASC")
    fun getAllMaterials(): Flow<List<Material>>

    @Query("SELECT * FROM material WHERE kode = :kode LIMIT 1")
    suspend fun getMaterialByKode(kode: String): Material?

    @Query("SELECT * FROM material WHERE id = :id LIMIT 1")
    suspend fun getMaterialById(id: Int): Material?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: Material): Long

    @Update
    suspend fun updateMaterial(material: Material)

    @Delete
    suspend fun deleteMaterial(material: Material)
}

@Dao
interface TransaksiDao {
    @Query("SELECT * FROM transaksi ORDER BY tanggal DESC")
    fun getAllTransaksi(): Flow<List<Transaksi>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaksi(transaksi: Transaksi): Long

    @Update
    suspend fun updateTransaksi(transaksi: Transaksi)
}

@Dao
interface ItemTransaksiDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemTransaksi(item: ItemTransaksi): Long

    @Query("SELECT * FROM item_transaksi WHERE transaksiId = :transaksiId")
    suspend fun getItemsByTransaksi(transaksiId: Int): List<ItemTransaksi>
}

@Dao
interface StokLogDao {
    @Query("SELECT * FROM stok_log ORDER BY id DESC")
    fun getAllStokLogs(): Flow<List<StokLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStokLog(stokLog: StokLog): Long
}
