package com.kelompok1.materialku.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kelompok1.materialku.data.local.dao.KategoriDao
import com.kelompok1.materialku.data.local.dao.MaterialDao
import com.kelompok1.materialku.data.local.dao.SatuanDao
import com.kelompok1.materialku.data.local.dao.SupplierDao
import com.kelompok1.materialku.data.local.dao.UserDao
import com.kelompok1.materialku.data.local.entity.KategoriEntity
import com.kelompok1.materialku.data.local.entity.MaterialEntity
import com.kelompok1.materialku.data.local.entity.SatuanEntity
import com.kelompok1.materialku.data.local.entity.SupplierEntity
import com.kelompok1.materialku.data.local.entity.UserEntity
import com.kelompok1.materialku.util.PasswordHasher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        KategoriEntity::class,
        SatuanEntity::class,
        SupplierEntity::class,
        MaterialEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MaterialKuDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun kategoriDao(): KategoriDao
    abstract fun satuanDao(): SatuanDao
    abstract fun supplierDao(): SupplierDao
    abstract fun materialDao(): MaterialDao

    companion object {
        private const val DB_NAME = "materialku.db"

        fun build(
            context: Context,
            hasher: PasswordHasher,
            seedScope: CoroutineScope
        ): MaterialKuDatabase {
            lateinit var instance: MaterialKuDatabase
            instance = Room.databaseBuilder(
                context.applicationContext,
                MaterialKuDatabase::class.java,
                DB_NAME
            )
                // Development-only. Belum ada data user yang perlu dipertahankan
                // antar versi schema; DB akan di-recreate dan re-seed otomatis.
                .fallbackToDestructiveMigration(true)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        seed(seedScope, hasher) { instance }
                    }

                    // Room memanggil ini (bukan onCreate) saat destructive
                    // migration menimpa DB lama. Tanpa override ini, tabel
                    // yang baru di-recreate akan kosong dan user nggak bisa
                    // login karena tabel `user` nihil.
                    override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                        super.onDestructiveMigration(db)
                        seed(seedScope, hasher) { instance }
                    }
                })
                .build()
            return instance
        }

        private fun seed(
            scope: CoroutineScope,
            hasher: PasswordHasher,
            dbProvider: () -> MaterialKuDatabase
        ) {
            scope.launch(Dispatchers.IO) {
                with(dbProvider()) {
                    userDao().insertAll(DatabaseSeeder.defaultUsers(hasher))
                    kategoriDao().insertAll(DatabaseSeeder.defaultKategoris())
                    satuanDao().insertAll(DatabaseSeeder.defaultSatuans())
                    supplierDao().insertAll(DatabaseSeeder.defaultSuppliers())
                }
            }
        }
    }
}
