# MaterialKu v1.1.0

Rilis minor pertama setelah v1.0.0. Menambahkan fitur **CRUD Supplier**, memperbaiki tata letak dashboard per role, dan membersihkan menu Pengaturan.

## 📦 Download

APK signed siap install: [`app-release.apk`](https://github.com/HasanMu/univ-s4-mobile-uas/blob/master/apk/app-release.apk) (~17 MB)

Login default (di-seed saat instalasi pertama; sama seperti v1.0.0):

| Role | Username | Password |
|---|---|---|
| ADMIN | `admin` | `admin123` |
| KASIR | `kasir` | `kasir123` |
| GUDANG | `gudang` | `gudang123` |
| MANAGER | `manager` | `manager123` |

## ✨ Yang baru sejak v1.0.0

### 🟣 Fitur baru — Supplier CRUD (#23)
- Layar **Supplier** di dashboard (visible untuk ADMIN / GUDANG / MANAGER)
- Tambah / edit / hapus supplier via dialog form (nama, kontak, alamat, status aktif)
- Validasi nama wajib & unik case-insensitive
- Delete aman — material yang terkait tetap ada, `supplierId` di-set null via FK `onDelete=SET_NULL`
- Data awal: 3 supplier seed (Toko Maju, Sumber Teknik, Umum)

### 🔴 Perbaikan — Dashboard auto re-pack (#23)
- Sebelumnya kartu menu yang tidak sesuai role di-set `INVISIBLE`, meninggalkan gap kosong (misal kasir cuma lihat POS di baris 2, baris 1 kosong)
- Sekarang kartu visible di-detach & disusun ulang **2-per-baris dari atas**; row unused → `GONE`
- Baris ganjil terakhir dapat `Space` filler biar kartu tetap 50% lebar (tidak melar full-width)

### 🧹 Cleanup — Menu Pengaturan (#24)
- Row **"Kebijakan Privasi"** & **"Bantuan"** dihapus (dulunya cuma toast "Segera hadir")
- Icon di row **Mode Gelap** (`ic_dark_mode` / moon) & **Versi Aplikasi** (`ic_info`) diisi — sebelumnya kotak berwarna kosong

## 👥 Kontributor v1.1.0

- **Rafie** — Supplier CRUD
- **Hasan** — Dashboard auto re-pack, Settings cleanup, release config

## 🔄 Upgrade dari v1.0.0

APK ini tandanya sama (RSA 2048 self-signed dari `keystore/materialku-release.jks`), bisa install langsung menimpa v1.0.0 — data lokal (login, transaksi, master data) tetap aman. Tidak ada perubahan skema database di rilis ini.

## 🏗️ Tech stack

Tidak berubah dari v1.0.0 — Kotlin + Jetpack (Room, Navigation, Hilt, DataStore, ViewBinding), Material Components 3, minSdk 30, targetSdk 36.

## 📝 Catatan

- Bluetooth thermal printer support masih pending — format struk 58mm sudah siap dikirim ke `BluetoothSocket.outputStream` via SPP UUID standar, tinggal wire hardware layer
- Refund / batal transaksi tetap out-of-scope (lihat `docs/DECISIONS.md`)
