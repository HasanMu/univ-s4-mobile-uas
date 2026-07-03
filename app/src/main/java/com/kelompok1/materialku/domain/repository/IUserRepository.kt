package com.kelompok1.materialku.domain.repository

import com.kelompok1.materialku.domain.model.User
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    fun observeAll(): Flow<List<User>>
    suspend fun findById(id: Int): User?
    suspend fun findByUsername(username: String): User?
    suspend fun insert(user: User): Long
    suspend fun update(user: User)
    suspend fun delete(id: Int)
    suspend fun count(): Int
}
