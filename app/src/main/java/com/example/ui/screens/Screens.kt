package com.example.ui.screens

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.entities.*
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.Screen
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale

// --- Color Constants matching PRD branding ---
val SlateDark = Color(0xFF2B2D31)
val AccentYellow = Color(0xFFFFA000)
val SoftYellowBg = Color(0xFFFFF8E1)
val SoftPurpleBg = Color(0xFFF3E5F5)
val AccordPurple = Color(0xFF7B1FA2)
val SoftTealBg = Color(0xFFE0F2F1)
val AccordTeal = Color(0xFF00796B)
val SoftOrangeBg = Color(0xFFFFE0B2)
val AccordOrange = Color(0xFFF57C00)
val SoftBlueBg = Color(0xFFECEFF1)
val AccordBlue = Color(0xFF455A64)
val CriticalRed = Color(0xFFFFEBEE)
val TextCriticalRed = Color(0xFFC62828)
val OkGreen = Color(0xFFE8F5E9)
val TextOkGreen = Color(0xFF2E7D32)

// Currency Formatter Helper
fun formatRupiah(value: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return formatter.format(value).replace("Rp", "Rp ").replace(",00", "")
}

@Composable
fun AppContent(viewModel: MainViewModel) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen
    val user by viewModel.currentUser

    // Light/Dark Color Scheme wrapper
    val appBg = if (viewModel.darkMode.value) Color(0xFF121212) else Color(0xFFF5F6FA)
    val textPrimary = if (viewModel.darkMode.value) Color.White else Color(0xFF212121)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = appBg
    ) {
        Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
            when (screen) {
                is Screen.Login -> LoginScreen(viewModel)
                is Screen.Dashboard -> DashboardScreen(viewModel)
                is Screen.MaterialList -> MaterialListScreen(viewModel)
                is Screen.TambahMaterial -> TambahMaterialScreen(viewModel)
                is Screen.KategoriList -> KategoriScreen(viewModel)
                is Screen.POS -> POSScreen(viewModel)
                is Screen.Laporan -> LaporanScreen(viewModel)
                is Screen.Stok -> StokScreen(viewModel)
                is Screen.UserManagement -> UserManagementScreen(viewModel)
            }
        }
    }
}

