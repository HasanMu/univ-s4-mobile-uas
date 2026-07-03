package com.kelompok1.materialku.domain.repository

import com.kelompok1.materialku.domain.model.User
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    /**
     * Verifikasi kredensial. Return User kalau valid, null kalau username tidak ada,
     * password salah, atau user tidak aktif.
     */
    suspend fun login(username: String, password: String): User?

    /**
     * Simpan sesi (userId, username, role) ke DataStore.
     */
    suspend fun saveSession(user: User)

    /**
     * Hapus sesi.
     */
    suspend fun clearSession()

    /**
     * Stream sesi tersimpan. Emit null kalau belum login / setelah logout.
     */
    fun observeSession(): Flow<SessionSnapshot?>
}

data class SessionSnapshot(
    val userId: Int,
    val username: String,
    val role: com.kelompok1.materialku.domain.model.RoleEnum
)
