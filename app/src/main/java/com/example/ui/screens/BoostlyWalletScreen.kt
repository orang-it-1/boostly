package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CoinTransactionEntity
import com.example.data.model.TransactionType
import com.example.data.model.UserEntity
import com.example.ui.model.DisplayTransactionItem
import com.example.ui.theme.BoostlyAlert
import com.example.ui.theme.BoostlyBorderLight
import com.example.ui.theme.BoostlyDarkGreenBtn
import com.example.ui.theme.BoostlyEmerald
import com.example.ui.theme.BoostlyForestGreen
import com.example.ui.theme.BoostlyGold
import com.example.ui.theme.BoostlyGoldDark
import com.example.ui.theme.BoostlyGoldLight
import com.example.ui.theme.BoostlyMintCard
import com.example.ui.theme.BoostlyMintPill
import com.example.ui.theme.BoostlyMintText
import com.example.ui.theme.BoostlyNeonLime
import com.example.ui.theme.BoostlyTextDark
import com.example.ui.theme.BoostlyTextGray
import com.example.ui.theme.BoostlyTextLightGray
import com.example.ui.theme.BoostlyTextMuted
import com.example.ui.theme.BoostlyTextWhite
import com.example.ui.theme.isThemeDark
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class WalletFilterTab(val label: String) {
  ALL("Semua"),
  EARNED("Koin Masuk 🟢"),
  SPENT("Koin Keluar 🔴")
}

/**
 * Screen 4: Dashboard Dompet Koin (Wallet & Transaction History)
 * Displays user's current coin balance, rupiah equivalent, earning vs spending summary,
 * quick actions (Withdraw/Tarik, QRIS Top-up, Follower Exchange), and interactive transaction history.
 */
