package com.example.ui.dialogs

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AlertRed
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldAuthentic

@Composable
fun AntiCheatInfoDialog(onDismiss: () -> Unit) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .testTag("anti_cheat_info_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(20.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(AlertRed.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = null,
              tint = AlertRed,
              modifier = Modifier.size(24.dp)
            )
          }
          Column {
            Text(
              text = "Protokol Anti-Curang",
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "SosmedGrow Authenticity Guard 2.0",
              fontSize = 11.sp,
              color = EmeraldAuthentic,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Untuk menjaga ekosistem komersial yang adil dan menjamin setiap pengikut adalah manusia asli & aktif, sistem menerapkan aturan ketat berikut:",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        RuleItem(
          icon = Icons.Default.HourglassBottom,
          title = "1. Delay Kunjungan & Timer Verifikasi",
          desc = "Sistem mewajibkan pengguna membuka link profil asli dan menunggu verifikasi otomatis sebelum tombol klaim koin aktif, mencegah serangan skrip bot klik cepat."
        )

        RuleItem(
          icon = Icons.Default.Block,
          title = "2. Dilarang Unfollow (Anti-Drop Penalty)",
          desc = "Jika pengguna melakukan unfollow setelah klaim koin, kreator dapat menandai akun tersebut. Koin akan ditarik paksa dan skor reputasi akan dipotong 15%."
        )

        RuleItem(
          icon = Icons.Default.VerifiedUser,
          title = "3. Wajib Akun Sosial Media Nyata",
          desc = "Klaim follow wajib menyertakan username akun Anda. Kreator dapat mengaudit keaslian follow langsung dari tab Validasi Pengikut."
        )

        RuleItem(
          icon = Icons.Default.Block,
          title = "4. Pembekuan Akun Permanen (Banned)",
          desc = "Pengguna dengan 3x pelanggaran curang atau skor keaslian di bawah 70% akan langsung dibekukan dan seluruh saldo koin disita."
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("close_anti_cheat_dialog_button")
        ) {
          Text("Saya Mengerti & Patuh", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun RuleItem(
  icon: ImageVector,
  title: String,
  desc: String
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = ElectricIndigo,
        modifier = Modifier.size(20.dp)
      )
      Column {
        Text(
          text = title,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = desc,
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 15.sp
        )
      }
    }
  }
}
