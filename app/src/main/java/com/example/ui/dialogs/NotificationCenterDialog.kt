package com.example.ui.dialogs

import android.Manifest
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.notification.PushNotificationManager
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldAuthentic
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.GoldCoinDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationCenterDialog(
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  var hasPermission by remember { mutableStateOf(PushNotificationManager.hasNotificationPermission(context)) }
  var isDailyEnabled by remember { mutableStateOf(PushNotificationManager.isDailyReminderEnabled(context)) }
  val scheduledTime = remember { PushNotificationManager.getScheduledTime(context) }
  var selectedHour by remember { mutableStateOf(scheduledTime.first) }
  var recentLogs by remember { mutableStateOf(PushNotificationManager.recentNotifications) }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    hasPermission = isGranted
    if (isGranted) {
      Toast.makeText(context, "Izin notifikasi aktif! Pengingat harian akan dikirim.", Toast.LENGTH_SHORT).show()
      PushNotificationManager.scheduleDailyCheckInReminder(context, selectedHour, 0)
    } else {
      Toast.makeText(context, "Izin notifikasi ditolak.", Toast.LENGTH_SHORT).show()
    }
  }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .testTag("notification_center_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ElectricIndigo.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = null,
                tint = ElectricIndigo,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Pusat Notifikasi Push",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Pengingat Harian & Follower Masuk",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
          IconButton(onClick = onDismiss) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Android 13+ Permission Card
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = if (hasPermission) EmeraldAuthentic.copy(alpha = 0.1f) else Color(0xFFFFF3CD),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (hasPermission) EmeraldAuthentic.copy(alpha = 0.4f) else Color(0xFFFFC107)
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Icon(
                imageVector = if (hasPermission) Icons.Default.CheckCircle else Icons.Default.NotificationsOff,
                contentDescription = null,
                tint = if (hasPermission) EmeraldAuthentic else Color(0xFFD97706),
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = if (hasPermission) "Status Izin: AKTIF" else "Izin Notifikasi Belum Aktif",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (hasPermission) EmeraldAuthentic else Color(0xFFB45309)
                )
                Text(
                  text = if (hasPermission) "Notifikasi bilah status Android siap ditampilkan" else "Ketuk Aktifkan agar pengingat & follower baru muncul",
                  fontSize = 10.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
            if (!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
              Button(
                onClick = {
                  permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo),
                shape = RoundedCornerShape(10.dp)
              ) {
                Text("Aktifkan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Daily Check-In Reminder Setting
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Alarm,
                  contentDescription = null,
                  tint = GoldCoinDark,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = "Pengingat Check-in Harian",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "Alarm harian untuk klaim bonus streak koin",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
              Switch(
                checked = isDailyEnabled,
                onCheckedChange = { enabled ->
                  isDailyEnabled = enabled
                  if (enabled) {
                    PushNotificationManager.scheduleDailyCheckInReminder(context, selectedHour, 0)
                    Toast.makeText(context, "Pengingat diset setiap pukul %02d:00 WIB".format(selectedHour), Toast.LENGTH_SHORT).show()
                  } else {
                    PushNotificationManager.cancelDailyReminder(context)
                    Toast.makeText(context, "Pengingat harian dimatikan", Toast.LENGTH_SHORT).show()
                  }
                },
                colors = SwitchDefaults.colors(checkedThumbColor = ElectricIndigo)
              )
            }

            AnimatedVisibility(visible = isDailyEnabled) {
              Column(modifier = Modifier.padding(top = 10.dp)) {
                Text(
                  text = "Pilih Waktu Pengingat:",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  listOf(7 to "07:00 Pagi", 8 to "08:00 Pagi", 12 to "12:00 Siang", 20 to "20:00 Malam").forEach { (hr, lbl) ->
                    val isSel = selectedHour == hr
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = if (isSel) ElectricIndigo else MaterialTheme.colorScheme.surface,
                      border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSel) ElectricIndigo else MaterialTheme.colorScheme.outlineVariant
                      ),
                      modifier = Modifier
                        .weight(1f)
                        .clickable {
                          selectedHour = hr
                          PushNotificationManager.scheduleDailyCheckInReminder(context, hr, 0)
                          Toast.makeText(context, "Alarm diset pukul $lbl", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                      Text(
                        text = lbl,
                        fontSize = 9.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                      )
                    }
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Direct Push Notification Testing Action Buttons
        Text(
          text = "Uji Coba Push Notification Nyata",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Ketuk untuk langsung memicu notifikasi di bilah status Android Anda:",
          fontSize = 10.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Test Buttons Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = {
              PushNotificationManager.showDailyCheckInNotification(context, 25)
              recentLogs = PushNotificationManager.recentNotifications
              Toast.makeText(context, "Notifikasi pengingat harian dikirim!", Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
          ) {
            Icon(imageVector = Icons.Default.CardGiftcard, contentDescription = null, tint = GoldCoinDark, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Tes Check-in", fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = {
              val sampleFollowers = listOf("andini_putri", "budi_santoso", "rizky_creator", "clarissa_style")
              val samplePlatforms = listOf("Instagram", "TikTok", "YouTube")
              PushNotificationManager.showNewFollowerNotification(
                context = context,
                followerUsername = sampleFollowers.random(),
                platform = samplePlatforms.random(),
                campaignTitle = "Boost Pengikut Nyata"
              )
              recentLogs = PushNotificationManager.recentNotifications
              Toast.makeText(context, "Notifikasi follower baru dikirim!", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
          ) {
            Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Tes Follower", fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
          onClick = {
            PushNotificationManager.showPaymentSuccessNotification(
              context = context,
              orderId = "SG-MID-${(100000..999999).random()}",
              gateway = "Midtrans",
              coinsAdded = 150,
              amountIdr = 25000
            )
            recentLogs = PushNotificationManager.recentNotifications
            Toast.makeText(context, "Notifikasi pembayaran QRIS dikirim!", Toast.LENGTH_SHORT).show()
          },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(imageVector = Icons.Default.QrCode, contentDescription = null, tint = EmeraldAuthentic, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Tes Notifikasi QRIS Sukses (+150 Koin)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notification History Log
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Riwayat Notifikasi Terbaru",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "${recentLogs.size} Pesan",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (recentLogs.isEmpty()) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "Belum ada riwayat notifikasi. Ketuk tombol tes di atas untuk mencoba pengiriman push notifikasi.",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(12.dp),
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        } else {
          val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            recentLogs.take(5).forEach { item ->
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(28.dp)
                      .clip(CircleShape)
                      .background(
                        when (item.iconType) {
                          "gift" -> GoldCoin.copy(alpha = 0.2f)
                          "follower" -> ElectricIndigo.copy(alpha = 0.2f)
                          "payment" -> EmeraldAuthentic.copy(alpha = 0.2f)
                          else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        }
                      ),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = when (item.iconType) {
                        "gift" -> Icons.Default.CardGiftcard
                        "follower" -> Icons.Default.PersonAdd
                        "payment" -> Icons.Default.CheckCircle
                        else -> Icons.Default.Notifications
                      },
                      contentDescription = null,
                      tint = when (item.iconType) {
                        "gift" -> GoldCoinDark
                        "follower" -> ElectricIndigo
                        "payment" -> EmeraldAuthentic
                        else -> MaterialTheme.colorScheme.primary
                      },
                      modifier = Modifier.size(16.dp)
                    )
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column(modifier = Modifier.weight(1f)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text(
                        text = item.title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                      )
                      Text(
                        text = timeFormat.format(Date(item.timestamp)),
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                    Text(
                      text = item.message,
                      fontSize = 10.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                      maxLines = 2
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
