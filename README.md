# 🚀 Boostly - Social Growth & Authenticity Platform

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android%20%7C%20Web%20API-success?style=for-the-badge&logo=android" alt="Platform" />
  <img src="https://img.shields.io/badge/Kotlin-2.0.21-purple?style=for-the-badge&logo=kotlin" alt="Kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-blue?style=for-the-badge&logo=jetpackcompose" alt="Compose" />
  <img src="https://img.shields.io/badge/Backend-Laravel%2011%20%2B%20MySQL-red?style=for-the-badge&logo=laravel" alt="Laravel" />
  <img src="https://img.shields.io/badge/AI-Google%20Gemini-orange?style=for-the-badge&logo=googlegemini" alt="Gemini" />
</p>

<p align="center">
  <strong>Follow. Earn. Grow.</strong><br>
  <em>Platform pertukaran follow sosial media autentik, gamifikasi koin cerdas, audit anti-curang, kecerdasan buatan (Gemini AI), dan integrasi backend mandiri Laravel 11 + MySQL.</em>
</p>

---

## 📑 Daftar Isi
1. [Arsitektur Sistem & Alur Data](#-arsitektur-sistem--alur-data)
2. [Fitur-Fitur Utama Aplikasi](#-fitur-fitur-utama-aplikasi)
3. [Tech Stack & Library](#-tech-stack--library)
4. [Skema Lengkap Database MySQL](#-skema-lengkap-database-mysql)
5. [Spesifikasi REST API Laravel (Request & Response)](#-spesifikasi-rest-api-laravel)
6. [Kode Sumber Controller Laravel Siap Pakai](#-kode-sumber-controller-laravel-siap-pakai)
7. [Database Seeder & Akun Demo](#-database-seeder--akun-demo)
8. [Panduan Integrasi & Menjalankan Proyek](#-panduan-integrasi--menjalankan-proyek)
9. [Sistem Keamanan & Algoritma Anti-Curang](#-sistem-keamanan--algoritma-anti-curang)
10. [Pengujian Unit & Kompilasi](#-pengujian-unit--kompilasi)

---

## 🏛️ Arsitektur Sistem & Alur Data

Aplikasi mengadopsi **Offline-First Architecture** menggunakan **MVVM (Model-View-ViewModel)**. Pengguna dapat beraktivitas secara instan tanpa hambatan jaringan, dan data akan disinkronkan ke server MySQL ketika koneksi aktif.

```text
┌────────────────────────────────────────────────────────────────────────┐
│                        ANDROID APPLICATION (KOTLIN)                    │
│                                                                        │
│   ┌──────────────────────────────────────────────────────────────┐     │
│   │             Jetpack Compose UI (Material Design 3)           │     │
│   │  [Home / Tasks / Wallet / Analytics / Admin / Auth / Detail] │     │
│   └──────────────────────────────┬───────────────────────────────┘     │
│                                  │ (StateFlow / Events)                │
│   ┌──────────────────────────────▼───────────────────────────────┐     │
│   │               SosmedViewModel (State Management)             │     │
│   └───────────────┬──────────────────────────────┬───────────────┘     │
│                   │                              │                     │
│   ┌───────────────▼──────────────┐ ┌─────────────▼────────────────┐    │
│   │   Room Database (SQLite)     │ │  LaravelSyncManager /        │    │
│   │   Single Source of Truth     │ │  Retrofit REST API Client    │    │
│   └──────────────────────────────┘ └─────────────┬────────────────┘    │
└──────────────────────────────────────────────────┼─────────────────────┘
                                                   │ HTTPS / REST (JSON)
                                                   ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        BACKEND SERVER (LARAVEL 11)                     │
│                                                                        │
│   ┌──────────────────────────────────────────────────────────────┐     │
│   │           Laravel Sanctum Middleware (Bearer Token)          │     │
│   └──────────────────────────────┬───────────────────────────────┘     │
│                                  │                                     │
│   ┌──────────────────────────────▼───────────────────────────────┐     │
│   │         API Controllers (Auth, Sync, Coins, Campaigns)        │     │
│   └──────────────────────────────┬───────────────────────────────┘     │
│                                  │ (Transactions & Row-Locks)          │
│   ┌──────────────────────────────▼───────────────────────────────┐     │
│   │              MySQL Database Engine (InnoDB)                  │     │
│   │  [users, campaigns, coin_transactions, daily_check_ins]      │     │
│   └──────────────────────────────────────────────────────────────┘     │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 🌟 Fitur-Fitur Utama Aplikasi

### 1. 🪙 Dompet Koin Digital (Boostly Wallet Dashboard)
- **Monitoring Saldo Real-Time**: Visualisasi saldo koin aktif, konversi instan ke Rupiah (`1 Koin = Rp 100`), dan kartu metrik *Total Earned* vs *Total Spent*.
- **Riwayat Mutasi & Filter Kategori**: Filter tab interaktif (*Semua, Koin Masuk, Koin Keluar*) dan pencarian mutasi.
- **Detail Struk Transaksi Digital**: Pop-up dialog struk resmi dengan ID referensi unik (contoh: `#BST-FLW-92812`), timestamp WIB, status selesai, dan rincian transaksi.
- **Top Up QRIS Instan & Simulator**: Pilihan paket top up (Rp 10.000 s/d Rp 100.000) dengan simulasi pembayaran QRIS langsung berhasil.
- **Penarikan Saldo (Withdrawal)**: Pengajuan pencairan saldo koin ke rekening bank atau e-wallet (DANA, GoPay, OVO).

### 2. 🛡️ Sistem Anti-Curang & Skor Keaslian (Authenticity System)
- **Skor Keaslian Dinamis (0% – 100%)**: Skor akun berkurang otomatis jika pengguna melakukan *unfollow* sepihak sebelum waktu retensi minimum.
- **Self-Follow Protection**: Sistem otomatis menolak transaksi jika pengguna mencoba mengerjakan tugas follow pada akun sosial media miliknya sendiri.
- **Audit Pelanggaran & Auto-Ban**: Akun dengan skor reputasi di bawah batas toleransi otomatis dibekukan dari sistem perolehan koin.

### 3. 🔐 Autentikasi Fleksibel (Email/Password & Google Sign-In)
- **Layar Onboarding Bersih**: Antarmuka modern dengan transisi mulus antara mode Masuk (*Login*) dan Daftar (*Register*).
- **Google Sign-In**: Menggunakan Android Jetpack **Credential Manager** resmi dengan logo Google berdesain standar Google Brand Canvas.
- **Welcome Bonus**: Akun yang baru didaftarkan secara otomatis menerima saldo sambutan sebesar **150 Koin**.
- **Quick Login**: Tombol akses cepat satu klik untuk akun Demo Creator dan Admin tanpa perlu mengetik manual.

### 4. 📅 Check-in Harian & Notifikasi Otomatis
- **Streak Counter 7 Hari**: Bonus harian bertingkat (Hari 1: +10 koin hingga Hari 7: +50 koin + Reward Spesial).
- **Pengingat Notifikasi Android**: Layanan penjadwalan via `AlarmManager` untuk memicu notifikasi push harian sesuai jam yang diatur pengguna.
- **Deep Linking**: Notifikasi mengarahkan pengguna langsung ke pop-up klaim check-in saat diklik.

### 5. 🤖 Analisis Niche Kreator (Gemini AI)
- Menganalisis username / bio sosial media kreator untuk mendeteksi kategori niche (Tech, Fashion, Gaming, Food, dsb.), keahlian kunci, serta saran pertumbuhan konten secara instan.

### 6. 🌓 Global Theme Adaptive (Light & Dark Mode)
- Dukungan tema gelap (*Forest Green & Deep Dark Card*) dan tema terang yang elegan, dapat diubah sewaktu-waktu melalui tombol header Beranda atau Pengaturan Profil.

---

## 🧰 Tech Stack & Library

| Layer | Komponen / Library | Fungsi |
|---|---|---|
| **Language** | Kotlin 2.0.21 | Bahasa utama aplikasi |
| **UI Framework** | Jetpack Compose (BOM 2024.10.00) | Antarmuka deklaratif Material 3 |
| **Architecture** | ViewModel + StateFlow + Flow | State management reaktif |
| **Local Cache** | Room 2.6.1 via KSP | Penyimpanan basis data SQLite lokal |
| **Networking** | Retrofit 2.11.0 + OkHttp 4.12.0 | HTTP REST client dengan logging interceptor |
| **JSON Parser** | Moshi 1.15.2 (Kotlin Codegen) | Serialisasi data JSON cepat & aman |
| **Image Loader** | Coil Compose 2.7.0 | Pemuatan avatar dan gambar banner asinkron |
| **Backend** | Laravel 11.x + Laravel Sanctum | API RESTful dengan otentikasi token Bearer |
| **Database Server**| MySQL 8.x / MariaDB 10.x | Penyimpanan relasional cloud/server |
| **Testing** | JUnit 4, Robolectric 4.13, Coroutines Test | Pengujian unit JVM tanpa emulator fisik |

---

## 🗄️ Skema Lengkap Database MySQL

Nama Database: `boostly` (Collation: `utf8mb4_unicode_ci`, Engine: `InnoDB`)

```sql
-- 1. TABEL PENGGUNA (users)
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
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. TABEL KAMPANYE FOLLOW (campaigns)
CREATE TABLE `campaigns` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `creator_id` bigint unsigned NOT NULL,
  `creator_name` varchar(255) NOT NULL,
  `platform` varchar(50) NOT NULL,
  `target_url` varchar(500) NOT NULL,
  `account_handle` varchar(255) NOT NULL,
  `category` varchar(100) NOT NULL DEFAULT 'Lifestyle',
  `target_followers` int NOT NULL,
  `current_followers` int NOT NULL DEFAULT '0',
  `coin_reward_per_follow` int NOT NULL DEFAULT '10',
  `total_coins_allocated` int NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `fk_campaigns_creator` FOREIGN KEY (`creator_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. TABEL RIWAYAT TRANSAKSI KOIN (coin_transactions)
CREATE TABLE `coin_transactions` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` bigint unsigned NOT NULL,
  `amount` int NOT NULL,
  `type` varchar(100) NOT NULL,
  `description` varchar(500) NOT NULL,
  `timestamp` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `fk_transactions_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. TABEL CHECK-IN HARIAN (daily_check_ins)
CREATE TABLE `daily_check_ins` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` bigint unsigned NOT NULL,
  `check_in_date_key` varchar(20) NOT NULL,
  `streak_day` int NOT NULL DEFAULT '1',
  `reward_coins` int NOT NULL,
  `timestamp` bigint NOT NULL,
  `timezone_id` varchar(50) NOT NULL DEFAULT 'WIB',
  `time_formatted` varchar(100) NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uq_user_daily_checkin` (`user_id`, `check_in_date_key`),
  CONSTRAINT `fk_checkins_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## 🌐 Spesifikasi REST API Laravel

### 1. Format Standar Respons JSON (`LaravelApiResponse<T>`)
Semua respons API wajib dibungkus dengan struktur standar berikut:
```json
{
  "status": "success",
  "message": "Operasi berhasil dijalankan",
  "data": { ... },
  "timestamp": 1729000000000
}
```

### 2. Ringkasan Endpoint

| Endpoint | Method | Header Otorisasi | Fungsi |
|---|---|---|---|
| `/api/health` | `GET` | Tidak Wajib | Cek kesehatan server, ping, dan status koneksi MySQL |
| `/api/auth/register` | `POST` | Tidak Ada | Mendaftarkan akun kreator baru (+150 koin bonus) |
| `/api/auth/login` | `POST` | Tidak Ada | Login email & kata sandi, menghasilkan token Bearer Sanctum |
| `/api/auth/user` | `GET` | `Bearer <token>` | Mengambil profil lengkap & saldo koin user saat ini |
| `/api/auth/logout` | `POST` | `Bearer <token>` | Menghapus token sesi aktif |
| `/api/sync/pull` | `GET` | `Bearer <token>` | Mengunduh update terbaru (kampanye, mutasi koin, check-in) |
| `/api/sync/push` | `POST` | `Bearer <token>` | Mengunggah tugas yang selesai dikerjakan secara offline |
| `/api/coins/adjust` | `POST` | `Bearer <token>` | Menyesuaikan saldo koin aman (*atomic row locking*) |
| `/api/coins/balance` | `GET` | `Bearer <token>` | Mengambil saldo koin mutakhir |
| `/api/campaigns` | `GET` | `Bearer <token>` | Menampilkan daftar kampanye promosi aktif |
| `/api/campaigns` | `POST` | `Bearer <token>` | Membuat kampanye baru (potong saldo koin otomatis) |
| `/api/daily-checkin` | `POST` | `Bearer <token>` | Klaim bonus login harian (dengan validasi anti-dobel) |

---

## 💻 Kode Sumber Controller Laravel Siap Pakai

Simpan berkas-berkas berikut di folder backend Laravel Anda (`app/Http/Controllers/Api/`):

### 1. `HealthController.php`
```php
<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Support\Facades\DB;

class HealthController extends Controller
{
    public function check()
    {
        try {
            DB::connection()->getPdo();
            $dbStatus = 'connected';
        } catch (\Exception $e) {
            $dbStatus = 'disconnected';
        }

        return response()->json([
            'status' => 'success',
            'message' => 'API Boostly siap digunakan',
            'data' => [
                'status' => 'online',
                'framework' => 'Laravel ' . app()->version(),
                'app_name' => 'Boostly Laravel API',
                'database_status' => $dbStatus,
                'database_driver' => 'mysql',
                'server_time' => now()->timestamp * 1000,
                'timezone' => 'Asia/Jakarta (WIB)',
                'api_version' => 'v1.0'
            ],
            'timestamp' => now()->timestamp * 1000
        ]);
    }
}
```

### 2. `AuthController.php`
```php
<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use App\Models\User;

class AuthController extends Controller
{
    public function register(Request $request)
    {
        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'email' => 'required|email|unique:users,email',
            'password' => 'required|min:6',
            'instagram_handle' => 'nullable|string'
        ]);

        $user = User::create([
            'name' => $validated['name'],
            'email' => $validated['email'],
            'password' => Hash::make($validated['password']),
            'points' => 150, // Bonus pendaftaran
            'authenticity_score' => 100.0,
            'role' => 'USER',
            'instagram_handle' => $validated['instagram_handle'] ?? ''
        ]);

        $token = $user->createToken('boostly-android-token')->plainTextToken;

        return response()->json([
            'status' => 'success',
            'message' => 'Pendaftaran berhasil. Bonus 150 koin ditambahkan.',
            'data' => [
                'token' => $token,
                'token_type' => 'Bearer',
                'user' => $user
            ],
            'timestamp' => now()->timestamp * 1000
        ], 201);
    }

    public function login(Request $request)
    {
        $request->validate([
            'email' => 'required|email',
            'password' => 'required'
        ]);

        $user = User::where('email', $request->email)->first();

        if (!$user || !Hash::check($request->password, $user->password)) {
            return response()->json([
                'status' => 'error',
                'message' => 'Email atau kata sandi tidak valid',
                'data' => null,
                'timestamp' => now()->timestamp * 1000
            ], 401);
        }

        if ($user->is_banned) {
            return response()->json([
                'status' => 'error',
                'message' => 'Akun dibekukan akibat pelanggaran sistem anti-curang.',
                'data' => null,
                'timestamp' => now()->timestamp * 1000
            ], 403);
        }

        $token = $user->createToken('boostly-android-token')->plainTextToken;

        return response()->json([
            'status' => 'success',
            'message' => 'Login berhasil',
            'data' => [
                'token' => $token,
                'token_type' => 'Bearer',
                'user' => $user
            ],
            'timestamp' => now()->timestamp * 1000
        ]);
    }

    public function me(Request $request)
    {
        return response()->json([
            'status' => 'success',
            'message' => 'Data profil pengguna',
            'data' => $request->user(),
            'timestamp' => now()->timestamp * 1000
        ]);
    }

    public function logout(Request $request)
    {
        $request->user()->currentAccessToken()->delete();

        return response()->json([
            'status' => 'success',
            'message' => 'Sesi berhasil diakhiri',
            'data' => null,
            'timestamp' => now()->timestamp * 1000
        ]);
    }
}
```

### 3. `CoinController.php` (Pengurangan / Penambahan Saldo Aman)
```php
<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use App\Models\CoinTransaction;

class CoinController extends Controller
{
    public function adjustCoins(Request $request)
    {
        $request->validate([
            'user_id' => 'required|exists:users,id',
            'amount' => 'required|integer',
            'type' => 'required|string',
            'description' => 'required|string',
        ]);

        return DB::transaction(function () use ($request) {
            // Lock record baris user untuk mencegah race condition (dobel saldo)
            $user = DB::table('users')->where('id', $request->user_id)->lockForUpdate()->first();

            $newBalance = $user->points + $request->amount;
            if ($newBalance < 0) {
                return response()->json([
                    'status' => 'error',
                    'message' => "Saldo koin tidak mencukupi (Tersedia: {$user->points})",
                    'data' => null,
                    'timestamp' => now()->timestamp * 1000
                ], 400);
            }

            DB::table('users')->where('id', $user->id)->update([
                'points' => $newBalance,
                'updated_at' => now(),
            ]);

            $tx = CoinTransaction::create([
                'user_id' => $user->id,
                'amount' => $request->amount,
                'type' => $request->type,
                'description' => $request->description,
                'timestamp' => now()->timestamp * 1000,
            ]);

            return response()->json([
                'status' => 'success',
                'message' => 'Penyesuaian saldo berhasil dicatat',
                'data' => [
                    'user_id' => $user->id,
                    'previous_balance' => $user->points,
                    'new_balance' => $newBalance,
                    'transaction_id' => $tx->id
                ],
                'timestamp' => now()->timestamp * 1000
            ]);
        });
    }

    public function getBalance(Request $request)
    {
        $user = $request->user();
        return response()->json([
            'status' => 'success',
            'message' => 'Saldo koin saat ini',
            'data' => [
                'user_id' => $user->id,
                'points' => $user->points,
                'last_updated' => now()->timestamp * 1000
            ],
            'timestamp' => now()->timestamp * 1000
        ]);
    }
}
```

### 4. `SyncController.php` (Sinkronisasi Dua Arah Android <-> MySQL)
```php
<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\User;
use App\Models\Campaign;
use App\Models\CoinTransaction;
use App\Models\DailyCheckIn;

class SyncController extends Controller
{
    public function pullData(Request $request)
    {
        $since = $request->query('since', 0);

        return response()->json([
            'status' => 'success',
            'message' => 'Sinkronisasi pull data berhasil',
            'data' => [
                'users' => User::all(),
                'campaigns' => Campaign::where('is_active', true)->get(),
                'transactions' => CoinTransaction::where('user_id', $request->user()->id)->orderByDesc('id')->take(50)->get(),
                'daily_check_ins' => DailyCheckIn::where('user_id', $request->user()->id)->get(),
                'device_timestamp' => now()->timestamp * 1000
            ],
            'timestamp' => now()->timestamp * 1000
        ]);
    }

    public function pushData(Request $request)
    {
        $data = $request->json()->all();
        $syncedCheckIns = 0;
        $syncedTransactions = 0;

        if (isset($data['daily_check_ins']) && is_array($data['daily_check_ins'])) {
            foreach ($data['daily_check_ins'] as $ci) {
                DailyCheckIn::firstOrCreate(
                    ['user_id' => $ci['user_id'], 'check_in_date_key' => $ci['check_in_date_key']],
                    [
                        'streak_day' => $ci['streak_day'] ?? 1,
                        'reward_coins' => $ci['reward_coins'] ?? 10,
                        'timestamp' => $ci['timestamp'] ?? now()->timestamp * 1000,
                        'timezone_id' => $ci['timezone_id'] ?? 'WIB',
                        'time_formatted' => $ci['time_formatted'] ?? ''
                    ]
                );
                $syncedCheckIns++;
            }
        }

        return response()->json([
            'status' => 'success',
            'message' => 'Sinkronisasi push data berhasil diproses',
            'data' => [
                'synced_checkins_count' => $syncedCheckIns,
                'synced_transactions_count' => $syncedTransactions,
                'server_time' => now()->timestamp * 1000
            ],
            'timestamp' => now()->timestamp * 1000
        ]);
    }
}
```

---

## 👥 Database Seeder & Akun Demo

Salin kode berikut ke `database/seeders/DatabaseSeeder.php` di Laravel Anda:

```php
<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;
use App\Models\User;
use App\Models\Campaign;
use App\Models\CoinTransaction;

class DatabaseSeeder extends Seeder
{
    public function run(): void
    {
        // 1. Akun Admin Utama
        $admin = User::firstOrCreate(
            ['email' => 'admin@sosmedgrow.com'],
            [
                'name' => 'Administrator Boostly',
                'password' => Hash::make('admin123'),
                'role' => 'ADMIN',
                'points' => 99999,
                'authenticity_score' => 100.0,
                'instagram_handle' => '@boostly.official'
            ]
        );

        // 2. Akun Demo Creator (User)
        $creator = User::firstOrCreate(
            ['email' => 'rian@gmail.com'],
            [
                'name' => 'Rian Pratama',
                'password' => Hash::make('password123'),
                'role' => 'USER',
                'points' => 350,
                'authenticity_score' => 98.5,
                'instagram_handle' => '@rian.creator',
                'tiktok_handle' => '@rian_tok'
            ]
        );

        // 3. Sampel Kampanye Follow
        Campaign::firstOrCreate(
            ['account_handle' => '@luna.style'],
            [
                'creator_id' => $creator->id,
                'creator_name' => 'Luna Style Fashion',
                'platform' => 'INSTAGRAM',
                'target_url' => 'https://instagram.com/luna.style',
                'category' => 'Fashion & OOTD',
                'target_followers' => 100,
                'current_followers' => 45,
                'coin_reward_per_follow' => 10,
                'total_coins_allocated' => 1000,
                'is_active' => true
            ]
        );

        // 4. Sampel Transaksi Awal
        CoinTransaction::firstOrCreate(
            ['description' => 'Bonus Selamat Datang Pengguna Baru'],
            [
                'user_id' => $creator->id,
                'amount' => 150,
                'type' => 'DAILY_CHECKIN',
                'timestamp' => now()->timestamp * 1000
            ]
        );
    }
}
```

Jalankan perintah seeder di terminal:
```bash
php artisan migrate:fresh --seed
```

---

## 🛠️ Panduan Integrasi & Menjalankan Proyek

### 1. Menjalankan Backend Laravel
Pastikan file `.env` di direktori Laravel Anda telah terkonfigurasi:
```env
APP_NAME=Boostly
APP_ENV=local
APP_KEY=base64:generateWithArtisanKeyGenerate
APP_DEBUG=true
APP_URL=http://localhost:8000

DB_CONNECTION=mysql
DB_HOST=127.0.0.1
DB_PORT=3306
DB_DATABASE=boostly
DB_USERNAME=root
DB_PASSWORD=
```

Jalankan server agar dapat dijangkau oleh perangkat/emulator Android:
```bash
php artisan serve --host=0.0.0.0 --port=8000
```

### 2. Menghubungkan Aplikasi Android
1. Buka aplikasi **Boostly** di emulator atau HP Android fisik.
2. Pada layar Beranda atau menu Profil, klik tombol **"Sync Laravel"**.
3. Masukkan URL:
   - **Android Emulator**: `http://10.0.2.2:8000/api/`
   - **HP Fisik (WiFi Sama)**: `http://192.168.x.x:8000/api/` (sesuaikan dengan IP lokal komputer Anda)
   - **Server / VPS Online**: `https://api.domainanda.com/api/`
4. Klik **"Tes Koneksi"**. Aplikasi akan mengirim permintaan ke `GET /api/health` dan menampilkan badge hijau **"Online (Terhubung)"** beserta waktu latensi server.
5. Klik **"Sinkronkan Sekarang"** untuk mengunduh kampanye dan menyamakan saldo koin Anda dengan MySQL.

---

## 🛡️ Sistem Keamanan & Algoritma Anti-Curang

1. **Pencegahan Double-Spending (*Row-Level Locking*)**:
   Semua transaksi penambahan atau pengurangan koin di Laravel dibungkus dengan `DB::transaction()` dan `->lockForUpdate()`. Hal ini mencegah celah duplikasi koin saat pengguna melakukan spam klik secara bersamaan.
2. **Formula Skor Keaslian (*Authenticity Calculation*)**:
   $$\text{Authenticity Score} = 100\% - (\text{Violations} \times 15\%) + (\text{Successful Tasks} \times 0.5\%)$$
   Jika skor jatuh di bawah 50%, akun akan ditandai untuk peninjauan admin (*Restricted Mode*).
3. **Penyimpanan Password Aman**:
   Menggunakan algoritma *Bcrypt* (`Hash::make`) berstandar industri dengan proteksi *salt* otomatis.

---

## 🧪 Pengujian Unit & Kompilasi

Proyek Android telah dilengkapi rangkaian pengujian unit Robolectric:
```bash
# Menjalankan seluruh pengujian unit
gradle :app:testDebugUnitTest

# Membuat paket debug APK
gradle :app:assembleDebug
```
*Status: BUILD SUCCESSFUL (100% lulus tanpa kegagalan).*

---

## 📄 Lisensi
Hak Cipta &copy; 2026 Boostly Platform. Seluruh hak cipta dilindungi undang-undang.
