package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CampaignEntity
import com.example.data.model.QrisOrderEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.components.AuthenticityScoreBadge
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldAuthentic
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.GoldCoinDark
import com.example.ui.theme.QrisRed
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboardScreen(
  users: List<UserEntity>,
  campaigns: List<CampaignEntity>,
  orders: List<QrisOrderEntity>,
  onSelectUserToAdjustCoins: (UserEntity) -> Unit,
  onToggleUserBan: (UserEntity) -> Unit,
  onOpenLaravelSync: () -> Unit = {}
) {
  var userSearchQuery by remember { mutableStateOf("") }
  var showSupabaseDetails by remember { mutableStateOf(false) }

  val idrFormat = remember {
    NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
      maximumFractionDigits = 0
    }
  }
  val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID"))

  val totalRevenue = orders.sumOf { it.priceIdr }
  val totalCirculatingCoins = users.sumOf { it.points }
  val activeCampaignsCount = campaigns.count { it.isActive }

  val filteredUsers = users.filter { user ->
    userSearchQuery.isBlank() ||
      user.name.contains(userSearchQuery, ignoreCase = true) ||
      user.email.contains(userSearchQuery, ignoreCase = true)
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("admin_dashboard_screen"),
    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp)
  ) {
    // Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.AdminPanelSettings,
              contentDescription = null,
              tint = ElectricIndigo,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Panel Kontrol Admin",
              fontSize = 20.sp,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.onBackground
            )
          }
          Text(
            text = "Kelola Keseluruhan Sistem, Koin User, & Omset QRIS",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = ElectricIndigo.copy(alpha = 0.15f)
        ) {
          Text(
            text = "SUPER ADMIN",
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            color = ElectricIndigo,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // System Overview Stats (2x2 Grid)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OverviewStatCard(
          title = "Omset Komersial QRIS",
          value = idrFormat.format(totalRevenue),
          subtitle = "${orders.size} Transaksi Masuk",
          color = EmeraldAuthentic,
          icon = Icons.Default.Payments,
          modifier = Modifier.weight(1f)
        )
        OverviewStatCard(
          title = "Total Koin Beredar",
          value = "$totalCirculatingCoins Koin",
          subtitle = "Ekonomi Aktif",
          color = GoldCoinDark,
          icon = Icons.Default.MonetizationOn,
          modifier = Modifier.weight(1f)
        )
      }
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OverviewStatCard(
          title = "Total Pengguna",
          value = "${users.size} User",
          subtitle = "Komunitas Terdaftar",
          color = ElectricIndigo,
          icon = Icons.Default.People,
          modifier = Modifier.weight(1f)
        )
        OverviewStatCard(
          title = "Kampanye Target Aktif",
          value = "$activeCampaignsCount Link",
          subtitle = "Saling Follow Berjalan",
          color = CyanAccent,
          icon = Icons.Default.QrCode,
          modifier = Modifier.weight(1f)
        )
      }
      Spacer(modifier = Modifier.height(20.dp))
    }

    // Cloud Database & Supabase Architectural Guide
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.4f)),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { showSupabaseDetails = !showSupabaseDetails }
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(CyanAccent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Storage,
                  contentDescription = null,
                  tint = CyanAccent,
                  modifier = Modifier.size(20.dp)
                )
              }
              Column {
                Text(
                  text = "Database & Website Laravel Backend",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "Arsitektur Hybrid: Room (Lokal) + Laravel REST API (Online Server)",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
            Text(
              text = if (showSupabaseDetails) "Tutup ▲" else "Lihat Detail ▼",
              fontSize = 11.sp,
              color = CyanAccent,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Laravel Bridge Button
          Button(
            onClick = onOpenLaravelSync,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth().testTag("admin_open_laravel_sync_btn")
          ) {
            Icon(
              imageVector = Icons.Default.CloudSync,
              contentDescription = null,
              tint = Color(0xFFFF2D20),
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              "Pengaturan & Sinkronisasi Database Laravel",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }

          if (showSupabaseDetails) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
              text = "Aplikasi Boostly kini dilengkapi REST API Client untuk terhubung langsung secara online ke backend framework Laravel website Anda. Data disimpan lokal di Room dan tersinkronisasi 2 arah ke database MySQL/PostgreSQL website Anda!",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(
                  text = "Struktur Tabel Supabase (PostgreSQL Schema):",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = ElectricIndigo
                )
                Text(
                  text = """
-- 1. Tabel Users
CREATE TABLE users (id SERIAL PRIMARY KEY, name TEXT, email TEXT UNIQUE, points INT DEFAULT 150, authenticity_score FLOAT DEFAULT 100);

-- 2. Tabel Campaigns
CREATE TABLE campaigns (id SERIAL PRIMARY KEY, creator_id INT REFERENCES users(id), platform TEXT, target_url TEXT, target_followers INT, current_followers INT DEFAULT 0);

-- 3. Tabel Verifications
CREATE TABLE follow_verifications (id SERIAL PRIMARY KEY, campaign_id INT, follower_id INT, status TEXT DEFAULT 'VERIFIED_ACTIVE');
                  """.trimIndent(),
                  fontSize = 10.sp,
                  fontFamily = FontFamily.Monospace,
                  color = MaterialTheme.colorScheme.onSurface,
                  lineHeight = 14.sp,
                  modifier = Modifier.padding(top = 4.dp)
                )
              }
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(20.dp))
    }

    // Section: User Management & Coin Modification
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Manajemen Pengguna & Koin (${users.size})",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
      }
      Spacer(modifier = Modifier.height(8.dp))

      OutlinedTextField(
        value = userSearchQuery,
        onValueChange = { userSearchQuery = it },
        placeholder = { Text("Cari nama atau email pengguna...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("admin_search_user_input")
      )

      Spacer(modifier = Modifier.height(10.dp))
    }

    items(filteredUsers, key = { it.id }) { user ->
      UserManagementCard(
        user = user,
        onAdjustCoins = { onSelectUserToAdjustCoins(user) },
        onToggleBan = { onToggleUserBan(user) }
      )
    }

    // Commercial QRIS Orders Section
    item {
      Spacer(modifier = Modifier.height(24.dp))
      Text(
        text = "Riwayat Omset Penjualan QRIS (${orders.size})",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )
      Spacer(modifier = Modifier.height(8.dp))
    }

    items(orders, key = { it.id }) { order ->
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(EmeraldAuthentic.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = EmeraldAuthentic,
                modifier = Modifier.size(20.dp)
              )
            }
            Column {
              Text(
                text = order.packageTitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "${order.transactionRef} • User #${order.userId}",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = idrFormat.format(order.priceIdr),
              fontSize = 14.sp,
              fontWeight = FontWeight.Black,
              color = EmeraldAuthentic
            )
            Text(
              text = "+${order.coinsGiven} Koin",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = GoldCoinDark
            )
          }
        }
      }
    }
  }
}

