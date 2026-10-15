package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Whatshot
import com.example.data.model.CoinTransactionEntity
import com.example.data.model.TransactionType
import com.example.data.remote.laravel.LaravelConnectionStatus
import com.example.data.remote.laravel.LaravelSyncStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.example.ui.theme.isThemeDark
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CampaignEntity
import com.example.data.model.UserEntity
import com.example.data.sync.DailyCheckInEvaluation
import com.example.ui.components.BoostlyBrandHeader
import com.example.ui.components.LiveClockMiniBadge
import com.example.ui.model.RecommendedCreator
import com.example.ui.theme.BoostlyAppBg
import com.example.ui.theme.BoostlyBorderLight
import com.example.ui.theme.BoostlyCreamBorder
import com.example.ui.theme.BoostlyCreamCard
import com.example.ui.theme.BoostlyDarkGreenBtn
import com.example.ui.theme.BoostlyDarkGreenCard
import com.example.ui.theme.BoostlyDarkGreenPill
import com.example.ui.theme.BoostlyEmerald
import com.example.ui.theme.BoostlyGold
import com.example.ui.theme.BoostlyGoldDark
import com.example.ui.theme.BoostlyGoldLight
import com.example.ui.theme.BoostlyMintCard
import com.example.ui.theme.BoostlyMintPill
import com.example.ui.theme.BoostlyMintText
import com.example.ui.theme.BoostlyNeonLime
import com.example.ui.theme.BoostlyTextDark
import com.example.ui.theme.BoostlyTextGray
import com.example.ui.theme.BoostlyTextLightGray
import com.example.ui.theme.BoostlyTextMuted
import com.example.ui.theme.BoostlyTextWhite

/**
 * Screen 1: Beranda (Home)
 * Matching exactly Phone 1 from the reference design:
 * Clean white background, dark green Saldo Koin card, cream banner, horizontal creator row,
 * and mint green Undang Teman card.
 */
