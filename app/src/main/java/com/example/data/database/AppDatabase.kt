package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.*
import com.example.data.entities.*
import com.example.util.HashUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        Kategori::class,
        Satuan::class,
        Supplier::class,
        Material::class,
        Transaksi::class,
        ItemTransaksi::class,
        StokLog::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun kategoriDao(): KategoriDao
    abstract fun satuanDao(): SatuanDao
    abstract fun supplierDao(): SupplierDao
    abstract fun materialDao(): MaterialDao
    abstract fun transaksiDao(): TransaksiDao
    abstract fun itemTransaksiDao(): ItemTransaksiDao
    abstract fun stokLogDao(): StokLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "materialku.db"
                )
                .addCallback(DatabaseSeederCallback(context.applicationContext))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseSeederCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                val database = getDatabase(context)
                
                // 1. Seed Users
                val userDao = database.userDao()
                userDao.insertUser(User(id = 1, username = "admin", passwordHash = HashUtils.sha256("admin123"), passwordPlain = "admin123", role = "ROLE_ADMIN", aktif = true))
                userDao.insertUser(User(id = 2, username = "kasir", passwordHash = HashUtils.sha256("kasir123"), passwordPlain = "kasir123", role = "ROLE_KASIR", aktif = true))
                userDao.insertUser(User(id = 3, username = "gudang", passwordHash = HashUtils.sha256("gudang123"), passwordPlain = "gudang123", role = "ROLE_GUDANG", aktif = true))
                userDao.insertUser(User(id = 4, username = "manager", passwordHash = HashUtils.sha256("manager123"), passwordPlain = "manager123", role = "ROLE_MANAGER", aktif = true))

                // 2. Seed Kategori
                val kategoriDao = database.kategoriDao()
                kategoriDao.insertKategori(Kategori(id = 1, nama = "Bahan", deskripsi = "Bahan pokok bangunan", createdAt = "2025-05-19", aktif = true))
                kategoriDao.insertKategori(Kategori(id = 2, nama = "Alat", deskripsi = "Alat pertukangan dan listrik", createdAt = "2025-05-19", aktif = true))

                // 3. Seed Satuan
                val satuanDao = database.satuanDao()
                satuanDao.insertSatuan(Satuan(id = 1, nama = "sak", simbol = "sak"))
                satuanDao.insertSatuan(Satuan(id = 2, nama = "kaleng", simbol = "kaleng"))
                satuanDao.insertSatuan(Satuan(id = 3, nama = "unit", simbol = "unit"))

                // 4. Seed Supplier
                val supplierDao = database.supplierDao()
                supplierDao.insertSupplier(Supplier(id = 1, nama = "Toko Maju", kontak = "08123456789", alamat = "Bandung", aktif = true))
                supplierDao.insertSupplier(Supplier(id = 2, nama = "Sumber Teknik", kontak = "08123456700", alamat = "Jakarta", aktif = true))

                // 5. Seed Material (Matches screenshot names and stock levels!)
                val materialDao = database.materialDao()
                materialDao.insertMaterial(Material(id = 1, kode = "MAT-001", nama = "Semen Tiga Roda", hargaJual = 65000.0, stokSaat = 50, minStok = 10, kategoriId = 1, satuanId = 1, supplierId = 1))
                materialDao.insertMaterial(Material(id = 2, kode = "MAT-002", nama = "Cat Nippon Paint", hargaJual = 85000.0, stokSaat = 5, minStok = 10, kategoriId = 1, satuanId = 2, supplierId = 1)) // CRITICAL
                materialDao.insertMaterial(Material(id = 3, kode = "MAT-003", nama = "Bor Listrik", hargaJual = 350000.0, stokSaat = 8, minStok = 3, kategoriId = 2, satuanId = 3, supplierId = 2))

                // 6. Seed mock transactions to populate reports
                val transaksiDao = database.transaksiDao()
                val itemTransaksiDao = database.itemTransaksiDao()
                
                // Match Laporan screenshot ("TRX-20250519-001", Rp 280.000, SELESAI, 19 Mei)
                transaksiDao.insertTransaksi(Transaksi(id = 1, noFaktur = "TRX-20250519-001", tanggal = "2026-06-23 10:30", totalHarga = 280000.0, status = "SELESAI", userId = 2))
                itemTransaksiDao.insertItemTransaksi(ItemTransaksi(id = 1, transaksiId = 1, materialId = 3, qty = 1, hargaSatuan = 350000.0, subtotal = 350000.0)) // simple mock
                
                 transaksiDao.insertTransaksi(Transaksi(id = 2, noFaktur = "TRX-20250519-002", tanggal = "2026-06-23 11:00", totalHarga = 150000.0, status = "DRAFT", userId = 2))
            }
        }
    }
}
