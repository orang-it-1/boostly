package com.example.ui.dialogs

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.UserEntity
import com.example.ui.theme.AlertRed
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldAuthentic
import com.example.ui.theme.GoldCoin

@Composable
fun AdminAddCoinDialog(
  user: UserEntity,
  onDismiss: () -> Unit,
  onConfirmAdjust: (amount: Int, reason: String) -> Unit
) {
  var coinAmountText by remember { mutableStateOf("100") }
  var isAddition by remember { mutableStateOf(true) }
  var reasonText by remember { mutableStateOf("Bonus Rewards Event Kreator") }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .testTag("admin_add_coin_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(ElectricIndigo.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AdminPanelSettings,
              contentDescription = null,
              tint = ElectricIndigo,
              modifier = Modifier.size(20.dp)
            )
          }
          Column {
            Text(
              text = "Kelola Koin User (Admin)",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Target: ${user.name} (${user.email})",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Current balance info
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Saldo Koin Saat Ini:", fontSize = 12.sp)
            Text(
              "${user.points} Koin",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = GoldCoin
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Operation Mode (Tambah vs Kurang)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (isAddition) EmeraldAuthentic else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .clickable { isAddition = true }
          ) {
            Box(modifier = Modifier.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
              Text(
                "+ Tambah Koin",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (isAddition) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurface
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (!isAddition) AlertRed else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .clickable { isAddition = false }
          ) {
            Box(modifier = Modifier.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
              Text(
                "- Kurangi Koin",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (!isAddition) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Preset Chips
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf(50, 100, 250, 500, 1000).forEach { amount ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .clickable { coinAmountText = amount.toString() }
            ) {
              Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                Text(
                  "$amount",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = coinAmountText,
          onValueChange = { coinAmountText = it.filter { char -> char.isDigit() } },
          label = { Text("Jumlah Nominal Koin") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("admin_coin_amount_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = reasonText,
          onValueChange = { reasonText = it },
          label = { Text("Alasan / Catatan Penyesuaian") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("admin_coin_reason_input")
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          TextButton(onClick = onDismiss) {
            Text("Batal")
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              val amount = coinAmountText.toIntOrNull() ?: 0
              if (amount > 0) {
                val finalDelta = if (isAddition) amount else -amount
                onConfirmAdjust(finalDelta, reasonText.trim())
              }
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isAddition) EmeraldAuthentic else AlertRed
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("admin_submit_coin_button")
          ) {
            Text(
              text = if (isAddition) "Tambahkan Koin" else "Kurangi Koin",
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}
