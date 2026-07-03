package com.kelompok1.materialku.domain.model

import java.time.LocalDateTime

data class User(
    val id: Int = 0,
    val username: String,
    val passwordHash: String,
    val role: RoleEnum,
    val aktif: Boolean = true,
    val lastLogin: LocalDateTime? = null
) {
    fun hasRole(r: RoleEnum): Boolean = role == r
    fun isAktif(): Boolean = aktif
}
