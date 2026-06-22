# MaterialKu — Dokumen Konteks Proyek (PRD / Project Brief)

> **Cara pakai file ini:** Upload file ini di awal chat baru. Isinya adalah seluruh konteks proyek MaterialKu (identitas, tech stack, arsitektur, domain model, keputusan desain, status, dan konvensi). Tujuannya agar AI langsung paham konteks tanpa perlu dijelaskan ulang. Jika ada keputusan baru atau perubahan, perbarui file ini agar tetap menjadi satu sumber kebenaran (single source of truth).
>
> **Status dokumen:** living document. Terakhir disusun pada fase: repo masih template kosong, desain (Milestone 2) sudah selesai, implementasi belum dimulai.

---

## 1. Ringkasan Proyek

**MaterialKu** adalah aplikasi **Android native (Kotlin)** untuk **inventaris + Point of Sale (POS)** sebuah **toko material bangunan**. Aplikasi bersifat **fully offline** — seluruh data tersimpan lokal di perangkat (Room/SQLite), tanpa server atau API jaringan. Satu-satunya perangkat eksternal adalah **thermal printer via Bluetooth** untuk cetak struk.

Proses utama: **transaksi penjualan (POS)** dan **manajemen material & stok** (termasuk pencatatan mutasi stok masuk/keluar dan pelaporan).

Proyek ini adalah tugas kuliah **Object Oriented Analysis and Design (OOAD)**, lanjut ke implementasi mobile.

---

## 2. Identitas & Tim

| Item | Nilai |
|---|---|
| Nama sistem | MaterialKu |
| Tim | Kelompok 1 |
| Kelas | TIF K 24A |
| Kampus | Universitas Teknologi Bandung (UTB), S1 Teknik Informatika |
| Mata kuliah | Object Oriented Analysis and Design (OOAD) |
| Anggota | Muzakki Fadlillah Gunadi (24552011062), Hasan Muhammad Sholeh (24552011092), Pria Pamungkas (24552011108), Muhammad Rafie Al'Ghifari (24552011052) |
| Repo GitHub | https://github.com/HasanMu/univ-s4-mobile-uas |

---

## 3. Tech Stack & Batasan Teknis (GROUND TRUTH dari repo)

Ini fakta aktual dari hasil clone repo — **patuhi ini, jangan berasumsi lain (khususnya: BUKAN Jetpack Compose).**

| Aspek | Nilai |
|---|---|
| Bahasa | Kotlin |
| UI toolkit | **Android Views + layout XML** (ConstraintLayout). **BUKAN Jetpack Compose.** |
| Package | `com.kelompok1.materialku` |
| minSdk | **30** (Android 11) |
| targetSdk / compileSdk | 36 |
| AGP | 9.0.1 |
| Java compatibility | 11 |
| Arsitektur | **MVVM + Clean Architecture** (3 layer: Presentation / Domain / Data) |
| Persistensi | **Room (SQLite)** untuk data operasional; **Jetpack DataStore** untuk preferensi/sesi |
| DI | **Hilt** (diasumsikan di desain; belum dipasang di repo) |
| Navigasi | **Navigation Component** (`nav_graph.xml` + `NavHostFragment`) — single-activity |
| Konektivitas | **Fully offline.** Tidak ada API/jaringan. |
| Perangkat eksternal | Thermal printer via **Bluetooth SPP (Serial Port Profile)** |
| Ekspor | Laporan PDF via Android `PdfDocument` API |

**Kondisi repo saat ini:** masih **template "Empty Views Activity" polos**. Baru ada `MainActivity.kt` (AppCompatActivity, `setContentView(R.layout.activity_main)`) dan `activity_main.xml` (ConstraintLayout "Hello World"). Dependensi baru bawaan template (core-ktx, appcompat, material, activity, constraintlayout). **Belum ada** Room, ViewModel/lifecycle, Navigation, Hilt, coroutines, atau kode domain/data apa pun. Artinya implementasi praktis dimulai dari nol mengikuti desain di dokumen ini.

---

## 4. Arsitektur — MVVM + Clean Architecture

