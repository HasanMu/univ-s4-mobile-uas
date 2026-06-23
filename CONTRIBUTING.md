# Contributing to MaterialKu

Panduan kontribusi untuk tim pengembang MaterialKu.

---

## Setup Development

### Prasyarat
- Android Studio (latest stable)
- JDK 11
- Kotlin 2.2.20
- AGP 9.0.1
- minSdk 30, targetSdk 36

### Cara Menjalankan
1. Clone repository
2. Buka di Android Studio
3. Sync Gradle
4. Run di emulator/device (API 30+)

### Branch Strategy
- `master` — branch utama, selalu stabil
- `feat/design` — branch desain & foundation
- `feat/<nama-fitur>` — branch fitur per kontributor
- Buat branch dari `feat/design` untuk fitur baru

---

## Arsitektur

MVVM + Clean Architecture (3 layer):

```
presentation/  → Fragment + ViewModel (UI)
domain/        → Model + Repository Interface + Service (business logic)
data/          → Room Entity + DAO + RepositoryImpl (data source)
```

### Package Structure
```
com.kelompok1.materialku/
├── MaterialKuApp.kt          (@HiltAndroidApp)
├── MainActivity.kt           (NavHost)
├── presentation/
│   ├── base/                  (BaseFragment, BaseViewModel)
│   ├── auth/                  (Login)
│   ├── dashboard/             (Dashboard)
│   ├── material/              (MaterialList, MaterialForm)
│   ├── kategori/              (Kategori & Satuan)
│   ├── pos/                   (POS Transaksi)
│   ├── stok/                  (Stok List)
│   ├── laporan/               (Laporan)
│   ├── user/                  (User Management)
│   └── settings/              (Settings)
├── domain/
│   ├── model/                 (Entity & Enum)
│   ├── repository/            (Interface Repository)
│   └── service/               (Business Service)
├── data/
│   ├── local/
│   │   ├── entity/            (Room @Entity)
│   │   ├── dao/               (Room DAO)
│   │   ├── MaterialKuDatabase.kt
│   │   └── PreferencesDataStore.kt
│   └── repository/            (RepositoryImpl)
└── util/                      (PdfExporter, formatter, dll)
```

