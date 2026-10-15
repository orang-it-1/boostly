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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.model.SocialPlatform
import com.example.data.model.UserEntity
import com.example.ui.components.PlatformBadge
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldAuthentic
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.GoldCoinDark
import com.example.ui.theme.GoldCoinLight

@Composable
fun CampaignsScreen(
  campaigns: List<CampaignEntity>,
  currentUser: UserEntity?,
  selectedPlatform: SocialPlatform?,
  searchQuery: String,
  onSelectPlatform: (SocialPlatform?) -> Unit,
  onSearchQueryChange: (String) -> Unit,
  onOpenFollowDialog: (CampaignEntity) -> Unit,
  onOpenCreateCampaign: () -> Unit,
  onShowAntiCheatInfo: () -> Unit
) {
  Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(bottom = 90.dp)
    ) {
      // Anti-Cheat Protective Banner
      item {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
          border = androidx.compose.foundation.BorderStroke(1.dp, ElectricIndigo.copy(alpha = 0.3f)),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onShowAntiCheatInfo)
            .testTag("anti_cheat_banner")
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
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
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = EmeraldAuthentic,
                modifier = Modifier.size(20.dp)
              )
            }
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Proteksi Anti-Curang Aktif",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Verifikasi link nyata, timer deteksi bot & penalti unfollow. Ketuk untuk detail.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 15.sp
              )
            }
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = "Detail",
              tint = ElectricIndigo,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      // Search & Platform Filters
      item {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
          OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Cari username, kreator, atau kategori...") },
            leadingIcon = {
              Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            },
            trailingIcon = {
              if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                  Icon(Icons.Default.Clear, contentDescription = "Hapus")
                }
              }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = MaterialTheme.colorScheme.surface,
              unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("search_campaign_input")
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Platform Filter Chips
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            item {
              FilterChip(
                label = "Semua",
                isSelected = selectedPlatform == null,
                onClick = { onSelectPlatform(null) }
              )
            }
            items(SocialPlatform.values()) { platform ->
              FilterChip(
                label = platform.displayName,
                isSelected = selectedPlatform == platform,
                onClick = { onSelectPlatform(platform) }
              )
            }
          }
        }
      }

      // Feed Section Title
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Target Link Aktif (${campaigns.size})",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = "Follow & Dapatkan Koin",
            fontSize = 12.sp,
            color = GoldCoinDark,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      // Empty State
      if (campaigns.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(40.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "Belum Ada Kampanye Tersedia",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              )
              Text(
                text = "Jadilah yang pertama meluncurkan target link dengan menukar koin Anda!",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
              )
            }
          }
        }
      }

      // Campaign Cards List
      items(campaigns, key = { it.id }) { campaign ->
        val isOwnCampaign = currentUser != null && campaign.creatorId == currentUser.id
        CampaignCard(
          campaign = campaign,
          isOwnCampaign = isOwnCampaign,
          onFollowClick = { onOpenFollowDialog(campaign) }
        )
      }
    }

    // Floating Button to Exchange Coins
    FloatingActionButton(
      onClick = onOpenCreateCampaign,
      containerColor = ElectricIndigo,
      contentColor = Color.White,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(end = 20.dp, bottom = 90.dp)
        .testTag("fab_create_campaign")
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.Add, contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "Tukar Koin", fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
private fun FilterChip(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(20.dp),
    color = if (isSelected) ElectricIndigo else MaterialTheme.colorScheme.surfaceVariant,
    modifier = Modifier
      .clip(RoundedCornerShape(20.dp))
      .clickable(onClick = onClick)
  ) {
    Text(
      text = label,
      color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
      fontSize = 12.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
    )
  }
}

@Composable
private fun CampaignCard(
  campaign: CampaignEntity,
  isOwnCampaign: Boolean,
  onFollowClick: () -> Unit
) {
  val progress = (campaign.currentFollowers.toFloat() / campaign.targetFollowers.toFloat()).coerceIn(0f, 1f)

  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .testTag("campaign_card_${campaign.id}")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        PlatformBadge(platform = campaign.platform)

        // Coin Reward Pill
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = GoldCoin.copy(alpha = 0.15f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.MonetizationOn,
              contentDescription = null,
              tint = GoldCoinDark,
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = "+${campaign.coinRewardPerFollow} Koin",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = GoldCoinDark
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = campaign.accountHandle,
        fontSize = 17.sp,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onSurface
      )

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(top = 2.dp)
      ) {
        Text(
          text = "oleh ${campaign.creatorName}",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = "•", fontSize = 11.sp, color = Color.Gray)
        Text(
          text = campaign.category,
          fontSize = 11.sp,
          color = ElectricIndigo,
          fontWeight = FontWeight.Medium
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Progress bar towards target followers
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Progres: ${campaign.currentFollowers} / ${campaign.targetFollowers} Pengikut",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontWeight = FontWeight.SemiBold
        )
        Text(
          text = "${(progress * 100).toInt()}%",
          fontSize = 11.sp,
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

      Spacer(modifier = Modifier.height(14.dp))

      // Action Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = EmeraldAuthentic.copy(alpha = 0.1f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Verified,
              contentDescription = null,
              tint = EmeraldAuthentic,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Anti-Unfollow Dilindungi",
              fontSize = 10.sp,
              color = EmeraldAuthentic,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        if (isOwnCampaign) {
          Text(
            text = "Kampanye Anda",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        } else {
          Button(
            onClick = onFollowClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo),
            modifier = Modifier.testTag("follow_campaign_button_${campaign.id}")
          ) {
            Icon(
              imageVector = Icons.Default.OpenInNew,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Buka & Follow",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
        }
      }
    }
  }
}
