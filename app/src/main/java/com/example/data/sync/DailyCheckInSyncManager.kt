package com.example.data.sync

import com.example.data.model.DailyCheckInEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class DailyStreakDayUiModel(
  val dayNumber: Int,
  val rewardCoins: Int,
  val isClaimed: Boolean,
  val isTodayToClaim: Boolean,
  val isUpcoming: Boolean,
  val isGrandBonus: Boolean
)

data class DailyCheckInEvaluation(
  val canClaimToday: Boolean,
  val alreadyClaimedToday: Boolean,
  val currentStreakDay: Int,
  val rewardCoinsForToday: Int,
  val todayDateKey: String,
  val timezoneDisplayName: String,
  val secondsUntilMidnightReset: Long,
  val countdownFormatted: String,
  val sevenDays: List<DailyStreakDayUiModel>,
  val isClockSkewDetected: Boolean = false,
  val clockSkewErrorMessage: String? = null,
  val lastCheckInDateKey: String? = null,
  val lastCheckInTimestamp: Long? = null
)

object DailyCheckInSyncManager {

  private val REWARD_TIERS = listOf(10, 15, 20, 25, 35, 45, 60)

  fun getRewardForStreakDay(dayNumber: Int): Int {
    val clampedIndex = ((dayNumber - 1).coerceAtLeast(0) % REWARD_TIERS.size)
    return REWARD_TIERS[clampedIndex]
  }

  fun getCalendarDateKey(timestamp: Long, tz: TimeZone = TimeZone.getDefault()): String {
    val cal = Calendar.getInstance(tz).apply {
      timeInMillis = timestamp
    }
    val y = cal.get(Calendar.YEAR)
    val m = cal.get(Calendar.MONTH) + 1
    val d = cal.get(Calendar.DAY_OF_MONTH)
    return String.format(Locale.US, "%04d-%02d-%02d", y, m, d)
  }

