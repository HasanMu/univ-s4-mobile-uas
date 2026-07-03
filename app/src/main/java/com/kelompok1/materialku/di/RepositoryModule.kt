package com.kelompok1.materialku.di

import com.kelompok1.materialku.data.repository.AuthRepositoryImpl
import com.kelompok1.materialku.domain.repository.IAuthRepository
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
}
