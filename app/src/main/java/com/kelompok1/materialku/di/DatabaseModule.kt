package com.kelompok1.materialku.di

import android.content.Context
import com.kelompok1.materialku.data.local.MaterialKuDatabase
import com.kelompok1.materialku.data.local.PreferencesDataStore
import com.kelompok1.materialku.data.local.dao.KategoriDao
import com.kelompok1.materialku.data.local.dao.MaterialDao
import com.kelompok1.materialku.data.local.dao.SatuanDao
import com.kelompok1.materialku.data.local.dao.StokLogDao
import com.kelompok1.materialku.data.local.dao.SupplierDao
import com.kelompok1.materialku.data.local.dao.UserDao
import com.kelompok1.materialku.util.PasswordHasher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        hasher: PasswordHasher
    ): MaterialKuDatabase = MaterialKuDatabase.build(
        context = context,
        hasher = hasher,
        seedScope = CoroutineScope(SupervisorJob())
    )

    @Provides
    fun provideUserDao(db: MaterialKuDatabase): UserDao = db.userDao()

    @Provides
    fun provideKategoriDao(db: MaterialKuDatabase): KategoriDao = db.kategoriDao()

    @Provides
    fun provideSatuanDao(db: MaterialKuDatabase): SatuanDao = db.satuanDao()

    @Provides
    fun provideSupplierDao(db: MaterialKuDatabase): SupplierDao = db.supplierDao()

    @Provides
    fun provideMaterialDao(db: MaterialKuDatabase): MaterialDao = db.materialDao()

    @Provides
    fun provideStokLogDao(db: MaterialKuDatabase): StokLogDao = db.stokLogDao()

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): PreferencesDataStore =
        PreferencesDataStore(context)
}