@Composable
fun BoostlyWalletScreen(
  user: UserEntity?,
  transactions: List<CoinTransactionEntity>,
  onBack: () -> Unit,
  onOpenExchangeFollowers: () -> Unit,
  onOpenQrisShop: () -> Unit,
  onWithdraw: (amount: Int, method: String, account: String) -> Unit = { _, _, _ -> },
  onClaimDailyBonus: () -> Unit = {}
) {
  val context = LocalContext.current
  val isDark = isThemeDark()
  val idrFormat = remember { NumberFormat.getNumberInstance(Locale("id", "ID")) }

  var selectedFilter by remember { mutableStateOf(WalletFilterTab.ALL) }
  var searchQuery by remember { mutableStateOf("") }
  var showWithdrawDialog by remember { mutableStateOf(false) }
  var selectedTransactionReceipt by remember { mutableStateOf<DisplayTransactionItem?>(null) }

  // Fallback demo mock transactions if user database is empty
  val defaultTransactions = remember {
    listOf(
      DisplayTransactionItem(
        id = 101,
        title = "Follow @luna.style",
        subtitle = "Tugas Follow Instagram",
        timestampText = "14 Okt 2026 • 09:41 WIB",
        amountText = "+10 Koin",
        amountValue = 10,
        isPositive = true,
        icon = Icons.Default.Person,
        categoryLabel = "TUGAS FOLLOW",
        statusText = "Selesai",
        referenceCode = "BST-FLW-92812"
      ),
      DisplayTransactionItem(
        id = 102,
        title = "Follow @tech.daily",
        subtitle = "Tugas Follow TikTok",
        timestampText = "14 Okt 2026 • 09:32 WIB",
        amountText = "+10 Koin",
        amountValue = 10,
        isPositive = true,
        icon = Icons.Default.Person,
        categoryLabel = "TUGAS FOLLOW",
        statusText = "Selesai",
        referenceCode = "BST-FLW-92790"
      ),
      DisplayTransactionItem(
        id = 103,
        title = "Follow @foodies.id",
        subtitle = "Tugas Follow Instagram",
        timestampText = "14 Okt 2026 • 09:21 WIB",
        amountText = "+10 Koin",
        amountValue = 10,
        isPositive = true,
        icon = Icons.Default.Person,
        categoryLabel = "TUGAS FOLLOW",
        statusText = "Selesai",
        referenceCode = "BST-FLW-92750"
      ),
      DisplayTransactionItem(
        id = 104,
        title = "Penukaran Kampanye Follower",
        subtitle = "Target: 50 Pengikut Baru",
        timestampText = "14 Okt 2026 • 08:55 WIB",
        amountText = "-50 Koin",
        amountValue = -50,
        isPositive = false,
        icon = Icons.Default.NearMe,
        categoryLabel = "KAMPANYE",
        statusText = "Diproses",
        referenceCode = "BST-CMP-81723"
      ),
      DisplayTransactionItem(
        id = 105,
        title = "Bonus Check-In Harian",
        subtitle = "Streak Hari ke-3 🔥",
        timestampText = "14 Okt 2026 • 08:00 WIB",
        amountText = "+15 Koin",
        amountValue = 15,
        isPositive = true,
        icon = Icons.Default.CardGiftcard,
        categoryLabel = "BONUS HARIAN",
        statusText = "Selesai",
        referenceCode = "BST-CHK-76120"
      ),
      DisplayTransactionItem(
        id = 106,
        title = "Top-Up QRIS Starter Pack",
        subtitle = "Pembayaran Midtrans QRIS Lunas",
        timestampText = "13 Okt 2026 • 21:10 WIB",
        amountText = "+120 Koin",
        amountValue = 120,
        isPositive = true,
        icon = Icons.Default.MonetizationOn,
        categoryLabel = "TOPUP QRIS",
        statusText = "Selesai",
        referenceCode = "BST-QRS-55201"
      )
    )
  }

  // Real transactions from Room database
  val sdf = remember { SimpleDateFormat("dd MMM yyyy • HH:mm 'WIB'", Locale("id", "ID")) }
  val realItems = remember(transactions) {
    transactions.map { t ->
      val (category, icon) = when (t.type) {
        TransactionType.DAILY_CHECKIN_BONUS, TransactionType.WELCOME_BONUS -> "BONUS" to Icons.Default.CardGiftcard
        TransactionType.EARNED_FOLLOW, TransactionType.EARNED_TASK -> "TUGAS FOLLOW" to Icons.Default.Person
        TransactionType.QRIS_PURCHASE -> "TOPUP QRIS" to Icons.Default.MonetizationOn
        TransactionType.EXCHANGED_CAMPAIGN -> "KAMPANYE" to Icons.Default.NearMe
        TransactionType.ADMIN_DEDUCT -> "PENARIKAN SALDO" to Icons.Default.AccountBalanceWallet
        TransactionType.ADMIN_TOPUP -> "PENYESUAIAN ADMIN" to Icons.Default.MonetizationOn
        else -> "TRANSAKSI" to Icons.Default.ReceiptLong
      }

      DisplayTransactionItem(
        id = t.id,
        title = t.description,
        subtitle = category,
        timestampText = sdf.format(Date(t.timestamp)),
        amountText = if (t.amount > 0) "+${t.amount} Koin" else "${t.amount} Koin",
        amountValue = t.amount,
        isPositive = t.amount > 0,
        icon = icon,
        categoryLabel = category,
        statusText = "Selesai",
        referenceCode = "BST-TX-${10000 + t.id}"
      )
    }
  }

  val displayList = if (realItems.isNotEmpty()) realItems else defaultTransactions

  // Calculations for summary metrics
  val totalEarned = remember(displayList) {
    displayList.filter { it.isPositive }.sumOf { kotlin.math.abs(it.amountValue) }
  }
  val totalSpent = remember(displayList) {
    displayList.filter { !it.isPositive }.sumOf { kotlin.math.abs(it.amountValue) }
  }
  val earnedCount = remember(displayList) { displayList.count { it.isPositive } }
  val spentCount = remember(displayList) { displayList.count { !it.isPositive } }

  // Filtered transactions
  val filteredTransactions = remember(displayList, selectedFilter, searchQuery) {
    displayList.filter { item ->
      val matchesTab = when (selectedFilter) {
        WalletFilterTab.ALL -> true
        WalletFilterTab.EARNED -> item.isPositive
        WalletFilterTab.SPENT -> !item.isPositive
      }
      val matchesQuery = searchQuery.isBlank() ||
        item.title.contains(searchQuery, ignoreCase = true) ||
        item.subtitle.contains(searchQuery, ignoreCase = true) ||
        item.categoryLabel.contains(searchQuery, ignoreCase = true) ||
        item.referenceCode.contains(searchQuery, ignoreCase = true)

      matchesTab && matchesQuery
    }
  }

  val currentCoins = user?.points ?: 245
  val idrEquivalent = currentCoins * 100L

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("boostly_wallet_screen"),
    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 95.dp)
  ) {
    // 1. Top Bar Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onBack) {
            Icon(
              imageVector = Icons.Default.ArrowBack,
              contentDescription = "Kembali",
              tint = MaterialTheme.colorScheme.onBackground,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Column {
            Text(
              text = "Dashboard Dompet",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground
            )
            Text(
              text = "Kelola saldo & transaksi koin Anda",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Wallet Address / ID Badge
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
          modifier = Modifier.clickable {
            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText("WalletID", "BST-${user?.id ?: 2}-ID"))
            Toast.makeText(context, "ID Dompet disalin!", Toast.LENGTH_SHORT).show()
          }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(
              text = "#BST-${user?.id ?: 2}",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (isDark) BoostlyNeonLime else BoostlyDarkGreenBtn
            )
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "Salin ID",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(11.dp)
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(18.dp))
    }

    // 2. Executive Hero Dashboard Card (Current Balance & Live Metrics)
    item {
      Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("wallet_balance_hero_card")
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.verticalGradient(
                listOf(
                  Color(0xFF092113),
                  Color(0xFF133B22),
                  Color(0xFF0D2817)
                )
              )
            )
            .border(1.5.dp, BoostlyNeonLime.copy(alpha = 0.35f), RoundedCornerShape(26.dp))
            .padding(22.dp)
        ) {
          Column {
            // Top tag: Status Aktif & Kurs Konversi
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = BoostlyDarkGreenBtn.copy(alpha = 0.8f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF265437))
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(6.dp)
                      .clip(CircleShape)
                      .background(BoostlyEmerald)
                  )
                  Text(
                    text = "SALDO RESMI BOSTLY",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFA3D9B5),
                    letterSpacing = 0.5.sp
                  )
                }
              }

              Text(
                text = "1 Koin = Rp 100",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = BoostlyGoldLight
              )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Balance Row with 3D Coin Badge
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text(
                  text = "Saldo Koin Tersedia",
                  fontSize = 12.sp,
                  color = Color(0xFFA3C2AF),
                  fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                  verticalAlignment = Alignment.Bottom,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text(
                    text = idrFormat.format(currentCoins),
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = (-1).sp
                  )
                  Text(
                    text = "KOIN",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BoostlyNeonLime,
                    modifier = Modifier.padding(bottom = 6.dp)
                  )
                }
                Text(
                  text = "≈ Rp ${idrFormat.format(idrEquivalent)} IDR",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = BoostlyGoldLight
                )
              }

              // Large 3D Gold Coin Circle Graphic with pulsing border
              Box(
                modifier = Modifier
                  .size(64.dp)
                  .clip(CircleShape)
                  .background(
                    Brush.linearGradient(
                      listOf(BoostlyGoldLight, BoostlyGold, BoostlyGoldDark)
                    )
                  )
                  .border(3.dp, BoostlyGoldLight, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.MonetizationOn,
                  contentDescription = null,
                  tint = Color(0xFF78350F),
                  modifier = Modifier.size(40.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sub-metrics Bar: Total Didapat vs Total Dipakai
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF091C10))
                .border(1.dp, Color(0xFF1E462B), RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Earned
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(BoostlyEmerald.copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = BoostlyEmerald,
                    modifier = Modifier.size(14.dp)
                  )
                }
                Column {
                  Text("Total Didapat", fontSize = 10.sp, color = Color(0xFF8FA899))
                  Text("+${idrFormat.format(totalEarned)} Koin", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BoostlyEmerald)
                }
              }

              Box(
                modifier = Modifier
                  .height(24.dp)
                  .width(1.dp)
                  .background(Color(0xFF1E462B))
              )

              // Spent
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(BoostlyAlert.copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = BoostlyAlert,
                    modifier = Modifier.size(14.dp)
                  )
                }
                Column {
                  Text("Total Dipakai", fontSize = 10.sp, color = Color(0xFF8FA899))
                  Text("-${idrFormat.format(totalSpent)} Koin", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFCA5A5))
                }
              }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Primary Quick Actions Grid (Tarik Saldo, Beli QRIS, Tukar Follower)
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              // Tarik Saldo
              Button(
                onClick = { showWithdrawDialog = true },
                colors = ButtonDefaults.buttonColors(
                  containerColor = BoostlyNeonLime,
                  contentColor = Color(0xFF0A1910)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                  .weight(1f)
                  .height(44.dp)
                  .testTag("tarik_koin_button")
              ) {
                Icon(
                  imageVector = Icons.Default.AccountBalanceWallet,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Tarik Saldo", fontSize = 12.sp, fontWeight = FontWeight.Black)
              }

              // Beli QRIS
              Button(
                onClick = onOpenQrisShop,
                colors = ButtonDefaults.buttonColors(
                  containerColor = Color(0xFF183D27),
                  contentColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E6342)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                  .weight(1f)
                  .height(44.dp)
                  .testTag("beli_koin_qris_button")
              ) {
                Icon(
                  imageVector = Icons.Default.QrCode,
                  contentDescription = null,
                  tint = BoostlyGoldLight,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Beli QRIS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Secondary Quick Action (Tukar Follower)
            OutlinedButton(
              onClick = onOpenExchangeFollowers,
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF265437)),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFA3D9B5)),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .testTag("tukar_follower_button")
            ) {
              Icon(Icons.Default.NearMe, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Tukar Koin Menjadi Follower Akun Anda", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(24.dp))
    }

    // 3. Section Title & Filter Tabs
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Riwayat Aktivitas Koin",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = "Daftar penerimaan & pengeluaran koin",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier.padding(2.dp)
        ) {
          Text(
            text = "${filteredTransactions.size} Transaksi",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Filter Tabs (Semua, Masuk, Keluar)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        WalletFilterTab.values().forEach { tab ->
          val isSelected = selectedFilter == tab
          val count = when (tab) {
            WalletFilterTab.ALL -> displayList.size
            WalletFilterTab.EARNED -> earnedCount
            WalletFilterTab.SPENT -> spentCount
          }

          FilterChip(
            selected = isSelected,
            onClick = { selectedFilter = tab },
            label = {
              Text(
                text = "${tab.label} ($count)",
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = if (isDark) Color(0xFF1B3D2A) else Color(0xFFEAF7EE),
              selectedLabelColor = if (isDark) BoostlyNeonLime else BoostlyDarkGreenBtn,
              containerColor = MaterialTheme.colorScheme.surface,
              labelColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = isSelected,
              borderColor = if (isSelected) {
                if (isDark) BoostlyNeonLime else BoostlyDarkGreenBtn
              } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
              }
            ),
            modifier = Modifier.testTag("filter_chip_${tab.name.lowercase()}")
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Search Field
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Cari berdasarkan nama akun atau deskripsi...", fontSize = 12.sp) },
        leadingIcon = {
          Icon(Icons.Default.Search, contentDescription = "Cari", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(24.dp)) {
              Icon(Icons.Default.Close, contentDescription = "Hapus", modifier = Modifier.size(16.dp))
            }
          }
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = MaterialTheme.colorScheme.surface,
          unfocusedContainerColor = MaterialTheme.colorScheme.surface,
          focusedBorderColor = if (isDark) BoostlyNeonLime else BoostlyDarkGreenBtn,
          unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("search_transactions_input")
      )

      Spacer(modifier = Modifier.height(14.dp))
    }

    // 4. Empty State
    if (filteredTransactions.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.ReceiptLong,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
              )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "Tidak Ada Transaksi",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = if (searchQuery.isNotEmpty()) {
                "Tidak ada hasil untuk pencarian \"$searchQuery\"."
              } else {
                "Belum ada riwayat transaksi pada kategori ini."
              },
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center
            )
          }
        }
      }
    }

    // 5. Transaction History List
    items(filteredTransactions, key = { it.id.toString() + it.referenceCode + it.timestampText }) { item ->
      TransactionDashboardRowItem(
        item = item,
        onClick = { selectedTransactionReceipt = item }
      )
      Spacer(modifier = Modifier.height(8.dp))
    }

    // 6. Security & Policy Information Card
    item {
      Spacer(modifier = Modifier.height(18.dp))
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (isDark) Color(0xFF112619) else BoostlyMintCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF1E462B) else Color(0xFFD1FAE5)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isDark) Color(0xFF183D27) else BoostlyDarkGreenBtn),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Shield,
              contentDescription = null,
              tint = BoostlyGold,
              modifier = Modifier.size(22.dp)
            )
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Keamanan Koin & Perlindungan Akun",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = if (isDark) BoostlyTextWhite else BoostlyTextDark
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Semua perolehan koin diverifikasi oleh sistem AI anti-curang. Koin berlaku selamanya dan dapat ditarik tunai kapan saja.",
              fontSize = 11.sp,
              color = if (isDark) Color(0xFFA3B8AB) else BoostlyTextGray,
              lineHeight = 16.sp
            )
          }
        }
      }
    }
  }

  // Modal 1: Interactive Withdrawal / Cashout Dialog
  if (showWithdrawDialog) {
    WithdrawCashoutDialog(
      currentBalance = currentCoins,
      onDismiss = { showWithdrawDialog = false },
      onConfirm = { amount, method, account ->
        showWithdrawDialog = false
        onWithdraw(amount, method, account)
      }
    )
  }

  // Modal 2: Digital Transaction Receipt Modal
  selectedTransactionReceipt?.let { receipt ->
    TransactionReceiptDetailDialog(
      item = receipt,
      onDismiss = { selectedTransactionReceipt = null }
    )
  }
}

