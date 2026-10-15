package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Clean Canvas-based Google "G" multicolored logo icon.
 */
@Composable
fun GoogleLogoIcon(
  modifier: Modifier = Modifier,
  size: Dp = 20.dp
) {
  Canvas(modifier = modifier.size(size)) {
    val w = this.size.width
    val h = this.size.height

    // Official Google Brand Colors
    val blueColor = Color(0xFF4285F4)
    val greenColor = Color(0xFF34A853)
    val yellowColor = Color(0xFFFBBC05)
    val redColor = Color(0xFFEA4335)

    val cx = w / 2f
    val cy = h / 2f
    val r = w / 2f
    val innerR = r * 0.58f

    // Blue Bar on right side
    drawRect(
      color = blueColor,
      topLeft = Offset(cx, cy - innerR * 0.45f),
      size = Size(r, innerR * 0.9f)
    )

    // Red sector (top)
    val redPath = Path().apply {
      moveTo(cx, cy)
      arcTo(
        rect = androidx.compose.ui.geometry.Rect(0f, 0f, w, h),
        startAngleDegrees = -135f,
        sweepAngleDegrees = 90f,
        forceMoveTo = false
      )
      close()
    }
    drawPath(redPath, redColor, style = Fill)

    // Yellow sector (left)
    val yellowPath = Path().apply {
      moveTo(cx, cy)
      arcTo(
        rect = androidx.compose.ui.geometry.Rect(0f, 0f, w, h),
        startAngleDegrees = 135f,
        sweepAngleDegrees = 90f,
        forceMoveTo = false
      )
      close()
    }
    drawPath(yellowPath, yellowColor, style = Fill)

    // Green sector (bottom)
    val greenPath = Path().apply {
      moveTo(cx, cy)
      arcTo(
        rect = androidx.compose.ui.geometry.Rect(0f, 0f, w, h),
        startAngleDegrees = 45f,
        sweepAngleDegrees = 90f,
        forceMoveTo = false
      )
      close()
    }
    drawPath(greenPath, greenColor, style = Fill)

    // Blue sector (right arc top & bottom)
    val bluePath = Path().apply {
      moveTo(cx, cy)
      arcTo(
        rect = androidx.compose.ui.geometry.Rect(0f, 0f, w, h),
        startAngleDegrees = -45f,
        sweepAngleDegrees = 90f,
        forceMoveTo = false
      )
      close()
    }
    drawPath(bluePath, blueColor, style = Fill)

    // Center cutout circle
    drawCircle(
      color = Color.White,
      radius = innerR,
      center = Offset(cx, cy)
    )

    // Horizontal bar to complete Google 'G' shape
    drawRect(
      color = blueColor,
      topLeft = Offset(cx - innerR * 0.1f, cy - innerR * 0.4f),
      size = Size(r * 1.05f - (cx - innerR * 0.1f), innerR * 0.8f)
    )

    // White gap between right upper quadrant and horizontal bar
    val gapPath = Path().apply {
      moveTo(cx, cy)
      arcTo(
        rect = androidx.compose.ui.geometry.Rect(cx - innerR * 1.05f, cy - innerR * 1.05f, cx + innerR * 1.05f, cy + innerR * 1.05f),
        startAngleDegrees = -45f,
        sweepAngleDegrees = 45f,
        forceMoveTo = false
      )
      close()
    }
    drawPath(gapPath, Color.White, style = Fill)
  }
}