### Design System
Lihat `docs/designs/design_system.md` untuk:
- Color palette (primary #F59E0B amber, background_dark #1A1D21)
- Typography (Inter, weights 600-800)
- Spacing & sizing
- Komponen UI standar
- Wireframe per screen

## Task Assignment

> Bagi tugas berdasarkan scope di bawah. Tentukan pembagian internal tim sendiri.

### Auth & Dashboard
**Scope:** Login, Dashboard, Settings, Navigation

**Tugas:**
1. Implementasi `LoginViewModel` — validasi, bcrypt, session DataStore
2. Implementasi `DashboardViewModel` — stats dari database
3. Implementasi `DashboardFragment` — menu visibility per role
4. Implementasi `SettingsFragment` — dark mode toggle (DataStore)
5. Wiring navigation actions di `nav_graph.xml`
6. Buat `domain/model/User.kt` dan `domain/model/Role.kt`
7. Buat `data/local/PreferencesDataStore.kt` (session, dark mode)
8. Buat `domain/repository/IAuthRepository.kt` + impl

**File yang akan dibuat/diubah:**
- `presentation/auth/LoginFragment.kt` (lengkap)
- `presentation/auth/LoginViewModel.kt` (lengkap)
- `presentation/dashboard/DashboardFragment.kt` (lengkap)
- `presentation/dashboard/DashboardViewModel.kt` (lengkap)
- `presentation/settings/SettingsFragment.kt` (lengkap)
- `presentation/settings/SettingsViewModel.kt` (lengkap)
- `domain/model/User.kt`
- `domain/model/Role.kt`
- `domain/repository/IAuthRepository.kt`
- `data/repository/AuthRepositoryImpl.kt`
- `data/local/PreferencesDataStore.kt`
- `res/navigation/nav_graph.xml` (wiring actions)

---

### Material & Kategori
**Scope:** Material CRUD, Kategori & Satuan CRUD

**Tugas:**
1. Implementasi `MaterialViewModel` — list, filter, search, CRUD
2. Implementasi `MaterialListFragment` — RecyclerView adapter, filter chips
3. Implementasi `MaterialFormFragment` — form validation, save/edit
4. Implementasi `KategoriViewModel` — list, add, edit, delete
5. Implementasi `KategoriFragment` — dual list, dialog form
6. Buat Room entities: `MaterialEntity`, `KategoriEntity`, `SatuanEntity`, `SupplierEntity`
7. Buat Room DAOs: `MaterialDao`, `KategoriDao`, `SatuanDao`, `SupplierDao`
8. Buat `MaterialKuDatabase.kt` dengan seeding data awal
9. Buat `domain/repository/IMaterialRepository.kt` + impl
10. Buat `domain/repository/IKategoriRepository.kt` + impl

**File yang akan dibuat/diubah:**
- `presentation/material/MaterialListFragment.kt` (lengkap)
- `presentation/material/MaterialFormFragment.kt` (lengkap)
- `presentation/material/MaterialViewModel.kt` (lengkap)
- `presentation/material/MaterialAdapter.kt`
- `presentation/kategori/KategoriFragment.kt` (lengkap)
- `presentation/kategori/KategoriViewModel.kt` (lengkap)
- `domain/model/Material.kt`
- `domain/model/Kategori.kt`
- `domain/model/Satuan.kt`
- `domain/model/Supplier.kt`
- `data/local/entity/MaterialEntity.kt`
- `data/local/entity/KategoriEntity.kt`
- `data/local/entity/SatuanEntity.kt`
- `data/local/entity/SupplierEntity.kt`
- `data/local/dao/MaterialDao.kt`
- `data/local/dao/KategoriDao.kt`
- `data/local/dao/SatuanDao.kt`
- `data/local/dao/SupplierDao.kt`
- `data/local/MaterialKuDatabase.kt`
- `domain/repository/IMaterialRepository.kt`
- `domain/repository/IKategoriRepository.kt`
- `data/repository/MaterialRepositoryImpl.kt`
- `data/repository/KategoriRepositoryImpl.kt`
- `res/layout/item_material.xml`
- `res/layout/dialog_kategori.xml`

---

### POS & Stok
**Scope:** POS Transaksi, Stok List, Stok Mutasi, Struk

**Tugas:**
1. Implementasi `PosViewModel` — material list, cart, checkout
2. Implementasi `PosFragment` — product list, add to cart, bottom sheet
3. Buat Cart BottomSheet (`CartBottomSheet.kt` + `fragment_cart.xml`)
4. Buat Struk BottomSheet (`StrukBottomSheet.kt` + `fragment_struk.xml`)
5. Implementasi `StokViewModel` — list, filter (kritis/aman)
6. Implementasi `StokListFragment` — RecyclerView, stok indicator
7. Buat Stok Mutasi Form (`StokMutasiBottomSheet.kt` + `fragment_stok_mutasi.xml`)
8. Buat Room entities: `TransaksiEntity`, `TransaksiDetailEntity`, `StokLogEntity`
9. Buat Room DAOs: `TransaksiDao`, `StokLogDao`
10. Buat `domain/repository/IPosRepository.kt` + impl
11. Buat `domain/repository/IStokRepository.kt` + impl
12. Buat `domain/service/StokService.kt` — update stok saat transaksi
13. Buat `presentation/printer/StrukPrinter.kt` — ESC/POS bluetooth

**File yang akan dibuat/diubah:**
- `presentation/pos/PosFragment.kt` (lengkap)
- `presentation/pos/PosViewModel.kt` (lengkap)
- `presentation/pos/CartBottomSheet.kt`
- `presentation/pos/StrukBottomSheet.kt`
- `presentation/stok/StokListFragment.kt` (lengkap)
- `presentation/stok/StokViewModel.kt` (lengkap)
- `presentation/stok/StokMutasiBottomSheet.kt`
- `presentation/printer/StrukPrinter.kt`
- `domain/model/Transaksi.kt`
- `domain/model/TransaksiDetail.kt`
- `domain/model/StokLog.kt`
- `data/local/entity/TransaksiEntity.kt`
- `data/local/entity/TransaksiDetailEntity.kt`
- `data/local/entity/StokLogEntity.kt`
- `data/local/dao/TransaksiDao.kt`
- `data/local/dao/StokLogDao.kt`
- `domain/repository/IPosRepository.kt`
- `domain/repository/IStokRepository.kt`
- `data/repository/PosRepositoryImpl.kt`
- `data/repository/StokRepositoryImpl.kt`
- `domain/service/StokService.kt`
- `res/layout/fragment_cart.xml`
- `res/layout/fragment_struk.xml`
- `res/layout/fragment_stok_mutasi.xml`
- `res/layout/item_pos_material.xml`
- `res/layout/item_stok.xml`

---

### Laporan & User Management
**Scope:** Laporan, User Management, PDF Export

**Tugas:**
1. Implementasi `LaporanViewModel` — summary, transaksi list, kritis list
2. Implementasi `LaporanFragment` — periode filter, summary cards, list
3. Buat `util/PdfExporter.kt` — generate PDF via Android PdfDocument
4. Implementasi `UserViewModel` — list, add, edit, delete
5. Implementasi `UserFragment` — RecyclerView, role badges, form dialog
6. Buat User Form Dialog (`UserFormDialog.kt` + `dialog_user_form.xml`)
7. Buat `domain/service/LaporanService.kt` — aggregate data
8. Buat `domain/repository/IUserRepository.kt` + impl
9. Buat `domain/repository/ILaporanRepository.kt` + impl
10. Seeding user awal (admin, kasir, gudang, manager) di database

**File yang akan dibuat/diubah:**
- `presentation/laporan/LaporanFragment.kt` (lengkap)
- `presentation/laporan/LaporanViewModel.kt` (lengkap)
- `presentation/user/UserFragment.kt` (lengkap)
- `presentation/user/UserViewModel.kt` (lengkap)
- `presentation/user/UserFormDialog.kt`
- `util/PdfExporter.kt`
- `domain/service/LaporanService.kt`
- `domain/repository/IUserRepository.kt`
- `domain/repository/ILaporanRepository.kt`
- `data/repository/UserRepositoryImpl.kt`
- `data/repository/LaporanRepositoryImpl.kt`
- `res/layout/item_transaksi.xml`
- `res/layout/item_user.xml`
- `res/layout/dialog_user_form.xml`

---

## Coding Standards

### Kotlin
- Ikuti [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Gunakan `camelCase` untuk variabel/fungsi
- Gunakan `PascalCase` untuk class
- Gunakan `UPPER_SNAKE` untuk constants
- Selalu gunakan `val` kecuali perlu `var`

### XML Layout
- Gunakan `snake_case` untuk id: `@+id/btn_login`
- Prefix id berdasarkan jenis: `btn_`, `tv_`, `iv_`, `rv_`, `et_`, `card_`, `chip_`
- Gunakan `@dimen/`, `@color/`, `@string/`, `@style/` — jangan hardcode
- Gunakan `match_parent`/`wrap_content`/`0dp` (dengan weight) dengan tepat

### Git
- Commit message format: `type: description`
  - `feat:` fitur baru
  - `fix:` bug fix
  - `refactor:` refactoring
  - `docs:` dokumentasi
  - `chore:` maintenance
- Buat branch: `feat/<nama-fitur>`
- 1 commit = 1 perubahan logis

### Testing
- Tulis unit test untuk ViewModel
- Tulis instrumented test untuk DAO
- Gunakan Hilt testing module

---

## Dependencies (sudah di-setup)

| Library | Version | Kegunaan |
|---|---|---|
| Kotlin | 2.2.20 | Bahasa utama |
| KSP | 2.2.20-2.0.3 | Annotation processing |
| Hilt | 2.56.3 | Dependency injection |
| Room | 2.8.2 | Local database |
| Navigation | 2.9.0 | Fragment navigation |
| Lifecycle | 2.9.3 | ViewModel, LiveData |
| Coroutines | 1.10.2 | Async operations |
| DataStore | 1.1.7 | Preferences |
| RecyclerView | 1.4.0 | List rendering |
| SplashScreen | 1.0.1 | Splash screen |

---

## Role Access Matrix

| Screen | Admin | Kasir | Gudang | Manager |
|---|---|---|---|---|
| Dashboard | ✅ | ✅ | ✅ | ✅ |
| Material CRUD | ✅ | ❌ | ✅ | ✅ (read) |
| Kategori & Satuan | ✅ | ❌ | ✅ | ✅ (read) |
| POS | ✅ | ✅ | ❌ | ❌ |
| Stok Mutasi | ✅ | ❌ | ✅ | ✅ (read) |
| Laporan | ✅ | ❌ | ❌ | ✅ |
| User Management | ✅ | ❌ | ❌ | ❌ |
| Settings | ✅ | ✅ | ✅ | ✅ |

---

## Default Users (seeding)

| Username | Password | Role |
|---|---|---|
| admin | admin123 | Admin |
| kasir | kasir123 | Kasir |
| gudang | gudang123 | Gudang |
| manager | manager123 | Manager |

---

## Questions?

Hubungi project lead atau buat issue di repository.