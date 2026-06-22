# MaterialKu — Design System & Wireframe

> Material Design 3 untuk Android Views. Basis untuk implementasi XML layout.

---

## 1. Design System

### 1.1 Color Palette (Material 3)

```
Primary:        #0061A4  (biru utama — aksi, header, FAB)
OnPrimary:      #FFFFFF
PrimaryContainer:#D1E4FF
OnPrimaryContainer:#001D36

Secondary:      #535F70  (abu-biru — elemen netral)
OnSecondary:    #FFFFFF
SecondaryContainer:#D7E3F7
OnSecondaryContainer:#101C2B

Tertiary:       #6B5778  (ungu — accent, badge)
OnTertiary:     #FFFFFF

Error:          #BA1A1A  (merah — hapus, stok kritis, error)
OnError:        #FFFFFF
ErrorContainer: #FFDAD6

Background:     #FDFCFF  (putih-biru — latar app)
OnBackground:   #1A1C1E

Surface:        #FDFCFF  (kartu, dialog)
OnSurface:      #1A1C1E
SurfaceVariant: #DFE2EB
OnSurfaceVariant:#43474E

Outline:        #74777F
OutlineVariant: #C3C7CF

// Role-based accent (badge di dashboard & user list)
Admin:    #0061A4  (Primary)
Kasir:    #006D40  (hijau)
Gudang:   #B8590A  (oranye)
Manager:  #6B5778  (Tertiary/ungu)
```

### 1.2 Typography (Material 3 Type Scale)

```
DisplayLarge:   57sp / Bold     — (tidak dipakai, app mobile kecil)
HeadlineLarge:  32sp / Bold     — judul dashboard
HeadlineMedium: 28sp / Bold     — judul fragment besar
HeadlineSmall:  24sp / Bold     — judul toolbar besar
TitleLarge:     22sp / SemiBold — judul toolbar
TitleMedium:    16sp / SemiBold — judul kartu, item list
TitleSmall:     14sp / SemiBold — label, button text
BodyLarge:      16sp / Regular  — teks utama
BodyMedium:     14sp / Regular  — teks sekunder, deskripsi
BodySmall:      12sp / Regular  — caption, helper text
LabelLarge:     14sp / Medium   — button, tab
LabelMedium:    12sp / Medium   — badge, chip
LabelSmall:     11sp / Medium   — micro label
```

### 1.3 Spacing & Sizing

```
Spacing unit: 4dp
xs: 4dp   | sm: 8dp   | md: 12dp  | lg: 16dp
xl: 24dp  | 2xl: 32dp | 3xl: 48dp

// Komponen
Toolbar height:        56dp
Bottom nav height:     80dp (jika dipakai)
FAB size:              56dp (extended: height 56dp)
Card corner radius:    12dp
Card elevation:        1dp (resting), 2dp (hovered)
Button height:         40dp (compact), 48dp (default)
Button corner radius:  20dp (pill)
TextField corner:      4dp (outlined)
List item height:      min 56dp (touch target)
Divider:               1dp, color OutlineVariant
```

### 1.4 Komponen UI Standar

| Komponen | Material 3 | Penggunaan |
|---|---|---|
| AppBar | `TopAppBar` (center-aligned) | Header tiap screen |
| Button | `Button`, `OutlinedButton`, `TextButton` | Aksi utama/sekunder/tertiary |
| FAB | `FloatingActionButton` / `ExtendedFAB` | Tambah material, tambah transaksi |
| Card | `ElevatedCard` / `OutlinedCard` | Item list, ringkasan info |
| Chip | `AssistChip` / `FilterChip` | Filter kategori, status badge |
| Dialog | `AlertDialog` | Konfirmasi hapus, info |
| BottomSheet | `ModalBottomSheet` | Struk, filter, opsi cepat |
| TextField | `OutlinedTextField` | Form input |
| Dropdown | `ExposedDropdownMenu` | Pilih kategori, satuan, supplier |
| Snackbar | `Snackbar` | Feedback aksi (sukses/error) |
| ProgressBar | `CircularProgressIndicator` | Loading state |
| EmptyState | Custom (illustration + text) | List kosong |
| SearchBar | `SearchView` di Toolbar | Cari material/transaksi |

