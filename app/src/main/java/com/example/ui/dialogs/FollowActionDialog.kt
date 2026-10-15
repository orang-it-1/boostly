package com.example.ui.dialogs

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CampaignEntity
import com.example.ui.components.PlatformBadge
import com.example.ui.theme.AlertRed
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldAuthentic
import com.example.ui.theme.GoldCoin
import kotlinx.coroutines.delay

@Composable
fun FollowActionDialog(
  campaign: CampaignEntity,
  onDismiss: () -> Unit,
  onVerifyAndClaim: (followerHandle: String) -> Unit,
  defaultHandle: String = ""
) {
  val context = LocalContext.current
  var hasOpenedLink by remember { mutableStateOf(false) }
  var countdownSeconds by remember { mutableIntStateOf(6) }
  var isTimerFinished by remember { mutableStateOf(false) }
  var followerHandle by remember { mutableStateOf(defaultHandle) }
  var handleErrorMessage by remember { mutableStateOf<String?>(null) }

  // Anti-cheat verification timer: counts down after opening link
  LaunchedEffect(hasOpenedLink) {
    if (hasOpenedLink) {
      countdownSeconds = 6
      while (countdownSeconds > 0) {
        delay(1000L)
        countdownSeconds--
      }
      isTimerFinished = true
    }
  }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .testTag("follow_action_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Platform & Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          PlatformBadge(platform = campaign.platform)
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = GoldCoin.copy(alpha = 0.15f)
          ) {
            Text(
              text = "+${campaign.coinRewardPerFollow} Koin",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = GoldCoin,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Target Account Info
        Text(
          text = campaign.accountHandle,
          fontSize = 20.sp,
          fontWeight = FontWeight.ExtraBold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Kategori: ${campaign.category}",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Anti-Cheat System Notice Card
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
          border = androidx.compose.foundation.BorderStroke(1.dp, ElectricIndigo.copy(alpha = 0.3f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = "Anti-Curang",
              tint = ElectricIndigo,
              modifier = Modifier.size(24.dp)
            )
            Column {
              Text(
                text = "Proteksi Anti-Curang Aktif",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricIndigo
              )
              Text(
                text = "1. Buka link profil target\n2. Tekan Follow di medsos\n3. Tunggu timer verifikasi sistem\n4. Dilarang unfollow (Anti-Drop penalty)",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step 1: Open Target Link
        OutlinedButton(
          onClick = {
            hasOpenedLink = true
            try {
              val intent = Intent(Intent.ACTION_VIEW, Uri.parse(campaign.targetUrl))
              context.startActivity(intent)
            } catch (e: Exception) {
              // Fallback
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("open_target_link_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = ElectricIndigo
          )
        ) {
          Icon(
            imageVector = Icons.Default.OpenInNew,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (hasOpenedLink) "Buka Link Lagi" else "1. Buka Profil Target (${campaign.platform.displayName})",
            fontWeight = FontWeight.Bold
          )
        }

        // Step 2: Verification Countdown
        if (hasOpenedLink && !isTimerFinished) {
          Spacer(modifier = Modifier.height(14.dp))
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(ElectricIndigo.copy(alpha = 0.08f))
              .padding(12.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = ElectricIndigo
              )
              Text(
                text = "Memverifikasi kunjungan: $countdownSeconds detik...",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricIndigo
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
              progress = { (6 - countdownSeconds) / 6f },
              modifier = Modifier.fillMaxWidth(),
              color = ElectricIndigo,
              trackColor = Color.LightGray.copy(alpha = 0.3f),
            )
          }
        }

        // Step 3: Handle Input
        AnimatedVisibility(visible = isTimerFinished || hasOpenedLink) {
          Column(modifier = Modifier.padding(top = 14.dp)) {
            OutlinedTextField(
              value = followerHandle,
              onValueChange = {
                followerHandle = it
                handleErrorMessage = null
              },
              label = { Text("Username Anda (@username)") },
              placeholder = { Text("cth: @akun_saya") },
              singleLine = true,
              isError = handleErrorMessage != null,
              supportingText = {
                Text(
                  text = handleErrorMessage ?: "Digunakan kreator untuk audit keaslian follow.",
                  fontSize = 10.sp
                )
              },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("follower_handle_input")
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
              if (followerHandle.trim().length < 3) {
                handleErrorMessage = "Masukkan username Anda untuk verifikasi"
                return@Button
              }
              onVerifyAndClaim(followerHandle.trim())
            },
            enabled = hasOpenedLink && isTimerFinished,
            colors = ButtonDefaults.buttonColors(
              containerColor = EmeraldAuthentic,
              disabledContainerColor = Color.Gray.copy(alpha = 0.2f)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("claim_follow_coins_button")
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Klaim +${campaign.coinRewardPerFollow} Koin", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
