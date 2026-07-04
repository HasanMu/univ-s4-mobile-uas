# MaterialKu v1.0.0

Rilis awal MaterialKu — aplikasi manajemen material toko bahan bangunan berbasis Android (offline-first, satu APK, tanpa backend).

## 📦 Download

APK signed siap install: [`app-release.apk`](https://github.com/HasanMu/univ-s4-mobile-uas/blob/master/apk/app-release.apk) (~16 MB)

Login default (di-seed saat instalasi pertama):

| Role | Username | Password |
|---|---|---|
| ADMIN | `admin` | `admin123` |
| KASIR | `kasir` | `kasir123` |
| GUDANG | `gudang` | `gudang123` |
| MANAGER | `manager` | `manager123` |

## ✨ Fitur utama

### Auth & Akses
- Login username/password dengan bcrypt hash (at.favre 0.10.2)
- Session persistent — tetap login setelah aplikasi ditutup
- Role-based menu access (ADMIN / KASIR / GUDANG / MANAGER)

### Master Data
- CRUD **Material** dengan kode unik, kategori/satuan/supplier FK, stokMin
- CRUD **Kategori** dan **Satuan** dengan validasi inline
- CRUD **User** dengan password reset + guard "delete self"
- Search material live filtering

### Transaksi & Stok
- **POS** — Cart + Checkout atomik via `db.withTransaction`
- Auto nomor faktur `TRX-YYYYMMDD-NNNN`
- Flow DRAFT: simpan draft → buka lagi → checkout atau edit
- **Stok Mutasi** — Masuk / Keluar dengan preview stok setelah
- **StokLog** audit trail — semua perubahan stok tercatat + user attribution

### Laporan
- Periode: Hari Ini / Minggu Ini / Bulan Ini / **Custom range** (MaterialDatePicker)
- Statistik: total transaksi, pendapatan, unit terjual, stok kritis
- **Detail transaksi** — tap row → header + item list + kasir + total
- **Ekspor PDF** via `android.graphics.pdf.PdfDocument` + share via chooser
- **Preview struk 58mm** ESC/POS format (siap dikirim ke thermal printer BT-SPP nantinya)

### Pengaturan
- Dark mode toggle persistent via DataStore
- Profil user aktif ditampilkan dinamis

## 🎨 Branding
- Adaptive launcher icon (foreground + background + monochrome untuk themed icons Android 13+)
- Logo MaterialKu di login screen
- 10 Material Icons custom VectorDrawable menggantikan Android system icons default

## 🏗️ Tech stack
- Kotlin, Jetpack (Room, Navigation, Lifecycle, DataStore, ViewBinding)
- Hilt untuk DI
- Material Components 3
- Coroutines + Flow untuk reactive state
- minSdk 30 (Android 11), targetSdk 36

## 👥 Kontributor (Kelompok 1 / TIF K 24A / UTB)
- Hasan Muhammad Sholeh — Auth, Dashboard, Settings, UI polish, branding
- Muhammad Rafie Al'Ghifari — Material, Kategori, Satuan CRUD
- Pria Pamungkas — Stok Mutasi, POS, Struk preview
- Muzakki Fadlillah Gunadi — User Management, Laporan, PDF export

## 📝 Catatan
- Aplikasi ini offline-only — tidak ada network/backend
- Data tersimpan lokal di SQLite via Room; kalau data reset, ulang seed berjalan
- Bluetooth printer support belum di-wire; format struk sudah siap dikirim ke `BluetoothSocket.outputStream` via SPP UUID standar