---

## 2. Navigation Flow

```
                    ┌─────────┐
                    │  Login  │
                    └────┬────┘
                         │ (autentikasi sukses)
                         ▼
                  ┌──────────────┐
                  │  Dashboard   │◄──────────────────────┐
                  └──────┬───────┘                       │
                         │                               │
          ┌──────────────┼──────────────┐                │
          ▼              ▼              ▼                │
   ┌──────────┐  ┌──────────┐  ┌──────────┐             │
   │ Material │  │   POS    │  │  Stok    │             │
   │  (CRUD)  │  │(Transaksi)│  │ (Mutasi) │             │
   └────┬─────┘  └────┬─────┘  └────┬─────┘             │
        │              │              │                  │
        ▼              ▼              ▼                  │
   ┌──────────┐  ┌──────────┐  ┌──────────┐             │
   │Material  │  │  Struk   │  │  Stok    │             │
   │  Form    │  │BottomSheet│  │  Mutasi  │             │
   └──────────┘  └──────────┘  │  Form    │             │
                                 └──────────┘             │
   ┌──────────┐  ┌──────────┐  ┌──────────┐             │
   │Kategori  │  │ Laporan  │  │  User    │             │
   │(CRUD)    │  │(+PDF)    │  │(CRUD)    │             │
   └──────────┘  └──────────┘  └──────────┘             │
                                                        │
   (semua fragment non-login kembali ke Dashboard)──────┘
```

**Role access matrix:**

| Screen | Admin | Kasir | Gudang | Manager |
|---|---|---|---|---|
| Dashboard | ✅ | ✅ | ✅ | ✅ |
| Material CRUD | ✅ | ❌ | ✅ | ✅ (read) |
| Kategori & Satuan | ✅ | ❌ | ✅ | ✅ (read) |
| POS | ✅ | ✅ | ❌ | ❌ |
| Stok Mutasi | ✅ | ❌ | ✅ | ✅ (read) |
| Laporan | ✅ | ❌ | ❌ | ✅ |
| User Management | ✅ | ❌ | ❌ | ❌ |

---

## 3. Wireframe per Screen

### 3.1 Login

```
┌─────────────────────────────┐
│                             │
│                             │
│                             │
│        [ LOGO ]             │
│      MaterialKu             │
│   Sistem Manajemen          │
│   Material Toko             │
│                             │
│  ┌─────────────────────┐    │
│  │ Username            │    │
│  │                     │    │
│  └─────────────────────┘    │
│                             │
│  ┌─────────────────────┐    │
│  │ Password         👁 │    │
│  │                     │    │
│  └─────────────────────┘    │
│                             │
│  ┌─────────────────────┐    │
│  │      MASUK          │    │  ← Button (Primary)
│  └─────────────────────┘    │
│                             │
│                             │
│  v1.0.0                     │
└─────────────────────────────┘
```

**Spesifikasi:**
- Background: `Background` color
- Logo: 80dp, centered, top ~30% screen
- TextField: outlined, full width minus 32dp margin
- Button: full width, Primary, 48dp height
- Toggle password visibility icon
- Snackbar untuk error login
- Loading indicator di button saat submit

---

### 3.2 Dashboard

```
┌─────────────────────────────┐
│  MaterialKu          [👤]   │  ← TopAppBar (nama app + avatar)
├─────────────────────────────┤
│  Selamat datang,            │
│  Hasan                      │  ← HeadlineSmall + BodyMedium
│  [Admin]                    │  ← Role badge (chip, warna role)
│                             │
│  ┌─────┐ ┌─────┐           │
│  │ 📦  │ │ 🏷️  │           │  ← Grid 2 kolom
│  │Mate-│ │Kate-│           │     Card dengan icon + label
│  │rial │ │gori│           │
│  └─────┘ └─────┘           │
│  ┌─────┐ ┌─────┐           │
│  │ 🛒  │ │ 📊  │           │
│  │ POS │ │Lapor│           │     Tampil/hilang berdasarkan role
│  │     │ │an  │           │
│  └─────┘ └─────┘           │
│  ┌─────┐ ┌─────┐           │
│  │ 📦  │ │ 👥  │           │
│  │Stok │ │User│           │
│  │     │ │    │           │
│  └─────┘ └─────┘           │
│                             │
│  ┌─────────────────────┐    │
│  │     KELUAR          │    │  ← OutlinedButton (logout)
│  └─────────────────────┘    │
└─────────────────────────────┘
```

