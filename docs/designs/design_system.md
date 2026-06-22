# MaterialKu — Design System & Wireframe

> Industrial Editorial Design untuk Android Views. Basis untuk implementasi XML layout.

---

## 1. Design System

### 1.1 Color Palette

```
// Primary — Amber / Keemasan (industrial warmth)
Primary:           #F59E0B
OnPrimary:         #1A1D21
PrimaryContainer:  #FEF3C7
OnPrimaryContainer:#78350F

// Secondary — Steel Blue (cool neutral)
Secondary:         #4A5568
OnSecondary:       #FFFFFF
SecondaryContainer:#EDF2F7
OnSecondaryContainer:#1A202C

// Tertiary — Deep Violet (accent)
Tertiary:          #7C3AED
OnTertiary:        #FFFFFF

// Error — Alert Red
Error:             #DC2626
OnError:           #FFFFFF
ErrorContainer:    #FEE2E2

// Background — Light & Dark
Background:        #F7F8FA
OnBackground:      #1A1D21
BackgroundDark:    #1A1D21
OnBackgroundDark:  #F7F8FA

// Surface
Surface:           #FFFFFF
OnSurface:         #1A1D21
SurfaceVariant:    #EDF2F7
OnSurfaceVariant:  #718096

// Outline
Outline:           #CBD5E0
OutlineVariant:    #E2E8F0

// Status
Success:           #059669
Warning:           #D97706

// Role-based accent (badge di dashboard & user list)
Admin:    #F59E0B  (Amber)
Kasir:    #059669  (Emerald)
Gudang:   #D97706  (Dark Amber)
Manager:  #7C3AED  (Violet)
```

### 1.2 Typography

```
// Display (tidak dipakai di mobile kecil)
HeadlineLarge:  32sp / Bold      — judul hero dashboard
HeadlineMedium: 28sp / Bold      — judul fragment besar
HeadlineSmall:  24sp / Bold      — judul toolbar besar
TitleLarge:     22sp / SemiBold  — judul toolbar
TitleMedium:    16sp / SemiBold  — judul kartu, item list
TitleSmall:     14sp / SemiBold  — label, button text
BodyLarge:      16sp / Regular   — teks utama
BodyMedium:     14sp / Regular   — teks sekunder, deskripsi
BodySmall:      12sp / Regular   — caption, helper text
LabelLarge:     14sp / Medium    — button, tab
LabelMedium:    12sp / Medium    — badge, chip
LabelSmall:     11sp / Medium    — micro label, section header (uppercase + letterSpacing 1.5)
```

**Special styles:**
- Section headers: uppercase, letterSpacing 1.5, LabelSmall, weight 700
- Stat numbers: 20-40sp, weight 800
- Role badges: uppercase, letterSpacing 0.5

### 1.3 Spacing & Sizing

```
Spacing unit: 4dp
xs: 4dp   | sm: 8dp   | md: 12dp  | lg: 16dp
xl: 24dp  | 2xl: 32dp | 3xl: 48dp

// Komponen
Toolbar height:        56dp
StatusBar height:      24-32dp (system)
Hero section padding:  24dp horizontal, 24-32dp vertical
Bottom nav height:     80dp (jika dipakai)
FAB size:              56dp
Card corner radius:    14-16dp
Card padding:          16-18dp
Button height:         48dp (default), 52dp (large)
Button corner radius:  24-27dp (pill)
TextField height:      52dp
TextField corner:      12dp
TextField icon size:   18dp
List item height:      min 56dp (touch target)
Badge corner radius:   8-10dp
FilterChip padding:    8dp horizontal, 14dp vertical
Divider:               1dp, color OutlineVariant
```

### 1.4 Komponen UI Standar

