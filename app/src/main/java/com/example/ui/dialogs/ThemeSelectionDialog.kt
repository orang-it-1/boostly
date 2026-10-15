package com.example.ui.dialogs

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
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.BoostlyDarkGreenBtn
import com.example.ui.theme.BoostlyNeonLime
import com.example.ui.theme.isThemeDark

@Composable
fun ThemeSelectionDialog(
  currentMode: AppThemeMode,
  onSelectMode: (AppThemeMode) -> Unit,
  onDismiss: () -> Unit
) {
  val isDark = isThemeDark()

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp)
        .testTag("theme_selection_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(22.dp)
      ) {
        // Header
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
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isDark) BoostlyNeonLime.copy(alpha = 0.2f) else BoostlyDarkGreenBtn.copy(alpha = 0.1f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = null,
                tint = if (isDark) BoostlyNeonLime else BoostlyDarkGreenBtn,
                modifier = Modifier.size(22.dp)
              )
            }

            Column {
              Text(
                text = "Tema & Tampilan",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Pilih mode visual untuk kenyamanan mata",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Tutup",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Options List
        val options = listOf(
          Triple(
            AppThemeMode.LIGHT,
            "Mode Terang (Light)",
            Icons.Default.LightMode
          ),
          Triple(
            AppThemeMode.DARK,
            "Mode Gelap (Dark)",
            Icons.Default.DarkMode
          ),
          Triple(
            AppThemeMode.SYSTEM,
            "Ikuti Sistem HP",
            Icons.Default.BrightnessAuto
          )
        )

        options.forEach { (mode, label, icon) ->
          val isSelected = currentMode == mode

          Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isSelected) {
              if (isDark) Color(0xFF1B3D2A) else Color(0xFFEAF7EE)
            } else {
              MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            },
            border = androidx.compose.foundation.BorderStroke(
              width = if (isSelected) 1.5.dp else 1.dp,
              color = if (isSelected) {
                if (isDark) BoostlyNeonLime else BoostlyDarkGreenBtn
              } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
              }
            ),
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 5.dp)
              .clickable { onSelectMode(mode) }
              .testTag("theme_option_${mode.name.lowercase()}")
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                      if (isSelected) {
                        if (isDark) BoostlyNeonLime else BoostlyDarkGreenBtn
                      } else {
                        MaterialTheme.colorScheme.surfaceVariant
                      }
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) {
                      if (isDark) Color.Black else Color.White
                    } else {
                      MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(18.dp)
                  )
                }

                Column {
                  Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = mode.subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              RadioButton(
                selected = isSelected,
                onClick = { onSelectMode(mode) },
                colors = RadioButtonDefaults.colors(
                  selectedColor = if (isDark) BoostlyNeonLime else BoostlyDarkGreenBtn
                )
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Accessibility Note Card
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(text = "💡", fontSize = 16.sp)
            Text(
              text = "Mode Gelap meningkatkan keterbacaan teks dan kontras tombol di tempat redup untuk kenyamanan penglihatan.",
              fontSize = 11.sp,
              lineHeight = 15.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = onDismiss,
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isDark) BoostlyNeonLime else BoostlyDarkGreenBtn,
            contentColor = if (isDark) Color.Black else Color.White
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("theme_dialog_close_btn")
        ) {
          Text("Selesai", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
      }
    }
  }
}
