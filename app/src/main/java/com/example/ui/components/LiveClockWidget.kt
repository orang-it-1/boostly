package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BoostlyDarkCard
import com.example.ui.theme.BoostlyDarkGreenBtn
import com.example.ui.theme.BoostlyEmerald
import com.example.ui.theme.BoostlyForestGreen
import com.example.ui.theme.BoostlyGold
import com.example.ui.theme.BoostlyNeonLime
import com.example.ui.theme.BoostlyTextMuted
import com.example.ui.theme.BoostlyTextWhite
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.sin

/**
 * State holder for real-time clock data updating every second
 */
class ClockState(
  val timestamp: Long,
  val hour: Int,
  val minute: Int,
  val second: Int,
  val timeString24: String,
  val timeString12: String,
  val amPm: String,
  val dateFormattedIndo: String,
  val greeting: String,
  val greetingIcon: androidx.compose.ui.graphics.vector.ImageVector,
  val secondsUntilMidnight: Long,
  val countdownFormatted: String,
  val timeZoneCode: String = "WIB"
)

@Composable
fun rememberLiveClock(): ClockState {
  var currentTimeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

  LaunchedEffect(Unit) {
    while (true) {
      currentTimeMillis = System.currentTimeMillis()
      delay(1000L)
    }
  }

  return remember(currentTimeMillis) {
    // Respect the user's actual device timezone (synced with system clock)
    val tz = TimeZone.getDefault()
    val cal = Calendar.getInstance(tz)
    cal.timeInMillis = currentTimeMillis

    val hour24 = cal.get(Calendar.HOUR_OF_DAY)
    val hour12 = cal.get(Calendar.HOUR).let { if (it == 0) 12 else it }
    val minute = cal.get(Calendar.MINUTE)
    val second = cal.get(Calendar.SECOND)
    val amPm = if (cal.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"

    val time24 = String.format(Locale.US, "%02d:%02d:%02d", hour24, minute, second)
    val time12 = String.format(Locale.US, "%02d:%02d:%02d", hour12, minute, second)

    // Determine standard zone label (WIB, WITA, WIT, or standard short code)
    val tzId = tz.id.lowercase()
    val rawOffsetHours = tz.rawOffset / (1000 * 60 * 60)
    val zoneLabel = when {
      rawOffsetHours == 7 || tzId.contains("jakarta") || tzId.contains("bangkok") -> "WIB"
      rawOffsetHours == 8 || tzId.contains("makassar") || tzId.contains("singapore") || tzId.contains("bali") -> "WITA"
      rawOffsetHours == 9 || tzId.contains("jayapura") || tzId.contains("tokyo") -> "WIT"
      else -> tz.getDisplayName(tz.inDaylightTime(Date(currentTimeMillis)), TimeZone.SHORT, Locale.US)
    }

    // Indonesian Date Formatter
    val dayNames = arrayOf("Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
    val monthNames = arrayOf(
      "Januari", "Februari", "Maret", "April", "Mei", "Juni",
      "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )

    val dayOfWeek = dayNames[cal.get(Calendar.DAY_OF_WEEK) - 1]
    val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
    val monthName = monthNames[cal.get(Calendar.MONTH)]
    val year = cal.get(Calendar.YEAR)

    val dateIndo = "$dayOfWeek, $dayOfMonth $monthName $year"

    // Dynamic greeting based on Indonesian cultural time periods
    val (greet, icon) = when (hour24) {
      in 4..10 -> "Selamat Pagi" to Icons.Default.WbSunny
      in 11..14 -> "Selamat Siang" to Icons.Default.WbSunny
      in 15..17 -> "Selamat Sore" to Icons.Default.WbTwilight
      else -> "Selamat Malam" to Icons.Default.NightsStay
    }

    // Countdown to midnight reset (24:00:00) in current timezone
    val midnightCal = Calendar.getInstance(tz).apply {
      timeInMillis = currentTimeMillis
      set(Calendar.HOUR_OF_DAY, 24)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }
    val diffMillis = midnightCal.timeInMillis - currentTimeMillis
    val diffSeconds = (diffMillis / 1000).coerceAtLeast(0)
    val hoursRemaining = diffSeconds / 3600
    val minutesRemaining = (diffSeconds % 3600) / 60
    val secondsRemaining = diffSeconds % 60
    val countdown = String.format(Locale.US, "%02dj %02dm %02dd", hoursRemaining, minutesRemaining, secondsRemaining)

    ClockState(
      timestamp = currentTimeMillis,
      hour = hour24,
      minute = minute,
      second = second,
      timeString24 = time24,
      timeString12 = time12,
      amPm = amPm,
      dateFormattedIndo = dateIndo,
      greeting = greet,
      greetingIcon = icon,
      secondsUntilMidnight = diffSeconds,
      countdownFormatted = countdown,
      timeZoneCode = zoneLabel
    )
  }
}

/**
 * Compact Live Clock Badge for Top App Bar
 */
@Composable
fun LiveClockMiniBadge(
  modifier: Modifier = Modifier,
  isDarkTheme: Boolean = false,
  clockState: ClockState = rememberLiveClock()
) {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse_clock")
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_alpha"
  )

  Surface(
    shape = RoundedCornerShape(20.dp),
    color = if (isDarkTheme) BoostlyDarkCard else Color(0xFFF3F4F6),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (isDarkTheme) BoostlyForestGreen else Color(0xFFE5E7EB)
    ),
    modifier = modifier.testTag("live_clock_mini_badge")
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      // Pulsing Live Indicator Dot (Green in light, Neon in dark)
      Box(
        modifier = Modifier
          .size(7.dp)
          .clip(CircleShape)
          .background((if (isDarkTheme) BoostlyNeonLime else BoostlyEmerald).copy(alpha = pulseAlpha))
      )

      Icon(
        imageVector = Icons.Default.AccessTime,
        contentDescription = null,
        tint = if (isDarkTheme) BoostlyNeonLime else Color(0xFF0F6E38),
        modifier = Modifier.size(13.dp)
      )

      Text(
        text = clockState.timeString24,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        color = if (isDarkTheme) BoostlyTextWhite else Color(0xFF111827)
      )

      Text(
        text = clockState.timeZoneCode,
        fontSize = 9.sp,
        fontWeight = FontWeight.SemiBold,
        color = if (isDarkTheme) BoostlyNeonLime else Color(0xFF0F6E38)
      )
    }
  }
}