Tiga layer dengan aturan dependency satu arah.

### Presentation Layer (Views/XML)
| Komponen | Tipe | Tanggung jawab |
|---|---|---|
| `MaterialKuApp` | Application | Entry point; inisialisasi Hilt; konfigurasi global |
| `MainActivity` | Activity | Single-activity host; menampung `NavHostFragment` (Navigation Component) |
| `NavGraph` | Navigation | `nav_graph.xml`: definisi seluruh rute & argument passing antar screen |
| Fragment (Screen) | Boundary | Tiap layar = satu Fragment + layout XML + adapter RecyclerView; mengamati ViewModel |
| ViewModel | Control | Menjembatani UI ↔ Domain; menyiapkan state (LiveData/StateFlow) |
| `StrukBottomSheet` | DialogFragment | `BottomSheetDialogFragment`: struk digital pasca transaksi; opsi cetak |
| `StrukPrinter` | Peripheral | Koneksi Bluetooth ke thermal printer; format byte struk |
| `PdfExporter` | Exporter | Generate laporan PDF via Android `PdfDocument` API |

### Domain Layer (bebas framework)
| Komponen | Tanggung jawab |
|---|---|
| `MaterialService` | Orkestrator CRUD material; validasi duplikasi kode |
| `StokService` | Kalkulasi saldo stok; deteksi stok kritis |
| `TransaksiService` | Buat & selesaikan transaksi; trigger `StokService` |
| `LaporanService` | Agregasi data penjualan & stok berdasarkan periode |
| `AuthService` | Verifikasi kredensial; manajemen sesi; kontrol role |
| Interface repository | `IMaterialRepository`, `IStokRepository`, `ITransaksiRepository`, `IUserRepository` |

### Data Layer
| Komponen | Tanggung jawab |
|---|---|
| `MaterialRepository` | Implementasi `IMaterialRepository` via `MaterialDao` (Room) |
| `StokRepository` | Implementasi `IStokRepository`; query log dengan filter |
| `TransaksiRepository` | Simpan `Transaksi` + `ItemTransaksi` dalam Room `@Transaction` |
| `UserRepository` | CRUD user; verifikasi password hash (bcrypt) |
| `MaterialKuDatabase` | Room database tunggal; singleton via Hilt; versi 1 |
| `PreferencesDataStore` | Simpan sesi login terakhir & preferensi printer |

### Aturan dependency (WAJIB)
- Presentation → Domain: **diizinkan** (ViewModel memakai UseCase/Service).
- Data → Domain: **diizinkan** (RepositoryImpl meng-implement interface Domain / `..|>` realize).
- Presentation → Data: **dilarang** (UI tidak boleh langsung menyentuh DAO/Repository).
- Domain → Presentation/Data: **dilarang** (Domain harus bebas dari semua framework).

### Topologi deployment (fully offline)
`Android Device (minSdk 30 / Android 11, ARM64)` memuat artifact `materialku.apk`, yang berkomunikasi in-process dengan: `materialku.db` (Room SQLite, "Room API"), `user_prefs.pb` (DataStore API), `laporan_*.pdf` (File I/O). `StrukPrinter` terhubung ke `Thermal Printer` eksternal via **Bluetooth SPP**.

---

## 5. Domain Model — Entity & Enum

**8 entity + 3 enum.** Visibilitas: semua atribut `-` private (enkapsulasi), method `+` public. Tipe data Kotlin: `Int`, `String`, `Double`, `Boolean`, `DateTime` (= `java.time.LocalDateTime`), `LocalDate`, enum class. Room: `@PrimaryKey(autoGenerate = true)` untuk `id`.

### Entity

