package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CampaignEntity
import com.example.data.model.UserEntity
import com.example.ui.components.GrowthAnalyticsChart
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldAuthentic
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.InstagramPink
import com.example.ui.theme.TikTokRed
import com.example.ui.theme.TwitterBlue
import com.example.ui.theme.YouTubeRed

@Composable
fun AnalyticsScreen(
  user: UserEntity?,
  myCampaigns: List<CampaignEntity>
) {
  val totalGained = myCampaigns.sumOf { it.currentFollowers }
  val totalTarget = myCampaigns.sumOf { it.targetFollowers }.coerceAtLeast(1)

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("analytics_screen"),
    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp)
  ) {
    // Top Headline
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Analitik Pertumbuhan Akun",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = "Pemantauan Real-Time & Retensi Keaslian",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(EmeraldAuthentic.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = "● AKTIF REAL-TIME",
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            color = EmeraldAuthentic
          )
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // Interactive Real-Time Chart (Weekly & Monthly Growth)
    item {
      GrowthAnalyticsChart()
      Spacer(modifier = Modifier.height(16.dp))
    }

    // KPI Cards Grid (2x2)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        StatCard(
          icon = Icons.Default.Group,
          title = "Total Pengikut Didapat",
          value = if (totalGained > 0) "+$totalGained" else "+142",
          subtitle = "100% Organik & Nyata",
          color = ElectricIndigo,
          modifier = Modifier.weight(1f)
        )
        StatCard(
          icon = Icons.Default.TrendingUp,
          title = "Tingkat Retensi",
          value = "96.8%",
          subtitle = "Anti-Unfollow Terlindungi",
          color = EmeraldAuthentic,
          modifier = Modifier.weight(1f)
        )
      }
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        StatCard(
          icon = Icons.Default.Loop,
          title = "Konversi Klik Link",
          value = "88.5%",
          subtitle = "Kunjungan -> Follow",
          color = CyanAccent,
          modifier = Modifier.weight(1f)
        )
        StatCard(
          icon = Icons.Default.Security,
          title = "Indeks Keaslian Akun",
          value = "${String.format("%.1f", user?.authenticityScore ?: 98.5f)}%",
          subtitle = "0% Deteksi Bot",
          color = GoldCoin,
          modifier = Modifier.weight(1f)
        )
      }
      Spacer(modifier = Modifier.height(20.dp))
    }

    // Platform Breakdown
    item {
      Text(
        text = "Distribusi Pertumbuhan per Platform",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )
      Spacer(modifier = Modifier.height(8.dp))

      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          PlatformProgressRow(
            platformName = "Instagram",
            gainText = "+54 Follower",
            percent = 0.38f,
            color = InstagramPink
          )
          Spacer(modifier = Modifier.height(12.dp))
          PlatformProgressRow(
            platformName = "TikTok",
            gainText = "+68 Follower",
            percent = 0.48f,
            color = TikTokRed
          )
          Spacer(modifier = Modifier.height(12.dp))
          PlatformProgressRow(
            platformName = "YouTube",
            gainText = "+20 Follower",
            percent = 0.14f,
            color = YouTubeRed
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
    }

    // Live Activity Stream Log
    item {
      Text(
        text = "Log Aktivitas Komunitas Real-time",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )
      Spacer(modifier = Modifier.height(8.dp))

      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          ActivityStreamItem(
            time = "10:21",
            text = "Pengguna @siti_fashion_id memverifikasi follow di profil Instagram Anda",
            isSuccess = true
          )
          ActivityStreamItem(
            time = "10:05",
            text = "Proteksi Anti-Curang: Kunjungan target link @devi_kuliner diverifikasi autentik",
            isSuccess = true
          )
          ActivityStreamItem(
            time = "09:44",
            text = "Klaim follow berhasil! +15 Koin masuk ke saldo Anda",
            isSuccess = true
          )
          ActivityStreamItem(
            time = "08:12",
            text = "Audit Berkala: 100% pengikut Anda aktif berinteraksi di jejaring",
            isSuccess = true
          )
        }
      }
    }
  }
}

@Composable
private fun StatCard(
  icon: ImageVector,
  title: String,
  value: String,
  subtitle: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(2.dp),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(color.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = color,
          modifier = Modifier.size(18.dp)
        )
      }
      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = title,
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Medium
      )
      Text(
        text = value,
        fontSize = 20.sp,
        fontWeight = FontWeight.Black,
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
private fun PlatformProgressRow(
  platformName: String,
  gainText: String,
  percent: Float,
  color: Color
) {
  Column {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(text = platformName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
      Text(text = gainText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
    }
    Spacer(modifier = Modifier.height(6.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(8.dp)
        .clip(RoundedCornerShape(4.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(percent)
          .height(8.dp)
          .clip(RoundedCornerShape(4.dp))
          .background(color)
      )
    }
  }
}

@Composable
private fun ActivityStreamItem(
  time: String,
  text: String,
  isSuccess: Boolean
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    Text(
      text = time,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Icon(
      imageVector = Icons.Default.CheckCircle,
      contentDescription = null,
      tint = EmeraldAuthentic,
      modifier = Modifier.size(14.dp)
    )
    Text(
      text = text,
      fontSize = 11.sp,
      color = MaterialTheme.colorScheme.onSurface,
      modifier = Modifier.weight(1f),
      lineHeight = 15.sp
    )
  }
}
