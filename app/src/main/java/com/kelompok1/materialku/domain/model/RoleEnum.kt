package com.kelompok1.materialku.domain.model

enum class RoleEnum {
    ROLE_ADMIN,
    ROLE_KASIR,
    ROLE_GUDANG,
    ROLE_MANAGER;

    fun displayName(): String = when (this) {
        ROLE_ADMIN -> "Admin"
        ROLE_KASIR -> "Kasir"
        ROLE_GUDANG -> "Gudang"
        ROLE_MANAGER -> "Manager"
    }
}