@Composable
private fun OverviewStatCard(
  title: String,
  value: String,
  subtitle: String,
  color: Color,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(2.dp),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = title,
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontWeight = FontWeight.Medium
        )
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = color,
          modifier = Modifier.size(16.dp)
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = value,
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = subtitle,
        fontSize = 10.sp,
        color = color,
        fontWeight = FontWeight.SemiBold
      )
    }
  }
}

@Composable
private fun UserManagementCard(
  user: UserEntity,
  onAdjustCoins: () -> Unit,
  onToggleBan: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 5.dp)
      .testTag("admin_user_card_${user.id}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(
                if (user.role == UserRole.ADMIN) ElectricIndigo.copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.surfaceVariant
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (user.role == UserRole.ADMIN) Icons.Default.AdminPanelSettings else Icons.Default.Person,
              contentDescription = null,
              tint = if (user.role == UserRole.ADMIN) ElectricIndigo else MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.size(20.dp)
            )
          }
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = user.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              if (user.role == UserRole.ADMIN) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "ADMIN",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Black,
                  color = ElectricIndigo
                )
              }
              if (user.isBanned) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "DIBEKUKAN",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Black,
                  color = AlertRed
                )
              }
            }
            Text(
              text = user.email,
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Coin Badge
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = GoldCoin.copy(alpha = 0.12f)
        ) {
          Text(
            text = "${user.points} Koin",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp,
            color = GoldCoinDark,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        AuthenticityScoreBadge(score = user.authenticityScore)

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          // Button to Add/Adjust Coins
          Button(
            onClick = onAdjustCoins,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo),
            modifier = Modifier.testTag("admin_adjust_coin_btn_${user.id}")
          ) {
            Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Kelola Koin", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }

          if (user.role != UserRole.ADMIN) {
            OutlinedButton(
              onClick = onToggleBan,
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (user.isBanned) EmeraldAuthentic else AlertRed
              )
            ) {
              Text(
                text = if (user.isBanned) "Pulihkan" else "Banned",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }
    }
  }
}
