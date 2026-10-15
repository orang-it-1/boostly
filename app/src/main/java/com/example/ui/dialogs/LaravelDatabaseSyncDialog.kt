package com.example.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.remote.laravel.LaravelConnectionStatus
import com.example.data.remote.laravel.LaravelSyncManager
import com.example.data.remote.laravel.LaravelSyncStatus
import com.example.ui.theme.BoostlyAppBg
import com.example.ui.theme.BoostlyDarkGreenBtn
import com.example.ui.theme.BoostlyDarkGreenCard
import com.example.ui.theme.BoostlyForestGreen
import com.example.ui.theme.BoostlyMintCard
import com.example.ui.theme.BoostlyMintText
import com.example.ui.theme.BoostlyNeonLime
import com.example.ui.theme.BoostlyTextDark
import com.example.ui.theme.BoostlyTextGray
import com.example.ui.theme.BoostlyTextLightGray
import com.example.ui.theme.BoostlyTextMuted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaravelDatabaseSyncDialog(
  connectionStatus: LaravelConnectionStatus,
  syncStatus: LaravelSyncStatus,
  savedBaseUrl: String,
  savedApiToken: String,
  syncManager: LaravelSyncManager,
  onDismiss: () -> Unit,
  onTestConnection: (url: String, token: String) -> Unit,
  onSyncDatabase: () -> Unit,
  onSaveConfig: (url: String, token: String) -> Unit,
  onResetSyncStatus: () -> Unit
) {
  val context = LocalContext.current

  var inputBaseUrl by remember { mutableStateOf(savedBaseUrl) }
  var inputApiToken by remember { mutableStateOf(savedApiToken) }
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Sinkronisasi, 1: Panduan Laravel
  var guideSubTab by remember { mutableIntStateOf(0) } // 0: routes/api.php, 1: Controller, 2: Migrasi

  val infiniteTransition = rememberInfiniteTransition(label = "sync_spin")
  val spinAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "spin_angle"
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(26.dp),
      color = Color.White,
      shadowElevation = 10.dp,
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .padding(vertical = 24.dp)
        .testTag("laravel_sync_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // 1. Header with Close Icon
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0xFFFF2D20).copy(alpha = 0.12f)), // Laravel red accent
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CloudSync,
                contentDescription = null,
                tint = Color(0xFFFF2D20),
                modifier = Modifier.size(24.dp)
              )
            }
            Column {
              Text(
                text = "Koneksi Website Laravel",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = BoostlyTextDark
              )
              Text(
                text = "Sinkronisasi Online Database REST API",
                fontSize = 11.sp,
                color = BoostlyTextMuted
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(36.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Tutup",
              tint = BoostlyTextGray
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Tab Navigation: "Sinkronisasi & Status" vs "Panduan Backend Laravel"
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = Color(0xFFF9FAFB),
          contentColor = BoostlyDarkGreenBtn,
          indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
              Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
              color = BoostlyDarkGreenBtn,
              height = 3.dp
            )
          },
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
        ) {
          Tab(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            text = {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Storage,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  "Koneksi & Sinkron",
                  fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                  fontSize = 12.sp
                )
              }
            }
          )
          Tab(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            text = {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Code,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  "Kode Laravel",
                  fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                  fontSize = 12.sp
                )
              }
            }
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Scrollable Body
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 500.dp)
            .verticalScroll(rememberScrollState())
        ) {
          if (selectedTab == 0) {
            // ================= TAB 0: KONEKSI & SINKRONISASI =================
            // Status Card
            when (connectionStatus) {
              is LaravelConnectionStatus.Connected -> {
                Card(
                  shape = RoundedCornerShape(16.dp),
                  colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                  border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81C784)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                  ) {
                    Box(
                      modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2E7D32)),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                      )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                      ) {
                        Text(
                          text = "Terhubung Online ke Laravel API",
                          fontSize = 13.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color(0xFF1B5E20)
                        )
                        Box(
                          modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF2E7D32))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                          Text(
                            text = "${connectionStatus.latencyMs} ms",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                          )
                        }
                      }
                      Text(
                        text = "Framework: ${connectionStatus.health.framework} • DB: ${connectionStatus.health.databaseDriver.uppercase()} (${connectionStatus.health.databaseStatus})",
                        fontSize = 11.sp,
                        color = Color(0xFF2E7D32)
                      )
                    }
                  }
                }
              }

              is LaravelConnectionStatus.Connecting -> {
                Card(
                  shape = RoundedCornerShape(16.dp),
                  colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                  border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                  ) {
                    CircularProgressIndicator(
                      modifier = Modifier.size(24.dp),
                      color = Color(0xFFF57F17),
                      strokeWidth = 2.dp
                    )
                    Column {
                      Text(
                        text = "Menguji Koneksi ke Laravel...",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF57F17)
                      )
                      Text(
                        text = "Memeriksa endpoint API dan status database...",
                        fontSize = 11.sp,
                        color = Color(0xFF8D6E63)
                      )
                    }
                  }
                }
              }

              is LaravelConnectionStatus.Failed -> {
                Card(
                  shape = RoundedCornerShape(16.dp),
                  colors = CardDefaults.cardColors(containerColor = Color(0xFFFDE8E8)),
                  border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF87171)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(14.dp)
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = null,
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(20.dp)
                      )
                      Text(
                        text = "Belum Terhubung ke Server Laravel",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFDC2626)
                      )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = connectionStatus.errorMessage,
                      fontSize = 11.sp,
                      color = Color(0xFFB91C1C)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = "💡 ${connectionStatus.helpHint}",
                      fontSize = 10.sp,
                      color = Color(0xFF7F1D1D)
                    )
                  }
                }
              }

              is LaravelConnectionStatus.Disconnected -> {
                Card(
                  shape = RoundedCornerShape(16.dp),
                  colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.CloudQueue,
                      contentDescription = null,
                      tint = BoostlyTextMuted,
                      modifier = Modifier.size(22.dp)
                    )
                    Column {
                      Text(
                        text = "Mode Offline Siap Sinkron",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BoostlyTextDark
                      )
                      Text(
                        text = "Database lokal Room menyimpan data pengguna, koin, kampanye, dan check-in.",
                        fontSize = 11.sp,
                        color = BoostlyTextMuted
                      )
                    }
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // URL input field
            Text(
              text = "Base URL REST API Laravel:",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = BoostlyTextDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
              value = inputBaseUrl,
              onValueChange = { inputBaseUrl = it },
              placeholder = { Text("Contoh: http://10.0.2.2:8000/api/") },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Outlined.Language,
                  contentDescription = null,
                  tint = BoostlyDarkGreenBtn,
                  modifier = Modifier.size(18.dp)
                )
              },
              trailingIcon = {
                if (inputBaseUrl.isNotEmpty()) {
                  IconButton(onClick = { inputBaseUrl = "" }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                  }
                }
              },
              singleLine = true,
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BoostlyDarkGreenBtn,
                unfocusedBorderColor = Color(0xFFE5E7EB)
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("laravel_base_url_input")
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Preset URL Chips for convenience
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              FilterChip(
                selected = inputBaseUrl.contains("10.0.2.2"),
                onClick = { inputBaseUrl = "http://10.0.2.2:8000/api/" },
                label = { Text("Emulator (10.0.2.2:8000)", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = BoostlyDarkGreenBtn.copy(alpha = 0.15f),
                  selectedLabelColor = BoostlyDarkGreenBtn
                )
              )
              FilterChip(
                selected = inputBaseUrl.contains("192.168"),
                onClick = { inputBaseUrl = "http://192.168.1.100:8000/api/" },
                label = { Text("WiFi Lokal (IP LAN)", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = BoostlyDarkGreenBtn.copy(alpha = 0.15f),
                  selectedLabelColor = BoostlyDarkGreenBtn
                )
              )
              FilterChip(
                selected = inputBaseUrl.startsWith("https://"),
                onClick = { inputBaseUrl = "https://boostly.my.id/api/" },
                label = { Text("Production (HTTPS)", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = BoostlyDarkGreenBtn.copy(alpha = 0.15f),
                  selectedLabelColor = BoostlyDarkGreenBtn
                )
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bearer Token input field
            Text(
              text = "Laravel Sanctum Token / API Key (Opsional):",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = BoostlyTextDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
              value = inputApiToken,
              onValueChange = { inputApiToken = it },
              placeholder = { Text("Masukkan Bearer Token jika API dilindungi") },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Outlined.VpnKey,
                  contentDescription = null,
                  tint = BoostlyDarkGreenBtn,
                  modifier = Modifier.size(18.dp)
                )
              },
              singleLine = true,
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BoostlyDarkGreenBtn,
                unfocusedBorderColor = Color(0xFFE5E7EB)
              ),
              modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: Tes Koneksi & Sinkronkan Sekarang
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedButton(
                onClick = {
                  onSaveConfig(inputBaseUrl, inputApiToken)
                  onTestConnection(inputBaseUrl, inputApiToken)
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("test_laravel_connection_btn")
              ) {
                Icon(
                  imageVector = Icons.Default.Refresh,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tes Koneksi", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              }

              Button(
                onClick = {
                  onSaveConfig(inputBaseUrl, inputApiToken)
                  onSyncDatabase()
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BoostlyDarkGreenBtn),
                modifier = Modifier
                  .weight(1.3f)
                  .testTag("sync_laravel_database_btn")
              ) {
                if (syncStatus is LaravelSyncStatus.Syncing) {
                  Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = null,
                    modifier = Modifier
                      .size(16.dp)
                      .rotate(spinAngle)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Menyinkronkan...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                } else {
                  Icon(
                    imageVector = Icons.Default.CloudSync,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Sinkronkan Database", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
              }
            }

            // Sync Status Result Banner
            AnimatedVisibility(visible = syncStatus is LaravelSyncStatus.Success) {
              val success = syncStatus as? LaravelSyncStatus.Success
              if (success != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                  shape = RoundedCornerShape(16.dp),
                  colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                  border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(14.dp)
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF16A34A),
                        modifier = Modifier.size(20.dp)
                      )
                      Text(
                        text = if (success.isSimulated) "Database Lokal Siap Disinkronkan!" else "Database Berhasil Disinkronkan!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF15803D)
                      )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Sync stats grid
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      SyncStatPill("Users", "${success.syncedUsers}")
                      SyncStatPill("Kampanye", "${success.syncedCampaigns}")
                      SyncStatPill("Transaksi", "${success.syncedTransactions}")
                      SyncStatPill("Check-In", "${success.syncedCheckIns}")
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    val sdf = SimpleDateFormat("HH:mm:ss 'WIB'", Locale("id", "ID"))
                    Text(
                      text = "Terakhir sinkron: ${sdf.format(Date(success.timestamp))}",
                      fontSize = 10.sp,
                      color = Color(0xFF15803D)
                    )
                  }
                }
              }
            }

            AnimatedVisibility(visible = syncStatus is LaravelSyncStatus.Error) {
              val err = syncStatus as? LaravelSyncStatus.Error
              if (err != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                  shape = RoundedCornerShape(16.dp),
                  colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                  border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Error,
                      contentDescription = null,
                      tint = Color(0xFFDC2626),
                      modifier = Modifier.size(18.dp)
                    )
                    Text(
                      text = err.message,
                      fontSize = 11.sp,
                      color = Color(0xFFB91C1C),
                      modifier = Modifier.weight(1f)
                    )
                  }
                }
              }
            }
          } else {
            // ================= TAB 1: PANDUAN INTEGRASI LARAVEL =================
            Column(modifier = Modifier.fillMaxWidth()) {
              Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Text(
                    text = "💡 Cara Menghubungkan ke Website Laravel Anda:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = BoostlyTextDark
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "1. Salin rute API ke 'routes/api.php' di proyek Laravel Anda.\n" +
                      "2. Buat controller 'BoostlyApiController' untuk menangani request dari Android.\n" +
                      "3. Jalankan 'php artisan serve --host=0.0.0.0 --port=8000' pada komputer Anda.\n" +
                      "4. Masukkan URL backend pada tab 'Koneksi & Sinkron'.",
                    fontSize = 11.sp,
                    color = BoostlyTextDark.copy(alpha = 0.85f),
                    lineHeight = 16.sp
                  )
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              // Sub-tabs for code:
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                FilterChip(
                  selected = guideSubTab == 0,
                  onClick = { guideSubTab = 0 },
                  label = { Text("routes/api.php", fontSize = 11.sp) }
                )
                FilterChip(
                  selected = guideSubTab == 1,
                  onClick = { guideSubTab = 1 },
                  label = { Text("BoostlyApiController.php", fontSize = 11.sp) }
                )
                FilterChip(
                  selected = guideSubTab == 2,
                  onClick = { guideSubTab = 2 },
                  label = { Text("Schema Database MySQL", fontSize = 11.sp) }
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              val codeToDisplay = when (guideSubTab) {
                0 -> syncManager.getLaravelRoutesCode()
                1 -> syncManager.getLaravelControllerCode()
                else -> """
// Migrasi Laravel (database/migrations/create_boostly_tables.php)
Schema::create('boostly_users', function (Blueprint ${'$'}table) {
    ${'$'}table->id();
    ${'$'}table->string('name');
    ${'$'}table->string('email')->unique();
    ${'$'}table->integer('points')->default(100);
    ${'$'}table->float('authenticity_score')->default(100.0);
    ${'$'}table->string('instagram_handle')->nullable();
    ${'$'}table->timestamps();
});

Schema::create('boostly_campaigns', function (Blueprint ${'$'}table) {
    ${'$'}table->id();
    ${'$'}table->foreignId('creator_id');
    ${'$'}table->string('account_handle');
    ${'$'}table->string('platform');
    ${'$'}table->integer('target_followers');
    ${'$'}table->integer('current_followers')->default(0);
    ${'$'}table->integer('coin_reward_per_follow')->default(10);
    ${'$'}table->boolean('is_active')->default(true);
    ${'$'}table->timestamps();
});
""".trimIndent()
              }

              // Code Box with Copy Button
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(Color(0xFF1E293B))
                  .padding(12.dp)
              ) {
                Column {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = when (guideSubTab) {
                        0 -> "PHP / routes/api.php"
                        1 -> "PHP / Controller"
                        else -> "Laravel Migration"
                      },
                      fontSize = 11.sp,
                      fontFamily = FontFamily.Monospace,
                      color = Color(0xFF94A3B8)
                    )
                    IconButton(
                      onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Laravel Code", codeToDisplay)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Kode disalin ke clipboard!", Toast.LENGTH_SHORT).show()
                      },
                      modifier = Modifier.size(28.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Salin Kode",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                      )
                    }
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = codeToDisplay,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 15.sp
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Bottom Dismiss Button
        Button(
          onClick = onDismiss,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F4F6)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Tutup", color = BoostlyTextDark, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
      }
    }
  }
}

@Composable
private fun SyncStatPill(label: String, count: String) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clip(RoundedCornerShape(10.dp))
      .background(Color(0xFFDCFCE7))
      .padding(horizontal = 12.dp, vertical = 6.dp)
  ) {
    Text(
      text = count,
      fontWeight = FontWeight.ExtraBold,
      fontSize = 14.sp,
      color = Color(0xFF15803D)
    )
    Text(
      text = label,
      fontSize = 10.sp,
      color = Color(0xFF166534)
    )
  }
}
