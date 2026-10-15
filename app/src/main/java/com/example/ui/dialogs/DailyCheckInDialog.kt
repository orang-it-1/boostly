package com.example.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.sync.DailyCheckInEvaluation
import com.example.data.sync.DailyStreakDayUiModel
import com.example.ui.theme.BoostlyBorderLight
import com.example.ui.theme.BoostlyCreamCard
import com.example.ui.theme.BoostlyDarkGreenBtn
import com.example.ui.theme.BoostlyGold
import com.example.ui.theme.BoostlyGoldDark
import com.example.ui.theme.BoostlyGoldLight
import com.example.ui.theme.BoostlyMintPill
import com.example.ui.theme.BoostlyMintText
import com.example.ui.theme.BoostlyTextDark
import com.example.ui.theme.BoostlyTextGray
import com.example.ui.theme.BoostlyTextWhite

@Composable
fun DailyCheckInDialog(
  evaluation: DailyCheckInEvaluation,
  totalCheckInsCount: Int,
  onClaim: () -> Unit,
  onDismiss: () -> Unit
) {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse_claim")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(900, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      shape = RoundedCornerShape(26.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(vertical = 16.dp)
        .testTag("daily_check_in_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top row: Title + Close Button
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
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0xFFFEF3C7)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFD97706),
                modifier = Modifier.size(20.dp)
              )
            }
            Column {
              Text(
                text = "Bonus Check-in Harian",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = BoostlyTextDark
              )
              Text(
                text = "Buka aplikasi setiap hari untuk koin gratis!",
                fontSize = 11.sp,
                color = BoostlyTextGray
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp).testTag("close_daily_checkin_dialog")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Tutup",
              tint = BoostlyTextGray,
              modifier = Modifier.size(20.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hero Streak & Total Check-In Banner
        Surface(
          shape = RoundedCornerShape(18.dp),
          color = BoostlyCreamCard,
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 12.dp),
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
                  .background(
                    Brush.linearGradient(
                      listOf(BoostlyGoldLight, BoostlyGold, BoostlyGoldDark)
                    )
                  )
                  .border(1.5.dp, BoostlyGoldLight, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.MonetizationOn,
                  contentDescription = null,
                  tint = Color(0xFF78350F),
                  modifier = Modifier.size(26.dp)
                )
              }

              Column {
                Text(
                  text = "🔥 Streak Hari ke-${evaluation.currentStreakDay}",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = BoostlyTextDark
                )
                Text(
                  text = "Total ${totalCheckInsCount} hari check-in tercatat",
                  fontSize = 11.sp,
                  color = BoostlyTextGray
                )
              }
            }

            // Streak milestone badge
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color(0xFFFEF3C7),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCD34D))
            ) {
              Text(
                text = "${evaluation.currentStreakDay}/7 Hari",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF92400E),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 7-Day Rewards Calendar Grid
        Text(
          text = "Jadwal Hadiah 7 Hari Beruntun",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = BoostlyTextDark,
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          evaluation.sevenDays.take(4).forEach { dayModel ->
            Box(modifier = Modifier.weight(1f)) {
              DayCardItem(dayModel = dayModel)
            }
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          evaluation.sevenDays.drop(4).forEach { dayModel ->
            Box(modifier = Modifier.weight(1f)) {
              DayCardItem(dayModel = dayModel)
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Timestamp Synchronization & Anti-Tamper Status Bar
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFFF3F4F6),
          border = androidx.compose.foundation.BorderStroke(1.dp, BoostlyBorderLight),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Sync,
                contentDescription = null,
                tint = BoostlyMintText,
                modifier = Modifier.size(14.dp)
              )
              Text(
                text = "Sinkronisasi Waktu: ${evaluation.timezoneDisplayName} (${evaluation.todayDateKey})",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = BoostlyTextDark
              )
            }

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = BoostlyTextGray,
                modifier = Modifier.size(12.dp)
              )
              Text(
                text = "Reset 00:00:00",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = BoostlyTextGray
              )
            }
          }
        }

        // Clock Skew Warning (if detected)
        if (evaluation.isClockSkewDetected) {
          Spacer(modifier = Modifier.height(8.dp))
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFEF2F2),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color(0xFFDC2626),
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = evaluation.clockSkewErrorMessage ?: "Waktu tidak sinkron",
                fontSize = 11.sp,
                color = Color(0xFF991B1B)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Big Action Button
        if (evaluation.canClaimToday) {
          Button(
            onClick = {
              onClaim()
              // keep dialog open so user sees claimed checkmark, or let them dismiss
            },
            colors = ButtonDefaults.buttonColors(containerColor = BoostlyDarkGreenBtn),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .fillMaxWidth()
              .scale(pulseScale)
              .testTag("claim_daily_bonus_button")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.padding(vertical = 4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.MonetizationOn,
                contentDescription = null,
                tint = BoostlyGold,
                modifier = Modifier.size(20.dp)
              )
              Text(
                text = "Klaim Bonus Hari Ini (+${evaluation.rewardCoinsForToday} Koin)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = BoostlyTextWhite
              )
            }
          }
        } else {
          // Already Claimed or Clock Issue
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFECFDF5),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = null,
                  tint = Color(0xFF059669),
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = "Bonus Hari Ini Sudah Diklaim!",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF065F46)
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Buka lagi besok! Reset dalam: ${evaluation.countdownFormatted}",
                fontSize = 11.sp,
                color = Color(0xFF047857),
                fontWeight = FontWeight.Medium
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Secondary Dismiss Button
        Text(
          text = "Nanti Saja",
          fontSize = 12.sp,
          color = BoostlyTextGray,
          fontWeight = FontWeight.Medium,
          modifier = Modifier
            .clickable(onClick = onDismiss)
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("dismiss_daily_checkin_button")
        )
      }
    }
  }
}