| Komponen | Penggunaan | Style Notes |
|---|---|---|
| AppBar | Header tiap screen | Transparent/bg color, back arrow + title + action icon |
| Hero Section | Dashboard top | Dark bg (`#1A1D21`), stats row, greeting + role badge |
| Button Primary | Aksi utama | Pill shape, amber bg, dark text |
| Button Outlined | Aksi sekunder | Pill shape, stroke, transparent bg |
| FAB | Tambah material/user | Circle, amber bg, white icon |
| Card | Item list, ringkasan | 14-16dp radius, 1dp stroke `outline-variant` |
| Card Header/Footer | Info grouping | Horizontal space-between layout |
| Badge | Status, role | Pill/chip, color-coded, uppercase |
| Chip/FilterChip | Filter kategori, status | 20dp radius, active = filled amber |
| Dialog | Konfirmasi hapus, form kecil | Standard Material dialog |
| BottomSheet | Struk, cart, filter | Modal, slide up |
| TextField | Form input | 12dp radius, icon prefix, dark stroke |
| Dropdown | Pilih kategori, satuan | Same style as TextField + chevron-right |
| Toggle/Switch | Mode gelap, settings | 52x28dp, amber active |
| Snackbar | Feedback aksi | Slide up from bottom |
| SearchBar | Cari material | Icon + placeholder, `surface-variant` bg |
| EmptyState | List kosong | Illustration + text, center |
| StatCard | Dashboard stats | Dark bg, large number + small label |
| ProfileCard | Settings header | Dark bg, avatar + name + role |

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
           ┌──────────────┼──────────────┬───────────────┐
           ▼              ▼              ▼               ▼
    ┌──────────┐  ┌──────────┐  ┌──────────┐     ┌──────────┐
    │ Material │  │   POS    │  │  Stok    │     │ Settings │
    │  (CRUD)  │  │(Transaksi)│  │ (Mutasi) │     │(Pengaturan)│
    └────┬─────┘  └────┬─────┘  └────┬─────┘     └──────────┘
         │              │              │
         ▼              ▼              ▼
    ┌──────────┐  ┌──────────┐  ┌──────────┐
    │Material  │  │  Struk   │  │  Stok    │
    │  Form    │  │BottomSheet│  │  Mutasi  │
    └──────────┘  └──────────┘  │  Form    │
                                  └──────────┘
    ┌──────────┐  ┌──────────┐  ┌──────────┐
    │Kategori  │  │ Laporan  │  │  User    │
    │(CRUD)    │  │(+PDF)    │  │(CRUD)    │
    └──────────┘  └──────────┘  └──────────┘

    (semua fragment non-login kembali ke Dashboard)
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
| Settings | ✅ | ✅ | ✅ | ✅ |

---

## 3. Visual Layout Summary (9 Screens)

| # | Screen | Layout Structure | Key Visual Elements | Background |
|---|--------|------------------|---------------------|------------|
| 1 | **Login** | Centered vertical, space-between | LogoFrame (88dp, amber) + 2 TextFields (icon prefix) + Pill Button | `BackgroundDark` |
| 2 | **Dashboard** | Vertical scroll, hero + grid | Hero stats (3 dark cards) + 2-col menu grid (6 cards, arrow icon) + Logout button | `Background` |
| 3 | **Material List** | Vertical list + FAB | AppBar + FilterChips + MaterialCards (badge, stock, price) + FAB | `Background` |
| 4 | **Material Form** | Vertical scroll form | 5 TextFields (icon prefix) + 3 Dropdowns + Pill Save button | `Background` |
| 5 | **POS** | Vertical list + sticky bottom bar | SearchBar + FilterChips + ProductCards (add button) + Cart bottom bar | `Background` |
| 6 | **Stok List** | Vertical list | AppBar + FilterChips + StockCards (big number, badge, mutasi button) | `Background` |
| 7 | **Laporan** | Vertical scroll, sections | Periode dropdown + Summary cards (amber + 2 cols) + Transaction list + Kritis card + Export button | `Background` |
| 8 | **User Management** | Vertical list + FAB | AppBar + UserCards (avatar, role badge, actions) + FAB | `Background` |
| 9 | **Settings** | Vertical scroll, grouped | ProfileCard (dark) + Toggle switch + About rows (icon + chevron) | `Background` |

---

## 4. Wireframe per Screen

### 4.1 Login

```
┌─────────────────────────────┐
│  9:41  [signal wifi battery]│  ← StatusBar (dark bg)
├─────────────────────────────┤
│                             │
│                             │
│        ┌─────────┐          │
│        │  📦     │          │  ← LogoFrame 88dp, amber bg
│        └─────────┘          │
│      MATERIALKU             │  ← AppName 32sp/800, uppercase
│  SISTEM MANAJEMEN MATERIAL  │  ← Tagline 13sp, letterSpacing 1.5
│        ━━━━━━               │  ← Deco line 48x3dp, amber
│                             │
│  USERNAME                   │  ← Label 11sp/600, uppercase
│  ┌─────────────────────┐    │
│  │ 👤  Masukkan ...    │    │  ← 52dp height, 12dp radius, icon prefix
│  └─────────────────────┘    │
│                             │
│  PASSWORD                   │
│  ┌─────────────────────┐    │
│  │ 🔒  Masukkan ...  👁│    │
│  └─────────────────────┘    │
│                             │
│  ┌─────────────────────┐    │
│  │       MASUK         │    │  ← Pill button, amber bg, dark text
│  └─────────────────────┘    │
│                             │
│         v1.0.0              │
└─────────────────────────────┘
```