**Spesifikasi:**
- Grid menu: 2 kolom, `GridLayout`/`FlexboxLayout`, card 1:1 ratio
- Card: ElevatedCard, icon 32dp + TitleMedium, padding 16dp
- Menu visibility diatur by role (inflate menu list di ViewModel)
- Avatar: `CircleImageView` atau Material avatar, klik → logout dialog
- Logout: AlertDialog konfirmasi → clear session → navigate ke Login
- Empty state tidak relevan di sini

---

### 3.3 Material List

```
┌─────────────────────────────┐
│  ← Material           [🔍]  │  ← TopAppBar + search icon
├─────────────────────────────┤
│  [Semua] [Bahan] [Alat] ... │  ← FilterChip horizontal scroll
├─────────────────────────────┤
│  ┌─────────────────────┐    │
│  │ Semen Tiga Roda     │    │  ← OutlinedCard
│  │ Kategori: Bahan      │    │
│  │ Stok: 50 sak         │    │  ← stok < minStok → merah
│  │ Rp 65.000 / sak      │    │
│  │ Supplier: Toko Maju  │    │
│  └─────────────────────┘    │
│  ┌─────────────────────┐    │
│  │ Cat Nippon Paint    │    │
│  │ Kategori: Bahan      │    │
│  │ Stok: 15 kaleng      │    │
│  │ Rp 85.000 / kaleng   │    │
│  │ Supplier: Toko Maju  │    │
│  └─────────────────────┘    │
│                             │
│  ...                        │
│                             │
│                     ┌─────┐ │
│                     │  +  │ │  ← FAB (tambah material)
│                     └─────┘ │
└─────────────────────────────┘
```

**Search expanded:**
```
│  ← [🔍 material...     [✕] │  ← SearchView di toolbar
```

**Empty state:**
```
│        [📦 illustration]    │
│      Belum ada material     │
│   Tekan + untuk menambah    │
```

**Spesifikasi:**
- List: `RecyclerView` + `StaggeredGrid` atau `LinearLayout` vertical
- Card: OutlinedCard, padding 16dp, corner 12dp
- Stok label: hijau jika `stokSaat >= minStok`, merah jika `< minStok`
- Klik card → MaterialForm (mode edit)
- FAB → MaterialForm (mode tambah)
- Search: filter by nama/kode material
- FilterChip: filter by kategori
- Swipe-to-delete (khusus admin/gudang) → Snackbar undo

---

### 3.4 Material Form

```
┌─────────────────────────────┐
│  ← Tambah Material          │  ← TopAppBar (ubah: "Edit Material")
├─────────────────────────────┤
│  Kode Material              │
│  ┌─────────────────────┐    │
│  │ MAT-001             │    │  ← auto-generate di mode tambah
│  └─────────────────────┘    │
│                             │
│  Nama Material *            │
│  ┌─────────────────────┐    │
│  │                     │    │
│  └─────────────────────┘    │
│                             │
│  Kategori *          [▼]    │
│  ┌─────────────────────┐    │
│  │ Pilih kategori      │    │  ← ExposedDropdown
│  └─────────────────────┘    │
│                             │
│  Satuan *            [▼]    │
│  ┌─────────────────────┐    │
│  │ Pilih satuan        │    │
│  └─────────────────────┘    │
│                             │
│  Supplier            [▼]    │
│  ┌─────────────────────┐    │
│  │ Pilih supplier      │    │
│  └─────────────────────┘    │
│                             │
│  Harga (Rp) *               │
│  ┌─────────────────────┐    │
│  │ 0                   │    │  ← numeric keyboard
│  └─────────────────────┘    │
│                             │
│  Stok Awal                  │
│  ┌─────────────────────┐    │
│  │ 0                   │    │  ← hanya di mode tambah
│  └─────────────────────┘    │
│                             │
│  Stok Minimum               │
│  ┌─────────────────────┐    │
│  │ 0                   │    │
│  └─────────────────────┘    │
│                             │
│  ┌─────────────────────┐    │
│  │      SIMPAN         │    │  ← Button Primary
│  └─────────────────────┘    │
│                             │
│  (mode edit: tambah tombol) │
│  ┌─────────────────────┐    │
│  │     HAPUS           │    │  ← TextButton Error color
│  └─────────────────────┘    │
└─────────────────────────────┘
```

