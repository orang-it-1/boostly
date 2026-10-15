package com.example.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SocialPlatform
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldAuthentic
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.GoldCoinDark
import com.example.ui.theme.GoldCoinLight
import com.example.ui.theme.InstagramPink
import com.example.ui.theme.QrisRed
import com.example.ui.theme.TikTokRed
import com.example.ui.theme.TwitterBlue
import com.example.ui.theme.YouTubeRed

@Composable
fun PlatformBadge(platform: SocialPlatform, modifier: Modifier = Modifier) {
  val badgeInfo: Pair<Color, String> = when (platform) {
    SocialPlatform.INSTAGRAM -> InstagramPink to "Instagram"
    SocialPlatform.TIKTOK -> TikTokRed to "TikTok"
    SocialPlatform.YOUTUBE -> YouTubeRed to "YouTube"
    SocialPlatform.X_TWITTER -> TwitterBlue to "X / Twitter"
    SocialPlatform.THREADS -> Color(0xFF262626) to "Threads"
  }
  val color = badgeInfo.first
  val name = badgeInfo.second

  Surface(
    shape = RoundedCornerShape(8.dp),
    color = color.copy(alpha = 0.15f),
    border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f)),
    modifier = modifier
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Box(
        modifier = Modifier
          .size(8.dp)
          .clip(CircleShape)
          .background(color)
      )
      Text(
        text = name,
        color = color,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

@Composable
fun CoinBalanceChip(
  coins: Int,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(20.dp),
    color = MaterialTheme.colorScheme.surfaceVariant,
    border = androidx.compose.foundation.BorderStroke(1.dp, GoldCoin.copy(alpha = 0.4f)),
    modifier = modifier
      .clip(RoundedCornerShape(20.dp))
      .clickable(onClick = onClick)
      .testTag("coin_balance_chip")
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Box(
        modifier = Modifier
          .size(24.dp)
          .clip(CircleShape)
          .background(Brush.linearGradient(listOf(GoldCoinLight, GoldCoinDark))),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.MonetizationOn,
          contentDescription = "Koin",
          tint = Color.White,
          modifier = Modifier.size(16.dp)
        )
      }
      Column {
        Text(
          text = "$coins Koin",
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
      }
      Box(
        modifier = Modifier
          .clip(CircleShape)
          .background(QrisRed.copy(alpha = 0.12f))
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Text(
          text = "+ QRIS",
          fontSize = 9.sp,
          fontWeight = FontWeight.Black,
          color = QrisRed
        )
      }
    }
  }
}

@Composable
fun AuthenticityScoreBadge(
  score: Float,
  modifier: Modifier = Modifier
) {
  val (color, label) = when {
    score >= 95f -> EmeraldAuthentic to "Sangat Autentik"
    score >= 80f -> CyanAccent to "Autentik"
    score >= 70f -> GoldCoin to "Perlu Audit"
    else -> AlertRed to "Risiko Curang"
  }

  Surface(
    shape = RoundedCornerShape(12.dp),
    color = color.copy(alpha = 0.12f),
    border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f)),
    modifier = modifier
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Icon(
        imageVector = Icons.Default.Verified,
        contentDescription = "Terverifikasi",
        tint = color,
        modifier = Modifier.size(16.dp)
      )
      Text(
        text = "Skor Keaslian: ${String.format("%.1f", score)}% ($label)",
        color = color,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold
      )
    }
  }
}

