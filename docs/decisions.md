# MaterialKu — Keputusan Implementasi (Baseline)

> Diturunkan dari PRD + diskusi. Update file ini saat ada keputusan baru.

## Keputusan Final

| # | Topik | Keputusan | Catatan |
|---|---|---|---|
| 1 | Supplier–Material | **Pilihan B (1:N)** — `supplierId` di entity Material | Diambil yang umum/sederhana dulu; bisa revisi jika ada use case pembelian |
| 2 | Password hashing | **bcrypt** — library `at.favre.lib:bcrypt:0.10.2`, cost 10 | Dipilih karena maintained, no reflection, kompatibel Android |
| 3 | DI | **Hilt** | Sesuai rekomendasi PRD |
| 4 | Pembatalan transaksi | **BATAL hanya untuk DRAFT** | SELESAI tidak bisa di-retur pada fase ini |
| 5 | Stok konsistensi | **`Material.stokSaat` = source of truth** untuk read cepat; `StokLog` = audit trail | Setiap mutasi wajib update dua-duanya dalam 1 `@Transaction` di `StokService` |
| 6 | Seeding user awal | **Ya** — admin/kasir/gudang/manager dengan password `<role>123` di `DatabaseSeeder`, di-hash bcrypt sebelum insert | Callback `onCreate` database |
| 7 | Field `Material.stokMin` | **Ada** — wajib untuk `isStokKritis()` (`stokSaat <= stokMin`) | Tidak eksplisit di PRD §5 tapi ada di wireframe form; ditemukan waktu audit branch Muzakki |
| 8 | Format tanggal Room | Serialize ke `String` ISO-8601 via `Converters` (`LocalDate.toString()` / `LocalDateTime.toString()`) | Standard, human-readable, sortable |
| 9 | Session storage | **DataStore Preferences** — key `session_user_id`, `session_username`, `session_role` | Nggak simpan password/hash di preferences |

## Tech Stack (dari PRD)

- Kotlin, Android Views + XML (BUKAN Compose)
- minSdk 30, targetSdk/compileSdk 36, AGP 9.0.1, Java 11
- Room + DataStore, Hilt, Navigation Component, coroutines
- Package: `com.kelompok1.materialku`
- Arsitektur: MVVM + Clean Architecture (presentation/domain/data)

## Struktur Package (single-module berlapis)

```
com.kelompok1.materialku/
├── MaterialKuApp.kt              (Application, @HiltAndroidApp)
├── MainActivity.kt               (single-activity, NavHost)
├── presentation/
│   ├── auth/                      (LoginFragment, LoginViewModel)
│   ├── dashboard/                 (DashboardFragment, DashboardViewModel)
│   ├── material/                  (MaterialListFragment, MaterialFormFragment, MaterialViewModel)
│   ├── kategori/                  (KategoriFragment, KategoriViewModel)
│   ├── pos/                       (PosFragment, PosViewModel, StrukBottomSheet)
│   ├── stok/                      (StokListFragment, StokMutasiFragment, StokViewModel)
│   ├── laporan/                   (LaporanFragment, LaporanViewModel)
│   ├── user/                      (UserFragment, UserViewModel)
│   └── printer/                   (StrukPrinter)
├── domain/
│   ├── model/                     (Entity & Enum: Material, Kategori, dst.)
│   ├── repository/                (Interface: IMaterialRepository, dst.)
│   └── service/                   (MaterialService, StokService, TransaksiService, LaporanService, AuthService)
├── data/
│   ├── local/
│   │   ├── entity/                (Room @Entity)
│   │   ├── dao/                   (Room DAO)
│   │   ├── Converters.kt          (TypeConverter enum/date)
│   │   ├── DatabaseSeeder.kt      (seed user awal)
│   │   ├── MaterialKuDatabase.kt
│   │   └── PreferencesDataStore.kt
│   └── repository/                (RepositoryImpl)
├── di/                            (Hilt module: DatabaseModule, RepositoryModule)
└── util/                          (PasswordHasher, PdfExporter, formatter, dst.)
```

## Screen List (9 screen)

1. **Login** — autentikasi, redirect by role
2. **Dashboard** — menu per role + hero stats
3. **Material List + Form** — CRUD material
4. **Kategori & Satuan** — CRUD referensi
5. **POS** — input transaksi + struk
6. **Stok List + Mutasi** — lihat stok, catat mutasi
7. **Laporan** — analitik + ekspor PDF
8. **User Management** — CRUD user (admin only)
9. **Settings** — pengaturan aplikasi (mode gelap, tentang)

## Dokumen Desain

- `docs/designs/design_system.md` — Design system lengkap (color, typography, spacing, komponen, wireframe)
- `docs/designs/mockup.pen` — Mockup interaktif (Pencil Editor)
- `docs/uml/usecase-materialku.png` — Diagram use case
- `docs/decisions.md` — File ini (keputusan implementasi)