/**
 * Hero Live Clock & Greeting Card with Analog Dial & Countdown Timer
 * Specifically crafted to make the application feel alive, dynamic, and aesthetic!
 */
@Composable
fun BoostlyLiveClockCard(
  userName: String = "Sobat Boostly",
  onResetTimerClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val clockState = rememberLiveClock()
  var is24HourFormat by remember { mutableStateOf(true) }

  val infiniteTransition = rememberInfiniteTransition(label = "neon_glow")
  val glowAlpha by infiniteTransition.animateFloat(
    initialValue = 0.25f,
    targetValue = 0.7f,
    animationSpec = infiniteRepeatable(
      animation = tween(1800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "glow_alpha"
  )

  Card(
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = BoostlyDarkCard),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      Brush.horizontalGradient(
        listOf(
          BoostlyForestGreen,
          BoostlyNeonLime.copy(alpha = glowAlpha),
          BoostlyForestGreen
        )
      )
    ),
    modifier = modifier
      .fillMaxWidth()
      .testTag("boostly_live_clock_card")
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.verticalGradient(
            listOf(
              Color(0xFF132B1D),
              BoostlyDarkCard
            )
          )
        )
        .padding(18.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Row 1: Greeting + Weather/Day Icon + Live Ticker Tag
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
                .size(32.dp)
                .clip(CircleShape)
                .background(BoostlyForestGreen),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = clockState.greetingIcon,
                contentDescription = null,
                tint = BoostlyGold,
                modifier = Modifier.size(18.dp)
              )
            }

            Column {
              Text(
                text = "${clockState.greeting},",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = BoostlyTextMuted
              )
              Text(
                text = userName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = BoostlyTextWhite
              )
            }
          }

          // Live Server Synced Chip
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF0A1F13),
            border = androidx.compose.foundation.BorderStroke(1.dp, BoostlyNeonLime.copy(alpha = 0.5f))
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(BoostlyNeonLime)
              )
              Text(
                text = "LIVE TIME",
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp,
                color = BoostlyNeonLime
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Row 2: Big Aesthetic Clock with Analog Mini Dial + Digital Time
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          // Left: Digital Clock Display with Seconds
          Column(modifier = Modifier.clickable { is24HourFormat = !is24HourFormat }) {
            Row(verticalAlignment = Alignment.Bottom) {
              Text(
                text = if (is24HourFormat) clockState.timeString24 else clockState.timeString12,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = BoostlyNeonLime,
                letterSpacing = 1.sp
              )
              if (!is24HourFormat) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = clockState.amPm,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = BoostlyGold,
                  modifier = Modifier.padding(bottom = 4.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Date Text in Indonesian
            Text(
              text = clockState.dateFormattedIndo,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              color = BoostlyTextMuted
            )
          }

          // Right: Analog Mini Clock Dial (Canvas)
          AnalogMiniClock(
            hour = clockState.hour,
            minute = clockState.minute,
            second = clockState.second,
            size = 54.dp
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Row 3: Daily Target Reset Countdown Banner
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = Color(0xFF0A1E14),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1B4028)),
          modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onResetTimerClick)
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
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.HourglassBottom,
                contentDescription = null,
                tint = BoostlyGold,
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = "Reset Target Harian dalam:",
                fontSize = 11.sp,
                color = BoostlyTextMuted,
                fontWeight = FontWeight.Medium
              )
            }

            Text(
              text = clockState.countdownFormatted,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = BoostlyGold
            )
          }
        }
      }
    }
  }
}