**Spesifikasi:**
- Background: `BackgroundDark` (`#1A1D21`)
- Logo: 88dp, amber bg (`#F59E0B`), dark icon
- TextField: 52dp height, 12dp radius, icon prefix 18dp, stroke `#3D4149`
- Button: pill 52dp height, 27dp radius, amber bg, dark text, uppercase
- Toggle password visibility icon

---

### 4.2 Dashboard

```
┌─────────────────────────────┐
│  9:41  [signal wifi battery]│
├─────────────────────────────┤
│ ━━━━━━━━━━━━━━━━━━━━━━━━━━━ │
│ Selamat datang,             │
│ Hasan                [ADMIN]│  ← 26sp/800 name + amber badge
│                             │
│ ┌─────┐ ┌─────┐ ┌─────┐    │
│ │ 128 │ │  12 │ │  3  │    │  ← Stats row, dark cards
│ │Mat  │ │Trx  │ │Krit │    │
│ └─────┘ └─────┘ └─────┘    │
│ ━━━━━━━━━━━━━━━━━━━━━━━━━━━ │
│                             │
│ MENU UTAMA                  │  ← Section header, uppercase
│                             │
│ ┌─────────┐ ┌─────────┐    │
│ │ 📦    ↗ │ │ 🏷️    ↗ │    │  ← 2-col grid, icon wrap + arrow
│ │ Material│ │ Kategori│    │
│ └─────────┘ └─────────┘    │
│ ┌─────────┐ ┌─────────┐    │
│ │ 🛒    ↗ │ │ 📊    ↗ │    │
│ │   POS   │ │ Laporan │    │
│ └─────────┘ └─────────┘    │
│ ┌─────────┐ ┌─────────┐    │
│ │ 📦    ↗ │ │ 👥    ↗ │    │
│ │   Stok  │ │  User   │    │
│ └─────────┘ └─────────┘    │
│                             │
│ ┌─────────────────────┐    │
│ │      KELUAR         │    │  ← Outlined button, error text
│ └─────────────────────┘    │
└─────────────────────────────┘
```

**Spesifikasi:**
- Hero: `BackgroundDark` bg, padding 24dp, greeting 13sp + name 26sp/800
- Role badge: pill, amber bg, white text, uppercase, icon shield
- Stats row: 3 kolom, dark card (`#252830`), 20sp/800 value + 11sp label
- Grid: 2 kolom, card 130dp height, 16dp radius, icon wrap 44dp + arrow-up-right
- Logout: outlined pill, error color

---

### 4.3 Material List

```
┌─────────────────────────────┐
│  ← Material          [🔍]   │
├─────────────────────────────┤
│ [Semua] [Bahan] [Alat]...   │  ← FilterChip, active = amber
├─────────────────────────────┤
│ ┌─────────────────────┐     │
│ │ Semen Tiga Roda     │     │
│ │ Bahan • Toko Maju   │     │
│ │              [AMAN] │     │  ← Badge top-right
│ ├─────────────────────┤     │
│ │ Stok: 50 sak        │     │
│ │ Rp 65.000 / sak     │     │  ← Price amber/bold
│ └─────────────────────┘     │
│ ┌─────────────────────┐     │
│ │ Cat Nippon Paint    │     │
│ │ Bahan • Toko Maju   │     │
│ │            [KRITIS] │     │  ← Red badge
│ ├─────────────────────┤     │
│ │ Stok: 5 kaleng      │     │  ← Red text
│ │ Rp 85.000 / kaleng  │     │
│ └─────────────────────┘     │
│                     ┌─────┐ │
│                     │  +  │ │  ← FAB
│                     └─────┘ │
└─────────────────────────────┘
```

**Spesifikasi:**
- Card: 16dp radius, 18dp padding, 1dp stroke
- Header: name 17sp/700 + category/supplier 12sp
- Badge: top-right, pill, green/red bg, uppercase
- Footer: stock 14sp/600 + price 14sp/700 amber
- FAB: 56dp circle, amber bg