| Class | Atribut | Method |
|---|---|---|
| **Material** | `id:Int`, `kode:String`, `nama:String`, `hargaJual:Double`, `stokSaat:Int`, `kategoriId:Int`, `satuanId:Int` | `isStokKritis():Boolean`, `formatHarga():String`, `updateStok(qty:Int)`, `getKategori():Kategori` |
| **Kategori** | `id:Int`, `nama:String`, `deskripsi:String`, `createdAt:LocalDate`, `aktif:Boolean` | `getNama():String`, `getMaterials():List` |
| **Satuan** | `id:Int`, `nama:String`, `simbol:String` | `getNama():String` |
| **Supplier** | `id:Int`, `nama:String`, `kontak:String`, `alamat:String`, `aktif:Boolean` | `getNama():String`, `getMaterials():List` |
| **Transaksi** | `id:Int`, `noFaktur:String`, `tanggal:DateTime`, `totalHarga:Double`, `status:StatusTransaksi`, `userId:Int` | `hitungTotal():Double`, `selesaikan()`, `batalkan()`, `getItems():List` |
| **ItemTransaksi** | `id:Int`, `transaksiId:Int`, `materialId:Int`, `qty:Int`, `hargaSatuan:Double`, `subtotal:Double` | `hitungSubtotal():Double`, `getMaterial():Material` |
| **StokLog** | `id:Int`, `materialId:Int`, `jenis:JenisStok`, `qty:Int`, `tanggal:LocalDate`, `keterangan:String` | `getJenis():JenisStok`, `getTanggal():LocalDate` |
| **User** | `id:Int`, `username:String`, `passwordHash:String`, `role:RoleEnum`, `aktif:Boolean`, `lastLogin:DateTime` | `hasRole(r:RoleEnum):Boolean`, `isAktif():Boolean` |

### Enum
- `RoleEnum`: `ROLE_ADMIN`, `ROLE_KASIR`, `ROLE_GUDANG`, `ROLE_MANAGER`
- `StatusTransaksi`: `DRAFT`, `SELESAI`, `BATAL`
- `JenisStok`: `MASUK`, `KELUAR`

---

## 6. Relasi Antar Class (11 relasi domain)

| Class A | Mult. | Tipe | Mult. | Class B | Keterangan |
|---|---|---|---|---|---|
| Kategori | 0..1 | Aggregation | 0..* | Material | Kategori mengelompokkan banyak material; material bisa tanpa kategori |
| Material | 1 | Association | 1 | Satuan | Setiap material punya tepat satu satuan ukur |
| Supplier | 1..* | Association | 0..* | Material | **M:N — lihat Keputusan Desain Terbuka (Bagian 7)** |
| Material | 1 | Composition | 0..* | StokLog | Log stok tidak bermakna tanpa material; ikut terhapus |
| Transaksi | 1 | Composition | 1..* | ItemTransaksi | Item hanya ada dalam konteks transaksinya; ikut terhapus |
| ItemTransaksi | 0..* | Association | 1 | Material | Item mengacu material yang dijual, tidak memilikinya |
| User | 1 | Association | 0..* | Transaksi | Kasir mencatat banyak transaksi |
| User | 1 | Association | 0..* | StokLog | Petugas gudang membuat entri log stok |
| User | * | Dependency | 1 | RoleEnum | User bergantung pada enum untuk role-nya |
| Transaksi | 1 | Dependency | 1 | StatusTransaksi | Status transaksi via enum |
| StokLog | 1 | Dependency | 1 | JenisStok | Jenis mutasi via enum |

Rekap keseluruhan proyek (target): **31 class** (8 entity + 3 enum + 6 ViewModel + 6 UseCase + 8 Screen) dan **38 relasi** (11 domain + 7 MVVM + 10 UseCase-Repository + 10 Component).

---

## 7. KEPUTUSAN DESAIN TERBUKA — Supplier ↔ Material

> **BELUM DIPUTUSKAN.** Ini wajib ditanyakan/diputuskan sebelum membuat entity Room & mapping database.

Relasi Supplier–Material di class diagram bersifat **many-to-many (M:N)**: satu supplier memasok banyak material, satu material bisa dipasok banyak supplier. Database relasional tidak bisa menyimpan M:N langsung. Dua opsi:

