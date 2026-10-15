package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.notification.PushNotificationManager
import com.example.ui.MainTab
import com.example.ui.SosmedMainScreen
import com.example.ui.SosmedViewModel
import com.example.ui.theme.SosmedGrowTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Initialize Push Notification Channels
    PushNotificationManager.initChannels(this)

    // Schedule Daily Check-In Reminder if enabled
    if (PushNotificationManager.isDailyReminderEnabled(this)) {
      val (hour, minute) = PushNotificationManager.getScheduledTime(this)
      PushNotificationManager.scheduleDailyCheckInReminder(this, hour, minute)
    }

    val openScreen = intent?.getStringExtra("OPEN_SCREEN")

    setContent {
      val viewModel: SosmedViewModel = viewModel()
      val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

      SosmedGrowTheme(themeMode = themeMode) {

        // Handle Deep Navigation from Push Notifications
        if (openScreen != null) {
          when (openScreen) {
            "DAILY_CHECKIN" -> {
              viewModel.setTab(MainTab.HOME)
              viewModel.openDailyCheckInDialog()
            }
            "CAMPAIGNS" -> {
              viewModel.setTab(MainTab.TASKS)
            }
            "WALLET" -> {
              viewModel.setTab(MainTab.WALLET)
            }
          }
        }

        SosmedMainScreen(viewModel = viewModel)
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
  }
}
