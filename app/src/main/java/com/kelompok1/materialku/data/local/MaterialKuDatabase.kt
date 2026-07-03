package com.kelompok1.materialku.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kelompok1.materialku.data.local.dao.UserDao
import com.kelompok1.materialku.data.local.entity.UserEntity
import com.kelompok1.materialku.util.PasswordHasher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MaterialKuDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

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
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        seedScope.launch(Dispatchers.IO) {
                            instance.userDao().insertAll(DatabaseSeeder.defaultUsers(hasher))
                        }
                    }
                })
                .build()
            return instance
        }
    }
}
