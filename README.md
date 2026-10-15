# Boostly - Social Growth & Authenticity Platform

<p align="center">
  <strong>Follow. Earn. Grow.</strong><br>
  <em>Platform pertukaran follow sosial media autentik, gamifikasi koin, kecerdasan buatan (Gemini AI), dan integrasi backend mandiri Laravel 11 + MySQL.</em>
</p>

---

## 📱 Tentang Aplikasi

**Boostly** adalah aplikasi Android berbasis **Kotlin** dan **Jetpack Compose (Material 3)** yang dirancang untuk membantu kreator konten dan pemilik bisnis mikro mengembangkan akun sosial media (Instagram, TikTok, YouTube, X/Twitter, Threads) secara organik dan saling menguntungkan (*win-win*).

Aplikasi ini menggunakan sistem **Offline-First Architecture** dengan **Room Database** lokal yang dapat disinkronkan secara mulus ke **Laravel 11 REST API & MySQL** mandiri, serta mendukung opsi integrasi **Firebase Auth & Cloud Firestore**.

---

## 🚀 Fitur Unggulan

### 1. 🪙 Sistem Koin & Dompet Transaksi (Boostly Wallet)
- **Saldo Koin Real-Time**: Visualisasi saldo koin, konversi nilai Rupiah (1 Koin = Rp 100), dan visual 3D Coin Badge.
- **Riwayat Mutasi Lengkap**: Pencatatan mutasi kredit/debit (*Earned Task, Exchanged Campaign, Daily Bonus, QRIS Topup*).
- **Filter & Pencarian**: Filter transaksi berdasarkan kategori (Semua, Koin Masuk, Koin Keluar) serta pencarian ID/keterangan transaksi.
- **Modal Struk Bukti Transaksi**: Menampilkan struk resmi transaksi digital dengan kode referensi unik (contoh: `#BST-FLW-92812`).

### 2. 🛡️ Sistem Anti-Curang & Skor Keaslian (Authenticity Score)
- **Skor Keaslian Dinamis (0 - 100%)**: Setiap akun memiliki skor reputasi yang berkurang jika terdeteksi melakukan tindakan curang (*unfollow* sepihak, pembatalan tugas).
- **Audit Pelanggaran**: Panel audit riwayat verifikasi tugas dan sanksi pembekuan akun (*auto-ban*) jika pelanggaran melampaui batas toleransi.
- **Proteksi Diri**: Sistem otomatis menolak pengguna yang mencoba mem-follow kampanye miliknya sendiri.

### 3. 🔐 Layar Autentikasi Bersih (Auth & Onboarding)
- **Dua Metode Autentikasi**:
  - Email & Password dengan validasi format instan.
  - **Google Sign-In** resmi menggunakan **Jetpack Credential Manager** & **GoogleAuthProvider**.
- **Akses Pengujian Cepat (*Quick Login*)**:
  - **Demo Creator**: `rian@gmail.com` (Role: USER)
  - **Administrator**: `admin@sosmedgrow.com` (Role: ADMIN)
- **Bonus Selamat Datang**: Pengguna baru otomatis memperoleh bonus awal **150 koin**.

### 4. 📅 Check-in Harian & Notifikasi Otomatis
- **Streak Counter Bertingkat**: Bonus koin harian yang meningkat seiring konsistensi check-in pengguna.
- **Pengingat Notifikasi Lokal**: Menggunakan `AlarmManager` dan Android Notification Channels untuk mengingatkan check-in harian pada waktu yang ditentukan pengguna.

### 5. 🤖 Analisis Karir Kreator (Gemini AI)
- Deteksi otomatis potensi niche, keahlian utama, dan rekomendasi strategi pertumbuhan konten berdasarkan bio profil atau handle sosial media pengguna.

### 6. 🌓 Global Theme Toggle (Light & Dark Mode)
- Dukungan tema gelap (*Dark Mode*) berdensitas tinggi dengan aksen *Forest Green & Gold*, serta tema terang (*Light Mode*) yang bersih dan ramah mata.
- Pengalihan cepat langsung dari top bar Beranda maupun menu Pengaturan Profil.

