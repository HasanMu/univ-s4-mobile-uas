package com.example.domain.repository

import com.example.data.entities.*
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    suspend fun getUserByUsername(username: String): User?
    fun getAllUsers(): Flow<List<User>>
    suspend fun saveUser(user: User): Long
    suspend fun deleteUser(user: User)
}

interface IMaterialRepository {
    fun getAllMaterials(): Flow<List<Material>>
    suspend fun getMaterialByKode(kode: String): Material?
    suspend fun getMaterialById(id: Int): Material?
    suspend fun saveMaterial(material: Material): Long
    suspend fun deleteMaterial(material: Material)
    
    fun getAllKategori(): Flow<List<Kategori>>
    suspend fun saveKategori(kategori: Kategori): Long
    suspend fun deleteKategori(kategori: Kategori)
    
    fun getAllSatuan(): Flow<List<Satuan>>
    suspend fun saveSatuan(satuan: Satuan): Long
    suspend fun deleteSatuan(satuan: Satuan)
    
    fun getAllSupplier(): Flow<List<Supplier>>
    suspend fun saveSupplier(supplier: Supplier): Long
    suspend fun deleteSupplier(supplier: Supplier)
}

interface ITransaksiRepository {
    fun getAllTransaksi(): Flow<List<Transaksi>>
    suspend fun saveTransaksi(transaksi: Transaksi): Long
    suspend fun updateTransaksi(transaksi: Transaksi)
    suspend fun saveItemTransaksi(item: ItemTransaksi): Long
    suspend fun getItemsForTransaksi(transaksiId: Int): List<ItemTransaksi>
}

interface IStokRepository {
    fun getAllStokLogs(): Flow<List<StokLog>>
    suspend fun saveStokLog(stokLog: StokLog): Long
}
