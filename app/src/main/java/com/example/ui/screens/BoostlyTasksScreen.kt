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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CampaignEntity
import com.example.data.model.FollowVerificationEntity
import com.example.data.model.SocialPlatform
import com.example.data.model.VerificationStatus
import com.example.ui.components.LiveClockMiniBadge
import com.example.ui.model.TaskAccountItem
import com.example.ui.theme.BoostlyAppBg
import com.example.ui.theme.BoostlyBorderLight
import com.example.ui.theme.BoostlyDarkGreenBtn
import com.example.ui.theme.BoostlyEmerald
import com.example.ui.theme.BoostlyGold
import com.example.ui.theme.BoostlyMintPill
import com.example.ui.theme.BoostlyMintText
import com.example.ui.theme.BoostlyNeonLime
import com.example.ui.theme.BoostlyTextDark
import com.example.ui.theme.BoostlyTextGray
import com.example.ui.theme.BoostlyTextLightGray

/**
 * Screen 2: Tugas Follow
 * Matching exactly Phone 2 from the reference design:
 * Clean white background, centered title, segmented pill switch [Tersedia ✦ | Riwayat],
 * list of accounts with category, +10 coin mint pill, and dark green Follow button.
 */
@Composable
fun BoostlyTasksScreen(
  campaigns: List<CampaignEntity>,
  history: List<FollowVerificationEntity>,
  onBack: () -> Unit,
  onOpenFollowTask: (CampaignEntity) -> Unit,
  onOpenProfileDetail: (handle: String) -> Unit
) {
  var selectedTab by remember { mutableStateOf("Tersedia") }
  var selectedCategory by remember { mutableStateOf("Semua") }
  var searchQuery by remember { mutableStateOf("") }

  val categories = listOf("Semua", "Fashion", "Tech", "Kuliner", "Travel", "Gaming", "Art")

  // The 7 default featured tasks matching the mockup exactly
  val defaultTasks = listOf(
    TaskAccountItem("@luna.style", "Fashion & Lifestyle", Color(0xFFE91E63), 10, targetUrl = "https://instagram.com/luna.style"),
    TaskAccountItem("@tech.daily", "Tech & Gadget", Color(0xFF1E88E5), 10, targetUrl = "https://instagram.com/tech.daily"),
    TaskAccountItem("@foodies.id", "Food & Kuliner", Color(0xFFFB8C00), 10, targetUrl = "https://instagram.com/foodies.id"),
    TaskAccountItem("@travelgram", "Travel & Adventure", Color(0xFF43A047), 10, targetUrl = "https://instagram.com/travelgram"),
    TaskAccountItem("@fitnesszone", "Health & Fitness", Color(0xFF8E24AA), 10, targetUrl = "https://instagram.com/fitnesszone"),
    TaskAccountItem("@gaminghub", "Gaming", Color(0xFFE53935), 10, targetUrl = "https://instagram.com/gaminghub"),
    TaskAccountItem("@artspace", "Art & Creativity", Color(0xFF00ACC1), 10, targetUrl = "https://instagram.com/artspace")
  )

  // Merge default mock tasks and real user campaign tasks
  val userTasks = campaigns.filter { !it.accountHandle.startsWith("@luna") && !it.accountHandle.startsWith("@tech.daily") }.map { c ->
    TaskAccountItem(
      handle = c.accountHandle,
      category = c.category,
      avatarColor = BoostlyEmerald,
      rewardCoins = c.coinRewardPerFollow,
      campaignId = c.id,
      targetUrl = c.targetUrl
    )
  }

  val combinedTasks = defaultTasks + userTasks

  val filteredTasks = combinedTasks.filter { task ->
    val matchesCategory = selectedCategory == "Semua" || task.category.contains(selectedCategory, ignoreCase = true)
    val matchesSearch = searchQuery.isBlank() || task.handle.contains(searchQuery, ignoreCase = true) || task.category.contains(searchQuery, ignoreCase = true)
    matchesCategory && matchesSearch
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("boostly_tasks_screen"),
    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 95.dp)
  ) {
    // 1. Header with back arrow, centered Title, and Live Clock Badge
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onBack) {
            Icon(
              imageVector = Icons.Default.ArrowBack,
              contentDescription = "Kembali",
              tint = MaterialTheme.colorScheme.onBackground,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Tugas Follow",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
        }

        LiveClockMiniBadge()
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // 2. Segmented Pill Tabs: [ Tersedia ✦ ] vs [ Riwayat ] (Exact mockup styling)
    item {
      Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFF1F4F2),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(4.dp),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          // Tab "Tersedia ✦"
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (selectedTab == "Tersedia") BoostlyNeonLime else Color.Transparent,
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(20.dp))
              .clickable { selectedTab = "Tersedia" }
          ) {
            Box(
              modifier = Modifier.padding(vertical = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text(
                  text = "Tersedia",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (selectedTab == "Tersedia") Color(0xFF111827) else BoostlyTextGray
                )
                if (selectedTab == "Tersedia") {
                  Text(
                    text = "✦",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                  )
                }
              }
            }
          }

          // Tab "Riwayat"
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (selectedTab == "Riwayat") BoostlyNeonLime else Color.Transparent,
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(20.dp))
              .clickable { selectedTab = "Riwayat" }
          ) {
            Box(
              modifier = Modifier.padding(vertical = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "Riwayat",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (selectedTab == "Riwayat") Color(0xFF111827) else BoostlyTextGray
              )
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(14.dp))
    }

    // 2b. Target Harian Verified Tasks Progress Card
    item {
      val verifiedCount = history.count { it.status == VerificationStatus.VERIFIED_ACTIVE }
      val targetGoal = 5
      val progress = (verifiedCount.toFloat() / targetGoal.toFloat()).coerceIn(0f, 1f)

      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
        border = androidx.compose.foundation.BorderStroke(1.dp, BoostlyBorderLight),
        modifier = Modifier.fillMaxWidth().testTag("target_harian_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                  .background(BoostlyMintPill),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.TrendingUp,
                  contentDescription = null,
                  tint = BoostlyMintText,
                  modifier = Modifier.size(16.dp)
                )
              }
              Text(
                text = "Target Follow Harian",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = BoostlyTextDark
              )
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (verifiedCount >= targetGoal) BoostlyEmerald else BoostlyNeonLime
            ) {
              Text(
                text = "$verifiedCount / $targetGoal Selesai",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (verifiedCount >= targetGoal) Color.White else Color(0xFF111827),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Progress Bar
          LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp)),
            color = BoostlyEmerald,
            trackColor = Color(0xFFE5E7EB)
          )

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = if (verifiedCount >= targetGoal) "🎉 Target tercapai! Bonus harian aktif" else "Selesaikan ${targetGoal - verifiedCount} follow lagi untuk bonus",
              fontSize = 11.sp,
              color = BoostlyTextGray
            )
            Text(
              text = "${(progress * 100).toInt()}%",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = BoostlyTextDark
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    if (selectedTab == "Tersedia") {
      // 3. Search Bar and Category Chips
      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Cari akun atau kategori...", color = BoostlyTextLightGray, fontSize = 13.sp) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BoostlyTextLightGray) },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Clear, contentDescription = "Hapus", tint = BoostlyTextLightGray)
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = BoostlyTextDark,
            unfocusedTextColor = BoostlyTextDark,
            focusedBorderColor = BoostlyDarkGreenBtn,
            unfocusedBorderColor = BoostlyBorderLight,
            focusedContainerColor = Color(0xFFF9FAFB),
            unfocusedContainerColor = Color(0xFFF9FAFB)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(categories) { cat ->
            val isSelected = selectedCategory == cat
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = if (isSelected) BoostlyNeonLime else Color(0xFFF3F4F6),
              modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable { selectedCategory = cat }
            ) {
              Text(
                text = cat,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color(0xFF111827) else BoostlyTextGray,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))
      }

      // Empty State
      if (filteredTasks.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(40.dp),
            contentAlignment = Alignment.Center
          ) {
            Text("Tidak ada tugas ditemukan.", color = BoostlyTextGray, fontSize = 14.sp)
          }
        }
      }

      // 4. Task List Items (Matching Phone 2)
      items(filteredTasks) { task ->
        TaskListItem(
          task = task,
          onItemClick = { onOpenProfileDetail(task.handle) },
          onFollowClick = {
            val campaign = campaigns.firstOrNull { it.id == task.campaignId }
              ?: CampaignEntity(
                id = task.campaignId,
                creatorId = 999,
                creatorName = task.handle.removePrefix("@"),
                platform = SocialPlatform.INSTAGRAM,
                targetUrl = task.targetUrl,
                accountHandle = task.handle,
                category = task.category,
                targetFollowers = 100,
                coinRewardPerFollow = task.rewardCoins,
                totalCoinsAllocated = 1000
              )
            onOpenFollowTask(campaign)
          }
        )
        Spacer(modifier = Modifier.height(12.dp))
      }

      // 5. Bottom note (Exact footer from mockup)
      item {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = "Follow akun dan dapatkan koin\nsetelah terverifikasi (maks. 24 jam).",
          fontSize = 11.sp,
          color = BoostlyTextLightGray,
          textAlign = TextAlign.Center,
          lineHeight = 16.sp,
          modifier = Modifier.fillMaxWidth()
        )
      }
    } else {
      // Riwayat Tab: Verifikasi dan Reward
      if (history.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(40.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("Belum ada riwayat follow.", color = BoostlyTextDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
              Spacer(modifier = Modifier.height(4.dp))
              Text("Selesaikan tugas follow di tab 'Tersedia' untuk mulai mengumpulkan koin.", color = BoostlyTextGray, fontSize = 12.sp, textAlign = TextAlign.Center)
            }
          }
        }
      } else {
        items(history) { record ->
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, BoostlyBorderLight),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth().padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(text = record.targetAccountHandle, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BoostlyTextDark)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "Status: ${record.status.name}", fontSize = 11.sp, color = BoostlyTextGray)
              }

              Surface(
                shape = RoundedCornerShape(12.dp),
                color = BoostlyMintPill
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = BoostlyGold, modifier = Modifier.size(12.dp))
                  Text(text = "+${record.coinsEarned}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BoostlyMintText)
                }
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Task List Item Composable
 * Avatar circle + Handle & Category + Mint pill badge + Dark Forest Green Follow Button
 */
@Composable
private fun TaskListItem(
  task: TaskAccountItem,
  onItemClick: () -> Unit,
  onFollowClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onItemClick)
      .testTag("task_item_${task.handle}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Left: Avatar + Details
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(task.avatarColor),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = task.handle.take(2).uppercase().replace("@", ""),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.White
          )
        }
        Column {
          Text(
            text = task.handle,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = task.category,
            fontSize = 11.sp,
            color = BoostlyTextGray
          )
        }
      }

      // Right: Coin Pill & Follow Button
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Mint Pill "+10 🪙"
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = BoostlyMintPill,
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD1FAE5))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
          ) {
            Text(
              text = "+${task.rewardCoins}",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = BoostlyMintText
            )
            Icon(
              imageVector = Icons.Default.MonetizationOn,
              contentDescription = null,
              tint = BoostlyGold,
              modifier = Modifier.size(12.dp)
            )
          }
        }

        // Dark forest green "Follow" Button
        Button(
          onClick = onFollowClick,
          colors = ButtonDefaults.buttonColors(
            containerColor = BoostlyDarkGreenBtn,
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(20.dp),
          contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp),
          modifier = Modifier.testTag("follow_btn_${task.handle}")
        ) {
          Text(
            text = "Follow",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}