@Composable
fun BoostlyHomeScreen(
  user: UserEntity?,
  campaigns: List<CampaignEntity>,
  dailyEvaluation: DailyCheckInEvaluation? = null,
  totalCheckInsCount: Int = 0,
  transactions: List<CoinTransactionEntity> = emptyList(),
  laravelConnectionStatus: LaravelConnectionStatus = LaravelConnectionStatus.Disconnected,
  laravelSyncStatus: LaravelSyncStatus = LaravelSyncStatus.Idle,
  onNavigateToTasks: () -> Unit,
  onNavigateToWallet: () -> Unit,
  onNavigateToDetail: (handle: String) -> Unit,
  onShowAntiCheatInfo: () -> Unit,
  onOpenQrisShop: () -> Unit,
  onClaimDailyBonus: () -> Unit = {},
  onOpenDailyCheckInDialog: () -> Unit = {},
  onOpenLaravelSyncDialog: () -> Unit = {},
  onOpenAiJobDetector: () -> Unit = {},
  onOpenNotificationCenter: () -> Unit = {},
  onToggleTheme: () -> Unit = {},
  onOpenThemeDialog: () -> Unit = {}
) {
  val isDark = isThemeDark()
  var showFaqDialog by remember { mutableStateOf(false) }
  var hasClaimedDaily by remember { mutableStateOf(false) }

  // 4 Featured Creators matching the mockup avatars
  val recommendedCreators = listOf(
    RecommendedCreator("@vianz", "Vian Pratama", Color(0xFFE57373), "Creator", 10),
    RecommendedCreator("@keziara", "Kezia Rahma", Color(0xFFBA68C8), "Lifestyle", 10),
    RecommendedCreator("@dimasr", "Dimas Rizky", Color(0xFF4FC3F7), "Tech", 10),
    RecommendedCreator("@selviaa", "Selvia Anindya", Color(0xFFFFB74D), "Beauty", 10),
    RecommendedCreator("@luna.style", "Luna Style", Color(0xFF81C784), "Fashion", 10)
  )

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("boostly_home_screen"),
    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 95.dp)
  ) {
    // 1. Top Bar: Logo Boostly (black text) on left, Live Clock, Bell, & Theme on right
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Boostly Brand Mark + Theme Adaptive text
        BoostlyBrandHeader(
          iconSize = 34.dp,
          fontSize = 24,
          textColor = MaterialTheme.colorScheme.onBackground
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Laravel Cloud / Online Status Pill
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = when (laravelConnectionStatus) {
              is LaravelConnectionStatus.Connected -> Color(0xFFE8F5E9)
              is LaravelConnectionStatus.Connecting -> Color(0xFFFFF8E1)
              else -> Color(0xFFF3F4F6)
            },
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              when (laravelConnectionStatus) {
                is LaravelConnectionStatus.Connected -> Color(0xFF81C784)
                is LaravelConnectionStatus.Connecting -> Color(0xFFFFD54F)
                else -> Color(0xFFE5E7EB)
              }
            ),
            modifier = Modifier
              .clickable(onClick = onOpenLaravelSyncDialog)
              .testTag("top_bar_laravel_sync_badge")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
            ) {
              Icon(
                imageVector = when (laravelConnectionStatus) {
                  is LaravelConnectionStatus.Connected -> Icons.Default.CloudDone
                  is LaravelConnectionStatus.Connecting -> Icons.Default.Sync
                  else -> Icons.Default.CloudSync
                },
                contentDescription = "Sync Laravel",
                tint = when (laravelConnectionStatus) {
                  is LaravelConnectionStatus.Connected -> Color(0xFF2E7D32)
                  is LaravelConnectionStatus.Connecting -> Color(0xFFF57F17)
                  else -> BoostlyDarkGreenBtn
                },
                modifier = Modifier.size(13.dp)
              )
              Text(
                text = when (laravelConnectionStatus) {
                  is LaravelConnectionStatus.Connected -> "Online"
                  is LaravelConnectionStatus.Connecting -> "Sync..."
                  else -> "Laravel"
                },
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = when (laravelConnectionStatus) {
                  is LaravelConnectionStatus.Connected -> Color(0xFF1B5E20)
                  is LaravelConnectionStatus.Connecting -> Color(0xFFF57F17)
                  else -> BoostlyDarkGreenBtn
                }
              )
            }
          }

          // Live Clock Mini Badge (shows real time ticking WIB)
          LiveClockMiniBadge()

          // Notification Bell with Red Dot Badge (Push Notifications Center)
          Box(contentAlignment = Alignment.TopEnd) {
            IconButton(
              onClick = onOpenNotificationCenter,
              modifier = Modifier.size(38.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifikasi",
                tint = if (isDark) Color.White else BoostlyTextDark,
                modifier = Modifier.size(24.dp)
              )
            }
            // Red dot badge
            Box(
              modifier = Modifier
                .padding(top = 7.dp, end = 7.dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(Color(0xFFEF4444))
            )
          }

          // Global Theme Toggle Button (Light/Dark Mode Switch)
          IconButton(
            onClick = onToggleTheme,
            modifier = Modifier
              .size(38.dp)
              .testTag("home_theme_toggle_btn")
          ) {
            Icon(
              imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
              contentDescription = if (isDark) "Beralih ke Mode Terang" else "Beralih ke Mode Gelap",
              tint = if (isDark) BoostlyNeonLime else BoostlyTextDark,
              modifier = Modifier.size(22.dp)
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // 1b. User Welcome & AI Detected Job Badge
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable(onClick = onOpenAiJobDetector),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Halo, ${user?.name ?: "Kreator"} 👋",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(
              text = if (!user?.detectedJob.isNullOrBlank()) "✨ ${user.detectedJob}" else "✨ Deteksi Pekerjaan AI",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF7C3AED)
            )
            if (!user?.recommendedNiche.isNullOrBlank()) {
              Text(
                text = "• ${user.recommendedNiche}",
                fontSize = 10.sp,
                color = BoostlyTextMuted
              )
            }
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFEDE9FE))
            .border(1.dp, Color(0xFFDDD6FE), RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = "AI Job ✨",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6D28D9)
          )
        }
      }
      Spacer(modifier = Modifier.height(12.dp))
    }

    // 2. Card 1: "Saldo Koin" (Dark Green rounded card matching mockup)
    item {
      Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = BoostlyDarkGreenCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .clickable(onClick = onNavigateToWallet)
          .testTag("saldo_koin_card")
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 20.dp)
        ) {
          // Top row: "Saldo Koin" + Right Arrow
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Saldo Koin",
              fontSize = 13.sp,
              color = Color(0xFFA3C2AF),
              fontWeight = FontWeight.Medium
            )
            Icon(
              imageVector = Icons.Default.ArrowForward,
              contentDescription = null,
              tint = Color(0xFFA3C2AF),
              modifier = Modifier.size(16.dp)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Bottom row: Gold Coin + "245" + Arrow on Left | "Riwayat" Pill Button on Right
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              // 3D Gold Coin Circle Graphic
              Box(
                modifier = Modifier
                  .size(38.dp)
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
                  modifier = Modifier.size(24.dp)
                )
              }

              Text(
                text = "${user?.points ?: 245}",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = BoostlyTextWhite,
                letterSpacing = (-0.5).sp
              )

              Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color(0xFFA3C2AF),
                modifier = Modifier.size(16.dp)
              )
            }

            // "Riwayat" Pill Button (Dark forest pill with white text)
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = BoostlyDarkGreenPill,
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF205234)),
              modifier = Modifier.clickable(onClick = onNavigateToWallet)
            ) {
              Text(
                text = "Riwayat",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = BoostlyTextWhite,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
              )
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // 2b. Card: "Bonus Check-in Harian" with Streak & 1-tap claim
    item {
      DailyCheckInHomeBanner(
        evaluation = dailyEvaluation,
        onClaim = onClaimDailyBonus,
        onOpenDialog = onOpenDailyCheckInDialog
      )
      Spacer(modifier = Modifier.height(16.dp))
    }

    // 3. Card 2: "Follow akun & dapatkan koin" (Cream Banner with Falling Coins illustration)
    item {
      Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1B2E21) else BoostlyCreamCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF265437) else BoostlyCreamBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Left: Texts & "Mulai Sekarang →" dark button
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Follow akun & dapatkan koin",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = if (isDark) BoostlyTextWhite else BoostlyTextDark
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Tugas sederhana, koin nyata!",
              fontSize = 12.sp,
              color = if (isDark) Color(0xFFA1B5A8) else BoostlyTextGray
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Dark green pill button
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = BoostlyDarkGreenBtn,
              modifier = Modifier
                .clickable(onClick = onNavigateToTasks)
                .testTag("mulai_sekarang_banner_button")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text(
                  text = "Mulai Sekarang",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = BoostlyTextWhite
                )
                Icon(
                  imageVector = Icons.Default.ArrowForward,
                  contentDescription = null,
                  tint = BoostlyTextWhite,
                  modifier = Modifier.size(13.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.width(12.dp))

          // Right: 3D Gold Coins Graphic
          Box(
            modifier = Modifier.size(72.dp),
            contentAlignment = Alignment.Center
          ) {
            Canvas(modifier = Modifier.size(72.dp)) {
              // Coin 1 (Back)
              drawCircle(
                color = BoostlyGoldDark,
                radius = 18.dp.toPx(),
                center = Offset(size.width * 0.65f, size.height * 0.35f)
              )
              // Coin 2 (Front Left)
              drawCircle(
                brush = Brush.linearGradient(listOf(BoostlyGoldLight, BoostlyGold, BoostlyGoldDark)),
                radius = 24.dp.toPx(),
                center = Offset(size.width * 0.45f, size.height * 0.6f)
              )
              // Coin 3 (Small Top)
              drawCircle(
                brush = Brush.linearGradient(listOf(BoostlyGoldLight, BoostlyGold)),
                radius = 12.dp.toPx(),
                center = Offset(size.width * 0.25f, size.height * 0.3f)
              )
            }
            Icon(
              imageVector = Icons.Default.MonetizationOn,
              contentDescription = null,
              tint = Color(0xFF78350F),
              modifier = Modifier.size(28.dp)
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(18.dp))
    }

    // 3b. Quick Actions Row (Tugas Baru, Tarik Koin, Topup QRIS, Sync Laravel)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Quick Action 1: Tugas Follow
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = if (isDark) Color(0xFF1E293B) else Color(0xFFF9FAFB),
          border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF334155) else BoostlyBorderLight),
          modifier = Modifier
            .weight(1f)
            .clickable(onClick = onNavigateToTasks)
            .testTag("quick_action_tugas")
        ) {
          Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isDark) Color(0xFF064E3B) else BoostlyMintPill),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Assignment,
                contentDescription = null,
                tint = if (isDark) BoostlyNeonLime else BoostlyMintText,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Tugas Follow",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (isDark) BoostlyTextWhite else BoostlyTextDark
            )
            Text(
              text = "+10 Koin",
              fontSize = 10.sp,
              color = BoostlyEmerald,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        // Quick Action 2: Tarik Saldo
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = if (isDark) Color(0xFF1E293B) else Color(0xFFF9FAFB),
          border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF334155) else BoostlyBorderLight),
          modifier = Modifier
            .weight(1f)
            .clickable(onClick = onNavigateToWallet)
            .testTag("quick_action_tarik")
        ) {
          Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isDark) Color(0xFF78350F).copy(alpha = 0.4f) else Color(0xFFFEF3C7)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.MonetizationOn,
                contentDescription = null,
                tint = if (isDark) BoostlyGoldLight else BoostlyGoldDark,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Dompet",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (isDark) BoostlyTextWhite else BoostlyTextDark
            )
            Text(
              text = "Tarik Koin",
              fontSize = 10.sp,
              color = if (isDark) Color(0xFF94A3B8) else BoostlyTextGray
            )
          }
        }

        // Quick Action 3: Topup QRIS
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = if (isDark) Color(0xFF1E293B) else Color(0xFFF9FAFB),
          border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF334155) else BoostlyBorderLight),
          modifier = Modifier
            .weight(1f)
            .clickable(onClick = onOpenQrisShop)
            .testTag("quick_action_qris")
        ) {
          Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isDark) Color(0xFF312E81) else Color(0xFFE0E7FF)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.QrCode,
                contentDescription = null,
                tint = if (isDark) Color(0xFFA5B4FC) else Color(0xFF4338CA),
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Beli Koin",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (isDark) BoostlyTextWhite else BoostlyTextDark
            )
            Text(
              text = "QRIS Instan",
              fontSize = 10.sp,
              color = if (isDark) Color(0xFF94A3B8) else BoostlyTextGray
            )
          }
        }

        // Quick Action 4: Laravel Cloud Sync
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = if (isDark) Color(0xFF1E293B) else Color(0xFFF9FAFB),
          border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF334155) else BoostlyBorderLight),
          modifier = Modifier
            .weight(1f)
            .clickable(onClick = onOpenLaravelSyncDialog)
            .testTag("quick_action_laravel_sync")
        ) {
          Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFFF2D20).copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CloudSync,
                contentDescription = null,
                tint = Color(0xFFFF2D20),
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Laravel",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (isDark) BoostlyTextWhite else BoostlyTextDark
            )
            Text(
              text = "Sync DB",
              fontSize = 10.sp,
              color = if (isDark) Color(0xFF94A3B8) else BoostlyTextGray
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(20.dp))
    }

    // 3c. Dashboard Card: "Aktivitas Koin Terkini" (Recent Transactions Preview)
    item {
      val recentDateFmt = remember { SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID")) }
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC)
        ),
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          if (isDark) Color(0xFF334155) else BoostlyBorderLight
        ),
        modifier = Modifier
          .fillMaxWidth()
          .clickable(onClick = onNavigateToWallet)
          .testTag("dashboard_recent_transactions_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          // Header: Title & "Lihat Dompet →"
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
                  .size(28.dp)
                  .clip(CircleShape)
                  .background(if (isDark) Color(0xFF0F3B20) else Color(0xFFD1FAE5)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.ReceiptLong,
                  contentDescription = null,
                  tint = BoostlyEmerald,
                  modifier = Modifier.size(16.dp)
                )
              }
              Column {
                Text(
                  text = "Aktivitas Koin Terkini",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isDark) BoostlyTextWhite else BoostlyTextDark
                )
                Text(
                  text = "Riwayat perolehan & pemakaian koin",
                  fontSize = 11.sp,
                  color = if (isDark) Color(0xFF94A3B8) else BoostlyTextGray
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (isDark) Color(0xFF0F2F1B) else BoostlyMintPill,
              modifier = Modifier.clickable(onClick = onNavigateToWallet)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Text(
                  text = "Buka Dompet",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isDark) BoostlyNeonLime else BoostlyDarkGreenBtn
                )
                Icon(
                  imageVector = Icons.Default.ArrowForward,
                  contentDescription = null,
                  tint = if (isDark) BoostlyNeonLime else BoostlyDarkGreenBtn,
                  modifier = Modifier.size(12.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // List top 3 recent transactions
          val previewItems = if (transactions.isNotEmpty()) {
            transactions.take(3)
          } else {
            // Default preview transactions when DB is fresh
            listOf(
              CoinTransactionEntity(
                userId = user?.id ?: 2,
                amount = 10,
                type = TransactionType.EARNED_FOLLOW,
                description = "Follow @luna.style (Tugas)",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 15
              ),
              CoinTransactionEntity(
                userId = user?.id ?: 2,
                amount = 15,
                type = TransactionType.DAILY_CHECKIN_BONUS,
                description = "Bonus Check-In Harian Streak",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 2
              ),
              CoinTransactionEntity(
                userId = user?.id ?: 2,
                amount = -50,
                type = TransactionType.EXCHANGED_CAMPAIGN,
                description = "Tukar 50 Follower Instagram",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 6
              )
            )
          }

          previewItems.forEachIndexed { index, tx ->
            val isEarn = tx.amount > 0
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
              ) {
                Box(
                  modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                      if (isEarn) {
                        if (isDark) Color(0xFF064E3B) else Color(0xFFDCFCE7)
                      } else {
                        if (isDark) Color(0xFF7F1D1D) else Color(0xFFFEE2E2)
                      }
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = if (isEarn) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = if (isEarn) BoostlyEmerald else Color(0xFFDC2626),
                    modifier = Modifier.size(16.dp)
                  )
                }

                Column {
                  Text(
                    text = tx.description,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) BoostlyTextWhite else BoostlyTextDark,
                    maxLines = 1
                  )
                  Text(
                    text = recentDateFmt.format(Date(tx.timestamp)),
                    fontSize = 10.sp,
                    color = if (isDark) Color(0xFF94A3B8) else BoostlyTextGray
                  )
                }
              }

              // Amount badge
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isEarn) {
                  if (isDark) Color(0xFF064E3B).copy(alpha = 0.6f) else Color(0xFFDCFCE7)
                } else {
                  if (isDark) Color(0xFF7F1D1D).copy(alpha = 0.6f) else Color(0xFFFEE2E2)
                }
              ) {
                Text(
                  text = if (isEarn) "+${tx.amount} Koin" else "${tx.amount} Koin",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isEarn) BoostlyEmerald else Color(0xFFDC2626),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }
            }

            if (index < previewItems.size - 1) {
              androidx.compose.material3.HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = if (isDark) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFE2E8F0)
              )
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(18.dp))
    }

    // 4. Section: "Rekomendasi Akun" with "Lihat Semua"
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Rekomendasi Akun",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = if (isDark) BoostlyTextWhite else BoostlyTextDark
        )
        Text(
          text = "Lihat Semua",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = BoostlyEmerald,
          modifier = Modifier.clickable(onClick = onNavigateToTasks)
        )
      }
      Spacer(modifier = Modifier.height(14.dp))

      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(recommendedCreators) { creator ->
          CreatorAvatarCard(
            creator = creator,
            onClick = { onNavigateToDetail(creator.handle) }
          )
        }
      }
      Spacer(modifier = Modifier.height(20.dp))
    }

    // 5. Card 3: "Undang Teman" (Mint Green Card matching mockup)
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isDark) Color(0xFF143021) else BoostlyMintCard
        ),
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          if (isDark) Color(0xFF265437) else Color(0xFFD1FAE5)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .clickable(onClick = onOpenQrisShop)
          .testTag("undang_teman_card")
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            // Gift Icon inside rounded square
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isDark) Color(0xFF0A2216) else Color(0xFFD1FAE5)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CardGiftcard,
                contentDescription = null,
                tint = BoostlyEmerald,
                modifier = Modifier.size(24.dp)
              )
            }
            Column {
              Text(
                text = "Undang Teman",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (isDark) BoostlyTextWhite else BoostlyTextDark
              )
              Text(
                text = "Dapatkan bonus koin tambahan!",
                fontSize = 12.sp,
                color = if (isDark) Color(0xFFA1B5A8) else BoostlyTextGray
              )
            }
          }

          Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = if (isDark) Color(0xFFA1B5A8) else BoostlyTextLightGray,
            modifier = Modifier.size(18.dp)
          )
        }
      }
      Spacer(modifier = Modifier.height(14.dp))
    }

    // 6. Security Assurance Badge
    item {
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isDark) Color(0xFF1E293B) else Color(0xFFF9FAFB),
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          if (isDark) Color(0xFF334155) else BoostlyBorderLight
        ),
        modifier = Modifier
          .fillMaxWidth()
          .clickable(onClick = onShowAntiCheatInfo)
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            tint = BoostlyEmerald,
            modifier = Modifier.size(20.dp)
          )
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Sistem Anti-Curang Aktif",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = if (isDark) BoostlyTextWhite else BoostlyTextDark
            )
            Text(
              text = "Setiap follow diverifikasi autentik. Follower tetap bertahan.",
              fontSize = 11.sp,
              color = if (isDark) Color(0xFF94A3B8) else BoostlyTextGray
            )
          }
        }
      }
    }
  }

  // FAQ Guide Dialog
  if (showFaqDialog) {
    Dialog(onDismissRequest = { showFaqDialog = false }) {
      Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier.fillMaxWidth().padding(8.dp)
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text(
            text = "Cara Kerja Boostly 🚀",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = BoostlyTextDark
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "1. Buka tab Tugas untuk memilih akun yang ingin di-follow.\n2. Buka profil target dan tekan Follow.\n3. Tunggu timer verifikasi sistem (6 detik).\n4. Koin otomatis masuk ke saldo Anda!\n5. Koin dapat ditukar kembali menjadi follower untuk akun Anda.",
            fontSize = 13.sp,
            color = BoostlyTextGray,
            lineHeight = 18.sp
          )
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = { showFaqDialog = false },
            colors = ButtonDefaults.buttonColors(containerColor = BoostlyDarkGreenBtn, contentColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("Mengerti", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

/**
 * Creator Avatar Card for Horizontal Carousel
 * Circular photo + Handle + Mint pill with coin reward
 */
@Composable
private fun CreatorAvatarCard(
  creator: RecommendedCreator,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clip(RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .padding(4.dp)
  ) {
    // Circle avatar with gradient / photo style
    Box(
      modifier = Modifier
        .size(60.dp)
        .clip(CircleShape)
        .background(creator.avatarColor)
        .border(1.5.dp, Color(0xFFE5E7EB), CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = creator.handle.take(2).uppercase().replace("@", ""),
        fontWeight = FontWeight.Black,
        fontSize = 20.sp,
        color = Color.White
      )
    }

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = creator.handle,
      fontSize = 12.sp,
      fontWeight = FontWeight.SemiBold,
      color = BoostlyTextDark
    )

    Spacer(modifier = Modifier.height(4.dp))

    // Mint green pill with coin icon + 10
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = BoostlyMintPill,
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD1FAE5))
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
      ) {
        Icon(
          imageVector = Icons.Default.MonetizationOn,
          contentDescription = null,
          tint = BoostlyGold,
          modifier = Modifier.size(11.dp)
        )
        Text(
          text = "+${creator.coinsReward}",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = BoostlyMintText
        )
      }
    }
  }
}

