package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldAuthentic
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.GoldCoinDark
import com.example.ui.theme.InstagramPink
import com.example.ui.theme.TikTokRed
import com.example.ui.theme.YouTubeRed

enum class ChartTimeframe(val label: String, val subtitle: String) {
  WEEKLY("Mingguan (7 Hari)", "Kenaikan Harian per Minggu"),
  MONTHLY("Bulanan (4 Minggu)", "Kenaikan Mingguan per Bulan")
}

data class ChartPointData(
  val label: String,
  val value: Float,
  val details: String,
  val dateLabel: String
)

@Composable
fun GrowthAnalyticsChart(
  modifier: Modifier = Modifier
) {
  var selectedTimeframe by remember { mutableStateOf(ChartTimeframe.WEEKLY) }
  var selectedPlatformFilter by remember { mutableStateOf("Semua") }
  var selectedPointIndex by remember { mutableIntStateOf(4) } // default selected day/week

  // Weekly Dataset (7 days)
  val weeklyPoints = remember(selectedPlatformFilter) {
    when (selectedPlatformFilter) {
      "Instagram" -> listOf(
        ChartPointData("Sen", 6f, "+6 IG Follower", "Senin"),
        ChartPointData("Sel", 9f, "+9 IG Follower", "Selasa"),
        ChartPointData("Rab", 14f, "+14 IG Follower", "Rabu"),
        ChartPointData("Kam", 8f, "+8 IG Follower", "Kamis"),
        ChartPointData("Jum", 12f, "+12 IG Follower", "Jumat"),
        ChartPointData("Sab", 18f, "+18 IG Follower", "Sabtu"),
        ChartPointData("Min", 15f, "+15 IG Follower", "Minggu")
      )
      "TikTok" -> listOf(
        ChartPointData("Sen", 8f, "+8 TT Follower", "Senin"),
        ChartPointData("Sel", 12f, "+12 TT Follower", "Selasa"),
        ChartPointData("Rab", 19f, "+19 TT Follower", "Rabu"),
        ChartPointData("Kam", 10f, "+10 TT Follower", "Kamis"),
        ChartPointData("Jum", 16f, "+16 TT Follower", "Jumat"),
        ChartPointData("Sab", 24f, "+24 TT Follower", "Sabtu"),
        ChartPointData("Min", 20f, "+20 TT Follower", "Minggu")
      )
      "YouTube" -> listOf(
        ChartPointData("Sen", 3f, "+3 Subs", "Senin"),
        ChartPointData("Sel", 4f, "+4 Subs", "Selasa"),
        ChartPointData("Rab", 6f, "+6 Subs", "Rabu"),
        ChartPointData("Kam", 5f, "+5 Subs", "Kamis"),
        ChartPointData("Jum", 7f, "+7 Subs", "Jumat"),
        ChartPointData("Sab", 9f, "+9 Subs", "Sabtu"),
        ChartPointData("Min", 8f, "+8 Subs", "Minggu")
      )
      else -> listOf(
        ChartPointData("Sen", 18f, "+18 Follower Baru", "Senin"),
        ChartPointData("Sel", 26f, "+26 Follower Baru", "Selasa"),
        ChartPointData("Rab", 39f, "+39 Follower Baru (Puncak)", "Rabu"),
        ChartPointData("Kam", 24f, "+24 Follower Baru", "Kamis"),
        ChartPointData("Jum", 35f, "+35 Follower Baru", "Jumat"),
        ChartPointData("Sab", 51f, "+51 Follower Baru (Viral)", "Sabtu"),
        ChartPointData("Min", 43f, "+43 Follower Baru", "Minggu")
      )
    }
  }

  // Monthly Dataset (4 weeks / 30 days)
  val monthlyPoints = remember(selectedPlatformFilter) {
    when (selectedPlatformFilter) {
      "Instagram" -> listOf(
        ChartPointData("Mgg 1", 52f, "+52 Pengikut IG", "Minggu ke-1"),
        ChartPointData("Mgg 2", 74f, "+74 Pengikut IG", "Minggu ke-2"),
        ChartPointData("Mgg 3", 95f, "+95 Pengikut IG", "Minggu ke-3"),
        ChartPointData("Mgg 4", 120f, "+120 Pengikut IG", "Minggu ke-4")
      )
      "TikTok" -> listOf(
        ChartPointData("Mgg 1", 65f, "+65 Pengikut TT", "Minggu ke-1"),
        ChartPointData("Mgg 2", 98f, "+98 Pengikut TT", "Minggu ke-2"),
        ChartPointData("Mgg 3", 132f, "+132 Pengikut TT", "Minggu ke-3"),
        ChartPointData("Mgg 4", 168f, "+168 Pengikut TT", "Minggu ke-4")
      )
      "YouTube" -> listOf(
        ChartPointData("Mgg 1", 20f, "+20 Pelanggan", "Minggu ke-1"),
        ChartPointData("Mgg 2", 28f, "+28 Pelanggan", "Minggu ke-2"),
        ChartPointData("Mgg 3", 38f, "+38 Pelanggan", "Minggu ke-3"),
        ChartPointData("Mgg 4", 46f, "+46 Pelanggan", "Minggu ke-4")
      )
      else -> listOf(
        ChartPointData("Mgg 1", 137f, "+137 Total Pengikut", "Minggu ke-1"),
        ChartPointData("Mgg 2", 200f, "+200 Total Pengikut", "Minggu ke-2"),
        ChartPointData("Mgg 3", 265f, "+265 Total Pengikut", "Minggu ke-3"),
        ChartPointData("Mgg 4", 334f, "+334 Total Pengikut", "Minggu ke-4")
      )
    }
  }

  val activePoints = if (selectedTimeframe == ChartTimeframe.WEEKLY) weeklyPoints else monthlyPoints
  val safePointIndex = selectedPointIndex.coerceIn(0, activePoints.size - 1)
  val activePoint = activePoints[safePointIndex]
  val totalGain = activePoints.sumOf { it.value.toInt() }
  val averageGain = totalGain / activePoints.size.toFloat()

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .background(MaterialTheme.colorScheme.surface)
      .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
      .padding(16.dp)
      .testTag("growth_analytics_chart")
  ) {
    // Header & Timeframe Switcher Tabs
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.AutoGraph,
            contentDescription = null,
            tint = ElectricIndigo,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Grafik Pertumbuhan Pengikut",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
        Text(
          text = selectedTimeframe.subtitle,
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      // Timeframe Tab Selector (Mingguan vs Bulanan)
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.padding(2.dp)
      ) {
        Row(modifier = Modifier.padding(2.dp)) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(if (selectedTimeframe == ChartTimeframe.WEEKLY) ElectricIndigo else Color.Transparent)
              .clickable {
                selectedTimeframe = ChartTimeframe.WEEKLY
                selectedPointIndex = 4
              }
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = "Mingguan",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (selectedTimeframe == ChartTimeframe.WEEKLY) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(if (selectedTimeframe == ChartTimeframe.MONTHLY) ElectricIndigo else Color.Transparent)
              .clickable {
                selectedTimeframe = ChartTimeframe.MONTHLY
                selectedPointIndex = 3
              }
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = "Bulanan",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (selectedTimeframe == ChartTimeframe.MONTHLY) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Platform Filter Chips
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      listOf("Semua", "Instagram", "TikTok", "YouTube").forEach { platform ->
        val isSelected = selectedPlatformFilter == platform
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (isSelected) {
            when (platform) {
              "Instagram" -> InstagramPink.copy(alpha = 0.2f)
              "TikTok" -> TikTokRed.copy(alpha = 0.2f)
              "YouTube" -> YouTubeRed.copy(alpha = 0.2f)
              else -> ElectricIndigo.copy(alpha = 0.2f)
            }
          } else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) {
              when (platform) {
                "Instagram" -> InstagramPink
                "TikTok" -> TikTokRed
                "YouTube" -> YouTubeRed
                else -> ElectricIndigo
              }
            } else Color.Transparent
          ),
          modifier = Modifier
            .clickable { selectedPlatformFilter = platform }
        ) {
          Text(
            text = platform,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) {
              when (platform) {
                "Instagram" -> InstagramPink
                "TikTok" -> TikTokRed
                "YouTube" -> YouTubeRed
                else -> ElectricIndigo
              }
            } else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Interactive Highlight Banner of Selected Point
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
      border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldAuthentic.copy(alpha = 0.3f)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "${activePoint.dateLabel} (${selectedPlatformFilter})",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = activePoint.details,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = EmeraldAuthentic
          )
        }
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = if (selectedTimeframe == ChartTimeframe.WEEKLY) "Total 7 Hari" else "Total Bulan Ini",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "+$totalGain Pengikut",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = ElectricIndigo
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Canvas Smooth Chart with Interactive Scrubber
    val values = activePoints.map { it.value }
    val maxVal = (values.maxOrNull() ?: 50f).coerceAtLeast(10f)

    Canvas(
      modifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
        .pointerInput(activePoints) {
          detectTapGestures { offset ->
            val w = size.width
            val stepX = w / (activePoints.size - 1).coerceAtLeast(1)
            val closestIndex = (offset.x / stepX + 0.5f).toInt().coerceIn(0, activePoints.size - 1)
            selectedPointIndex = closestIndex
          }
        }
    ) {
      val w = size.width
      val h = size.height
      val paddingBottom = 22.dp.toPx()
      val usableHeight = h - paddingBottom
      val stepX = w / (activePoints.size - 1).coerceAtLeast(1)

      // Horizontal subtle grid lines
      val gridLines = 4
      for (i in 0..gridLines) {
        val y = usableHeight * (i.toFloat() / gridLines)
        drawLine(
          color = Color.Gray.copy(alpha = 0.15f),
          start = Offset(0f, y),
          end = Offset(w, y),
          strokeWidth = 1.dp.toPx()
        )
      }

      val points = values.mapIndexed { index, value ->
        val x = index * stepX
        val y = usableHeight - ((value / maxVal) * (usableHeight * 0.85f))
        Offset(x, y)
      }

      // Smooth Curve
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

        // Gradient Area Fill
        drawPath(
          path = fillPath,
          brush = Brush.verticalGradient(
            colors = listOf(
              ElectricIndigo.copy(alpha = 0.38f),
              CyanAccent.copy(alpha = 0.15f),
              Color.Transparent
            ),
            startY = 0f,
            endY = usableHeight
          )
        )

        // Gradient Stroke Line
        drawPath(
          path = path,
          brush = Brush.horizontalGradient(
            listOf(ElectricIndigo, CyanAccent, EmeraldAuthentic)
          ),
          style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw Nodes
        points.forEachIndexed { idx, point ->
          val isSelected = idx == safePointIndex
          if (isSelected) {
            // Draw vertical indicator line for selected point
            drawLine(
              color = EmeraldAuthentic.copy(alpha = 0.5f),
              start = Offset(point.x, 0f),
              end = Offset(point.x, usableHeight),
              strokeWidth = 1.5.dp.toPx()
            )

            // Outer pulse circle
            drawCircle(
              color = EmeraldAuthentic.copy(alpha = 0.25f),
              radius = 12.dp.toPx(),
              center = point
            )
            drawCircle(
              color = Color.White,
              radius = 6.dp.toPx(),
              center = point
            )
            drawCircle(
              color = EmeraldAuthentic,
              radius = 4.dp.toPx(),
              center = point
            )
          } else {
            drawCircle(
              color = Color.White,
              radius = 4.5.dp.toPx(),
              center = point
            )
            drawCircle(
              color = ElectricIndigo,
              radius = 2.5.dp.toPx(),
              center = point
            )
          }
        }
      }
    }

    // X-Axis Labels Row (Clickable)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      activePoints.forEachIndexed { index, point ->
        val isSelected = index == safePointIndex
        Text(
          text = point.label,
          fontSize = 11.sp,
          fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
          color = if (isSelected) EmeraldAuthentic else MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier
            .clickable { selectedPointIndex = index }
            .padding(horizontal = 4.dp, vertical = 2.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Growth Summary Cards
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.weight(1f)
      ) {
        Column(modifier = Modifier.padding(8.dp)) {
          Text(
            text = "Rata-Rata Kenaikan",
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "+${String.format("%.1f", averageGain)} / ${if (selectedTimeframe == ChartTimeframe.WEEKLY) "hari" else "minggu"}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }

      Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.weight(1f)
      ) {
        Column(modifier = Modifier.padding(8.dp)) {
          Text(
            text = "Retensi Pengikut",
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "97.4% Organik",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = EmeraldAuthentic
          )
        }
      }

      Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.weight(1f)
      ) {
        Column(modifier = Modifier.padding(8.dp)) {
          Text(
            text = "Tren Kecepatan",
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "+24.8% vs Lalu",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = CyanAccent
          )
        }
      }
    }
  }
}
