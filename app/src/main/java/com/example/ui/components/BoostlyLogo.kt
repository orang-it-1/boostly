package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BoostlyGold
import com.example.ui.theme.BoostlyGoldDark
import com.example.ui.theme.BoostlyGoldLight
import com.example.ui.theme.BoostlyNeonLime
import com.example.ui.theme.BoostlyNeonLimeLight
import com.example.ui.theme.BoostlyTextDark

@Composable
fun BoostlyIconMark(
  modifier: Modifier = Modifier,
  size: Dp = 40.dp
) {
  Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
    Canvas(modifier = Modifier.size(size)) {
      val w = this.size.width
      val h = this.size.height

      val lime = BoostlyNeonLime
      val strokeW = w * 0.18f

      // Draw outer curved magnet/b loop path with upward arrow
      val path = Path().apply {
        // Left vertical stalk
        moveTo(w * 0.22f, h * 0.2f)
        lineTo(w * 0.22f, h * 0.65f)
        // Bottom curve
        arcTo(
          rect = Rect(w * 0.22f, h * 0.35f, w * 0.78f, h * 0.9f),
          startAngleDegrees = 180f,
          sweepAngleDegrees = -180f,
          forceMoveTo = false
        )
        // Right side going up
        lineTo(w * 0.78f, h * 0.32f)
      }

      drawPath(
        path = path,
        color = lime,
        style = Stroke(
          width = strokeW,
          cap = StrokeCap.Round,
          join = StrokeJoin.Round
        )
      )

      // Draw arrow head at top right
      val arrowPath = Path().apply {
        moveTo(w * 0.62f, h * 0.35f)
        lineTo(w * 0.85f, h * 0.20f)
        lineTo(w * 0.85f, h * 0.45f)
      }
      drawPath(
        path = arrowPath,
        color = lime,
        style = Stroke(width = strokeW * 0.85f, cap = StrokeCap.Round, join = StrokeJoin.Round)
      )

      // Draw center gold coin
      val coinCenter = Offset(w * 0.5f, h * 0.58f)
      val coinRadius = w * 0.17f

      drawCircle(
        brush = Brush.linearGradient(listOf(BoostlyGoldLight, BoostlyGold, BoostlyGoldDark)),
        radius = coinRadius,
        center = coinCenter
      )

      // Coin inner rim
      drawCircle(
        color = BoostlyGoldDark,
        radius = coinRadius * 0.75f,
        center = coinCenter,
        style = Stroke(width = w * 0.03f)
      )
    }
  }
}

@Composable
fun BoostlyBrandHeader(
  modifier: Modifier = Modifier,
  iconSize: Dp = 32.dp,
  fontSize: Int = 22,
  textColor: Color = BoostlyTextDark
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    BoostlyIconMark(size = iconSize)
    Text(
      text = "Boostly",
      fontSize = fontSize.sp,
      fontWeight = FontWeight.Black,
      color = textColor,
      letterSpacing = (-0.5).sp
    )
  }
}