@Composable
private fun DayCardItem(dayModel: DailyStreakDayUiModel) {
  val containerColor = when {
    dayModel.isClaimed -> BoostlyMintPill
    dayModel.isTodayToClaim -> Color(0xFFFEF3C7)
    dayModel.isGrandBonus -> Color(0xFFFFFBEB)
    else -> Color(0xFFF9FAFB)
  }

  val borderColor = when {
    dayModel.isClaimed -> Color(0xFF86EFAC)
    dayModel.isTodayToClaim -> Color(0xFFF59E0B)
    dayModel.isGrandBonus -> Color(0xFFFCD34D)
    else -> BoostlyBorderLight
  }

  Surface(
    shape = RoundedCornerShape(12.dp),
    color = containerColor,
    border = androidx.compose.foundation.BorderStroke(
      width = if (dayModel.isTodayToClaim) 1.5.dp else 1.dp,
      color = borderColor
    ),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp, horizontal = 4.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = "Hari ${dayModel.dayNumber}",
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        color = when {
          dayModel.isClaimed -> BoostlyMintText
          dayModel.isTodayToClaim -> Color(0xFFB45309)
          else -> BoostlyTextGray
        }
      )

      Spacer(modifier = Modifier.height(4.dp))

      // Icon state
      Box(
        modifier = Modifier.size(24.dp),
        contentAlignment = Alignment.Center
      ) {
        when {
          dayModel.isClaimed -> {
            Box(
              modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(Color(0xFF059669)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(13.dp)
              )
            }
          }
          dayModel.isGrandBonus -> {
            Icon(
              imageVector = Icons.Default.CardGiftcard,
              contentDescription = null,
              tint = Color(0xFFD97706),
              modifier = Modifier.size(20.dp)
            )
          }
          dayModel.isTodayToClaim -> {
            Icon(
              imageVector = Icons.Default.MonetizationOn,
              contentDescription = null,
              tint = Color(0xFFD97706),
              modifier = Modifier.size(20.dp)
            )
          }
          else -> {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = Color(0xFF9CA3AF),
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "+${dayModel.rewardCoins}",
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        color = when {
          dayModel.isClaimed -> BoostlyMintText
          dayModel.isTodayToClaim -> Color(0xFF92400E)
          dayModel.isGrandBonus -> Color(0xFFB45309)
          else -> BoostlyTextDark
        }
      )
    }
  }
}