**Spesifikasi:**
- Form: `ScrollView` vertical
- Validasi: field bertanda * wajib diisi
- Kode material: auto-generate `MAT-XXX` (read-only di mode tambah, editable di edit)
- Stok Awal: hanya muncul di mode tambah (di edit, stok diatur via Stok Mutasi)
- Hapus (mode edit): AlertDialog konfirmasi → cek relasi → hapus atau tolak
- Simpan: validasi → simpan → Snackbar sukses → pop back

---

### 3.5 Kategori & Satuan

```
┌─────────────────────────────┐
│  ← Kategori & Satuan        │
├─────────────────────────────┤
│  Kategori                   │  ← TitleMedium section header
│  ┌─────────────────────┐    │
│  │ Bahan    [✏️] [🗑️]  │    │  ← list item dengan edit/delete
│  │ Alat     [✏️] [🗑️]  │    │
│  │ Lainnya  [✏️] [🗑️]  │    │
│  └─────────────────────┘    │
│  ┌─────────────────────┐    │
│  │ + Tambah Kategori   │    │  ← OutlinedButton
│  └─────────────────────┘    │
│                             │
│  Satuan                     │
│  ┌─────────────────────┐    │
│  │ sak      [✏️] [🗑️]  │    │
│  │ kaleng   [✏️] [🗑️]  │    │
│  │ unit     [✏️] [🗑️]  │    │
│  │ kg       [✏️] [🗑️]  │    │
│  └─────────────────────┘    │
│  ┌─────────────────────┐    │
│  │ + Tambah Satuan     │    │
│  └─────────────────────┘    │
└─────────────────────────────┘
```

**Tambah (inline dialog):**
```
┌─────────────────────────────┐
│  Tambah Kategori            │
│  ┌─────────────────────┐    │
│  │ Nama kategori       │    │
│  └─────────────────────┘    │
│           [Batal]  [Simpan] │
└─────────────────────────────┘
```

**Spesifikasi:**
- Dua section: Kategori dan Satuan, dipisah divider
- List: sederhana, `RecyclerView` atau `LinearLayout` dynamic
- Tambah: AlertDialog dengan TextField
- Edit: sama seperti tambah, pre-filled
- Hapus: konfirmasi → cek relasi (material terkait) → tolak jika masih dipakai

---

### 3.6 POS (Transaksi)

```
┌─────────────────────────────┐
│  ← Transaksi Baru           │
├─────────────────────────────┤
│  [🔍 Cari material...]      │  ← Search bar
│  [Semua] [Bahan] [Alat]     │  ← FilterChip
├─────────────────────────────┤
│  ┌─────────────────────┐    │
│  │ Semen Tiga Roda     │    │  ← Material card (tap = add to cart)
│  │ Rp 65.000 / sak     │    │
│  │ Stok: 50            │    │
│  └─────────────────────┘    │
│  ┌─────────────────────┐    │
│  │ Cat Nippon Paint    │    │
│  │ Rp 85.000 / kaleng  │    │
│  │ Stok: 15            │    │
│  └─────────────────────┘    │
│                             │
│  ...                        │
│                             │
├─────────────────────────────┤  ← Bottom bar (sticky)
│  🛒 3 item                  │
│  Total: Rp 280.000          │
│              ┌──────────┐   │
│              │ Checkout │   │  ← Button Primary
│              └──────────┘   │
└─────────────────────────────┘
```

