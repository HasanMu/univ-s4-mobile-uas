package com.kelompok1.materialku.data.repository

import com.kelompok1.materialku.data.local.PreferencesDataStore
import com.kelompok1.materialku.data.local.dao.UserDao
import com.kelompok1.materialku.domain.model.User
import com.kelompok1.materialku.domain.repository.IAuthRepository
import com.kelompok1.materialku.domain.repository.SessionSnapshot
import com.kelompok1.materialku.util.PasswordHasher
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val prefs: PreferencesDataStore,
    private val hasher: PasswordHasher
) : IAuthRepository {

    override suspend fun login(username: String, password: String): User? {
        val entity = userDao.findByUsername(username.trim()) ?: return null
        if (!entity.aktif) return null
        if (!hasher.verify(password, entity.passwordHash)) return null
        val now = LocalDateTime.now()
        userDao.updateLastLogin(entity.id, now)
        return entity.toDomain().copy(lastLogin = now)
    }

    override suspend fun saveSession(user: User) {
        prefs.saveSession(user.id, user.username, user.role)
    }

    override suspend fun clearSession() {
        prefs.clearSession()
    }

    override fun observeSession(): Flow<SessionSnapshot?> = prefs.session
}
