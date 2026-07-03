package com.kelompok1.materialku.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kelompok1.materialku.data.local.entity.UserEntity
import java.time.LocalDateTime

@Dao
interface UserDao {

    @Query("SELECT * FROM user WHERE username = :username LIMIT 1")
    suspend fun findByUsername(username: String): UserEntity?

    @Query("SELECT * FROM user WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): UserEntity?

    @Query("UPDATE user SET lastLogin = :lastLogin WHERE id = :id")
    suspend fun updateLastLogin(id: Int, lastLogin: LocalDateTime)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(users: List<UserEntity>): List<Long>

    @Query("SELECT COUNT(*) FROM user")
    suspend fun count(): Int
}
