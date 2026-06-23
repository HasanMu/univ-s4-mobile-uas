package com.example.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.entities.*
import com.example.domain.repository.*
import com.example.util.HashUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

sealed class Screen {
    object Login : Screen()
    object Dashboard : Screen()
    object MaterialList : Screen()
    object TambahMaterial : Screen()
    object KategoriList : Screen()
    object POS : Screen()
    object Laporan : Screen()
    object Stok : Screen()
    object UserManagement : Screen()
    object Pengaturan : Screen()
}

class MainViewModel(
    private val userRepository: IUserRepository,
    private val materialRepository: IMaterialRepository,
    private val transaksiRepository: ITransaksiRepository,
    private val stokRepository: IStokRepository
) : ViewModel() {

    // Global navigation/UI State
    var currentScreen = mutableStateOf<Screen>(Screen.Login)
    var currentUser = mutableStateOf<User?>(null)
    var darkMode = mutableStateOf(false)

    // Form inputs and selection caching in VM
    var edittingMaterial = mutableStateOf<Material?>(null)

    // Observe data stream from repositories as stateflows
    val users: StateFlow<List<User>> = userRepository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val materials: StateFlow<List<Material>> = materialRepository.getAllMaterials()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val kategori: StateFlow<List<Kategori>> = materialRepository.getAllKategori()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val satuan: StateFlow<List<Satuan>> = materialRepository.getAllSatuan()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val supplier: StateFlow<List<Supplier>> = materialRepository.getAllSupplier()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transaksi: StateFlow<List<Transaksi>> = transaksiRepository.getAllTransaksi()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stokLogs: StateFlow<List<StokLog>> = stokRepository.getAllStokLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart management for POS
    val cart = mutableStateMapOf<Material, Int>()
    var searchQuery = mutableStateOf("")
    var posSearchQuery = mutableStateOf("")
    var selectedCategoryFilter = mutableStateOf<Int?>(null)

    // --- Authentication ---
    fun login(usernameInput: String, passwordInput: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val user = userRepository.getUserByUsername(usernameInput.trim().lowercase())
            if (user == null) {
                onResult(false, "Username tidak ditemukan")
                return@launch
            }
            if (!user.aktif) {
                onResult(false, "Akun Anda dinonaktifkan")
                return@launch
            }
            
            val inputHash = HashUtils.sha256(passwordInput)
            if (user.passwordHash == inputHash) {
                // Update login timestamp
                val updatedUser = user.copy(lastLogin = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                userRepository.saveUser(updatedUser)
                
                currentUser.value = updatedUser
                currentScreen.value = Screen.Dashboard
                onResult(true, "Sugeng rawuh, ${user.username}")
            } else {
                onResult(false, "Password salah")
            }
        }
    }

    fun logout() {
        currentUser.value = null
        cart.clear()
        currentScreen.value = Screen.Login
    }

    // --- User Management ---
    fun addUser(username: String, passwordPlain: String, role: String, onResult: (Boolean, String) -> Unit) {
        if (username.isBlank() || passwordPlain.isBlank()) {
            onResult(false, "Username dan Password tidak boleh kosong")
            return
        }
        viewModelScope.launch {
            val existing = userRepository.getUserByUsername(username.trim().lowercase())
            if (existing != null) {
                onResult(false, "Username sudah digunakan")
                return@launch
            }
            userRepository.saveUser(
                User(
                    username = username.trim().lowercase(),
                    passwordHash = HashUtils.sha256(passwordPlain),
                    passwordPlain = passwordPlain,
                    role = role,
                    aktif = true
                )
            )
            onResult(true, "User berhasil ditambahkan")
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            userRepository.deleteUser(user)
        }
    }

    fun updateUser(user: User, newUsername: String, passwordPlain: String?, role: String, onResult: (Boolean, String) -> Unit) {
        if (newUsername.isBlank()) {
            onResult(false, "Username tidak boleh kosong")
            return
        }
        viewModelScope.launch {
            val trimmedLowerUsername = newUsername.trim().lowercase()
            val existing = userRepository.getUserByUsername(trimmedLowerUsername)
            if (existing != null && existing.id != user.id) {
                onResult(false, "Username sudah digunakan oleh pengguna lain")
                return@launch
            }
            
            val finalPasswordHash = if (!passwordPlain.isNullOrBlank()) {
                HashUtils.sha256(passwordPlain)
            } else {
                user.passwordHash
            }

            val finalPasswordPlain = if (!passwordPlain.isNullOrBlank()) {
                passwordPlain
            } else {
                user.passwordPlain
            }
            
            val updatedUser = user.copy(
                username = trimmedLowerUsername,
                passwordHash = finalPasswordHash,
                passwordPlain = finalPasswordPlain,
                role = role
            )
            userRepository.saveUser(updatedUser)
            
            // If the updated user is the currently logged in user, refresh the current user state
            if (currentUser.value?.id == user.id) {
                currentUser.value = updatedUser
            }
            onResult(true, "User berhasil diperbarui")
        }
    }

    // --- Material Management ---
    fun addMaterial(
        kode: String,
        nama: String,
        harga: Double,
        stok: Int,
        minStok: Int,
        kategoriId: Int,
        satuanId: Int,
        supplierId: Int,
        onResult: (Boolean, String) -> Unit
    ) {
        if (kode.isBlank() || nama.isBlank() || harga <= 0.0 || stok < 0 || minStok < 0) {
            onResult(false, "Input tidak valid")
            return
        }
        viewModelScope.launch {
            val existing = materialRepository.getMaterialByKode(kode.trim().uppercase())
            if (existing != null) {
                onResult(false, "Kode material sudah terdaftar")
                return@launch
            }
            val materialId = materialRepository.saveMaterial(
                Material(
                    kode = kode.trim().uppercase(),
                    nama = nama.trim(),
                    hargaJual = harga,
                    stokSaat = stok,
                    minStok = minStok,
                    kategoriId = kategoriId,
                    satuanId = satuanId,
                    supplierId = supplierId
                )
            ).toInt()

            // Automatically record an initial entry log
            if (stok > 0) {
                stokRepository.saveStokLog(
                    StokLog(
                        materialId = materialId,
                        jenis = "MASUK",
                        qty = stok,
                        tanggal = LocalDate.now().toString(),
                        keterangan = "Stok awal material baru"
                    )
                )
            }
            onResult(true, "Material berhasil ditambahkan")
        }
    }

    fun updateMaterial(
        material: Material,
        nama: String,
        harga: Double,
        minStok: Int,
        kategoriId: Int,
        satuanId: Int,
        supplierId: Int,
        onResult: (Boolean, String) -> Unit
    ) {
        if (nama.isBlank() || harga <= 0.0 || minStok < 0) {
            onResult(false, "Input tidak valid")
            return
        }
        viewModelScope.launch {
            materialRepository.saveMaterial(
                material.copy(
                    nama = nama,
                    hargaJual = harga,
                    minStok = minStok,
                    kategoriId = kategoriId,
                    satuanId = satuanId,
                    supplierId = supplierId
                )
            )
            onResult(true, "Material berhasil diubah")
        }
    }

    fun deleteMaterial(material: Material) {
        viewModelScope.launch {
            materialRepository.deleteMaterial(material)
        }
    }

    // --- Kategori & Satuan & Supplier ---
    fun addKategori(nama: String, deskripsi: String) {
        if (nama.isBlank()) return
        viewModelScope.launch {
            materialRepository.saveKategori(
                Kategori(
                    nama = nama,
                    deskripsi = deskripsi,
                    createdAt = LocalDate.now().toString()
                )
            )
        }
    }

    fun deleteKategori(kategori: Kategori) {
        viewModelScope.launch {
            materialRepository.deleteKategori(kategori)
        }
    }

    fun addSatuan(nama: String, simbol: String) {
        if (nama.isBlank() || simbol.isBlank()) return
        viewModelScope.launch {
            materialRepository.saveSatuan(Satuan(nama = nama, simbol = simbol))
        }
    }

    fun deleteSatuan(satuan: Satuan) {
        viewModelScope.launch {
            materialRepository.deleteSatuan(satuan)
        }
    }

    fun addSupplier(nama: String, kontak: String, alamat: String) {
        if (nama.isBlank()) return
        viewModelScope.launch {
            materialRepository.saveSupplier(
                Supplier(
                    nama = nama,
                    kontak = kontak,
                    alamat = alamat
                )
            )
        }
    }

    fun deleteSupplier(supplier: Supplier) {
        viewModelScope.launch {
            materialRepository.deleteSupplier(supplier)
        }
    }

    // --- Stock Log Mutation ---
    fun recordStockMutation(material: Material, jenis: String, qty: Int, keterangan: String, onResult: (Boolean, String) -> Unit) {
        if (qty <= 0) {
            onResult(false, "Jumlah mutasi harus lebih besar dari 0")
            return
        }
        viewModelScope.launch {
            val m = materialRepository.getMaterialById(material.id) ?: return@launch
            val newStok = if (jenis == "MASUK") m.stokSaat + qty else m.stokSaat - qty
            
            if (newStok < 0) {
                onResult(false, "Mutasi gagal: Stok saat ini (${m.stokSaat}) kurang dari jumlah pengeluaran ($qty)")
                return@launch
            }

            // Save log
            stokRepository.saveStokLog(
                StokLog(
                    materialId = m.id,
                    jenis = jenis,
                    qty = qty,
                    tanggal = LocalDate.now().toString(),
                    keterangan = keterangan
                )
            )

            // Update item stock
            materialRepository.saveMaterial(m.copy(stokSaat = newStok))
            onResult(true, "Mutasi stok berhasil direkam")
        }
    }

    // --- POS Cart Operations ---
    fun addToCart(material: Material) {
        val qty = cart[material] ?: 0
        if (qty < material.stokSaat) {
            cart[material] = qty + 1
        }
    }

    fun removeFromCart(material: Material) {
        val qty = cart[material] ?: return
        if (qty > 1) {
            cart[material] = qty - 1
        } else {
            cart.remove(material)
        }
    }

    fun checkout(onResult: (Boolean, String, Transaksi?, List<ItemTransaksi>?) -> Unit) {
        if (cart.isEmpty()) {
            onResult(false, "Keranjang belanja kosong", null, null)
            return
        }
        viewModelScope.launch {
            // Validate availability of all materials
            for ((material, qty) in cart) {
                val dbMaterial = materialRepository.getMaterialById(material.id)
                if (dbMaterial == null) {
                    onResult(false, "Material '${material.nama}' tidak ditemukan", null, null)
                    return@launch
                }
                if (dbMaterial.stokSaat < qty) {
                    onResult(false, "Stok '${material.nama}' tidak cukup. Tersisa: ${dbMaterial.stokSaat}", null, null)
                    return@launch
                }
            }

            // Perform checkout atomic transaction simulation
            val total = cart.entries.sumOf { it.key.hargaJual * it.value }
            val dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            val cleanDate = LocalDate.now().toString().replace("-", "")
            val noFaktur = "TRX-$cleanDate-${(1000..9999).random()}"
            
            val txId = transaksiRepository.saveTransaksi(
                Transaksi(
                    noFaktur = noFaktur,
                    tanggal = dateStr,
                    totalHarga = total,
                    status = "SELESAI",
                    userId = currentUser.value?.id ?: 2 // Default to budget budi if none
                )
            ).toInt()

            val listItemsSaved = mutableListOf<ItemTransaksi>()

            for ((material, qty) in cart) {
                val currentDbItem = materialRepository.getMaterialById(material.id)!!
                val newStok = currentDbItem.stokSaat - qty
                
                // 1. Deduct Stock
                materialRepository.saveMaterial(currentDbItem.copy(stokSaat = newStok))
                
                // 2. Create item transaction
                val subtotal = currentDbItem.hargaJual * qty
                val itItem = ItemTransaksi(
                    transaksiId = txId,
                    materialId = currentDbItem.id,
                    qty = qty,
                    hargaSatuan = currentDbItem.hargaJual,
                    subtotal = subtotal
                )
                transaksiRepository.saveItemTransaksi(itItem)
                listItemsSaved.add(itItem)

                // 3. Create Stock Log
                stokRepository.saveStokLog(
                    StokLog(
                        materialId = currentDbItem.id,
                        jenis = "KELUAR",
                        qty = qty,
                        tanggal = LocalDate.now().toString(),
                        keterangan = "POS Penjualan: Faktur $noFaktur"
                    )
                )
            }

            val savedTx = Transaksi(id = txId, noFaktur = noFaktur, tanggal = dateStr, totalHarga = total, status = "SELESAI", userId = currentUser.value?.id ?: 2)
            cart.clear()
            onResult(true, "Transaksi berhasil disimpan", savedTx, listItemsSaved)
        }
    }
}

class MainViewModelFactory(
    private val userRepository: IUserRepository,
    private val materialRepository: IMaterialRepository,
    private val transaksiRepository: ITransaksiRepository,
    private val stokRepository: IStokRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(userRepository, materialRepository, transaksiRepository, stokRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