**Cart BottomSheet (klik 🛒 atau checkout):**
```
┌─────────────────────────────┐
│  Keranjang            [✕]   │
├─────────────────────────────┤
│  Semen Tiga Roda            │
│  Rp 65.000    [−] 2 [+]     │  ← qty stepper
│  Rp 130.000                 │
│  ───────────────────        │
│  Cat Nippon Paint           │
│  Rp 85.000    [−] 1 [+]     │
│  Rp 85.000                  │
│  ───────────────────        │
│  Cat Nippon Paint           │
│  Rp 65.000    [−] 1 [+]     │
│  Rp 65.000                  │
│  ───────────────────        │
│                             │
│  Total: Rp 280.000          │  ← HeadlineSmall
│                             │
│  ┌─────────────────────┐    │
│  │   BAYAR & CETAK     │    │  ← Button Primary
│  └─────────────────────┘    │
│  ┌─────────────────────┐    │
│  │     SIMPAN DRAFT     │    │  ← OutlinedButton
│  └─────────────────────┘    │
└─────────────────────────────┘
```

**Spesifikasi:**
- Material list: sama seperti Material List tapi tap = add to cart (bukan edit)
- Stok habis (`stokSaat = 0`): card disabled, label "Stok habis"
- Qty tidak boleh > stokSaat
- Bottom bar: sticky di bawah, menampilkan jumlah item + total
- Cart: ModalBottomSheet, qty stepper per item
- Bayar & Cetak: simpan transaksi (status SELESAI) → kurangi stok → buka StrukBottomSheet
- Simpan Draft: simpan transaksi (status DRAFT) → tidak kurangi stok → pop back
- Search & filter berlaku di material list

---

### 3.7 Struk BottomSheet

```
┌─────────────────────────────┐
│  Struk Transaksi      [✕]   │
├─────────────────────────────┤
│        MaterialKu           │
│      Toko Bangunan Jaya     │
│      Jl. Merdeka No. 10     │
│      Telp: 08123456789      │
│  ─────────────────────────  │
│  TRX-20250519-001           │
│  19 Mei 2025, 14:30         │
│  Kasir: Hasan               │
│  ─────────────────────────  │
│  Semen Tiga Roda            │
│   2 sak x Rp 65.000   130000│
│  Cat Nippon Paint           │
│   1 kaleng x Rp 85.000 85000│
│  Cat Nippon Paint           │
│   1 sak x Rp 65.000   65000 │
│  ─────────────────────────  │
│  Total:          Rp 280.000 │
│  ─────────────────────────  │
│  Terima kasih               │
│                             │
│  ┌─────────────────────┐    │
│  │   🖨️ CETAK          │    │  ← Button Primary
│  └─────────────────────┘    │
│  ┌─────────────────────┐    │
│  │      SELESAI         │    │  ← OutlinedButton
│  └─────────────────────┘    │
└─────────────────────────────┘
```

**Spesifikasi:**
- Format struk: monospace font, 58mm width (ESC/POS standard)
- Cetak: Bluetooth printer connection → ESC/POS command
- Jika printer tidak terhubung: dialog pilih printer / error
- Selesai: pop back to Dashboard
- Data struk: transaksi + detail + user + toko info

---

### 3.8 Stok List

```
┌─────────────────────────────┐
│  ← Stok Material     [🔍]   │
├─────────────────────────────┤
│  [Semua] [Kritis] [Aman]    │  ← FilterChip (status stok)
├─────────────────────────────┤
│  ┌─────────────────────┐    │
│  │ Semen Tiga Roda     │    │
│  │ Stok: 50 sak        │    │  ← hijau (aman)
│  │ Min: 10             │    │
│  │ [Catat Mutasi]      │    │  ← TextButton
│  └─────────────────────┘    │
│  ┌─────────────────────┐    │
│  │ Cat Nippon Paint    │    │
│  │ Stok: 5 kaleng      │    │  ← merah (kritis, < minStok)
│  │ Min: 10             │    │
│  │ [Catat Mutasi]      │    │
│  └─────────────────────┘    │
│                             │
│  ...                        │
└─────────────────────────────┘
```