// ==========================================
// 1. LOGIN SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1F22))
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Large Box Logo
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(AccentYellow, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = "Logo",
                tint = Color.Black,
                modifier = Modifier.size(54.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "MATERIALKU",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            letterSpacing = 1.5.sp
        )

        Text(
            text = "SISTEM MANAJEMEN MATERIAL",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.LightGray,
            letterSpacing = 1.sp
        )

        Divider(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .width(48.dp),
            color = AccentYellow,
            thickness = 3.dp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Username Input
        Text(
            text = "USERNAME",
            fontWeight = FontWeight.Bold,
            color = Color.LightGray,
            fontSize = 11.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            placeholder = { Text("Masukkan username...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "User", tint = Color.LightGray) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = AccentYellow,
                unfocusedBorderColor = Color.DarkGray,
                focusedContainerColor = Color(0xFF2B2D31),
                unfocusedContainerColor = Color(0xFF2B2D31)
            ),
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Password Input
        Text(
            text = "PASSWORD",
            fontWeight = FontWeight.Bold,
            color = Color.LightGray,
            fontSize = 11.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Masukkan password...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Lock", tint = Color.LightGray) },
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Info else Icons.Default.Lock,
                        contentDescription = "Toggle Password",
                        tint = Color.LightGray
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = AccentYellow,
                unfocusedBorderColor = Color.DarkGray,
                focusedContainerColor = Color(0xFF2B2D31),
                unfocusedContainerColor = Color(0xFF2B2D31)
            ),
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Button Masuk
        Button(
            onClick = {
                viewModel.login(username, password) { success, msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = AccentYellow),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "MASUK",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(50.dp))
        Text(
            text = "v1.0.0",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

// ==========================================
// 2. DASHBOARD SCREEN
// ==========================================
@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val currentUser by viewModel.currentUser
    val materials by viewModel.materials.collectAsState()
    val transactions by viewModel.transaksi.collectAsState()
    val ctx = LocalContext.current

    // Live Metrics Calculations
    val countMaterial = materials.size
    val countTransactions = transactions.size
    val countCritical = materials.count { it.isStokKritis() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (viewModel.darkMode.value) Color(0xFF1E1F22) else Color(0xFFF5F6FA))
    ) {
        // Upper Black Header Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1F22))
                .padding(top = 40.dp, start = 24.dp, end = 24.dp, bottom = 28.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Selamat datang,",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Text(
                        text = currentUser?.username?.replaceFirstChar { it.uppercase() } ?: "User",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Admin Badge / Settings Icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(AccentYellow, RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = currentUser?.role?.replace("ROLE_", "") ?: "USER",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { viewModel.darkMode.value = !viewModel.darkMode.value }) {
                        Icon(
                            imageVector = if (viewModel.darkMode.value) Icons.Default.Info else Icons.Default.Refresh,
                            contentDescription = "Theme",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stat Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(modifier = Modifier.weight(1f), title = "Material", count = countMaterial.toString(), valueColor = Color.White)
                StatCard(modifier = Modifier.weight(1f), title = "Transaksi", count = countTransactions.toString(), valueColor = Color.White)
                StatCard(modifier = Modifier.weight(1f), title = "Kritis", count = countCritical.toString(), valueColor = if (countCritical > 0) Color.Red else Color.White)
            }
        }

        // Lower Grid Options
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "MENU UTAMA",
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Grid items
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MenuTile(
                        modifier = Modifier.weight(1f),
                        title = "Material",
                        icon = Icons.Default.Build,
                        iconBg = SoftYellowBg,
                        iconTint = AccentYellow,
                        onClick = { viewModel.currentScreen.value = Screen.MaterialList }
                    )
                    MenuTile(
                        modifier = Modifier.weight(1f),
                        title = "Kategori",
                        icon = Icons.Default.Lock,
                        iconBg = SoftPurpleBg,
                        iconTint = AccordPurple,
                        onClick = { viewModel.currentScreen.value = Screen.KategoriList }
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MenuTile(
                        modifier = Modifier.weight(1f),
                        title = "POS",
                        icon = Icons.Default.ShoppingCart,
                        iconBg = SoftTealBg,
                        iconTint = AccordTeal,
                        onClick = { viewModel.currentScreen.value = Screen.POS }
                    )
                    MenuTile(
                        modifier = Modifier.weight(1f),
                        title = "Laporan",
                        icon = Icons.Default.Star,
                        iconBg = SoftOrangeBg,
                        iconTint = AccordOrange,
                        onClick = { viewModel.currentScreen.value = Screen.Laporan }
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MenuTile(
                        modifier = Modifier.weight(1f),
                        title = "Stok",
                        icon = Icons.Default.Home,
                        iconBg = SoftBlueBg,
                        iconTint = AccordBlue,
                        onClick = { viewModel.currentScreen.value = Screen.Stok }
                    )
                    
                    // Admin Only Module
                    if (currentUser?.role == "ROLE_ADMIN") {
                        MenuTile(
                            modifier = Modifier.weight(1f),
                            title = "User",
                            icon = Icons.Default.Person,
                            iconBg = SoftYellowBg,
                            iconTint = AccentYellow,
                            onClick = { viewModel.currentScreen.value = Screen.UserManagement }
                        )
                    } else {
                        Box(modifier = Modifier.weight(1f)) // Empty block for alignment
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Logout Button
            OutlinedButton(
                onClick = { viewModel.logout() },
                border = BorderStroke(1.dp, Color.LightGray),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Keluar",
                        tint = Color(0xFFC62828)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "KELUAR",
                        color = Color(0xFFC62828),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, title: String, count: String, valueColor: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2B2D31)),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.height(84.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = count, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = valueColor)
            Text(text = title, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun MenuTile(modifier: Modifier = Modifier, title: String, icon: ImageVector, iconBg: Color, iconTint: Color, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .height(115.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(iconBg, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(20.dp))
                }
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Go",
                    tint = Color.LightGray,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

// ==========================================
// 3. MATERIAL SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialListScreen(viewModel: MainViewModel) {
    val materials by viewModel.materials.collectAsState()
    val categories by viewModel.kategori.collectAsState()
    val units by viewModel.satuan.collectAsState()
    val suppliers by viewModel.supplier.collectAsState()

    var searchQuery by viewModel.searchQuery
    var selectedCategoryFilter by viewModel.selectedCategoryFilter

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // App bar top
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 8.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.currentScreen.value = Screen.Dashboard }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Material",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray)
        }

        // Search Bar field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari material...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(12.dp)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        // Filters categories ScrollRow
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterTabButton(
                name = "Semua",
                selected = selectedCategoryFilter == null,
                onClick = { selectedCategoryFilter = null }
            )
            categories.forEach { cat ->
                FilterTabButton(
                    name = cat.nama,
                    selected = selectedCategoryFilter == cat.id,
                    onClick = { selectedCategoryFilter = cat.id }
                )
            }
        }

        val filteredList = materials.filter { item ->
            val matchesCategory = selectedCategoryFilter == null || item.kategoriId == selectedCategoryFilter
            val matchesQuery = item.nama.lowercase().contains(searchQuery.lowercase()) ||
                    item.kode.lowercase().contains(searchQuery.lowercase())
            matchesCategory && matchesQuery
        }

        // Materials Recycler View Simulation
        Box(modifier = Modifier.weight(1f)) {
            if (filteredList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tidak ada material", color = Color.Gray)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    filteredList.forEach { mat ->
                        val catName = categories.find { it.id == mat.kategoriId }?.nama ?: "Kategori"
                        val satName = units.find { it.id == mat.satuanId }?.nama ?: "unit"
                        val supName = suppliers.find { it.id == mat.supplierId }?.nama ?: "Supplier"

                        MaterialCard(
                            material = mat,
                            categoryName = catName,
                            unitName = satName,
                            supplierName = supName,
                            onEdit = {
                                viewModel.edittingMaterial.value = mat
                                viewModel.currentScreen.value = Screen.TambahMaterial
                            },
                            onDelete = {
                                viewModel.deleteMaterial(mat)
                            }
                        )
                    }
                }
            }

            // FAB
            FloatingActionButton(
                onClick = {
                    viewModel.edittingMaterial.value = null
                    viewModel.currentScreen.value = Screen.TambahMaterial
                },
                containerColor = AccentYellow,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Material", tint = Color.Black)
            }
        }
    }
}

@Composable
fun FilterTabButton(name: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                color = if (selected) AccentYellow else Color.White,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                color = if (selected) AccentYellow else Color.LightGray,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = name,
            fontWeight = FontWeight.Bold,
            color = if (selected) Color.Black else Color.Gray,
            fontSize = 14.sp
        )
    }
}

@Composable
fun MaterialCard(
    material: Material,
    categoryName: String,
    unitName: String,
    supplierName: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isCritical = material.isStokKritis()

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = material.nama,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "$categoryName • $supplierName",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                // AMAN / KRITIS Status Badge
                Box(
                    modifier = Modifier
                        .background(
                            if (isCritical) CriticalRed else OkGreen,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isCritical) "KRITIS" else "AMAN",
                        fontWeight = FontWeight.Bold,
                        color = if (isCritical) TextCriticalRed else TextOkGreen,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Stok", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        text = "${material.stokSaat} $unitName",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCritical) TextCriticalRed else Color.Black
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Harga", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        text = "${formatRupiah(material.hargaJual)} / $unitName",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentYellow
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color.LightGray, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(4.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray)
                }
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFC62828))
                }
            }
        }
    }
}

