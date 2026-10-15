package com.example

import com.example.data.model.DailyCheckInEntity
import com.example.data.sync.DailyCheckInSyncManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class DailyCheckInSyncManagerTest {

  private val testTz = TimeZone.getTimeZone("Asia/Jakarta")

  @Test
  fun testRewardTiers() {
    assertEquals(10, DailyCheckInSyncManager.getRewardForStreakDay(1))
    assertEquals(15, DailyCheckInSyncManager.getRewardForStreakDay(2))
    assertEquals(20, DailyCheckInSyncManager.getRewardForStreakDay(3))
    assertEquals(25, DailyCheckInSyncManager.getRewardForStreakDay(4))
    assertEquals(35, DailyCheckInSyncManager.getRewardForStreakDay(5))
    assertEquals(45, DailyCheckInSyncManager.getRewardForStreakDay(6))
    assertEquals(60, DailyCheckInSyncManager.getRewardForStreakDay(7))
    // Cycle after Day 7
    assertEquals(10, DailyCheckInSyncManager.getRewardForStreakDay(8))
  }

  @Test
  fun testFirstTimeCheckIn() {
    val cal = Calendar.getInstance(testTz)
    val evaluation = DailyCheckInSyncManager.evaluateDailyCheckInStatus(null, cal.timeInMillis, testTz)

    assertTrue(evaluation.canClaimToday)
    assertFalse(evaluation.alreadyClaimedToday)
    assertEquals(1, evaluation.currentStreakDay)
    assertEquals(10, evaluation.rewardCoinsForToday)
    assertFalse(evaluation.isClockSkewDetected)
    assertEquals(7, evaluation.sevenDays.size)
    assertTrue(evaluation.sevenDays[0].isTodayToClaim)
  }

  @Test
  fun testAlreadyClaimedToday() {
    val cal = Calendar.getInstance(testTz)
    val todayKey = DailyCheckInSyncManager.getCalendarDateKey(cal.timeInMillis, testTz)

    val existingCheckIn = DailyCheckInEntity(
      userId = 1,
      checkInDateKey = todayKey,
      streakDay = 2,
      rewardCoins = 15,
      timestamp = cal.timeInMillis - 3600000L, // 1 hour ago
      timezoneId = "WIB"
    )

    val evaluation = DailyCheckInSyncManager.evaluateDailyCheckInStatus(existingCheckIn, cal.timeInMillis, testTz)

    assertFalse(evaluation.canClaimToday)
    assertTrue(evaluation.alreadyClaimedToday)
    assertEquals(2, evaluation.currentStreakDay)
    // Next reward when returning tomorrow
    assertEquals(20, evaluation.rewardCoinsForToday)
    assertFalse(evaluation.isClockSkewDetected)
  }

  @Test
  fun testConsecutiveDayCheckIn() {
    val calToday = Calendar.getInstance(testTz)
    val calYesterday = Calendar.getInstance(testTz).apply {
      add(Calendar.DAY_OF_YEAR, -1)
    }
    val yesterdayKey = DailyCheckInSyncManager.getCalendarDateKey(calYesterday.timeInMillis, testTz)

    val existingCheckIn = DailyCheckInEntity(
      userId = 1,
      checkInDateKey = yesterdayKey,
      streakDay = 3,
      rewardCoins = 20,
      timestamp = calYesterday.timeInMillis,
      timezoneId = "WIB"
    )

    val evaluation = DailyCheckInSyncManager.evaluateDailyCheckInStatus(existingCheckIn, calToday.timeInMillis, testTz)

    assertTrue(evaluation.canClaimToday)
    assertFalse(evaluation.alreadyClaimedToday)
    assertEquals(4, evaluation.currentStreakDay)
    assertEquals(25, evaluation.rewardCoinsForToday)
    assertFalse(evaluation.isClockSkewDetected)
    assertTrue(evaluation.sevenDays[3].isTodayToClaim)
    assertTrue(evaluation.sevenDays[0].isClaimed)
    assertTrue(evaluation.sevenDays[1].isClaimed)
    assertTrue(evaluation.sevenDays[2].isClaimed)
  }

  @Test
  fun testBrokenStreakAfterMissingTwoDays() {
    val calToday = Calendar.getInstance(testTz)
    val calThreeDaysAgo = Calendar.getInstance(testTz).apply {
      add(Calendar.DAY_OF_YEAR, -3)
    }
    val oldKey = DailyCheckInSyncManager.getCalendarDateKey(calThreeDaysAgo.timeInMillis, testTz)

    val existingCheckIn = DailyCheckInEntity(
      userId = 1,
      checkInDateKey = oldKey,
      streakDay = 5,
      rewardCoins = 35,
      timestamp = calThreeDaysAgo.timeInMillis,
      timezoneId = "WIB"
    )

    val evaluation = DailyCheckInSyncManager.evaluateDailyCheckInStatus(existingCheckIn, calToday.timeInMillis, testTz)

    assertTrue(evaluation.canClaimToday)
    assertFalse(evaluation.alreadyClaimedToday)
    // Reset back to Day 1
    assertEquals(1, evaluation.currentStreakDay)
    assertEquals(10, evaluation.rewardCoinsForToday)
    assertFalse(evaluation.isClockSkewDetected)
  }

  @Test
  fun testClockTamperingRollbackDetection() {
    val calToday = Calendar.getInstance(testTz)
    val calFutureTampered = Calendar.getInstance(testTz).apply {
      add(Calendar.DAY_OF_YEAR, 2)
    }

    val existingCheckIn = DailyCheckInEntity(
      userId = 1,
      checkInDateKey = "2026-10-15",
      streakDay = 4,
      rewardCoins = 25,
      timestamp = calFutureTampered.timeInMillis,
      timezoneId = "WIB"
    )

    // Current time is BEFORE the last recorded check-in
    val evaluation = DailyCheckInSyncManager.evaluateDailyCheckInStatus(existingCheckIn, calToday.timeInMillis, testTz)

    assertFalse(evaluation.canClaimToday)
    assertTrue(evaluation.isClockSkewDetected)
  }
}