---

## 🛠️ Tech Stack & Arsitektur

| Komponen | Teknologi |
|---|---|
| **Bahasa Pemrograman** | Kotlin 2.x |
| **UI Framework** | Jetpack Compose (Material Design 3) |
| **Arsitektur Aplikasi** | MVVM (Model-View-ViewModel) + Clean Architecture |
| **Asynchronous & Reactive** | Kotlin Coroutines, StateFlow, Flow |
| **Penyimpanan Lokal** | Android Jetpack Room Database (SQLite) via KSP |
| **Jaringan & REST API** | Square Retrofit 2 + OkHttp + Moshi Converter |
| **Backend Mandiri** | **Laravel 11.x + MySQL** (Sanctum Bearer Token) |
| **Opsi Cloud Backend** | Firebase Authentication & Cloud Firestore |
| **Pemuatan Gambar** | Coil Compose |
| **Pengujian Unit** | JUnit 4, Robolectric, Coroutines Test |

---

## 🗄️ Dokumentasi Skema Database MySQL (`boostly`)

### 1. Tabel `users`
Menyimpan kredensial pengguna, saldo koin, skor reputasi, dan tautan sosial media.
```sql
CREATE TABLE `users` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL UNIQUE,
  `password` varchar(255) NOT NULL,
  `role` enum('USER','ADMIN') NOT NULL DEFAULT 'USER',
  `points` int NOT NULL DEFAULT '150',
  `authenticity_score` decimal(5,2) NOT NULL DEFAULT '100.00',
  `violations_count` int NOT NULL DEFAULT '0',
  `is_banned` tinyint(1) NOT NULL DEFAULT '0',
  `instagram_handle` varchar(255) NULL DEFAULT '',
  `tiktok_handle` varchar(255) NULL DEFAULT '',
  `twitter_handle` varchar(255) NULL DEFAULT '',
  `youtube_handle` varchar(255) NULL DEFAULT '',
  `email_verified_at` timestamp NULL,
  `remember_token` varchar(100) NULL,
  `created_at` timestamp NULL,
  `updated_at` timestamp NULL
);
```

### 2. Tabel `campaigns`
Menyimpan tugas promosi follow sosial media yang dialokasikan oleh kreator.
```sql
CREATE TABLE `campaigns` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `creator_id` bigint unsigned NOT NULL,
  `creator_name` varchar(255) NOT NULL,
  `platform` varchar(255) NOT NULL,
  `target_url` varchar(255) NOT NULL,
  `account_handle` varchar(255) NOT NULL,
  `category` varchar(255) NOT NULL DEFAULT 'Lifestyle',
  `target_followers` int NOT NULL,
  `current_followers` int NOT NULL DEFAULT '0',
  `coin_reward_per_follow` int NOT NULL DEFAULT '10',
  `total_coins_allocated` int NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NULL,
  `updated_at` timestamp NULL,
  FOREIGN KEY (`creator_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
);
```

### 3. Tabel `coin_transactions`
Mencatat seluruh mutasi saldo koin secara transparan dan berurutan.
```sql
CREATE TABLE `coin_transactions` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` bigint unsigned NOT NULL,
  `amount` int NOT NULL,
  `type` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `timestamp` bigint NOT NULL,
  `created_at` timestamp NULL,
  `updated_at` timestamp NULL,
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
);
```

