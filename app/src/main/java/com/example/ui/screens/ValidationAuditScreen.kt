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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FollowVerificationEntity
import com.example.data.model.UserEntity
import com.example.data.model.VerificationStatus
import com.example.ui.components.AuthenticityScoreBadge
import com.example.ui.components.PlatformBadge
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldAuthentic
import com.example.ui.theme.GoldCoin
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ValidationAuditScreen(
  user: UserEntity?,
  verifications: List<FollowVerificationEntity>,
  onConfirmValid: (FollowVerificationEntity) -> Unit,
  onReportCheater: (FollowVerificationEntity) -> Unit,
  onShowAntiCheatInfo: () -> Unit
) {
  val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID"))

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp)
  ) {
    // Top Authenticity Score Card
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
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(EmeraldAuthentic.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.GppGood,
                  contentDescription = null,
                  tint = EmeraldAuthentic,
                  modifier = Modifier.size(24.dp)
                )
              }
              Column {
                Text(
                  text = "Validasi Pengikut Aktif",
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "Sistem Audit Keaslian & Anti-Drop",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            AuthenticityScoreBadge(score = user?.authenticityScore ?: 100f)
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Authenticity Metrics Bar
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            MetricPill(
              title = "Human Real Score",
              value = "98.9%",
              color = EmeraldAuthentic,
              modifier = Modifier.weight(1f)
            )
            MetricPill(
              title = "Bot Terdeteksi",
              value = "0.0%",
              color = CyanAccent,
              modifier = Modifier.weight(1f)
            )
            MetricPill(
              title = "Retensi 7 Hari",
              value = "96.4%",
              color = ElectricIndigo,
              modifier = Modifier.weight(1f)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "Setiap pengikut diverifikasi secara real-time. Jika Anda mendeteksi akun pengikut langsung unfollow atau akun bot tidak autentik, laporkan untuk memotong koin mereka & melindungi reputasi Anda.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 15.sp
          )
        }
      }
    }

    item { Spacer(modifier = Modifier.height(20.dp)) }

    // Section Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Antrean Audit Pengikut Masuk (${verifications.size})",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
    }

    if (verifications.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = null,
                tint = EmeraldAuthentic,
                modifier = Modifier.size(36.dp)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Semua Pengikut Tervalidasi",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(
                text = "Pengikut baru dari kampanye Anda akan muncul di sini untuk diaudit keasliannya.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
              )
            }
          }
        }
      }
    }

    items(verifications, key = { it.id }) { item ->
      VerificationAuditCard(
        item = item,
        dateFormat = dateFormat,
        onConfirm = { onConfirmValid(item) },
        onReport = { onReportCheater(item) }
      )
    }
  }
}

@Composable
private fun MetricPill(
  title: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = color.copy(alpha = 0.08f),
    border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = title,
        fontSize = 9.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = value,
        fontSize = 13.sp,
        fontWeight = FontWeight.Black,
        color = color
      )
    }
  }
}

@Composable
private fun VerificationAuditCard(
  item: FollowVerificationEntity,
  dateFormat: SimpleDateFormat,
  onConfirm: () -> Unit,
  onReport: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 5.dp)
      .testTag("verification_item_${item.id}")
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
              .size(36.dp)
              .clip(CircleShape)
              .background(ElectricIndigo.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Person,
              contentDescription = null,
              tint = ElectricIndigo,
              modifier = Modifier.size(20.dp)
            )
          }
          Column {
            Text(
              text = item.followerName,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Handle: ${item.followerHandle}",
              fontSize = 12.sp,
              color = ElectricIndigo,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        // Status Badge
        when (item.status) {
          VerificationStatus.VERIFIED_ACTIVE -> {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = EmeraldAuthentic.copy(alpha = 0.12f)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = null,
                  tint = EmeraldAuthentic,
                  modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "AKTIF & VALID",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = EmeraldAuthentic
                )
              }
            }
          }
          VerificationStatus.PENDING_AUDIT -> {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = GoldCoin.copy(alpha = 0.15f)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.HourglassTop,
                  contentDescription = null,
                  tint = GoldCoin,
                  modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "MENUNGGU AUDIT",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = GoldCoin
                )
              }
            }
          }
          VerificationStatus.FLAGGED_CHEATING -> {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = AlertRed.copy(alpha = 0.15f)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = null,
                  tint = AlertRed,
                  modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "TERINDIKASI CURANG",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = AlertRed
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Target: ${item.targetAccountHandle} (${item.targetPlatform.displayName})",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          text = dateFormat.format(Date(item.createdAt)),
          fontSize = 10.sp,
          color = Color.Gray
        )
      }

      if (item.status == VerificationStatus.FLAGGED_CHEATING && item.cheatReportReason != null) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = AlertRed.copy(alpha = 0.08f),
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
        ) {
          Text(
            text = "Alasan Penalti: ${item.cheatReportReason}",
            fontSize = 11.sp,
            color = AlertRed,
            modifier = Modifier.padding(8.dp)
          )
        }
      }

      // Actions if pending or can audit
      if (item.status != VerificationStatus.FLAGGED_CHEATING) {
        Spacer(modifier = Modifier.height(12.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedButton(
            onClick = onReport,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed),
            modifier = Modifier.testTag("report_cheater_button_${item.id}")
          ) {
            Icon(imageVector = Icons.Default.Report, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Laporkan Curang / Unfollow", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
          }

          if (item.status == VerificationStatus.PENDING_AUDIT) {
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = onConfirm,
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = EmeraldAuthentic),
              modifier = Modifier.testTag("confirm_valid_button_${item.id}")
            ) {
              Text("Konfirmasi Valid", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}