/**
 * Enhanced Transaction Row Card
 * Shows distinct status, icon, amount with IDR equivalent, category, and opens receipt on click.
 */
@Composable
private fun TransactionDashboardRowItem(
  item: DisplayTransactionItem,
  onClick: () -> Unit
) {
  val isDark = isThemeDark()
  val idrEquivalent = kotlin.math.abs(item.amountValue) * 100L
  val idrFormat = remember { NumberFormat.getNumberInstance(Locale("id", "ID")) }

  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      MaterialTheme.colorScheme.outline.copy(alpha = if (isDark) 0.35f else 0.5f)
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag("tx_item_${item.id}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Left side: Icon badge + Title + Category + Time
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(
              if (item.isPositive) {
                if (isDark) Color(0xFF143A24) else Color(0xFFE8F5E9)
              } else {
                if (isDark) Color(0xFF381919) else Color(0xFFFFEBEE)
              }
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = if (item.isPositive) BoostlyEmerald else BoostlyAlert,
            modifier = Modifier.size(22.dp)
          )
        }

        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (item.isPositive) {
                if (isDark) Color(0xFF1B3D2A) else Color(0xFFEAF7EE)
              } else {
                if (isDark) Color(0xFF3A1C1C) else Color(0xFFFEE2E2)
              }
            ) {
              Text(
                text = item.categoryLabel,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (item.isPositive) {
                  if (isDark) BoostlyNeonLime else Color(0xFF0F6E38)
                } else {
                  if (isDark) Color(0xFFFCA5A5) else Color(0xFFB91C1C)
                },
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
              )
            }

            Text(
              text = item.referenceCode,
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Spacer(modifier = Modifier.height(3.dp))

          Text(
            text = item.title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
          )

          Spacer(modifier = Modifier.height(2.dp))

          Text(
            text = item.timestampText,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Right side: Amount in Coins & IDR estimate
      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = item.amountText,
          fontSize = 14.sp,
          fontWeight = FontWeight.ExtraBold,
          color = if (item.isPositive) BoostlyEmerald else BoostlyAlert
        )
        Text(
          text = "≈ Rp ${idrFormat.format(idrEquivalent)}",
          fontSize = 10.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = BoostlyEmerald,
            modifier = Modifier.size(10.dp)
          )
          Text(
            text = item.statusText,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            color = BoostlyEmerald
          )
        }
      }
    }
  }
}

