# APK Release

Folder ini berisi APK release siap install untuk MaterialKu.

## Cara install

1. Download `app-release.apk`
2. Di perangkat Android: aktifkan **"Install unknown apps"** untuk browser/file manager yang dipakai
3. Buka file APK → **Install**
4. Login default (bcrypt-hashed, di-seed saat instalasi pertama):

| Role | Username | Password |
|---|---|---|
| ADMIN | `admin` | `admin123` |
| KASIR | `kasir` | `kasir123` |
| GUDANG | `gudang` | `gudang123` |
| MANAGER | `manager` | `manager123` |

## Detail build

- **Version**: 1.1.0 (versionCode 2)
- **Signing**: RSA 2048, self-signed (SHA384withRSA), valid 10.000 hari
- **minSdk**: 30 (Android 11)
- **targetSdk**: 36 (Android 15+)
- **Package**: `com.kelompok1.materialku`
- **Ukuran**: ~17 MB

## Build ulang dari source

```bash
./gradlew.bat assembleRelease
```

Output APK ada di `app/build/outputs/apk/release/app-release.apk`. Butuh `keystore/materialku-release.jks` + kredensial di `local.properties` (lihat `README.md` root).

## Lihat juga

- [GitHub Releases](https://github.com/HasanMu/univ-s4-mobile-uas/releases) — versi historis + release notes