- **Pilihan A — pertahankan M:N.** Tambah entity penghubung `MaterialSupplier` (composite PK `materialId` + `supplierId`), query via Room `@Relation` + `@Junction`. Konsekuensi: jumlah entity jadi 9; relasi bertambah. Lebih realistis secara domain.
- **Pilihan B — sederhanakan jadi 1:N.** Tambah `supplierId:Int` pada entity `Material` (sejajar `kategoriId`, `satuanId`). Relasi jadi Supplier `1` —— `0..*` Material. Tanpa junction. Lebih sederhana.

**Rekomendasi saat ini: Pilihan B**, karena tidak ada use case yang mengelola pembelian/perbandingan supplier — supplier hanya data referensi. (Putuskan final bersama dosen/tim.)

---

## 8. Use Case & Aktor

**4 aktor:** Admin (`ROLE_ADMIN`), Kasir (`ROLE_KASIR`), Gudang (`ROLE_GUDANG`), Manager (`ROLE_MANAGER`).

| Aktor | Use case |
|---|---|
| Admin | Kelola Pengguna, Kelola Material (CRUD), Kelola Kategori & Satuan, Lihat Laporan |
| Kasir | Input Transaksi Penjualan (POS), Lihat Stok |
| Gudang | Catat Mutasi Stok (Masuk/Keluar), Lihat Stok |
| Manager | Dashboard Analitik, Lihat Laporan, Lihat Stok |
| Semua | Login & Autentikasi |

**Relasi:**
- `«include»` (wajib): Input Transaksi → Cari/Lihat Material; Input Transaksi → Perbarui Stok; Catat Mutasi Stok → Perbarui Stok.
- `«extend»` (opsional): Cetak Struk → Input Transaksi; Ekspor Laporan PDF → Lihat Laporan.
- **Login = precondition sesi, BUKAN `«include»`.** Dilakukan sekali di awal sesi, bukan sub-langkah tiap use case. Jangan tarik panah include dari semua use case ke Login. Tidak ada entity "Login" terpisah — autentikasi ditangani `User` + `AuthService`.

---

## 9. Aturan Bisnis, Validasi & Boundary Conditions

| Kondisi | Risiko | Respon sistem | Penanggung jawab |
|---|---|---|---|
| Qty / harga ≤ 0 atau kosong | Total salah, stok minus | Tolak input + pesan validasi | ItemTransaksi / TransaksiService / ViewModel |
| Stok tidak cukup saat penjualan | Stok negatif | Batasi qty sesuai stok, tolak transaksi | StokService |
| Kode material duplikat | Data ganda | Tolak simpan, minta kode unik | MaterialService |
| Login gagal / role tak berwenang | Akses tidak sah | Tolak akses; sembunyikan menu per role | AuthService |
| Database gagal menyimpan | Data tersimpan separuh | Rollback `@Transaction` + pesan gagal | Repository / Room |
| Printer tidak terhubung saat cetak | Transaksi gagal disimpan | Simpan transaksi tetap berhasil; cetak ditunda/ulang | StrukPrinter |

Konsistensi data yang dijaga: `Material.stokSaat` vs akumulasi `StokLog`; `Transaksi.totalHarga` vs jumlah `ItemTransaksi.subtotal`. Password disimpan sebagai hash **bcrypt**, bukan plaintext.

### State transaksi
`DRAFT → SELESAI` (saat diselesaikan; stok dipotong) atau `DRAFT → BATAL`. Pembatalan transaksi yang sudah memotong stok harus mengembalikan stok.

---

## 10. Concurrency

Konteks penting: **aplikasi lokal satu perangkat**, jadi concurrency bukan multi-user web, melainkan soal thread/coroutine & interaksi UI.

| Proses bersamaan | Masalah | Strategi |
|---|---|---|
| Tap ganda tombol simpan transaksi | Transaksi & potong stok 2× | Disable tombol saat proses; debounce/status control; idempotensi |
| Penjualan & mutasi stok menulis material hampir bersamaan | Race condition, saldo tak konsisten | Room `@Transaction` (atomik); single source of truth via repository; coroutine + `Mutex` |
| Cetak struk (Bluetooth) sambil menyimpan | UI freeze; cetak sebelum commit | I/O di `Dispatchers.IO`; cetak hanya setelah commit berhasil |

---

## 11. Event Penting