---

### 4.4 Material Form

```
┌─────────────────────────────┐
│  ← Tambah Material          │
├─────────────────────────────┤
│ KODE MATERIAL               │  ← Label 11sp/600 uppercase
│ ┌─────────────────────┐     │
│ │ #   MAT-001         │     │  ← Icon prefix
│ └─────────────────────┘     │
│ NAMA MATERIAL *             │
│ ┌─────────────────────┐     │
│ │ 📦  Masukkan nama   │     │
│ └─────────────────────┘     │
│ KATEGORI *                  │
│ ┌─────────────────────┐     │
│ │ 🏷️  Pilih kategori  ▼│     │
│ └─────────────────────┘     │
│ SATUAN *                    │
│ ┌─────────────────────┐     │
│ │ 📏  Pilih satuan    ▼│     │
│ └─────────────────────┘     │
│ SUPPLIER                    │
│ ┌─────────────────────┐     │
│ │ 🚚  Pilih supplier  ▼│     │
│ └─────────────────────┘     │
│ HARGA (Rp) *                │
│ ┌─────────────────────┐     │
│ │ 💵  0               │     │
│ └─────────────────────┘     │
│ STOK AWAL                   │
│ ┌─────────────────────┐     │
│ │ 📚  0               │     │
│ └─────────────────────┘     │
│ STOK MINIMUM                │
│ ┌─────────────────────┐     │
│ │ ⚠️  0               │     │
│ └─────────────────────┘     │
│                             │
│ ┌─────────────────────┐     │
│ │       SIMPAN        │     │
│ └─────────────────────┘     │
└─────────────────────────────┘
```

**Spesifikasi:**
- Label: 11sp/600, uppercase, letterSpacing 0.5
- Input: 52dp height, 12dp radius, icon prefix 18dp, stroke `#CBD5E0`
- Dropdown: same + chevron-down trailing
- Button: pill 54dp, amber bg

---

### 4.5 POS (Transaksi)

```
┌─────────────────────────────┐
│  ← Transaksi Baru           │
├─────────────────────────────┤
│ [🔍 Cari material...]       │
│ [Semua] [Bahan] [Alat]      │
├─────────────────────────────┤
│ ┌─────────────────────┐     │
│ │ Semen Tiga Roda     │  ➕ │  ← Add button circle
│ │ Rp 65.000 / sak     │     │
│ │ Stok: 50            │     │
│ └─────────────────────┘     │
│ ┌─────────────────────┐     │
│ │ Cat Nippon Paint    │  ➕ │
│ │ Rp 85.000 / kaleng  │     │
│ │ Stok: 15            │     │
│ └─────────────────────┘     │
├─────────────────────────────┤
│ 🛒 3 item                   │
│ Rp 280.000         [Checkout]│  ← Bottom bar sticky
└─────────────────────────────┘
```

**Spesifikasi:**
- Card: horizontal, 14dp radius, add button 36dp circle amber
- Bottom bar: 80dp height, sticky, total 20sp/800 + checkout pill button

---

### 4.6 Cart BottomSheet

```
┌─────────────────────────────┐
│ Keranjang           [✕]     │
├─────────────────────────────┤
│ Semen Tiga Roda             │
│ Rp 65.000    [−] 2 [+]      │  ← Stepper
│ Rp 130.000                  │
│ ─────────────────────────── │
│ Cat Nippon Paint            │
│ Rp 85.000    [−] 1 [+]      │
│ Rp 85.000                   │
│ ─────────────────────────── │
│ Total:         Rp 280.000   │  ← 20sp/800
│                             │
│ ┌─────────────────────┐     │
│ │   BAYAR & CETAK     │     │
│ └─────────────────────┘     │
│ ┌─────────────────────┐     │
│ │    SIMPAN DRAFT     │     │
│ └─────────────────────┘     │
└─────────────────────────────┘
```

---

### 4.7 Struk BottomSheet

```
┌─────────────────────────────┐
│ Struk Transaksi     [✕]     │
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
│   2 sak x Rp 65.000 130.000 │
│  Cat Nippon Paint           │
│   1 kaleng x Rp 85.000 85.000│
│  ─────────────────────────  │
│  Total:         Rp 280.000  │
│  ─────────────────────────  │
│       Terima kasih          │
│                             │
│  [🖨️ CETAK]    [SELESAI]    │
└─────────────────────────────┘
```

---

### 4.8 Stok List

