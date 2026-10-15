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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PlayCircleOutline
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CampaignEntity
import com.example.data.model.SocialPlatform
import com.example.ui.theme.BoostlyAppBg
import com.example.ui.theme.BoostlyBorderLight
import com.example.ui.theme.BoostlyDarkGreenBtn
import com.example.ui.theme.BoostlyGold
import com.example.ui.theme.BoostlyMintPill
import com.example.ui.theme.BoostlyMintText
import com.example.ui.theme.BoostlyTextDark
import com.example.ui.theme.BoostlyTextGray
import com.example.ui.theme.BoostlyTextLightGray
import com.example.ui.theme.BoostlyVerifiedBlue

data class PhotoPost(
  val caption: String,
  val likes: String,
  val colors: List<Color>
)

/**
 * Screen 3: Detail Akun (@luna.style)
 * Matching exactly Phone 3 from the reference design:
 * Clean white background, circular avatar, handle with verified checkmark,
 * 1.2K / 45.8K / 312 stats, bio, [+10 🪙] pill & [Follow] button, tabs, and 3x2 photo grid.
 */
@Composable
fun BoostlyProfileDetailScreen(
  handle: String = "@luna.style",
  category: String = "Fashion & Lifestyle",
  onBack: () -> Unit,
  onFollowAction: (CampaignEntity) -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  var isFollowing by remember { mutableStateOf(false) }

  // 6 Photo cards for the 3-column aesthetic grid matching mockup
  val posts = listOf(
    PhotoPost("OOTD minimalist cafe vibe", "2.4K", listOf(Color(0xFFE2D4C9), Color(0xFFC7B198))),
    PhotoPost("City lights & modern architecture", "3.1K", listOf(Color(0xFFB0C4DE), Color(0xFF778899))),
    PhotoPost("Golden hour street portrait", "4.8K", listOf(Color(0xFFFFDAB9), Color(0xFFDEB887))),
    PhotoPost("Warm coffee morning aesthetics", "1.9K", listOf(Color(0xFFD7CCC8), Color(0xFFA1887F))),
    PhotoPost("Emerald green evening outfit", "5.2K", listOf(Color(0xFFA5D6A7), Color(0xFF66BB6A))),
    PhotoPost("Summer breeze sunset session", "3.7K", listOf(Color(0xFFFFE0B2), Color(0xFFFFB74D)))
  )

  LazyVerticalGrid(
    columns = GridCells.Fixed(3),
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("boostly_profile_detail_screen"),
    contentPadding = PaddingValues(bottom = 95.dp),
    horizontalArrangement = Arrangement.spacedBy(3.dp),
    verticalArrangement = Arrangement.spacedBy(3.dp)
  ) {
    // 1. Top Bar + Profile Header + Bio + Action Buttons
    item(span = { GridItemSpan(3) }) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top Navigation Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onBack) {
            Icon(
              imageVector = Icons.Default.ArrowBack,
              contentDescription = "Kembali",
              tint = MaterialTheme.colorScheme.onBackground,
              modifier = Modifier.size(24.dp)
            )
          }

          IconButton(onClick = { /* More options */ }) {
            Icon(
              imageVector = Icons.Default.MoreHoriz,
              contentDescription = "Menu",
              tint = MaterialTheme.colorScheme.onBackground,
              modifier = Modifier.size(24.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Large Circular Avatar
        Box(
          modifier = Modifier
            .size(92.dp)
            .clip(CircleShape)
            .background(
              Brush.linearGradient(
                listOf(Color(0xFFF06292), Color(0xFFE91E63), Color(0xFFAD1457))
              )
            )
            .border(2.dp, Color(0xFFE5E7EB), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = handle.take(2).uppercase().replace("@", ""),
            fontWeight = FontWeight.Black,
            fontSize = 32.sp,
            color = Color.White
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Handle with Blue Verified Checkmark Badge
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text(
            text = handle,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Terverifikasi",
            tint = BoostlyVerifiedBlue,
            modifier = Modifier.size(18.dp)
          )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Category
        Text(
          text = category,
          fontSize = 13.sp,
          color = BoostlyTextGray
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 3-Column Stats Row: 1.2K Postingan | 45.8K Pengikut | 312 Mengikuti
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "1.2K", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BoostlyTextDark)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "Postingan", fontSize = 11.sp, color = BoostlyTextGray)
          }
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "45.8K", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BoostlyTextDark)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "Pengikut", fontSize = 11.sp, color = BoostlyTextGray)
          }
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "312", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BoostlyTextDark)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "Mengikuti", fontSize = 11.sp, color = BoostlyTextGray)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bio Text (Exact from mockup)
        Text(
          text = "Style is a way to say who you are\nwithout having to speak. ✨",
          fontSize = 13.sp,
          color = BoostlyTextDark,
          textAlign = TextAlign.Center,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Action Buttons Row: [+10 🪙] Pill + [Follow] Wide Button
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Mint Pill "+10 🪙"
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = BoostlyMintPill,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD1FAE5)),
            modifier = Modifier.height(44.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 16.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text(
                text = "+10",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = BoostlyMintText
              )
              Icon(
                imageVector = Icons.Default.MonetizationOn,
                contentDescription = null,
                tint = BoostlyGold,
                modifier = Modifier.size(14.dp)
              )
            }
          }

          // Wide Dark Forest Green "Follow" Button
          Button(
            onClick = {
              isFollowing = !isFollowing
              val campaign = CampaignEntity(
                id = 101,
                creatorId = 101,
                creatorName = handle.removePrefix("@"),
                platform = SocialPlatform.INSTAGRAM,
                targetUrl = "https://instagram.com/${handle.removePrefix("@")}",
                accountHandle = handle,
                category = category,
                targetFollowers = 200,
                coinRewardPerFollow = 10,
                totalCoinsAllocated = 2000
              )
              onFollowAction(campaign)
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isFollowing) BoostlyMintPill else BoostlyDarkGreenBtn,
              contentColor = if (isFollowing) BoostlyMintText else Color.White
            ),
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier
              .weight(1f)
              .height(44.dp)
              .testTag("detail_follow_action_button")
          ) {
            Text(
              text = if (isFollowing) "Mengikuti ✓" else "Follow",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Grid Tabs Row: [Grid icon] [Reels icon] [Bookmark icon]
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          IconButton(onClick = { selectedTab = 0 }) {
            Icon(
              imageVector = Icons.Default.GridOn,
              contentDescription = "Grid",
              tint = if (selectedTab == 0) BoostlyTextDark else BoostlyTextLightGray,
              modifier = Modifier.size(24.dp)
            )
          }
          IconButton(onClick = { selectedTab = 1 }) {
            Icon(
              imageVector = Icons.Default.PlayCircleOutline,
              contentDescription = "Reels",
              tint = if (selectedTab == 1) BoostlyTextDark else BoostlyTextLightGray,
              modifier = Modifier.size(24.dp)
            )
          }
          IconButton(onClick = { selectedTab = 2 }) {
            Icon(
              imageVector = Icons.Default.BookmarkBorder,
              contentDescription = "Tersimpan",
              tint = if (selectedTab == 2) BoostlyTextDark else BoostlyTextLightGray,
              modifier = Modifier.size(24.dp)
            )
          }
        }
      }
    }

    // 2. 3-Column Photo Grid
    items(posts.size) { index ->
      val post = posts[index]
      Box(
        modifier = Modifier
          .aspectRatio(1f)
          .background(Brush.verticalGradient(post.colors)),
        contentAlignment = Alignment.BottomStart
      ) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.08f))
        )
      }
    }
  }
}
