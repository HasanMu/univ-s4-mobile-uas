package com.kelompok1.materialku.di

import com.kelompok1.materialku.data.repository.AuthRepositoryImpl
import com.kelompok1.materialku.data.repository.KategoriRepositoryImpl
import com.kelompok1.materialku.data.repository.MaterialRepositoryImpl
import com.kelompok1.materialku.data.repository.SatuanRepositoryImpl
import com.kelompok1.materialku.data.repository.SupplierRepositoryImpl
import com.kelompok1.materialku.data.repository.UserRepositoryImpl
import com.kelompok1.materialku.domain.repository.IAuthRepository
import com.kelompok1.materialku.domain.repository.IKategoriRepository
import com.kelompok1.materialku.domain.repository.IMaterialRepository
import com.kelompok1.materialku.domain.repository.ISatuanRepository
import com.kelompok1.materialku.domain.repository.ISupplierRepository
import com.kelompok1.materialku.domain.repository.IUserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): IAuthRepository

    @Binds
    @Singleton
    abstract fun bindKategoriRepository(impl: KategoriRepositoryImpl): IKategoriRepository

    @Binds
    @Singleton
    abstract fun bindSatuanRepository(impl: SatuanRepositoryImpl): ISatuanRepository

    @Binds
    @Singleton
    abstract fun bindSupplierRepository(impl: SupplierRepositoryImpl): ISupplierRepository

    @Binds
    @Singleton
    abstract fun bindMaterialRepository(impl: MaterialRepositoryImpl): IMaterialRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): IUserRepository
}