```
┌─────────────────────────────┐
│  ← Stok Material     [🔍]   │
├─────────────────────────────┤
│ [Semua] [Kritis] [Aman]     │
├─────────────────────────────┤
│ ┌─────────────────────┐     │
│ │ Semen Tiga Roda     │     │
│ │              [AMAN] │     │
│ ├─────────────────────┤     │
│ │  50               10│     │  ← Big number + min
│ │ sak            min  │     │
│ ├─────────────────────┤     │
│ │  [✏️ Catat Mutasi]  │     │  ← Text button
│ └─────────────────────┘     │
│ ┌─────────────────────┐     │
│ │ Cat Nippon Paint    │     │
│ │            [KRITIS] │     │
│ ├─────────────────────┤     │
│ │   5               10│     │  ← Red number
│ │ kaleng         min  │     │
│ ├─────────────────────┤     │
│ │  [✏️ Catat Mutasi]  │     │
│ └─────────────────────┘     │
└─────────────────────────────┘
```

**Spesifikasi:**
- Big stock number: 24sp/800, color coded (green/red)
- Unit label: 12sp
- Min label: 13sp/600 + 11sp caption
- Mutasi button: pill, primary-container bg

---

### 4.9 Stok Mutasi Form

```
┌─────────────────────────────┐
│ Catat Mutasi Stok           │
├─────────────────────────────┤
│ Material: Semen Tiga Roda   │
│ Stok saat ini: 50 sak       │
│                             │
│ TIPE MUTASI *               │
│ ┌─────────────────────┐     │
│ │ ▼ Pilih tipe        │     │
│ └─────────────────────┘     │
│ JUMLAH *                    │
│ ┌─────────────────────┐     │
│ │ 0                   │     │
│ └─────────────────────┘     │
│ ALASAN                      │
│ ┌─────────────────────┐     │
│ │                     │     │
│ │                     │     │
│ └─────────────────────┘     │
│ Stok setelah: 55 sak        │
│                             │
│ ┌─────────────────────┐     │
│ │      SIMPAN         │     │
│ └─────────────────────┘     │
│           [Batal]           │
└─────────────────────────────┘
```

---

### 4.10 Laporan

```
┌─────────────────────────────┐
│  ← Laporan                  │
├─────────────────────────────┤
│ PERIODE                     │  ← Section label uppercase
│ ┌─────────────────────┐     │
│ │ ▼ Hari Ini          │     │
│ └─────────────────────┘     │
│                             │
│ ┌─────────────────────┐     │
│ │ TOTAL TRANSAKSI     │     │  ← Amber summary card
│ │        12           │     │
│ └─────────────────────┘     │
│ ┌─────────┐ ┌─────────┐     │
│ │PENDAPATAN│ │TERJUAL  │     │
│ │Rp 3.5jt │ │ 45 unit │     │
│ └─────────┘ └─────────┘     │
│                             │
│ TRANSAKSI TERBARU           │
│ ┌─────────────────────┐     │
│ │ TRX-001    Rp280rb  │     │
│ │ 19 Mei,14:30 [SELESAI]│    │
│ └─────────────────────┘     │
│ ┌─────────────────────┐     │
│ │ TRX-002    Rp150rb  │     │
│ │ 19 Mei,15:00 [DRAFT]│     │
│ └─────────────────────┘     │
│                             │
│ STOK KRITIS                 │
│ ┌─────────────────────┐     │
│ │ Cat Nippon Paint    │     │  ← Error container bg
│ │ 5 kaleng (min: 10)  │     │
│ └─────────────────────┘     │
│                             │
│ ┌─────────────────────┐     │
│ │    📄 Ekspor PDF    │     │
│ └─────────────────────┘     │
└─────────────────────────────┘
```

**Spesifikasi:**
- Section headers: uppercase, 11sp/700, letterSpacing 1.5
- Summary total card: amber bg, 40sp/800 number
- Summary row: 2 kolom, 14dp radius
- Transaction cards: horizontal, badge status (green/outline)
- Kritis card: error-container bg

---

### 4.11 User Management

