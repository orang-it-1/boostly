package com.example.data.notification

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class AppNotificationItem(
  val id: String,
  val title: String,
  val message: String,
  val channelId: String,
  val timestamp: Long = System.currentTimeMillis(),
  val iconType: String = "info"
)

object PushNotificationManager {
  const val CHANNEL_DAILY_REMINDER = "channel_daily_checkin"
  const val CHANNEL_NEW_FOLLOWERS = "channel_new_followers"
  const val CHANNEL_PAYMENT_GATEWAY = "channel_payment_gateway"

  private const val PREFS_NAME = "sosmed_push_prefs"
  private const val KEY_DAILY_REMINDER_ENABLED = "daily_reminder_enabled"
  private const val KEY_DAILY_REMINDER_HOUR = "daily_reminder_hour"
  private const val KEY_DAILY_REMINDER_MINUTE = "daily_reminder_minute"

  private val _recentNotifications = mutableListOf<AppNotificationItem>()
  val recentNotifications: List<AppNotificationItem> get() = _recentNotifications.toList()

  fun initChannels(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

      // 1. Channel Daily Reminder
      val dailyChannel = NotificationChannel(
        CHANNEL_DAILY_REMINDER,
        "Pengingat Check-in Harian",
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "Pengingat otomatis setiap hari untuk klaim bonus koin harian Anda"
        enableLights(true)
        enableVibration(true)
      }

      // 2. Channel New Followers
      val followerChannel = NotificationChannel(
        CHANNEL_NEW_FOLLOWERS,
        "Notifikasi Pengikut Baru",
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "Pemberitahuan real-time saat ada follower baru bergabung atau kampanye bertambah"
        enableLights(true)
        enableVibration(true)
      }

      // 3. Channel Payment Gateway
      val paymentChannel = NotificationChannel(
        CHANNEL_PAYMENT_GATEWAY,
        "Status Pembayaran QRIS",
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "Konfirmasi real-time transaksi Midtrans, Xendit & Tripay"
        enableLights(true)
        enableVibration(true)
      }

      notificationManager.createNotificationChannels(listOf(dailyChannel, followerChannel, paymentChannel))
    }
  }

  fun hasNotificationPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
      ) == PackageManager.PERMISSION_GRANTED
    } else {
      true
    }
  }

  fun showDailyCheckInNotification(context: Context, rewardCoins: Int = 25) {
    initChannels(context)

    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      putExtra("OPEN_SCREEN", "DAILY_CHECKIN")
    }
    val pendingIntent = PendingIntent.getActivity(
      context,
      1001,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val title = "🎁 Hadiah Check-In Harian Siap Diklaim!"
    val message = "Klaim +$rewardCoins koin gratis Anda hari ini untuk boost followers sosmed Anda. Jangan sampai streak putus!"

    val notification = NotificationCompat.Builder(context, CHANNEL_DAILY_REMINDER)
      .setSmallIcon(android.R.drawable.ic_dialog_info)
      .setContentTitle(title)
      .setContentText(message)
      .setStyle(NotificationCompat.BigTextStyle().bigText(message))
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setCategory(NotificationCompat.CATEGORY_REMINDER)
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)
      .build()

    logNotification(title, message, CHANNEL_DAILY_REMINDER, "gift")

    if (hasNotificationPermission(context)) {
      try {
        NotificationManagerCompat.from(context).notify(1001, notification)
      } catch (e: SecurityException) {
        // Handled safely
      }
    }
  }

  fun showNewFollowerNotification(
    context: Context,
    followerUsername: String,
    platform: String,
    campaignTitle: String
  ) {
    initChannels(context)

    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      putExtra("OPEN_SCREEN", "CAMPAIGNS")
    }
    val pendingIntent = PendingIntent.getActivity(
      context,
      1002,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val title = "🎉 Pengikut Baru di $platform!"
    val message = "@$followerUsername baru saja mengikuti akun Anda di kampanye \"$campaignTitle\". Target Anda semakin dekat!"

    val notification = NotificationCompat.Builder(context, CHANNEL_NEW_FOLLOWERS)
      .setSmallIcon(android.R.drawable.ic_menu_myplaces)
      .setContentTitle(title)
      .setContentText(message)
      .setStyle(NotificationCompat.BigTextStyle().bigText(message))
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setCategory(NotificationCompat.CATEGORY_SOCIAL)
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)
      .build()

    logNotification(title, message, CHANNEL_NEW_FOLLOWERS, "follower")

    if (hasNotificationPermission(context)) {
      try {
        val notifId = (System.currentTimeMillis() % 100000).toInt() + 2000
        NotificationManagerCompat.from(context).notify(notifId, notification)
      } catch (e: SecurityException) {
        // Handled safely
      }
    }
  }

  fun showPaymentSuccessNotification(
    context: Context,
    orderId: String,
    gateway: String,
    coinsAdded: Int,
    amountIdr: Long
  ) {
    initChannels(context)

    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      putExtra("OPEN_SCREEN", "WALLET")
    }
    val pendingIntent = PendingIntent.getActivity(
      context,
      1003,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val rupiahFormatted = "Rp " + String.format(Locale.US, "%,d", amountIdr).replace(',', '.')
    val title = "✅ Pembayaran QRIS $gateway Sukses!"
    val message = "Transaksi $orderId ($rupiahFormatted) telah berhasil diverifikasi. Saldo koin bertambah +$coinsAdded Koin!"

    val notification = NotificationCompat.Builder(context, CHANNEL_PAYMENT_GATEWAY)
      .setSmallIcon(android.R.drawable.stat_sys_upload_done)
      .setContentTitle(title)
      .setContentText(message)
      .setStyle(NotificationCompat.BigTextStyle().bigText(message))
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setCategory(NotificationCompat.CATEGORY_STATUS)
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)
      .build()

    logNotification(title, message, CHANNEL_PAYMENT_GATEWAY, "payment")

    if (hasNotificationPermission(context)) {
      try {
        NotificationManagerCompat.from(context).notify(1003, notification)
      } catch (e: SecurityException) {
        // Handled safely
      }
    }
  }

  private fun logNotification(title: String, message: String, channel: String, icon: String) {
    val item = AppNotificationItem(
      id = System.currentTimeMillis().toString(),
      title = title,
      message = message,
      channelId = channel,
      timestamp = System.currentTimeMillis(),
      iconType = icon
    )
    _recentNotifications.add(0, item)
    if (_recentNotifications.size > 20) {
      _recentNotifications.removeLast()
    }
  }

  fun scheduleDailyCheckInReminder(context: Context, hourOfDay: Int = 8, minute: Int = 0) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit()
      .putBoolean(KEY_DAILY_REMINDER_ENABLED, true)
      .putInt(KEY_DAILY_REMINDER_HOUR, hourOfDay)
      .putInt(KEY_DAILY_REMINDER_MINUTE, minute)
      .apply()

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
    val intent = Intent(context, DailyCheckInReminderReceiver::class.java).apply {
      action = "com.example.ACTION_DAILY_CHECKIN_REMINDER"
    }
    val pendingIntent = PendingIntent.getBroadcast(
      context,
      8888,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val calendar = Calendar.getInstance().apply {
      set(Calendar.HOUR_OF_DAY, hourOfDay)
      set(Calendar.MINUTE, minute)
      set(Calendar.SECOND, 0)
      if (before(Calendar.getInstance())) {
        add(Calendar.DAY_OF_YEAR, 1)
      }
    }

    try {
      alarmManager.setInexactRepeating(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        AlarmManager.INTERVAL_DAY,
        pendingIntent
      )
    } catch (e: Exception) {
      // Inexact fallback
    }
  }

  fun cancelDailyReminder(context: Context) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().putBoolean(KEY_DAILY_REMINDER_ENABLED, false).apply()

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
    val intent = Intent(context, DailyCheckInReminderReceiver::class.java).apply {
      action = "com.example.ACTION_DAILY_CHECKIN_REMINDER"
    }
    val pendingIntent = PendingIntent.getBroadcast(
      context,
      8888,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.cancel(pendingIntent)
  }

  fun isDailyReminderEnabled(context: Context): Boolean {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getBoolean(KEY_DAILY_REMINDER_ENABLED, true)
  }

  fun getScheduledTime(context: Context): Pair<Int, Int> {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val hour = prefs.getInt(KEY_DAILY_REMINDER_HOUR, 8)
    val minute = prefs.getInt(KEY_DAILY_REMINDER_MINUTE, 0)
    return hour to minute
  }
}