**Spesifikasi:**
- List: sama struktur dengan Material List, tapi fokus di stok
- Filter: Kritis (stok < minStok), Aman (stok >= minStok)
- Card menampilkan: nama, stok saat ini + satuan, stok minimum
- Warna stok: hijau (aman), merah (kritis)
- "Catat Mutasi" → Stok Mutasi Form (bottom sheet atau fragment)

---

### 3.9 Stok Mutasi Form

```
┌─────────────────────────────┐
│  Catat Mutasi Stok          │  ← BottomSheet atau fragment
├─────────────────────────────┤
│  Material                   │
│  Semen Tiga Roda            │  ← read-only (dari list)
│  Stok saat ini: 50 sak      │
│                             │
│  Tipe Mutasi *       [▼]    │
│  ┌─────────────────────┐    │
│  │ Pilih tipe          │    │  ← MASUK / KELUAR / ADJUSTMENT
│  └─────────────────────┘    │
│                             │
│  Jumlah *                   │
│  ┌─────────────────────┐    │
│  │ 0                   │    │  ← numeric keyboard
│  └─────────────────────┘    │
│                             │
│  Alasan                     │
│  ┌─────────────────────┐    │
│  │                     │    │  ← multiline (opsional)
│  │                     │    │
│  └─────────────────────┘    │
│                             │
│  Stok setelah: 55 sak       │  ← preview (auto-calculate)
│                             │
│  ┌─────────────────────┐    │
│  │      SIMPAN         │    │
│  └─────────────────────┘    │
│           [Batal]           │
└─────────────────────────────┘
```

**Spesifikasi:**
- Tipe: MASUK (tambah stok), KELUAR (kurangi stok), ADJUSTMENT (set absolut)
- KELUAR: jumlah tidak boleh > stokSaat
- ADJUSTMENT: set stok ke nilai absolut (untuk koreksi)
- Preview stok setelah: update real-time saat jumlah berubah
- Simpan: create StokLog → update Material.stokSaat → Snackbar → dismiss

---

### 3.10 Laporan

```
┌─────────────────────────────┐
│  ← Laporan                  │
├─────────────────────────────┤
│  Periode            [▼]    │
│  ┌─────────────────────┐    │
│  │ Hari Ini            │    │  ← dropdown: Hari Ini/Minggu/Bulan/Custom
│  └─────────────────────┘    │
│                             │
│  ┌─────────────────────┐    │
│  │ Total Transaksi     │    │  ← Summary card
│  │      12             │    │
│  └─────────────────────┘    │
│  ┌──────────┐ ┌──────────┐  │
│  │ Pendapatan│ │ Material │  │
│  │ Rp 3.5jt  │ │ Terjual  │  │
│  │           │ │ 45 unit  │  │
│  └──────────┘ └──────────┘  │
│                             │
│  Transaksi Terbaru          │  ← section header
│  ┌─────────────────────┐    │
│  │ TRX-20250519-001    │    │
│  │ 19 Mei, 14:30       │    │
│  │ Rp 280.000  SELESAI │    │  ← status badge
│  └─────────────────────┘    │
│  ┌─────────────────────┐    │
│  │ TRX-20250519-002    │    │
│  │ 19 Mei, 15:00       │    │
│  │ Rp 150.000  DRAFT   │    │
│  └─────────────────────┘    │
│                             │
│  Stok Kritis                │  ← section header
│  ┌─────────────────────┐    │
│  │ Cat Nippon Paint    │    │
│  │ 5 kaleng (min: 10)  │    │
│  └─────────────────────┘    │
│                             │
│  ┌─────────────────────┐    │
│  │  📄 Ekspor PDF       │    │  ← OutlinedButton
│  └─────────────────────┘    │
└─────────────────────────────┘
```

**Spesifikasi:**
- Periode: dropdown (Hari Ini / Minggu Ini / Bulan Ini / Custom range)
- Custom range: DatePickerDialog (start + end)
- Summary cards: total transaksi, pendapatan, material terjual
- Transaksi terbaru: 5-10 transaksi terakhir di periode
- Stok kritis: list material dengan stok < minStok
- Ekspor PDF: generate PDF via Android PdfDocument → share intent
- Manager: full access; Admin: full access

---

### 3.11 User Management