```
┌─────────────────────────────┐
│  ← Manajemen User           │
├─────────────────────────────┤
│ ┌─────────────────────┐     │
│ │ 👤 Hasan     [ADMIN]│     │
│ │    @hasan           │     │
│ │       ✏️    🗑️      │     │
│ └─────────────────────┘     │
│ ┌─────────────────────┐     │
│ │ 👤 Budi     [KASIR] │     │
│ │    @budi            │     │
│ │       ✏️    🗑️      │     │
│ └─────────────────────┘     │
│ ┌─────────────────────┐     │
│ │ 👤 Siti    [GUDANG] │     │
│ │    @siti            │     │
│ └─────────────────────┘     │
│ ┌─────────────────────┐     │
│ │ 👤 Rina   [MANAGER] │     │
│ │    @rina            │     │
│ └─────────────────────┘     │
│                     ┌─────┐ │
│                     │  +  │ │
│                     └─────┘ │
└─────────────────────────────┘
```

**Spesifikasi:**
- Card: horizontal, 14dp radius, 48dp avatar circle
- Avatar: role-colored bg, white initial 18sp/800
- Badge: role-colored bg, uppercase
- Actions: edit (grey) + delete (red) icons

---

### 4.12 Settings (Pengaturan)

```
┌─────────────────────────────┐
│  ← Pengaturan               │
├─────────────────────────────┤
│ ┌─────────────────────┐     │
│ │ 👤  Hasan           │     │  ← Profile card dark
│ │     Admin           │     │
│ └─────────────────────┘     │
│                             │
│ TAMPILAN                    │  ← Section header
│ ┌─────────────────────┐     │
│ │ 🌙  Mode Gelap   [●]│     │  ← Toggle switch
│ └─────────────────────┘     │
│                             │
│ TENTANG                     │
│ ┌─────────────────────┐     │
│ │ ℹ️  Versi Aplikasi  │     │
│ │     v1.0.0          │     │
│ └─────────────────────┘     │
│ ┌─────────────────────┐     │
│ │ 📄  Kebijakan Privasi│     │
│ │                 >   │     │
│ └─────────────────────┘     │
│ ┌─────────────────────┐     │
│ │ ❓  Bantuan         │     │
│ │                 >   │     │
│ └─────────────────────┘     │
└─────────────────────────────┘
```

**Spesifikasi:**
- Profile card: `BackgroundDark` bg, 16dp radius, 56dp avatar
- Section headers: uppercase, 11sp/700, letterSpacing 1.5
- Rows: 14dp radius, icon wrap 40dp + label + value/chevron
- Toggle: 52x28dp, amber active, 24dp knob

---

## 5. Komponen Reusable

### 5.1 EmptyStateView
```xml
<LinearLayout vertical, center, padding 48dp>
    <ImageView illustration 80dp />
    <TextView TitleMedium "Belum ada data" />
    <TextView BodyMedium "Tekan + untuk menambah" />
</LinearLayout>
```

### 5.2 LoadingStateView
```xml
<FrameLayout match_parent>
    <CircularProgressBar center, 48dp, amber color />
</FrameLayout>
```

### 5.3 RoleBadge
```xml
<FrameLayout horizontal, padding [6,12], radius 20dp, bg roleColor>
    <Icon shield 14dp, white />
    <Text LabelSmall, uppercase, white />
</FrameLayout>
```

### 5.4 StatusBadge (AMAN/KRITIS)
```xml
<FrameLayout horizontal, padding [4,10], radius 8dp, bg green/redContainer>
    <Text LabelMedium, weight 700, green/red />
</FrameLayout>
```

### 5.5 SectionHeader
```xml
<TextView LabelSmall, uppercase, letterSpacing 1.5, color onSurfaceVariant />
```

---

## 6. Dimensi & Layout Strategy

- **Phone portrait only** (tidak support tablet landscape di fase ini)
- **Base width**: 360dp — target optimal 412dp
- **ScrollView**: semua form screen
- **RecyclerView**: semua list screen
- **BottomSheet**: cart, struk, mutasi stok
- **Fragment**: semua screen (single-activity architecture)
- **Edge-to-edge**: enabled, handle insets di toolbar & bottom bar

---

## 7. Animasi & Transisi

| Event | Animasi |
|---|---|
| Fragment transition | Fade + slide (default Material) |
| Card tap | Ripple effect + slight elevation |
| Snackbar | Slide up from bottom |
| BottomSheet | Slide up |
| FAB press | Scale down 0.95 |
| Loading → Content | Fade in 200ms |
| Toggle switch | TranslateX knob 200ms |

---

## 8. Accessibility

- Semua touch target ≥ 48dp
- Content description di semua icon button
- Kontras warna ≥ 4.5:1
- Support font scale sistem (sp unit)
- TalkBack labels di semua elemen interaktif