| Event | Pemicu | Penangan | Output / perubahan status |
|---|---|---|---|
| Input transaksi penjualan | Kasir simpan | TransaksiService + StokService | Transaksi DRAFT→SELESAI; stok berkurang; struk siap |
| Pembatalan transaksi | Kasir/Admin | TransaksiService | Status →BATAL; stok dikembalikan bila perlu |
| Catat mutasi stok | Gudang | StokService | StokLog baru; `Material.stokSaat` diperbarui |
| Stok mencapai titik kritis | `stokSaat` ≤ ambang | `StokService` / `Material.isStokKritis()` | Flag stok kritis pada UI |
| Login | User submit kredensial | AuthService | Sesi dibuat; role diverifikasi; `lastLogin` diperbarui |

---

## 12. Mapping Object → Database (Room)

Pedoman: Class → Table, Attribute → Column, Object → Row, Association 1:N → Foreign Key di sisi many, Association M:N → tabel penghubung.

Contoh skema (asumsi Pilihan B untuk Supplier):
```
kategori(id PK, nama, deskripsi, createdAt, aktif)
satuan(id PK, nama, simbol)
supplier(id PK, nama, kontak, alamat, aktif)
material(id PK, kode, nama, hargaJual, stokSaat,
         kategoriId FK->kategori, satuanId FK->satuan, supplierId FK->supplier)
user(id PK, username, passwordHash, role, aktif, lastLogin)
transaksi(id PK, noFaktur, tanggal, totalHarga, status, userId FK->user)
item_transaksi(id PK, transaksiId FK->transaksi, materialId FK->material,
               qty, hargaSatuan, subtotal)
stok_log(id PK, materialId FK->material, jenis, qty, tanggal, keterangan)
```
Jika Pilihan A: hapus `supplierId` dari `material`, tambah `material_supplier(materialId FK, supplierId FK, PK gabungan)`.

---

## 13. Konvensi & Standar

### Penamaan
Gunakan nama resmi entity konsisten di semua dokumen & kode: `Material`, `Kategori`, `Satuan`, `Supplier`, `Transaksi`, `ItemTransaksi`, `StokLog`, `User` (catatan: **`StokLog`**, bukan "StockLog"). Service: `<Domain>Service`. ViewModel: `<Domain>ViewModel`. Repository interface diawali `I`.

### Gaya dokumen (penting untuk deliverable tertulis)
- **Formal, minim warna.** Skema dua-tone: header tabel/judul **navy** (`1e3a5f`), isi/box **abu-abu muda** (`f3f4f6`), teks **gelap** (`1f2937`). Hindari banyak warna callout (kesan "AI").
- **Tanpa simbol/emoji dekoratif**: jangan pakai ✅ ❌ ◆ ◇ → △ ▷. Tulis sebagai teks biasa ("Terpenuhi", "Diizinkan", "Aggregation", dst.).
- **Boleh**: em dash (—) dan tanda kutip melengkung (" " ' ') — itu tipografi formal yang benar.
- UML stereotype `«include»`, `«extend»`, `«use»`, `«realize»` tetap dipakai (notasi sah, bukan dekorasi).

### PlantUML (notasi relasi)
`o--` aggregation, `*--` composition, `-->` association, `..>` dependency, `..|>` realization. Multiplisitas dalam tanda kutip di tiap ujung.

---

## 14. Konteks Akademik

### OOAD Milestone 2 (struktural) — SELESAI
Dokumen berisi 4 diagram struktural (Class, Object, Component, Deployment) + 1 Use Case (pelengkap behavioral) + checklist verifikasi.

### Modul Pertemuan 12-13 — Object-Oriented Design & Implementation Strategies
Cakupan: system design, OO decomposition, concurrency, design pattern, controlling events, boundary conditions (P12); object design, implementation of control (Boundary/Control/Entity), packaging classes, design optimization (low coupling/high cohesion), implementing associations/constraints/state charts, **object mapping to database** (P13).

Klasifikasi class ala modul (Boundary/Control/Entity): Entity = 8 entity domain; Control = Service + ViewModel; Boundary = Fragment/Activity + `StrukPrinter`.

