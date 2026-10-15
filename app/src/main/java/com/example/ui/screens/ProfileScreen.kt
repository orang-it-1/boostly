package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.BoostlyDarkGreenBtn
import com.example.ui.theme.BoostlyNeonLime
import com.example.ui.theme.isThemeDark
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CoinTransactionEntity
import com.example.data.model.TransactionType
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.components.AuthenticityScoreBadge
import com.example.ui.theme.AlertRed
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldAuthentic
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.GoldCoinDark
import com.example.ui.theme.QrisRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
  user: UserEntity?,
  transactions: List<CoinTransactionEntity>,
  onOpenQrisShop: () -> Unit,
  onShowAntiCheatInfo: () -> Unit,
  onSwitchToAdminDemo: () -> Unit,
  onLogout: () -> Unit,
  onOpenLaravelSync: () -> Unit = {},
  onOpenAiJobDetector: () -> Unit = {},
  themeMode: AppThemeMode = AppThemeMode.LIGHT,
  onToggleTheme: () -> Unit = {},
  onOpenThemeDialog: () -> Unit = {}
) {
  val isDark = isThemeDark()
  val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("profile_screen"),
    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp)
  ) {
    // User Profile Header Card
    item {
      Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(54.dp)
                  .clip(CircleShape)
                  .background(ElectricIndigo.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Person,
                  contentDescription = null,
                  tint = ElectricIndigo,
                  modifier = Modifier.size(30.dp)
                )
              }
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = user?.name ?: "Pengguna",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  if (user?.role == UserRole.ADMIN) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = ElectricIndigo.copy(alpha = 0.15f)
                    ) {
                      Text(
                        text = "ADMIN",
                        color = ElectricIndigo,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                }
                Text(
                  text = user?.email ?: "",
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            AuthenticityScoreBadge(score = user?.authenticityScore ?: 100f)
          }

          // AI Detected Job Badge & Trigger
          Spacer(modifier = Modifier.height(12.dp))
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF3E8FF),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD8B4FE)),
            modifier = Modifier
              .fillMaxWidth()
              .clickable(onClick = onOpenAiJobDetector)
              .testTag("profile_detected_job_badge")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("✨", fontSize = 16.sp)
                Column {
                  Text(
                    text = if (!user?.detectedJob.isNullOrBlank()) user!!.detectedJob else "Profesi Belum Dideteksi AI",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B21A8)
                  )
                  Text(
                    text = if (!user?.recommendedNiche.isNullOrBlank()) {
                      "Niche: ${user!!.recommendedNiche} • Akurasi ${(user.jobConfidence * 100).toInt()}%"
                    } else {
                      "Ketuk untuk menganalisis pekerjaan dengan Gemini AI"
                    },
                    fontSize = 10.sp,
                    color = Color(0xFF7E22CE)
                  )
                }
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0xFF8B5CF6))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = if (!user?.detectedJob.isNullOrBlank()) "Ubah AI" else "Deteksi AI",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Coin Balance Banner
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Saldo Koin Komunitas",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = "${user?.points ?: 0} Koin",
                  fontSize = 24.sp,
                  fontWeight = FontWeight.Black,
                  color = GoldCoinDark
                )
              }

              Button(
                onClick = onOpenQrisShop,
                colors = ButtonDefaults.buttonColors(containerColor = QrisRed),
                shape = RoundedCornerShape(12.dp)
              ) {
                Icon(imageVector = Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Beli via QRIS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Quick Action Chips
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedButton(
              onClick = onShowAntiCheatInfo,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.weight(1f)
            ) {
              Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Aturan Anti-Curang", fontSize = 11.sp)
            }

            if (user?.role != UserRole.ADMIN) {
              OutlinedButton(
                onClick = onSwitchToAdminDemo,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("Coba Akun Admin", fontSize = 11.sp)
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Laravel Database Bridge Button
          Button(
            onClick = onOpenLaravelSync,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("profile_laravel_sync_btn")
          ) {
            Icon(
              imageVector = Icons.Default.CloudSync,
              contentDescription = null,
              tint = Color(0xFFFF2D20),
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              "Koneksi & Sinkron Database Laravel",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(20.dp))
    }

    // Theme & Accessibility Settings Card
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .clickable(onClick = onOpenThemeDialog)
          .testTag("profile_theme_card")
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isDark) BoostlyNeonLime.copy(alpha = 0.2f) else BoostlyDarkGreenBtn.copy(alpha = 0.1f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                contentDescription = null,
                tint = if (isDark) BoostlyNeonLime else BoostlyDarkGreenBtn,
                modifier = Modifier.size(22.dp)
              )
            }

            Column {
              Text(
                text = "Tema & Tampilan",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "${themeMode.title} • Ketuk untuk opsi tema",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Switch(
            checked = isDark,
            onCheckedChange = { onToggleTheme() },
            colors = SwitchDefaults.colors(
              checkedThumbColor = BoostlyNeonLime,
              checkedTrackColor = Color(0xFF1B3D2A)
            ),
            modifier = Modifier.testTag("profile_theme_switch")
          )
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // Coin Transactions History Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Riwayat Transaksi Koin (${transactions.size})",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
    }

    if (transactions.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
            Text("Belum ada riwayat transaksi koin.", fontSize = 12.sp, color = Color.Gray)
          }
        }
      }
    }

    items(transactions, key = { it.id }) { tx ->
      val isPositive = tx.amount >= 0
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
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(
                  if (isPositive) EmeraldAuthentic.copy(alpha = 0.12f)
                  else AlertRed.copy(alpha = 0.12f)
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = null,
                tint = if (isPositive) EmeraldAuthentic else AlertRed,
                modifier = Modifier.size(16.dp)
              )
            }
            Column {
              Text(
                text = tx.description,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = dateFormat.format(Date(tx.timestamp)),
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Text(
            text = if (isPositive) "+${tx.amount} Koin" else "${tx.amount} Koin",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp,
            color = if (isPositive) EmeraldAuthentic else AlertRed
          )
        }
      }
    }

    // Logout Button
    item {
      Spacer(modifier = Modifier.height(24.dp))
      OutlinedButton(
        onClick = onLogout,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("logout_button")
      ) {
        Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Keluar dari Akun", fontWeight = FontWeight.Bold)
      }
    }
  }
}