/**
 * Interactive Cashout / Withdrawal Dialog
 * Supports E-Wallets (DANA, GoPay, OVO, ShopeePay) and Bank Transfer.
 */
@Composable
private fun WithdrawCashoutDialog(
  currentBalance: Int,
  onDismiss: () -> Unit,
  onConfirm: (amount: Int, method: String, account: String) -> Unit
) {
  val context = LocalContext.current
  val isDark = isThemeDark()
  val idrFormat = remember { NumberFormat.getNumberInstance(Locale("id", "ID")) }

  val methods = listOf("DANA", "GoPay", "OVO", "ShopeePay", "Bank BCA")
  var selectedMethod by remember { mutableStateOf("DANA") }

  val coinOptions = listOf(200, 500, 1000, 2500)
  var selectedCoins by remember { mutableIntStateOf(500) }
  var accountNumber by remember { mutableStateOf("") }
  var accountName by remember { mutableStateOf("") }
  var errorText by remember { mutableStateOf<String?>(null) }

  val rupiahAmount = selectedCoins * 100L

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(26.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp)
        .testTag("withdraw_cashout_dialog")
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
                imageVector = Icons.Default.AccountBalanceWallet,
                contentDescription = null,
                tint = if (isDark) BoostlyNeonLime else BoostlyDarkGreenBtn,
                modifier = Modifier.size(22.dp)
              )
            }
            Column {
              Text(
                text = "Tarik Saldo Koin",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Cairkan koin ke E-Wallet atau Bank",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Tutup", tint = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Balance reminder banner
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Saldo Koin Anda:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
              text = "${idrFormat.format(currentBalance)} Koin (Rp ${idrFormat.format(currentBalance * 100L)})",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = if (isDark) BoostlyNeonLime else BoostlyDarkGreenBtn
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Select Nominal Preset Chips
        Text("Pilih Jumlah Penarikan:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          coinOptions.forEach { coins ->
            val isSelected = selectedCoins == coins
            val isAffordable = currentBalance >= coins

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (isSelected) {
                if (isDark) Color(0xFF1B3D2A) else Color(0xFFEAF7EE)
              } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
              },
              border = androidx.compose.foundation.BorderStroke(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) {
                  if (isDark) BoostlyNeonLime else BoostlyDarkGreenBtn
                } else {
                  MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                }
              ),
              modifier = Modifier
                .weight(1f)
                .clickable {
                  selectedCoins = coins
                  errorText = null
                }
            ) {
              Column(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = "$coins 🪙",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) {
                    if (isDark) BoostlyNeonLime else BoostlyDarkGreenBtn
                  } else {
                    MaterialTheme.colorScheme.onSurface
                  }
                )
                Text(
                  text = "Rp ${idrFormat.format(coins * 100L)}",
                  fontSize = 10.sp,
                  color = if (!isAffordable) BoostlyAlert else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Select E-Wallet Provider
        Text("Metode Pencairan:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          methods.forEach { method ->
            val isSelected = selectedMethod == method
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) {
                if (isDark) BoostlyNeonLime else BoostlyDarkGreenBtn
              } else {
                MaterialTheme.colorScheme.surfaceVariant
              },
              modifier = Modifier
                .weight(1f)
                .clickable { selectedMethod = method }
            ) {
              Text(
                text = method,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) {
                  if (isDark) Color.Black else Color.White
                } else {
                  MaterialTheme.colorScheme.onSurfaceVariant
                },
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 6.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Input Nomor HP / Akun
        OutlinedTextField(
          value = accountNumber,
          onValueChange = {
            accountNumber = it
            errorText = null
          },
          label = { Text("Nomor HP / No. Rekening ($selectedMethod)") },
          placeholder = { Text("Contoh: 081234567890") },
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("withdraw_account_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = accountName,
          onValueChange = { accountName = it },
          label = { Text("Nama Pemilik Akun") },
          placeholder = { Text("Sesuai nama terdaftar di $selectedMethod") },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth()
        )

        // Error message if any
        errorText?.let {
          Spacer(modifier = Modifier.height(6.dp))
          Text(text = "⚠️ $it", fontSize = 11.sp, color = BoostlyAlert)
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = {
            if (accountNumber.trim().length < 8) {
              errorText = "Masukkan nomor akun yang valid (minimal 8 digit)."
              return@Button
            }
            if (currentBalance < selectedCoins) {
              errorText = "Saldo koin Anda tidak mencukupi untuk penarikan ini."
              return@Button
            }
            onConfirm(selectedCoins, selectedMethod, accountNumber.trim())
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isDark) BoostlyNeonLime else BoostlyDarkGreenBtn,
            contentColor = if (isDark) Color.Black else Color.White
          ),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("confirm_withdraw_button")
        ) {
          Text("Tarik Rp ${idrFormat.format(rupiahAmount)} Sekarang", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }
  }
}