### 4. Tabel `daily_check_ins`
Mencatat riwayat check-in harian per pengguna untuk validasi bonus dan *anti-cheat*.
```sql
CREATE TABLE `daily_check_ins` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` bigint unsigned NOT NULL,
  `check_in_date_key` varchar(255) NOT NULL,
  `streak_day` int NOT NULL,
  `reward_coins` int NOT NULL,
  `timestamp` bigint NOT NULL,
  `timezone_id` varchar(255) NOT NULL DEFAULT 'WIB',
  `time_formatted` varchar(255) NULL,
  `created_at` timestamp NULL,
  `updated_at` timestamp NULL,
  UNIQUE KEY `daily_check_ins_user_id_check_in_date_key_unique` (`user_id`,`check_in_date_key`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
);
```

---

## 🌐 Dokumentasi REST API Laravel (`routes/api.php`)

### Format Baku Response JSON (`LaravelApiResponse<T>`)
Semua endpoint mengembalikan struktur respons terstandar:
```json
{
  "status": "success",
  "message": "Pesan status keberhasilan/kegagalan",
  "data": { ... },
  "timestamp": 1729000000000
}
```

### Daftar Endpoint API

| Method | Endpoint | Autentikasi | Keterangan |
|---|---|---|---|
| `GET` | `/api/health` | Publik | Tes status koneksi server, versi API, dan koneksi MySQL |
| `POST` | `/api/auth/register` | Publik | Registrasi akun baru (otomatis mendapat bonus 150 koin) |
| `POST` | `/api/auth/login` | Publik | Login email & password, mengembalikan Bearer Token Sanctum |
| `GET` | `/api/auth/user` | Sanctum | Mendapatkan data profil dan status akun yang sedang login |
| `POST` | `/api/auth/logout` | Sanctum | Menghapus token otorisasi sesi Android |
| `GET` | `/api/sync/pull` | Sanctum | Mengunduh profil, kampanye, dan transaksi terbaru ke Room DB |
| `POST` | `/api/sync/push` | Sanctum | Mengunggah tugas yang dikerjakan saat offline ke server |
| `POST` | `/api/coins/adjust` | Sanctum | Menambah/mengurangi saldo koin aman (*row locking / race condition safe*) |
| `GET` | `/api/coins/balance` | Sanctum | Cek saldo koin aktif dan riwayat transaksi pengguna |
| `POST` | `/api/campaigns` | Sanctum | Membuat kampanye baru (memotong kuota koin secara otomatis) |
| `GET` | `/api/campaigns` | Sanctum | Mengambil daftar kampanye yang sedang aktif |
| `POST` | `/api/daily-checkin` | Sanctum | Mengklaim check-in harian dengan streak counter bertingkat |
| `POST` | `/api/payments/callback` | Publik | Webhook konfirmasi pembayaran QRIS otomatis |

---

## ⚙️ Panduan Menghubungkan Aplikasi Android ke Laravel API

### 1. Menjalankan Server Laravel di PC / Laptop
Jalankan perintah berikut di direktori proyek Laravel Anda:
```bash
php artisan serve --host=0.0.0.0 --port=8000
```

### 2. Konfigurasi URL di Aplikasi Android Boostly
1. Buka aplikasi Boostly di emulator atau HP fisik.
2. Di layar Beranda atau menu Profil, klik kartu **"Sync Laravel"** atau **"Koneksi Database"**.
3. Tentukan **Base URL**:
   - **Android Emulator**: `http://10.0.2.2:8000/api/`
   - **HP Fisik (WiFi Sama)**: `http://<IP-LAPTOP-ANDA>:8000/api/` (contoh: `http://192.168.1.15:8000/api/`)
   - **Hosting / VPS Online**: `https://api.domainanda.com/api/`
4. Masukkan **Bearer Token** akun Anda.
5. Klik **"Tes Koneksi"**. Indikator akan berubah menjadi hijau **"Online (Terhubung)"** dengan informasi latensi server.
6. Klik **"Sinkronkan Sekarang"** untuk melakukan sinkronisasi data dua arah antara Room DB dan MySQL.

---

## 🧪 Menjalankan Pengujian

Untuk memverifikasi integritas seluruh komponen, ViewModel, dan Room Repository:
```bash
gradle :app:testDebugUnitTest
```
*Seluruh unit test teruji 100% lulus tanpa kegagalan.*

---

## 📄 Lisensi
Hak Cipta &copy; 2026 Boostly Platform. Seluruh hak cipta dilindungi undang-undang.
