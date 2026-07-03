package com.kelompok1.materialku.data.repository

import com.kelompok1.materialku.data.local.dao.UserDao
import com.kelompok1.materialku.data.local.entity.UserEntity
import com.kelompok1.materialku.domain.model.User
import com.kelompok1.materialku.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val dao: UserDao
) : IUserRepository {

    override fun observeAll(): Flow<List<User>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun findById(id: Int): User? = dao.findById(id)?.toDomain()

    override suspend fun findByUsername(username: String): User? =
        dao.findByUsername(username)?.toDomain()

    override suspend fun insert(user: User): Long =
        dao.insert(UserEntity.fromDomain(user))

    override suspend fun update(user: User) {
        dao.update(UserEntity.fromDomain(user))
    }

    override suspend fun delete(id: Int) {
        dao.delete(id)
    }

    override suspend fun count(): Int = dao.count()
}
