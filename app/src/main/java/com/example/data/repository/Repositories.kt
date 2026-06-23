package com.example.data.repository

import com.example.data.dao.*
import com.example.data.entities.*
import com.example.domain.repository.*
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl(private val userDao: UserDao) : IUserRepository {
    override suspend fun getUserByUsername(username: String): User? = 
        userDao.getUserByUsername(username)
        
    override fun getAllUsers(): Flow<List<User>> = 
        userDao.getAllUsers()
        
    override suspend fun saveUser(user: User): Long = 
        userDao.insertUser(user)
        
    override suspend fun deleteUser(user: User) = 
        userDao.deleteUser(user)
}

class MaterialRepositoryImpl(
    private val materialDao: MaterialDao,
    private val kategoriDao: KategoriDao,
    private val satuanDao: SatuanDao,
    private val supplierDao: SupplierDao
) : IMaterialRepository {

    override fun getAllMaterials(): Flow<List<Material>> = 
        materialDao.getAllMaterials()
        
    override suspend fun getMaterialByKode(kode: String): Material? = 
        materialDao.getMaterialByKode(kode)

    override suspend fun getMaterialById(id: Int): Material? = 
        materialDao.getMaterialById(id)
        
    override suspend fun saveMaterial(material: Material): Long = 
        materialDao.insertMaterial(material)
        
    override suspend fun deleteMaterial(material: Material) = 
        materialDao.deleteMaterial(material)

    override fun getAllKategori(): Flow<List<Kategori>> = 
        kategoriDao.getAllKategori()
        
    override suspend fun saveKategori(kategori: Kategori): Long = 
        kategoriDao.insertKategori(kategori)
        
    override suspend fun deleteKategori(kategori: Kategori) = 
        kategoriDao.deleteKategori(kategori)

    override fun getAllSatuan(): Flow<List<Satuan>> = 
        satuanDao.getAllSatuan()
        
    override suspend fun saveSatuan(satuan: Satuan): Long = 
        satuanDao.insertSatuan(satuan)
        
    override suspend fun deleteSatuan(satuan: Satuan) = 
        satuanDao.deleteSatuan(satuan)

    override fun getAllSupplier(): Flow<List<Supplier>> = 
        supplierDao.getAllSupplier()
        
    override suspend fun saveSupplier(supplier: Supplier): Long = 
        supplierDao.insertSupplier(supplier)
        
    override suspend fun deleteSupplier(supplier: Supplier) = 
        supplierDao.deleteSupplier(supplier)
}

class TransaksiRepositoryImpl(
    private val transaksiDao: TransaksiDao,
    private val itemTransaksiDao: ItemTransaksiDao
) : ITransaksiRepository {

    override fun getAllTransaksi(): Flow<List<Transaksi>> = 
        transaksiDao.getAllTransaksi()
        
    override suspend fun saveTransaksi(transaksi: Transaksi): Long = 
        transaksiDao.insertTransaksi(transaksi)
        
    override suspend fun updateTransaksi(transaksi: Transaksi) = 
        transaksiDao.updateTransaksi(transaksi)
        
    override suspend fun saveItemTransaksi(item: ItemTransaksi): Long = 
        itemTransaksiDao.insertItemTransaksi(item)
        
    override suspend fun getItemsForTransaksi(transaksiId: Int): List<ItemTransaksi> = 
        itemTransaksiDao.getItemsByTransaksi(transaksiId)
}

class StokRepositoryImpl(private val stokLogDao: StokLogDao) : IStokRepository {
    override fun getAllStokLogs(): Flow<List<StokLog>> = 
        stokLogDao.getAllStokLogs()
        
    override suspend fun saveStokLog(stokLog: StokLog): Long = 
        stokLogDao.insertStokLog(stokLog)
}