/**
 * Digital Transaction Receipt Modal
 * Displays a clean receipt with details, status, and button to copy transaction reference code.
 */
@Composable
private fun TransactionReceiptDetailDialog(
  item: DisplayTransactionItem,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val isDark = isThemeDark()
  val idrEquivalent = kotlin.math.abs(item.amountValue) * 100L
  val idrFormat = remember { NumberFormat.getNumberInstance(Locale("id", "ID")) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(26.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
        .testTag("transaction_receipt_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(22.dp)
      ) {
        // Receipt Header Icon
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (item.isPositive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
          ) {
            Text(
              text = item.categoryLabel,
              fontSize = 10.sp,
              fontWeight = FontWeight.Black,
              color = if (item.isPositive) Color(0xFF0F6E38) else Color(0xFFB91C1C),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Tutup", tint = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Center Amount Display
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(54.dp)
              .clip(CircleShape)
              .background(
                if (item.isPositive) BoostlyEmerald.copy(alpha = 0.15f)
                else BoostlyAlert.copy(alpha = 0.15f)
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (item.isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
              contentDescription = null,
              tint = if (item.isPositive) BoostlyEmerald else BoostlyAlert,
              modifier = Modifier.size(26.dp)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = item.amountText,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = if (item.isPositive) BoostlyEmerald else BoostlyAlert
          )

          Text(
            text = "≈ Rp ${idrFormat.format(idrEquivalent)} IDR",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(6.dp))

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = BoostlyEmerald.copy(alpha = 0.15f)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BoostlyEmerald, modifier = Modifier.size(12.dp))
              Text("Transaksi Sukses & Terverifikasi", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BoostlyEmerald)
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Dashed Divider / Details Table
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            ReceiptRow("Deskripsi", item.title)
            Spacer(modifier = Modifier.height(8.dp))
            ReceiptRow("Waktu", item.timestampText)
            Spacer(modifier = Modifier.height(8.dp))
            ReceiptRow("ID Referensi", item.referenceCode)
            Spacer(modifier = Modifier.height(8.dp))
            ReceiptRow("Status Anti-Curang", "Autentik (Terverifikasi)")
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = {
              val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              cm.setPrimaryClip(ClipData.newPlainText("TxRef", item.referenceCode))
              Toast.makeText(context, "ID Referensi disalin!", Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Salin ID", fontSize = 12.sp)
          }

          Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isDark) BoostlyNeonLime else BoostlyDarkGreenBtn,
              contentColor = if (isDark) Color.Black else Color.White
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.weight(1f)
          ) {
            Text("Tutup", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Text(
      text = value,
      fontSize = 11.sp,
      fontWeight = FontWeight.SemiBold,
      color = MaterialTheme.colorScheme.onSurface,
      textAlign = TextAlign.End,
      maxLines = 2
    )
  }
}