// ==========================================
// 4. ADD / EDIT MATERIAL FORM SCREEN
// ==========================================
@Composable
fun TambahMaterialScreen(viewModel: MainViewModel) {
    val target = viewModel.edittingMaterial.value
    val isEdit = target != null

    val categories by viewModel.kategori.collectAsState()
    val units by viewModel.satuan.collectAsState()
    val suppliers by viewModel.supplier.collectAsState()
    val ctx = LocalContext.current

    var kode by remember { mutableStateOf(target?.kode ?: "") }
    var nama by remember { mutableStateOf(target?.nama ?: "") }
    var hargaRow by remember { mutableStateOf(target?.hargaJual?.toInt()?.toString() ?: "0") }
    var stokRow by remember { mutableStateOf(target?.stokSaat?.toString() ?: "0") }
    var minStokRow by remember { mutableStateOf(target?.minStok?.toString() ?: "0") }

    var selectedKategoriId by remember { mutableStateOf(target?.kategoriId ?: categories.firstOrNull()?.id ?: 1) }
    var selectedSatuanId by remember { mutableStateOf(target?.satuanId ?: units.firstOrNull()?.id ?: 1) }
    var selectedSupplierId by remember { mutableStateOf(target?.supplierId ?: suppliers.firstOrNull()?.id ?: 1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 8.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.currentScreen.value = Screen.MaterialList }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = if (isEdit) "Ubah Material" else "Tambah Material",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Material code
            FormLabel(text = "KODE MATERIAL")
            OutlinedTextField(
                value = kode,
                onValueChange = { if (!isEdit) kode = it },
                enabled = !isEdit,
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Nama material
            FormLabel(text = "NAMA MATERIAL *")
            OutlinedTextField(
                value = nama,
                onValueChange = { nama = it },
                placeholder = { Text("Masukkan nama material...") },
                leadingIcon = { Icon(Icons.Default.Build, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Harga Jual
            FormLabel(text = "HARGA JUAL (Rp) *")
            OutlinedTextField(
                value = hargaRow,
                onValueChange = { hargaRow = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Initial and Critical Stock
            if (!isEdit) {
                FormLabel(text = "STOK AWAL")
                OutlinedTextField(
                    value = stokRow,
                    onValueChange = { stokRow = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            FormLabel(text = "STOK MINIMUM (Titik Kritis) *")
            OutlinedTextField(
                value = minStokRow,
                onValueChange = { minStokRow = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Select Dropdowns
            FormLabel(text = "KATEGORI")
            SimpleDropdownSelection(
                options = categories.map { it.id to it.nama },
                selectedId = selectedKategoriId,
                onSelect = { selectedKategoriId = it }
            )

            FormLabel(text = "SATUAN")
            SimpleDropdownSelection(
                options = units.map { it.id to it.nama },
                selectedId = selectedSatuanId,
                onSelect = { selectedSatuanId = it }
            )

            FormLabel(text = "SUPPLIER")
            SimpleDropdownSelection(
                options = suppliers.map { it.id to it.nama },
                selectedId = selectedSupplierId,
                onSelect = { selectedSupplierId = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val hrg = hargaRow.toDoubleOrNull() ?: 0.0
                    val stk = stokRow.toIntOrNull() ?: 0
                    val minS = minStokRow.toIntOrNull() ?: 0

                    if (isEdit) {
                        viewModel.updateMaterial(
                            target!!,
                            nama,
                            hrg,
                            minS,
                            selectedKategoriId,
                            selectedSatuanId,
                            selectedSupplierId
                        ) { ok, msg ->
                            Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
                            if (ok) viewModel.currentScreen.value = Screen.MaterialList
                        }
                    } else {
                        viewModel.addMaterial(
                            kode,
                            nama,
                            hrg,
                            stk,
                            minS,
                            selectedKategoriId,
                            selectedSatuanId,
                            selectedSupplierId
                        ) { ok, msg ->
                            Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
                            if (ok) viewModel.currentScreen.value = Screen.MaterialList
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentYellow),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "SIMPAN",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FormLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        letterSpacing = 1.sp
    )
}

@Composable
fun SimpleDropdownSelection(options: List<Pair<Int, String>>, selectedId: Int, onSelect: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val currentText = options.find { it.first == selectedId }?.second ?: "Pilih"

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = currentText, color = Color.Black)
                Icon(imageVector = Icons.Default.Info, contentDescription = "Drop")
            }
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.second) },
                    onClick = {
                        onSelect(item.first)
                        expanded = false
                    }
                )
            }
        }
    }
}

// ==========================================
// 5. KATEGORI SCREEN
// ==========================================
@Composable
fun KategoriScreen(viewModel: MainViewModel) {
    val categories by viewModel.kategori.collectAsState()
    var nameInput by remember { mutableStateOf("") }
    var descInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 8.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.currentScreen.value = Screen.Dashboard }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Kategori",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Add form card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Tambah Kategori Baru", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    placeholder = { Text("Nama Kategori...") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descInput,
                    onValueChange = { descInput = it },
                    placeholder = { Text("Deskripsi...") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        if (nameInput.isNotBlank()) {
                            viewModel.addKategori(nameInput, descInput)
                            nameInput = ""
                            descInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentYellow),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Tambah", color = Color.Black)
                }
            }
        }

        // Categories List
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            categories.forEach { cat ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = cat.nama, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = cat.deskripsi, fontSize = 13.sp, color = Color.Gray)
                        }
                        IconButton(onClick = { viewModel.deleteKategori(cat) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 6. POS (POINT OF SALE) SCREEN
// ==========================================
@Composable
fun POSScreen(viewModel: MainViewModel) {
    val materials by viewModel.materials.collectAsState()
    val categories by viewModel.kategori.collectAsState()
    val cart = viewModel.cart
    val ctx = LocalContext.current

    var posSearch by viewModel.posSearchQuery
    var selectedCatId by remember { mutableStateOf<Int?>(null) }
    
    // Bottom sheet dialog states for Invoice Receipt stub
    var showReceipt by remember { mutableStateOf(false) }
    var currentReceiptHeader by remember { mutableStateOf<Transaksi?>(null) }
    var currentReceiptItems by remember { mutableStateOf<List<ItemTransaksi>?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 8.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.currentScreen.value = Screen.Dashboard }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Transaksi Baru",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // POS Search
        OutlinedTextField(
            value = posSearch,
            onValueChange = { posSearch = it },
            placeholder = { Text("Cari material...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        // Filters horizontal row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterTabButton(name = "Semua", selected = selectedCatId == null, onClick = { selectedCatId = null })
            categories.forEach { cat ->
                FilterTabButton(name = cat.nama, selected = selectedCatId == cat.id, onClick = { selectedCatId = cat.id })
            }
        }

        val filteredMaterials = materials.filter { item ->
            val matchesCategory = selectedCatId == null || item.kategoriId == selectedCatId
            val matchesQuery = item.nama.lowercase().contains(posSearch.lowercase()) ||
                    item.kode.lowercase().contains(posSearch.lowercase())
            matchesCategory && matchesQuery
        }

        // Materials Selection list
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            filteredMaterials.forEach { mat ->
                val quantityInCart = cart[mat] ?: 0

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = mat.nama, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = "Stok: ${mat.stokSaat}", fontSize = 12.sp, color = if (mat.isStokKritis()) Color.Red else Color.Gray)
                            Text(text = formatRupiah(mat.hargaJual), fontSize = 14.sp, color = AccentYellow, fontWeight = FontWeight.Bold)
                        }

                        // Cart buttons adding/removing
                        if (quantityInCart > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { viewModel.removeFromCart(mat) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Minus", tint = Color.Gray)
                                }
                                Text(
                                    text = quantityInCart.toString(),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                IconButton(onClick = { viewModel.addToCart(mat) }, enabled = quantityInCart < mat.stokSaat) {
                                    Icon(Icons.Default.Add, contentDescription = "Plus", tint = AccentYellow)
                                }
                            }
                        } else {
                            Button(
                                onClick = { viewModel.addToCart(mat) },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentYellow),
                                enabled = mat.stokSaat > 0
                            ) {
                                Text("+ Tambah", color = Color.Black)
                            }
                        }
                    }
                }
            }
        }

        // POS Total Bottom Checkout footer
        if (cart.isNotEmpty()) {
            val totalItems = cart.values.sum()
            val totalPrice = cart.entries.sumOf { it.key.hargaJual * it.value }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1F22)),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "$totalItems item", color = Color.LightGray, fontSize = 13.sp)
                        Text(text = formatRupiah(totalPrice), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            viewModel.checkout { ok, msg, finalTx, listItems ->
                                if (ok) {
                                    currentReceiptHeader = finalTx
                                    currentReceiptItems = listItems
                                    showReceipt = true
                                }
                                Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentYellow),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Checkout", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Receipt Bottom Sheet Dialogue Simulation
    if (showReceipt && currentReceiptHeader != null) {
        Dialog(onDismissRequest = { showReceipt = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "M A T E R I A L K U",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Toko Bahan Bangunan Unggulan\nTelp: 08123456789\nFaksi: Bandung",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Divider(color = Color.DarkGray, thickness = 1.dp)

                    // Stamp invoice details
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("No. Faktur:", fontSize = 11.sp, color = Color.Gray)
                        Text(currentReceiptHeader!!.noFaktur, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tanggal:", fontSize = 11.sp, color = Color.Gray)
                        Text(currentReceiptHeader!!.tanggal, fontSize = 11.sp, color = Color.Black)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Kasir:", fontSize = 11.sp, color = Color.Gray)
                        Text(viewModel.currentUser.value?.username ?: "Kasir", fontSize = 11.sp, color = Color.Black)
                    }

                    Divider(color = Color.LightGray, thickness = 0.5.dp)

                    // Items bought logs
                    currentReceiptItems?.forEach { item ->
                        val mat = materials.find { it.id == item.materialId }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = "${mat?.nama ?: "Item"} x${item.qty}",
                                fontSize = 13.sp,
                                color = Color.Black,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = formatRupiah(item.subtotal),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        }
                    }

                    Divider(color = Color.DarkGray, thickness = 1.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "TOTAL:", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                        Text(
                            text = formatRupiah(currentReceiptHeader!!.totalHarga),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = AccentYellow
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Terima kasih atas kunjungan Anda!\nBarang yang sudah dibeli\ntidak dapat ditukar/dikembalikan.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showReceipt = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Tutup", color = Color.Black)
                        }

                        Button(
                            onClick = {
                                Toast.makeText(ctx, "Mengirim instruksi cetak ke printer Bluetooth...", Toast.LENGTH_SHORT).show()
                                showReceipt = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentYellow),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cetak Struk", color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 7. LAPORAN SCREEN
// ==========================================
@Composable
fun LaporanScreen(viewModel: MainViewModel) {
    val materials by viewModel.materials.collectAsState()
    val transactions by viewModel.transaksi.collectAsState()
    val ctx = LocalContext.current

    var selectedPeriod by remember { mutableStateOf("Semua") } // Hari Ini, Bulan Ini, Semua

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 8.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.currentScreen.value = Screen.Dashboard }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Laporan",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Period filter tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Hari Ini", "Bulan Ini", "Semua").forEach { period ->
                FilterTabButton(
                    name = period,
                    selected = selectedPeriod == period,
                    onClick = { selectedPeriod = period }
                )
            }
        }

        // Compute sums dynamically
        val todayStr = LocalDate.now().toString() // e.g. 2026-06-23
        val monthStr = todayStr.substring(0, 7) // e.g. 2026-06

        val filteredTransactions = transactions.filter { tx ->
            when (selectedPeriod) {
                "Hari Ini" -> tx.tanggal.startsWith(todayStr)
                "Bulan Ini" -> tx.tanggal.startsWith(monthStr)
                else -> true
            }
        }

        val totalEarnings = filteredTransactions.sumOf { it.totalHarga }
        val criticalCount = materials.count { it.isStokKritis() }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main dynamic stat banner card (large orange box matching mockup!)
            Card(
                colors = CardDefaults.cardColors(containerColor = AccentYellow),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "TOTAL TRANSAKSI", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(text = filteredTransactions.size.toString(), fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }

            // Stat columns (Revenue and selling details)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Pendapatan", fontSize = 11.sp, color = Color.Gray)
                        Text(formatRupiah(totalEarnings), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Material Terjual", fontSize = 11.sp, color = Color.Gray)
                        Text("${filteredTransactions.size * 3} unit", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black) // mock summary metric matching PRD
                    }
                }
            }

            // Critical Stock warning box
            if (criticalCount > 0) {
                Text(text = "STOK KRITIS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                materials.filter { it.isStokKritis() }.forEach { mat ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CriticalRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = mat.nama, fontWeight = FontWeight.Bold, color = TextCriticalRed)
                                Text(text = "${mat.stokSaat} unit (minimum: ${mat.minStok})", fontSize = 12.sp, color = Color.DarkGray)
                            }
                            Icon(imageVector = Icons.Default.Warning, contentDescription = "Alert", tint = TextCriticalRed)
                        }
                    }
                }
            }

            // Transactions list
            Text(text = "TRANSAKSI TERBARU", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            
            if (filteredTransactions.isEmpty()) {
                Text("Tidak ada transaksi dalam periode ini", color = Color.Gray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            } else {
                filteredTransactions.forEach { tx ->
                    val isDone = tx.status == "SELESAI"

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = tx.noFaktur, fontWeight = FontWeight.Bold)
                                Text(text = tx.tanggal, fontSize = 11.sp, color = Color.Gray)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = formatRupiah(tx.totalHarga), fontWeight = FontWeight.Bold, color = Color.Black)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .background(if (isDone) OkGreen else Color.LightGray, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = tx.status,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDone) TextOkGreen else Color.DarkGray
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // PDF Exporter Button utilizing PdfDocument Native Android code!
            Button(
                onClick = {
                    exportReportToPdf(ctx, filteredTransactions, selectedPeriod)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = BorderStroke(1.dp, AccordOrange),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = AccordOrange)
                Spacer(modifier = Modifier.width(8.dp))
                // PDF Label
                Text(text = "Ekspor PDF", color = AccordOrange, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// PDF Exporter Native Code implementation
fun exportReportToPdf(context: Context, transactions: List<Transaksi>, period: String) {
    try {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        // 1. Draw PDF header
        paint.textSize = 14f
        paint.isFakeBoldText = true
        canvas.drawText("LAPORAN PENJUALAN - MATERIALKU", 10f, 30f, paint)

        paint.textSize = 10f
        paint.isFakeBoldText = false
        canvas.drawText("Periode: $period", 10f, 50f, paint)
        canvas.drawText("Tanggal cetak: ${LocalDate.now()}", 10f, 65f, paint)

        // Draw divider
        canvas.drawLine(10f, 75f, 290f, 75f, paint)

        // 2. Draw items log table
        var y = 100f
        paint.textSize = 9f
        canvas.drawText("No. Faktur", 10f, y, paint)
        canvas.drawText("Tanggal", 100f, y, paint)
        canvas.drawText("Total Harga", 200f, y, paint)
        y += 15f
        canvas.drawLine(10f, y - 5f, 290f, y - 5f, paint)

        transactions.forEach { tx ->
            if (y < 550f) {
                canvas.drawText(tx.noFaktur, 10f, y, paint)
                val cleanDate = tx.tanggal.substringBefore(" ")
                canvas.drawText(cleanDate, 100f, y, paint)
                canvas.drawText(formatRupiah(tx.totalHarga), 200f, y, paint)
                y += 15f
            }
        }

        // 3. Draw summary footer
        y += 20f
        canvas.drawLine(10f, y - 10f, 290f, y - 10f, paint)
        paint.isFakeBoldText = true
        val totalSum = transactions.sumOf { it.totalHarga }
        canvas.drawText("TOTAL PENDAPATAN:  ${formatRupiah(totalSum)}", 10f, y, paint)

        pdfDocument.finishPage(page)

        // 4. Write output to disk
        val path = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(path, "Laporan_Penjualan_${period.replace(" ", "_")}.pdf")
        val outputStream = FileOutputStream(file)
        pdfDocument.writeTo(outputStream)
        pdfDocument.close()

        Toast.makeText(context, "PDF berhasil disimpan ke: \n${file.absolutePath}", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Gagal mengekspor PDF: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

// ==========================================
// 8. STOK SCREEN (STOCK LOG MUTATION CAPABILITY)
// ==========================================
@Composable
fun StokScreen(viewModel: MainViewModel) {
    val materials by viewModel.materials.collectAsState()
    val units by viewModel.satuan.collectAsState()
    val ctx = LocalContext.current

    var searchQ by remember { mutableStateOf("") }
    var showMutationDialog by remember { mutableStateOf(false) }
    var activeMatForMutation by remember { mutableStateOf<Material?>(null) }

    // Dialog inputs
    var qtyInput by remember { mutableStateOf("") }
    var mutationKind by remember { mutableStateOf("MASUK") } // MASUK, KELUAR
    var notesInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 8.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.currentScreen.value = Screen.Dashboard }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Stok Material",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Search Bar
        OutlinedTextField(
            value = searchQ,
            onValueChange = { searchQ = it },
            placeholder = { Text("Cari material...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        val filtered = materials.filter { it.nama.lowercase().contains(searchQ.lowercase()) || it.kode.lowercase().contains(searchQ.lowercase()) }

        // Stock list
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            filtered.forEach { mat ->
                val unitName = units.find { it.id == mat.satuanId }?.nama ?: "unit"
                val isCritical = mat.isStokKritis()

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = mat.nama, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                Text(text = "Kode: ${mat.kode}", fontSize = 12.sp, color = Color.Gray)
                            }

                            Box(
                                modifier = Modifier
                                    .background(if (isCritical) CriticalRed else OkGreen, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isCritical) "KRITIS" else "AMAN",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCritical) TextCriticalRed else TextOkGreen,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text("STOK SAAT INI", fontSize = 10.sp, color = Color.Gray)
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = mat.stokSaat.toString(),
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCritical) TextCriticalRed else Color.Black
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$unitName / min: ${mat.minStok}",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                            }

                            // Yellow "Catat Mutasi" button
                            Button(
                                onClick = {
                                    activeMatForMutation = mat
                                    qtyInput = ""
                                    mutationKind = "MASUK"
                                    notesInput = ""
                                    showMutationDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SoftYellowBg),
                                border = BorderStroke(1.dp, AccentYellow),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = AccentYellow, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Catat Mutasi", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal dialog record stock mutation
    if (showMutationDialog && activeMatForMutation != null) {
        Dialog(onDismissRequest = { showMutationDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "Selesaikan Mutasi Baru", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = activeMatForMutation!!.nama, fontWeight = FontWeight.Medium, color = Color.Gray)

                    Divider(color = Color.LightGray)

                    // Selection radio kind
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = mutationKind == "MASUK", onClick = { mutationKind = "MASUK" })
                            Text("Masuk", fontWeight = FontWeight.Bold, color = TextOkGreen)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = mutationKind == "KELUAR", onClick = { mutationKind = "KELUAR" })
                            Text("Keluar", fontWeight = FontWeight.Bold, color = TextCriticalRed)
                        }
                    }

                    // Qty Input
                    OutlinedTextField(
                        value = qtyInput,
                        onValueChange = { qtyInput = it },
                        placeholder = { Text("Jumlah unit...") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Keterangan notes input
                    OutlinedTextField(
                        value = notesInput,
                        onValueChange = { notesInput = it },
                        placeholder = { Text("Keterangan...") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(onClick = { showMutationDialog = false }, modifier = Modifier.weight(1f)) {
                            Text("Batal", color = Color.Black)
                        }
                        Button(
                            onClick = {
                                val amount = qtyInput.toIntOrNull() ?: 0
                                viewModel.recordStockMutation(
                                    activeMatForMutation!!,
                                    mutationKind,
                                    amount,
                                    notesInput
                                ) { ok, msg ->
                                    Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
                                    if (ok) showMutationDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentYellow),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Simpan", color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 9. USER MANAGEMENT SCREEN
// ==========================================
@Composable
fun UserManagementScreen(viewModel: MainViewModel) {
    val users by viewModel.users.collectAsState()
    val ctx = LocalContext.current

    var showAddDialog by remember { mutableStateOf(false) }
    var usernameInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("ROLE_KASIR") } // ROLE_ADMIN, ROLE_KASIR, ROLE_GUDANG, ROLE_MANAGER

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 8.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.currentScreen.value = Screen.Dashboard }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Manajemen User",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                users.forEach { usr ->
                    val initialLetter = usr.username.firstOrNull()?.uppercase() ?: "U"

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                // Letter Avatar
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(AccentYellow, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = initialLetter,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color.Black
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(text = usr.username.replaceFirstChar { it.uppercase() }, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(text = "@${usr.username}", fontSize = 12.sp, color = Color.Gray)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Role Badge
                                val roleName = usr.role.replace("ROLE_", "")
                                val badgeBg = when (usr.role) {
                                    "ROLE_ADMIN" -> SoftYellowBg
                                    "ROLE_KASIR" -> SoftTealBg
                                    "ROLE_GUDANG" -> SoftOrangeBg
                                    else -> SoftPurpleBg
                                }
                                val badgeText = when (usr.role) {
                                    "ROLE_ADMIN" -> AccentYellow
                                    "ROLE_KASIR" -> AccordTeal
                                    "ROLE_GUDANG" -> AccordOrange
                                    else -> AccordPurple
                                }

                                Box(
                                    modifier = Modifier
                                        .background(badgeBg, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(text = roleName, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = badgeText)
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Disable deleting self
                                if (usr.username != viewModel.currentUser.value?.username) {
                                    IconButton(onClick = { viewModel.deleteUser(usr) }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // FAB
            FloatingActionButton(
                onClick = {
                    usernameInput = ""
                    passwordInput = ""
                    selectedRole = "ROLE_KASIR"
                    showAddDialog = true
                },
                containerColor = AccentYellow,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add User", tint = Color.Black)
            }
        }
    }

    // Modal Add user dialog
    if (showAddDialog) {
        Dialog(onDismissRequest = { showAddDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "Tambah User Baru", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    Divider(color = Color.LightGray)

                    // Username Input
                    OutlinedTextField(
                        value = usernameInput,
                        onValueChange = { usernameInput = it },
                        placeholder = { Text("Username...") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Password plain Input
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        placeholder = { Text("Password...") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation()
                    )

                    // Role selection dropdown
                    Text(text = "Pilih Role Pengguna", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    var expandedRoleMenu by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedRoleMenu = true }
                        ) {
                            Text(
                                text = selectedRole.replace("ROLE_", ""),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        DropdownMenu(expanded = expandedRoleMenu, onDismissRequest = { expandedRoleMenu = false }) {
                            listOf("ROLE_ADMIN", "ROLE_KASIR", "ROLE_GUDANG", "ROLE_MANAGER").forEach { r ->
                                DropdownMenuItem(
                                    text = { Text(r.replace("ROLE_", "")) },
                                    onClick = {
                                        selectedRole = r
                                        expandedRoleMenu = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(onClick = { showAddDialog = false }, modifier = Modifier.weight(1f)) {
                            Text("Batal", color = Color.Black)
                        }
                        Button(
                            onClick = {
                                viewModel.addUser(usernameInput, passwordInput, selectedRole) { ok, msg ->
                                    Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
                                    if (ok) showAddDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentYellow),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Simpan", color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}
