package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.SocialPlatform
import com.example.data.model.UserEntity
import com.example.ui.components.PlatformBadge
import com.example.ui.theme.AlertRed
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldAuthentic
import com.example.ui.theme.GoldCoin

@Composable
fun CreateCampaignDialog(
  user: UserEntity,
  onDismiss: () -> Unit,
  onConfirmExchange: (
    platform: SocialPlatform,
    targetUrl: String,
    accountHandle: String,
    category: String,
    targetFollowers: Int,
    coinReward: Int
  ) -> Unit
) {
  var selectedPlatform by remember { mutableStateOf(SocialPlatform.INSTAGRAM) }
  var targetUrl by remember { mutableStateOf("https://instagram.com/my_profile") }
  var accountHandle by remember { mutableStateOf("@my_profile") }
  var category by remember { mutableStateOf("Kreator & Hiburan") }
  var targetFollowers by remember { mutableIntStateOf(10) }
  var coinReward by remember { mutableIntStateOf(15) }
  var validationError by remember { mutableStateOf<String?>(null) }

  val totalCost = targetFollowers * coinReward
  val canAfford = user.points >= totalCost

  val scrollState = rememberScrollState()

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .testTag("create_campaign_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(scrollState)
          .padding(20.dp)
      ) {
        // Header
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
                .clip(RoundedCornerShape(10.dp))
                .background(ElectricIndigo.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Campaign,
                contentDescription = null,
                tint = ElectricIndigo,
                modifier = Modifier.size(20.dp)
              )
            }
            Column {
              Text(
                text = "Tukar Koin Jadi Follower",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Luncurkan Kampanye Target Link",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          // Balance Chip
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = GoldCoin.copy(alpha = 0.15f)
          ) {
            Text(
              text = "Saldo: ${user.points} Koin",
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              color = GoldCoin,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Platform Selector
        Text(
          text = "Pilih Platform Sosial Media",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          SocialPlatform.values().forEach { platform ->
            val isSelected = selectedPlatform == platform
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) ElectricIndigo else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                  selectedPlatform = platform
                  if (targetUrl.startsWith("https://")) {
                    when (platform) {
                      SocialPlatform.INSTAGRAM -> targetUrl = "https://instagram.com/my_profile"
                      SocialPlatform.TIKTOK -> targetUrl = "https://tiktok.com/@my_profile"
                      SocialPlatform.YOUTUBE -> targetUrl = "https://youtube.com/@my_profile"
                      SocialPlatform.X_TWITTER -> targetUrl = "https://x.com/my_profile"
                      SocialPlatform.THREADS -> targetUrl = "https://threads.net/@my_profile"
                    }
                  }
                }
            ) {
              Box(
                modifier = Modifier.padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = when (platform) {
                    SocialPlatform.INSTAGRAM -> "IG"
                    SocialPlatform.TIKTOK -> "TikTok"
                    SocialPlatform.YOUTUBE -> "YT"
                    SocialPlatform.X_TWITTER -> "X"
                    SocialPlatform.THREADS -> "Th"
                  },
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Inputs
        OutlinedTextField(
          value = targetUrl,
          onValueChange = { targetUrl = it },
          label = { Text("Link Target Profil") },
          placeholder = { Text("https://...") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("target_url_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = accountHandle,
          onValueChange = { accountHandle = it },
          label = { Text("Username Akun Target") },
          placeholder = { Text("@username_anda") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("account_handle_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = category,
          onValueChange = { category = it },
          label = { Text("Kategori / Niche") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Target Followers Slider
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Target Jumlah Pengikut:",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = "$targetFollowers Follower",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = ElectricIndigo
          )
        }
        Slider(
          value = targetFollowers.toFloat(),
          onValueChange = { targetFollowers = it.toInt() },
          valueRange = 5f..100f,
          steps = 18,
          modifier = Modifier.fillMaxWidth()
        )

        // Coin Reward per Follower
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Hadiah Koin per Follow:",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = "$coinReward Koin / orang",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = GoldCoin
          )
        }
        Slider(
          value = coinReward.toFloat(),
          onValueChange = { coinReward = it.toInt() },
          valueRange = 10f..30f,
          steps = 3,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Calculation Summary Card
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (canAfford) MaterialTheme.colorScheme.surfaceVariant else AlertRed.copy(alpha = 0.1f),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (canAfford) ElectricIndigo.copy(alpha = 0.3f) else AlertRed.copy(alpha = 0.5f)
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Total Koin Ditukar:",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = "$totalCost Koin",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (canAfford) GoldCoin else AlertRed
              )
            }
            if (!canAfford) {
              Text(
                text = "Saldo koin Anda tidak mencukupi (Perlu ${totalCost - user.points} koin lagi). Silakan follow kampanye lain atau beli via QRIS.",
                fontSize = 11.sp,
                color = AlertRed,
                modifier = Modifier.padding(top = 4.dp)
              )
            } else {
              Text(
                text = "Sisa saldo setelah penukaran: ${user.points - totalCost} Koin",
                fontSize = 11.sp,
                color = EmeraldAuthentic,
                modifier = Modifier.padding(top = 4.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          TextButton(onClick = onDismiss) {
            Text("Batal")
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              if (targetUrl.trim().length < 5 || accountHandle.trim().length < 3) {
                validationError = "Mohon lengkapi url dan username target."
                return@Button
              }
              onConfirmExchange(
                selectedPlatform,
                targetUrl.trim(),
                accountHandle.trim(),
                category.trim(),
                targetFollowers,
                coinReward
              )
            },
            enabled = canAfford,
            colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("confirm_exchange_campaign_button")
          ) {
            Icon(
              imageVector = Icons.Default.Campaign,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Luncurkan Kampanye ($totalCost Koin)", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