@Composable
fun RealtimeGrowthChart(
  dataPoints: List<Float> = listOf(24f, 38f, 45f, 62f, 85f, 110f, 142f),
  labels: List<String> = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"),
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(MaterialTheme.colorScheme.surface)
      .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
      .padding(16.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Pertumbuhan Pengikut Real-time",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "+142 Pengikut Baru 7 Hari Terakhir (100% Organik)",
          fontSize = 11.sp,
          color = EmeraldAuthentic,
          fontWeight = FontWeight.SemiBold
        )
      }
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .background(EmeraldAuthentic.copy(alpha = 0.15f))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text(
          text = "● LIVE",
          color = EmeraldAuthentic,
          fontSize = 10.sp,
          fontWeight = FontWeight.Black
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Canvas(
      modifier = Modifier
        .fillMaxWidth()
        .height(140.dp)
    ) {
      val w = size.width
      val h = size.height
      val paddingBottom = 20.dp.toPx()
      val usableHeight = h - paddingBottom

      val maxVal = (dataPoints.maxOrNull() ?: 100f).coerceAtLeast(1f)
      val minVal = 0f

      // Draw horizontal grid lines
      val gridLines = 3
      for (i in 0..gridLines) {
        val y = usableHeight * (i.toFloat() / gridLines)
        drawLine(
          color = Color.Gray.copy(alpha = 0.15f),
          start = Offset(0f, y),
          end = Offset(w, y),
          strokeWidth = 1.dp.toPx()
        )
      }

      val stepX = w / (dataPoints.size - 1)
      val points = dataPoints.mapIndexed { index, value ->
        val x = index * stepX
        val y = usableHeight - ((value - minVal) / (maxVal - minVal) * usableHeight)
        Offset(x, y)
      }

      // Smooth Curve Path
      val path = Path()
      val fillPath = Path()

      if (points.isNotEmpty()) {
        path.moveTo(points[0].x, points[0].y)
        fillPath.moveTo(points[0].x, usableHeight)
        fillPath.lineTo(points[0].x, points[0].y)

        for (i in 0 until points.size - 1) {
          val p0 = points[i]
          val p1 = points[i + 1]
          val cx = (p0.x + p1.x) / 2
          path.cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
          fillPath.cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
        }

        fillPath.lineTo(points.last().x, usableHeight)
        fillPath.close()

        // Draw Fill Gradient
        drawPath(
          path = fillPath,
          brush = Brush.verticalGradient(
            colors = listOf(
              ElectricIndigo.copy(alpha = 0.35f),
              ElectricIndigo.copy(alpha = 0.0f)
            ),
            startY = 0f,
            endY = usableHeight
          )
        )

        // Draw Stroke Curve
        drawPath(
          path = path,
          brush = Brush.horizontalGradient(
            listOf(ElectricIndigo, CyanAccent, EmeraldAuthentic)
          ),
          style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw Nodes
        points.forEach { point ->
          drawCircle(
            color = Color.White,
            radius = 5.dp.toPx(),
            center = point
          )
          drawCircle(
            color = ElectricIndigo,
            radius = 3.dp.toPx(),
            center = point
          )
        }
      }
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      labels.forEach { label ->
        Text(
          text = label,
          fontSize = 10.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontWeight = FontWeight.Medium
        )
      }
    }
  }
}

@Composable
fun QrisBarcodeVisual(
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .size(190.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(Color.White)
      .padding(12.dp),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(160.dp)) {
      val blockSize = size.width / 15f
      val qrColor = Color(0xFF1E293B)

      // Draw Corner Markers (Top Left, Top Right, Bottom Left)
      fun drawMarker(startX: Float, startY: Float) {
        // Outer box
        drawRect(
          color = qrColor,
          topLeft = Offset(startX, startY),
          size = androidx.compose.ui.geometry.Size(blockSize * 4, blockSize * 4)
        )
        // Inner white cutout
        drawRect(
          color = Color.White,
          topLeft = Offset(startX + blockSize * 0.7f, startY + blockSize * 0.7f),
          size = androidx.compose.ui.geometry.Size(blockSize * 2.6f, blockSize * 2.6f)
        )
        // Center black dot
        drawRect(
          color = qrColor,
          topLeft = Offset(startX + blockSize * 1.3f, startY + blockSize * 1.3f),
          size = androidx.compose.ui.geometry.Size(blockSize * 1.4f, blockSize * 1.4f)
        )
      }

      drawMarker(0f, 0f)
      drawMarker(size.width - blockSize * 4, 0f)
      drawMarker(0f, size.height - blockSize * 4)

      // Procedural Matrix Dots for realistic QRIS look
      val matrix = listOf(
        Pair(5, 1), Pair(6, 1), Pair(8, 1), Pair(9, 1),
        Pair(5, 2), Pair(7, 2), Pair(9, 2),
        Pair(5, 3), Pair(8, 3), Pair(9, 3),
        Pair(1, 5), Pair(2, 5), Pair(4, 5), Pair(6, 5), Pair(7, 5), Pair(10, 5), Pair(12, 5), Pair(13, 5),
        Pair(0, 6), Pair(3, 6), Pair(5, 6), Pair(7, 6), Pair(8, 6), Pair(11, 6), Pair(14, 6),
        Pair(2, 7), Pair(4, 7), Pair(6, 7), Pair(8, 7), Pair(10, 7), Pair(12, 7),
        Pair(1, 8), Pair(3, 8), Pair(5, 8), Pair(7, 8), Pair(9, 8), Pair(13, 8),
        Pair(6, 9), Pair(8, 9), Pair(10, 9), Pair(12, 9), Pair(14, 9),
        Pair(5, 11), Pair(7, 11), Pair(9, 11), Pair(11, 11), Pair(13, 11),
        Pair(6, 12), Pair(8, 12), Pair(10, 12), Pair(12, 12),
        Pair(5, 13), Pair(7, 13), Pair(9, 13), Pair(11, 13), Pair(13, 13)
      )

      matrix.forEach { (x, y) ->
        drawRect(
          color = qrColor,
          topLeft = Offset(x * blockSize, y * blockSize),
          size = androidx.compose.ui.geometry.Size(blockSize * 0.9f, blockSize * 0.9f)
        )
      }

      // Center G symbol / QRIS icon marker
      drawCircle(
        color = QrisRed,
        radius = blockSize * 1.2f,
        center = Offset(size.width / 2, size.height / 2)
      )
      drawCircle(
        color = Color.White,
        radius = blockSize * 0.7f,
        center = Offset(size.width / 2, size.height / 2)
      )
    }
  }
}