  fun formatHumanTimestamp(timestamp: Long, tz: TimeZone = TimeZone.getDefault()): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale("id", "ID")).apply {
      timeZone = tz
    }
    return sdf.format(Date(timestamp))
  }

  fun getTimeZoneCode(tz: TimeZone = TimeZone.getDefault()): String {
    val tzId = tz.id.lowercase()
    val rawOffsetHours = tz.rawOffset / (1000 * 60 * 60)
    return when {
      rawOffsetHours == 7 || tzId.contains("jakarta") || tzId.contains("bangkok") -> "WIB"
      rawOffsetHours == 8 || tzId.contains("makassar") || tzId.contains("singapore") || tzId.contains("bali") -> "WITA"
      rawOffsetHours == 9 || tzId.contains("jayapura") || tzId.contains("tokyo") -> "WIT"
      else -> tz.getDisplayName(tz.inDaylightTime(Date()), TimeZone.SHORT, Locale.US)
    }
  }

  fun calculateCalendarDayDifference(
    previousTimestamp: Long,
    currentTimestamp: Long,
    tz: TimeZone = TimeZone.getDefault()
  ): Int {
    val calPrev = Calendar.getInstance(tz).apply {
      timeInMillis = previousTimestamp
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }
    val calCurr = Calendar.getInstance(tz).apply {
      timeInMillis = currentTimestamp
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }
    val diffMs = calCurr.timeInMillis - calPrev.timeInMillis
    return Math.round(diffMs.toDouble() / (24.0 * 60.0 * 60.0 * 1000.0)).toInt()
  }

  fun calculateSecondsUntilMidnight(
    currentTimestamp: Long,
    tz: TimeZone = TimeZone.getDefault()
  ): Long {
    val midnightCal = Calendar.getInstance(tz).apply {
      timeInMillis = currentTimestamp
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
      add(Calendar.DAY_OF_YEAR, 1)
    }
    val diffSec = (midnightCal.timeInMillis - currentTimestamp) / 1000L
    return diffSec.coerceAtLeast(0L)
  }

  fun formatCountdown(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return String.format(Locale.US, "%02d:%02d:%02d", h, m, s)
  }

  /**
   * Robust evaluation of user daily check-in state.
   * Handles:
   * 1. First-time check-in (no history)
   * 2. Already claimed today (dayDiff == 0)
   * 3. Consecutive claim (dayDiff == 1)
   * 4. Broken streak (dayDiff > 1)
   * 5. Clock tampering / rollback detection (dayDiff < 0 or current < prev)
   */
  fun evaluateDailyCheckInStatus(
    latestCheckIn: DailyCheckInEntity?,
    currentTimestamp: Long,
    tz: TimeZone = TimeZone.getDefault()
  ): DailyCheckInEvaluation {
    val todayKey = getCalendarDateKey(currentTimestamp, tz)
    val tzCode = getTimeZoneCode(tz)
    val secondsUntilMidnight = calculateSecondsUntilMidnight(currentTimestamp, tz)
    val countdown = formatCountdown(secondsUntilMidnight)

    // Case A: No check-in history yet
    if (latestCheckIn == null) {
      val streakDay = 1
      val reward = getRewardForStreakDay(streakDay)
      val sevenDays = (1..7).map { day ->
        DailyStreakDayUiModel(
          dayNumber = day,
          rewardCoins = getRewardForStreakDay(day),
          isClaimed = false,
          isTodayToClaim = day == 1,
          isUpcoming = day > 1,
          isGrandBonus = day == 7
        )
      }
      return DailyCheckInEvaluation(
        canClaimToday = true,
        alreadyClaimedToday = false,
        currentStreakDay = 1,
        rewardCoinsForToday = reward,
        todayDateKey = todayKey,
        timezoneDisplayName = tzCode,
        secondsUntilMidnightReset = secondsUntilMidnight,
        countdownFormatted = countdown,
        sevenDays = sevenDays
      )
    }

    val dayDiff = calculateCalendarDayDifference(latestCheckIn.timestamp, currentTimestamp, tz)

    // Case B: Clock skew / rollback detected
    if (dayDiff < 0 || currentTimestamp < latestCheckIn.timestamp) {
      val sevenDays = (1..7).map { day ->
        DailyStreakDayUiModel(
          dayNumber = day,
          rewardCoins = getRewardForStreakDay(day),
          isClaimed = day <= latestCheckIn.streakDay,
          isTodayToClaim = false,
          isUpcoming = day > latestCheckIn.streakDay,
          isGrandBonus = day == 7
        )
      }
      return DailyCheckInEvaluation(
        canClaimToday = false,
        alreadyClaimedToday = true,
        currentStreakDay = latestCheckIn.streakDay,
        rewardCoinsForToday = 0,
        todayDateKey = todayKey,
        timezoneDisplayName = tzCode,
        secondsUntilMidnightReset = secondsUntilMidnight,
        countdownFormatted = countdown,
        sevenDays = sevenDays,
        isClockSkewDetected = true,
        clockSkewErrorMessage = "Waktu perangkat tidak sinkron. Harap gunakan waktu jaringan otomatis.",
        lastCheckInDateKey = latestCheckIn.checkInDateKey,
        lastCheckInTimestamp = latestCheckIn.timestamp
      )
    }

    // Case C: Already claimed today
    if (dayDiff == 0 || latestCheckIn.checkInDateKey == todayKey) {
      val completedStreak = latestCheckIn.streakDay
      val nextDay = if (completedStreak >= 7) 1 else completedStreak + 1
      val nextReward = getRewardForStreakDay(nextDay)

      val sevenDays = (1..7).map { day ->
        DailyStreakDayUiModel(
          dayNumber = day,
          rewardCoins = getRewardForStreakDay(day),
          isClaimed = day <= completedStreak,
          isTodayToClaim = false,
          isUpcoming = day > completedStreak,
          isGrandBonus = day == 7
        )
      }

      return DailyCheckInEvaluation(
        canClaimToday = false,
        alreadyClaimedToday = true,
        currentStreakDay = completedStreak,
        rewardCoinsForToday = nextReward, // next reward when claimed tomorrow
        todayDateKey = todayKey,
        timezoneDisplayName = tzCode,
        secondsUntilMidnightReset = secondsUntilMidnight,
        countdownFormatted = countdown,
        sevenDays = sevenDays,
        lastCheckInDateKey = latestCheckIn.checkInDateKey,
        lastCheckInTimestamp = latestCheckIn.timestamp
      )
    }

    // Case D: Consecutive day (Yesterday was last check-in) -> streak continues!
    val streakToClaim = if (dayDiff == 1) {
      if (latestCheckIn.streakDay >= 7) 1 else latestCheckIn.streakDay + 1
    } else {
      // Case E: Broken streak (missed >= 2 days) -> reset to Day 1
      1
    }

    val rewardToday = getRewardForStreakDay(streakToClaim)

    val sevenDays = (1..7).map { day ->
      DailyStreakDayUiModel(
        dayNumber = day,
        rewardCoins = getRewardForStreakDay(day),
        isClaimed = day < streakToClaim,
        isTodayToClaim = day == streakToClaim,
        isUpcoming = day > streakToClaim,
        isGrandBonus = day == 7
      )
    }

    return DailyCheckInEvaluation(
      canClaimToday = true,
      alreadyClaimedToday = false,
      currentStreakDay = streakToClaim,
      rewardCoinsForToday = rewardToday,
      todayDateKey = todayKey,
      timezoneDisplayName = tzCode,
      secondsUntilMidnightReset = secondsUntilMidnight,
      countdownFormatted = countdown,
      sevenDays = sevenDays,
      lastCheckInDateKey = latestCheckIn.checkInDateKey,
      lastCheckInTimestamp = latestCheckIn.timestamp
    )
  }
}
