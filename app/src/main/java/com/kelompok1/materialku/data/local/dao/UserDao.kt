package com.kelompok1.materialku.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kelompok1.materialku.data.local.entity.UserEntity
import java.time.LocalDateTime
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM user ORDER BY username ASC")
    fun observeAll(): Flow<List<UserEntity>>

    @Query("SELECT * FROM user WHERE username = :username LIMIT 1")
    suspend fun findByUsername(username: String): UserEntity?

    @Query("SELECT * FROM user WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): UserEntity?

    @Query("UPDATE user SET lastLogin = :lastLogin WHERE id = :id")
    suspend fun updateLastLogin(id: Int, lastLogin: LocalDateTime)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(users: List<UserEntity>): List<Long>

    @Update
    suspend fun update(user: UserEntity)

    @Query("DELETE FROM user WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT COUNT(*) FROM user")
    suspend fun count(): Int
}