```
┌─────────────────────────────┐
│  ← Manajemen User           │
├─────────────────────────────┤
│  ┌─────────────────────┐    │
│  │ 👤 Hasan       [Admin]│    │  ← avatar + role badge
│  │    hasan             │    │
│  │    [✏️] [🗑️]         │    │
│  └─────────────────────┘    │
│  ┌─────────────────────┐    │
│  │ 👤 Budi       [Kasir]│    │
│  │    budi              │    │
│  │    [✏️] [🗑️]         │    │
│  └─────────────────────┘    │
│  ┌─────────────────────┐    │
│  │ 👤 Siti       [Gudang]│   │
│  │    siti              │    │
│  │    [✏️] [🗑️]         │    │
│  └─────────────────────┘    │
│                     ┌─────┐ │
│                     │  +  │ │  ← FAB (tambah user)
│                     └─────┘ │
└─────────────────────────────┘
```

**User Form (dialog atau fragment):**
```
┌─────────────────────────────┐
│  Tambah User                │
├─────────────────────────────┤
│  Nama *                     │
│  ┌─────────────────────┐    │
│  │                     │    │
│  └─────────────────────┘    │
│                             │
│  Username *                 │
│  ┌─────────────────────┐    │
│  │                     │    │
│  └─────────────────────┘    │
│                             │
│  Password *                 │
│  ┌─────────────────────┐    │
│  │                  👁 │    │
│  └─────────────────────┘    │
│                             │
│  Role *             [▼]    │
│  ┌─────────────────────┐    │
│  │ Pilih role          │    │  ← Admin/Kasir/Gudang/Manager
│  └─────────────────────┘    │
│                             │
│  ┌─────────────────────┐    │
│  │      SIMPAN         │    │
│  └─────────────────────┘    │
│           [Batal]           │
└─────────────────────────────┘
```

**Spesifikasi:**
- List: RecyclerView, card per user
- Role badge: chip dengan warna role
- Tambah/Edit: form dengan field nama, username, password, role
- Password: di-edit opsional (kosong = tidak ubah)
- Hapus: konfirmasi → cek relasi (transaksi terkait) → tolak jika masih ada
- Admin only: fragment tidak accessible untuk role lain

---

## 4. Komponen Reusable

### 4.1 EmptyStateView

```xml
<!-- empty_state.xml -->
<LinearLayout vertical, center>
    <ImageView illustration 80dp />
    <TextView TitleMedium "Belum ada data" />
    <TextView BodyMedium "Tekan + untuk menambah" />
</LinearLayout>
```

### 4.2 LoadingStateView

```xml
<FrameLayout match_parent>
    <CircularProgressBar center, 48dp />
</FrameLayout>
```

### 4.3 RoleBadge (chip)

```xml
<Chip
    text="Admin"
    backgroundColor={role color}
    textColor={OnPrimary} />
```

### 4.4 StokIndicator

```xml
<LinearLayout horizontal>
    <TextView "Stok: 50 sak" />
    <View dot color={green/red} />
</LinearLayout>
```

---

## 5. Dimensi & Layout Strategy

- **Phone portrait only** (tidak support tablet landscape di fase ini)
- **Base width**: 360dp (small phone) — target optimal 412dp (modern phone)
- **ScrollView**: semua form screen
- **RecyclerView**: semua list screen
- **BottomSheet**: cart, struk, mutasi stok, form kecil
- **Fragment**: semua screen (single-activity architecture)
- **Edge-to-edge**: enabled, handle insets di toolbar & bottom bar

---

## 6. Animasi & Transisi

| Event | Animasi |
|---|---|
| Fragment transition | Fade + slide (default Material) |
| FAB → Form | Shared element (opsional, nice-to-have) |
| Card tap | Ripple effect (default) |
| Snackbar | Slide up from bottom |
| BottomSheet | Slide up (default Material) |
| Loading → Content | Fade in |
| Empty state | Fade in |

---

## 7. Accessibility

- Semua touch target ≥ 48dp
- Content description di semua icon button
- Kontras warna ≥ 4.5:1 (Material 3 palette sudah memenuhi)
- Support font scale sistem (sp unit)
- TalkBack: label semantik di semua elemen interaktif