Catatan penyesuaian terhadap contoh modul: modul memakai pola **MVC** ("Controller Layer"), MaterialKu memakai **MVVM + Clean Architecture** → petakan "Controller" ke **ViewModel**, "Service Layer" ke **Domain Service/UseCase**. Contoh concurrency di modul bersifat web multi-user; untuk MaterialKu sesuaikan ke konteks single-device (lihat Bagian 10).

### Tugas Terstruktur P12-13 (deliverable)
Nama & deskripsi sistem; aktor & use case; system design/lapisan; OO decomposition; daftar class & tanggung jawab; event & boundary condition; concurrency & design pattern; object design (atribut/method/visibility/relasi); package diagram; constraint & state chart; **mapping object/association/inheritance ke database**; kesimpulan. Bobot rubrik terbesar pada ketepatan class/atribut/method/relasi (20) dan mapping DB (15).

### LKM Active Learning P12-13 — SEDANG DIKERJAKAN
Lembar kerja 8 aktivitas (Problem Trigger, Think-Pair-Share, System Design Canvas, OO Decomposition, Event Identification, Boundary Condition, Concurrency Analysis, Gallery Walk). Aktivitas 1-7 sudah ada draft jawaban; Aktivitas 8 menunggu feedback kelas.

---

## 15. Artefak yang Sudah Dibuat

- **Dokumen Milestone 2 (.docx)** — sudah dirapikan: formal, tanpa simbol, dua-tone, teks Compose→Views diperbaiki, gambar Deployment diperbarui ke SDK 30.
- **5 diagram PlantUML** — Class, Object, Component, Deployment (SDK 30), Use Case (file `.puml` terpisah dalam zip).
- **Draft jawaban LKM Pertemuan 12-13** — Aktivitas 1-7.

---

## 16. Status Saat Ini & Langkah Berikutnya

**Status:** desain selesai; repo masih template kosong; implementasi belum dimulai.

**Keputusan yang masih menggantung:**
1. Supplier–Material: Pilihan A atau B (lihat Bagian 7). **Belum final.**
2. DI: Hilt vs manual.
3. Struktur package: single-module berlapis vs multi-module Gradle.

**Langkah implementasi (urutan disarankan):**
1. Putuskan Supplier–Material → finalkan entity.
2. Tambah dependency Gradle: Room (+ kapt/ksp), ViewModel/lifecycle, Navigation Component, coroutines, (opsional) Hilt, library bcrypt, (opsional) library cetak Bluetooth.
3. Bentuk struktur package Clean Architecture (`presentation` / `domain` / `data`).
4. Implement entity Room + DAO + database; seeding user awal per role.
5. Repository → Service/UseCase → ViewModel → Fragment + layout XML.
6. Fitur: Login, CRUD Material, POS/transaksi (+ potong stok atomik), mutasi stok, laporan + ekspor PDF, cetak struk Bluetooth.

---

## 17. Skenario Contoh (dari Object Diagram)

Snapshot runtime transaksi **TRX-20250519-001** (untuk uji/seed):
- `budi : User` — id=3, username="budi_kasir", role=ROLE_KASIR, aktif=true, lastLogin=2025-05-19 08:00
- `trx001 : Transaksi` — id=101, noFaktur="TRX-20250519-001", tanggal=2025-05-19 10:32:15, totalHarga=175000.0, status=SELESAI, userId=3
- `item1 : ItemTransaksi` — id=201, transaksiId=101, materialId=11, qty=2, hargaSatuan=65000.0, subtotal=130000.0
- `item2 : ItemTransaksi` — id=202, transaksiId=101, materialId=17, qty=1, hargaSatuan=45000.0, subtotal=45000.0
- `semen : Material` — id=11, kode="MTR-011", nama="Semen Tiga Roda", hargaJual=65000.0, stokSaat=48
- `pasir : Material` — id=17, kode="MTR-017", nama="Pasir Bangka", hargaJual=45000.0, stokSaat=12

Verifikasi: 2×65000 + 1×45000 = 175000. Konsisten.