/**
 * Custom Canvas Analog Mini Clock Dial with ticking second hand
 */
@Composable
fun AnalogMiniClock(
  hour: Int,
  minute: Int,
  second: Int,
  size: Dp = 50.dp,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .size(size)
      .clip(CircleShape)
      .background(Color(0xFF0B1F13))
      .border(1.5.dp, BoostlyForestGreen, CircleShape),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(size)) {
      val center = Offset(this.size.width / 2f, this.size.height / 2f)
      val radius = this.size.width / 2f

      // Draw 12 Hour Tick Marks
      for (i in 0 until 12) {
        val angleRad = Math.toRadians((i * 30.0) - 90.0)
        val tickLength = if (i % 3 == 0) radius * 0.18f else radius * 0.10f
        val startX = center.x + (radius - tickLength) * cos(angleRad).toFloat()
        val startY = center.y + (radius - tickLength) * sin(angleRad).toFloat()
        val endX = center.x + (radius - 2.dp.toPx()) * cos(angleRad).toFloat()
        val endY = center.y + (radius - 2.dp.toPx()) * sin(angleRad).toFloat()

        drawLine(
          color = if (i % 3 == 0) BoostlyNeonLime else BoostlyForestGreen,
          start = Offset(startX, startY),
          end = Offset(endX, endY),
          strokeWidth = if (i % 3 == 0) 2.dp.toPx() else 1.dp.toPx(),
          cap = StrokeCap.Round
        )
      }

      // Hour Hand
      val hourAngleRad = Math.toRadians(((hour % 12 + minute / 60f) * 30.0) - 90.0)
      val hourHandLength = radius * 0.50f
      drawLine(
        color = Color.White,
        start = center,
        end = Offset(
          center.x + hourHandLength * cos(hourAngleRad).toFloat(),
          center.y + hourHandLength * sin(hourAngleRad).toFloat()
        ),
        strokeWidth = 2.5.dp.toPx(),
        cap = StrokeCap.Round
      )

      // Minute Hand
      val minuteAngleRad = Math.toRadians(((minute + second / 60f) * 6.0) - 90.0)
      val minuteHandLength = radius * 0.70f
      drawLine(
        color = BoostlyEmerald,
        start = center,
        end = Offset(
          center.x + minuteHandLength * cos(minuteAngleRad).toFloat(),
          center.y + minuteHandLength * sin(minuteAngleRad).toFloat()
        ),
        strokeWidth = 1.8.dp.toPx(),
        cap = StrokeCap.Round
      )

      // Second Hand (Neon Lime with Gold Accent)
      val secondAngleRad = Math.toRadians((second * 6.0) - 90.0)
      val secondHandLength = radius * 0.82f
      drawLine(
        color = BoostlyNeonLime,
        start = center,
        end = Offset(
          center.x + secondHandLength * cos(secondAngleRad).toFloat(),
          center.y + secondHandLength * sin(secondAngleRad).toFloat()
        ),
        strokeWidth = 1.2.dp.toPx(),
        cap = StrokeCap.Round
      )

      // Center Pin
      drawCircle(
        color = BoostlyGold,
        radius = 3.dp.toPx(),
        center = center
      )
    }
  }
}
