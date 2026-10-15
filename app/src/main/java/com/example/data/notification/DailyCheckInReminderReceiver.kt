package com.example.data.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DailyCheckInReminderReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent?) {
    // Show high priority push notification for daily check-in
    PushNotificationManager.showDailyCheckInNotification(context, rewardCoins = 25)

    // Ensure next day's alarm is scheduled if enabled
    if (PushNotificationManager.isDailyReminderEnabled(context)) {
      val (hour, minute) = PushNotificationManager.getScheduledTime(context)
      PushNotificationManager.scheduleDailyCheckInReminder(context, hour, minute)
    }
  }
}