@Composable
private fun DailyCheckInHomeBanner(
  evaluation: DailyCheckInEvaluation?,
  onClaim: () -> Unit,
  onOpenDialog: () -> Unit
) {
  val eval = evaluation ?: return
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onOpenDialog)
      .testTag("daily_check_in_home_banner")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Left: Flame / Streak Info
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(if (eval.canClaimToday) Color(0xFFFEF3C7) else BoostlyMintPill),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (eval.canClaimToday) Icons.Default.Whatshot else Icons.Default.Check,
              contentDescription = null,
              tint = if (eval.canClaimToday) Color(0xFFD97706) else Color(0xFF059669),
              modifier = Modifier.size(22.dp)
            )
          }

          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = "Bonus Check-In Harian",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFEF3C7)
              ) {
                Text(
                  text = "🔥 Hari ${eval.currentStreakDay}/7",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF92400E),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(2.dp))

            if (eval.canClaimToday) {
              Text(
                text = "+${eval.rewardCoinsForToday} Koin gratis siap diklaim!",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFD97706)
              )
            } else {
              Text(
                text = "Sudah diklaim ✓ • Reset: ${eval.countdownFormatted}",
                fontSize = 11.sp,
                color = BoostlyTextGray
              )
            }
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Right Action: Klaim Button or "Lihat" Pill
        if (eval.canClaimToday) {
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = BoostlyDarkGreenBtn,
            modifier = Modifier
              .clickable(onClick = onClaim)
              .testTag("home_claim_daily_bonus_btn")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.MonetizationOn,
                contentDescription = null,
                tint = BoostlyGold,
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = "Klaim",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BoostlyTextWhite
              )
            }
          }
        } else {
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFF3F4F6),
            border = androidx.compose.foundation.BorderStroke(1.dp, BoostlyBorderLight),
            modifier = Modifier.clickable(onClick = onOpenDialog)
          ) {
            Text(
              text = "Lihat",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = BoostlyTextDark,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
          }
        }
      }

      // 7-day mini streak progress dots
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        eval.sevenDays.forEach { dayModel ->
          val dotColor = when {
            dayModel.isClaimed -> Color(0xFF10B981) // Green check
            dayModel.isTodayToClaim -> Color(0xFFF59E0B) // Amber active
            dayModel.isGrandBonus -> Color(0xFFFCD34D) // Grand yellow
            else -> Color(0xFFE5E7EB)
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .height(5.dp)
              .clip(RoundedCornerShape(3.dp))
              .background(dotColor)
          )
        }
      }
    }
  }
}

