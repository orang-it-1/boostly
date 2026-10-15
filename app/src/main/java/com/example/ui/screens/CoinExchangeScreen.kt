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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CampaignEntity
import com.example.data.model.UserEntity
import com.example.ui.components.PlatformBadge
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldAuthentic
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.GoldCoinDark
import com.example.ui.theme.GoldCoinLight
import com.example.ui.theme.QrisRed

@Composable
fun CoinExchangeScreen(
  user: UserEntity?,
  myCampaigns: List<CampaignEntity>,
  onOpenCreateExchange: () -> Unit,
  onOpenQrisShop: () -> Unit
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp)
  ) {
    // Top Hero Balance Card
    item {
      Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.linearGradient(
                listOf(
                  ElectricIndigo.copy(alpha = 0.15f),
                  CyanAccent.copy(alpha = 0.08f)
                )
              )
            )
            .padding(20.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Saldo Koin Anda",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = "${user?.points ?: 0} Koin",
                  fontSize = 30.sp,
                  fontWeight = FontWeight.Black,
                  color = GoldCoinDark
                )
              }

              Button(
                onClick = onOpenQrisShop,
                colors = ButtonDefaults.buttonColors(containerColor = QrisRed),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("topup_qris_button")
              ) {
                Icon(
                  imageVector = Icons.Default.QrCode,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Beli Koin QRIS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = "Tukarkan koin yang Anda kumpulkan dari hasil follow pengguna lain menjadi follower asli & aktif untuk akun Anda!",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
              onClick = onOpenCreateExchange,
              colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("start_exchange_campaign_button")
            ) {
              Icon(imageVector = Icons.Default.RocketLaunch, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Tukar Koin Jadi Follower Sekarang", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    item { Spacer(modifier = Modifier.height(20.dp)) }

    // Preset Package Explanations
    item {
      Text(
        text = "Skema Penukaran Koin Komunitas",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )
      Spacer(modifier = Modifier.height(8.dp))
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        PackageCard(
          title = "Paket Mini",
          followers = "10 Follower",
          cost = "150 Koin",
          modifier = Modifier.weight(1f)
        )
        PackageCard(
          title = "Paket Medium",
          followers = "30 Follower",
          cost = "450 Koin",
          isPopular = true,
          modifier = Modifier.weight(1f)
        )
        PackageCard(
          title = "Paket Pro",
          followers = "100 Follower",
          cost = "1.500 Koin",
          modifier = Modifier.weight(1f)
        )
      }
    }

    item { Spacer(modifier = Modifier.height(24.dp)) }

    // User's Own Active Campaigns
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Kampanye Penukaran Saya (${myCampaigns.size})",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
    }

    if (myCampaigns.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(30.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.Campaign,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(36.dp)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Belum Ada Kampanye Aktif",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(
                text = "Tukarkan koin Anda untuk menambahkan link profil target Anda ke komunitas.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
              )
            }
          }
        }
      }
    }

    items(myCampaigns, key = { it.id }) { campaign ->
      val progress = (campaign.currentFollowers.toFloat() / campaign.targetFollowers.toFloat()).coerceIn(0f, 1f)
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 5.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            PlatformBadge(platform = campaign.platform)
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (campaign.isActive) EmeraldAuthentic.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.15f)
            ) {
              Text(
                text = if (campaign.isActive) "SEDANG BERJALAN" else "SELESAI",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (campaign.isActive) EmeraldAuthentic else Color.Gray,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = campaign.accountHandle,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Perolehan: ${campaign.currentFollowers} / ${campaign.targetFollowers} Pengikut",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "${(progress * 100).toInt()}%",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = EmeraldAuthentic
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = EmeraldAuthentic,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
          )
        }
      }
    }
  }
}

@Composable
private fun PackageCard(
  title: String,
  followers: String,
  cost: String,
  isPopular: Boolean = false,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = if (isPopular) ElectricIndigo.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    border = androidx.compose.foundation.BorderStroke(
      if (isPopular) 1.5.dp else 1.dp,
      if (isPopular) ElectricIndigo else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    ),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      if (isPopular) {
        Text(
          text = "POPULER",
          fontSize = 8.sp,
          fontWeight = FontWeight.Black,
          color = ElectricIndigo
        )
      }
      Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = followers,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = cost,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = GoldCoinDark
      )
    }
  }
}
