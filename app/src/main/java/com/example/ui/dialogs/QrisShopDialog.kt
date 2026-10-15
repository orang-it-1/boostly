package com.example.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.CountDownTimer
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.payment.PaymentGateway
import com.example.data.payment.PaymentGatewayManager
import com.example.data.payment.PaymentStatus
import com.example.data.payment.PaymentTransaction
import com.example.data.repository.QrisCoinPackage
import com.example.ui.components.QrisBarcodeVisual
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldAuthentic
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.GoldCoinDark
import com.example.ui.theme.GoldCoinLight
import com.example.ui.theme.QrisRed
import java.text.NumberFormat
import java.util.Locale

@Composable
fun QrisShopDialog(
  packages: List<QrisCoinPackage>,
  initiallySelectedPackage: QrisCoinPackage?,
  paymentGatewayManager: PaymentGatewayManager? = null,
  onDismiss: () -> Unit,
  onConfirmPurchase: (QrisCoinPackage) -> Unit
) {
  val context = LocalContext.current
  var activePackage by remember { mutableStateOf(initiallySelectedPackage) }
  var isPaymentScreen by remember { mutableStateOf(initiallySelectedPackage != null) }

  var selectedGateway by remember { mutableStateOf(PaymentGateway.MIDTRANS) }
  var isSandboxMode by remember { mutableStateOf(true) }
  var showGatewaySettings by remember { mutableStateOf(false) }

  // Gateway Keys Config State
  var customServerKey by remember { mutableStateOf("SB-Mid-server-SosmedGrowDemo2026") }
  var customXenditKey by remember { mutableStateOf("xnd_development_SosmedGrowDemo2026") }
  var customTripayKey by remember { mutableStateOf("DEV-SosmedGrowTripayDemo2026") }

  // Active Transaction State
  var activeTransaction by remember { mutableStateOf<PaymentTransaction?>(null) }
  var isCheckingStatus by remember { mutableStateOf(false) }
  var remainingSeconds by remember { mutableIntStateOf(900) } // 15 mins countdown

  // Timer countdown effect for active QRIS
  LaunchedEffect(isPaymentScreen, activeTransaction) {
    if (isPaymentScreen && activeTransaction != null) {
      remainingSeconds = 900
      while (remainingSeconds > 0) {
        kotlinx.coroutines.delay(1000)
        remainingSeconds--
      }
    }
  }

  val idrFormat = remember {
    NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
      maximumFractionDigits = 0
    }
  }

  // Auto initialize QRIS transaction on package selection
  fun prepareQrisTransaction(pkg: QrisCoinPackage) {
    activePackage = pkg
    isPaymentScreen = true

    if (paymentGatewayManager != null) {
      paymentGatewayManager.updateConfig(
        gateway = selectedGateway,
        isSandbox = isSandboxMode,
        midtransKey = customServerKey,
        xenditKey = customXenditKey,
        tripayKey = customTripayKey
      )
      paymentGatewayManager.initiateQrisCheckout(pkg) { tx ->
        activeTransaction = tx
      }
    } else {
      // Local fallback representation
      val randomCode = (100000..999999).random()
      val orderId = "SG-${selectedGateway.name.take(3)}-$randomCode"
      val emvco = "00020101021226680016ID.CO.QRIS.WWW01189360091100222047870215ID1029384756201520459995303360540" +
        pkg.priceIdr + "5802ID5919SOSMEDGROW OFFICIAL6007JAKARTA62190115" + orderId + "6304C92A"
      activeTransaction = PaymentTransaction(
        orderId = orderId,
        gateway = selectedGateway,
        amountIdr = pkg.priceIdr,
        coins = pkg.coins,
        bonusCoins = pkg.bonusCoins,
        qrisString = emvco,
        qrImageUrl = "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=" + emvco,
        status = PaymentStatus.PENDING,
        merchantName = "${selectedGateway.displayName} (NMID: ID1029384756201)"
      )
    }
  }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(6.dp)
        .testTag("qris_shop_dialog")
    ) {
      if (!isPaymentScreen || activePackage == null) {
        // Step 1: Package & Gateway Selection Screen
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = QrisRed,
                  modifier = Modifier.padding(end = 6.dp)
                ) {
                  Text(
                    text = "QRIS",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
                Text(
                  text = "Payment Gateway Nyata",
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 16.sp,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
              Text(
                text = "Didukung Midtrans, Xendit & Tripay",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            IconButton(onClick = onDismiss) {
              Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Tutup")
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Payment Gateway Provider Selection
          Text(
            text = "Pilih Penyedia Payment Gateway:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            PaymentGateway.values().forEach { gw ->
              val isSelected = selectedGateway == gw
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) Color(gw.badgeColorHex) else MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (isSelected) Color(gw.badgeColorHex) else MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                  .weight(1f)
                  .clickable { selectedGateway = gw }
              ) {
                Column(
                  modifier = Modifier.padding(8.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = gw.displayName.replace(" QRIS", ""),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "QRIS Otomatis",
                    fontSize = 9.sp,
                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Gateway Mode / Settings Toggle
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
              .clickable { showGatewaySettings = !showGatewaySettings }
              .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = ElectricIndigo, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Konfigurasi Gateway (${if (isSandboxMode) "Sandbox Mode" else "Production Live"})",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
            Text(
              text = if (showGatewaySettings) "Tutup ▲" else "Ubah ▼",
              fontSize = 10.sp,
              color = ElectricIndigo,
              fontWeight = FontWeight.Bold
            )
          }

          AnimatedVisibility(visible = showGatewaySettings) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("Mode Sandbox (Testing API):", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                Switch(
                  checked = isSandboxMode,
                  onCheckedChange = { isSandboxMode = it },
                  colors = SwitchDefaults.colors(checkedThumbColor = ElectricIndigo)
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              OutlinedTextField(
                value = when (selectedGateway) {
                  PaymentGateway.MIDTRANS -> customServerKey
                  PaymentGateway.XENDIT -> customXenditKey
                  PaymentGateway.TRIPAY -> customTripayKey
                },
                onValueChange = {
                  when (selectedGateway) {
                    PaymentGateway.MIDTRANS -> customServerKey = it
                    PaymentGateway.XENDIT -> customXenditKey = it
                    PaymentGateway.TRIPAY -> customTripayKey = it
                  }
                },
                label = { Text("API / Server Key (${selectedGateway.name})", fontSize = 10.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = "Pilih Paket Koin:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Package List Cards
          packages.forEach { pkg ->
            val isPopular = pkg.isPopular
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (isPopular) ElectricIndigo.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
              ),
              border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                if (isPopular) ElectricIndigo else Color.Transparent
              ),
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { prepareQrisTransaction(pkg) }
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(40.dp)
                      .clip(CircleShape)
                      .background(Brush.linearGradient(listOf(GoldCoinLight, GoldCoinDark))),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.MonetizationOn,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(24.dp)
                    )
                  }
                  Spacer(modifier = Modifier.width(12.dp))
                  Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = "${pkg.coins} Koin",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                      )
                      if (pkg.bonusCoins > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                          shape = RoundedCornerShape(6.dp),
                          color = EmeraldAuthentic.copy(alpha = 0.15f)
                        ) {
                          Text(
                            text = "+${pkg.bonusCoins} Bonus",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = EmeraldAuthentic,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                          )
                        }
                      }
                    }
                    Text(
                      text = pkg.title,
                      fontSize = 11.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }

                Column(horizontalAlignment = Alignment.End) {
                  Text(
                    text = idrFormat.format(pkg.priceIdr),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = EmeraldAuthentic
                  )
                  Text(
                    text = "Beli Sekarang ›",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricIndigo
                  )
                }
              }
            }
          }
        }
      } else {
        // Step 2: Live Payment Gateway QRIS Screen
        val pkg = activePackage!!
        val tx = activeTransaction
        val isSettled = tx?.status == PaymentStatus.SETTLEMENT

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Top Navigation
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            IconButton(onClick = { isPaymentScreen = false }) {
              Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali")
            }
            Text(
              text = if (isSettled) "Pembayaran Berhasil!" else "Pembayaran QRIS ${selectedGateway.displayName}",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            )
            IconButton(onClick = onDismiss) {
              Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
            }
          }

          if (isSettled) {
            // Payment Completed Success Banner
            Spacer(modifier = Modifier.height(16.dp))
            Box(
              modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(EmeraldAuthentic.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = EmeraldAuthentic,
                modifier = Modifier.size(48.dp)
              )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "LUNAS & BERHASIL!",
              fontSize = 20.sp,
              fontWeight = FontWeight.Black,
              color = EmeraldAuthentic
            )
            Text(
              text = "+${pkg.coins + pkg.bonusCoins} Koin telah berhasil ditambahkan ke saldo akun Anda.",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))
            Button(
              onClick = {
                onDismiss()
              },
              colors = ButtonDefaults.buttonColors(containerColor = EmeraldAuthentic),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("Selesai & Lihat Saldo", fontWeight = FontWeight.Bold)
            }
          } else {
            // Official QRIS National Header Badge
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = QrisRed,
              modifier = Modifier.padding(vertical = 6.dp)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "QRIS",
                  fontWeight = FontWeight.Black,
                  color = Color.White,
                  fontSize = 14.sp
                )
                Text(
                  text = " | Standar Pembayaran Nasional (${selectedGateway.name})",
                  fontSize = 10.sp,
                  color = Color.White
                )
              }
            }

            Text(
              text = tx?.merchantName ?: "Merchant: SosmedGrow Official",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Order ID: ${tx?.orderId ?: "SG-MID-849201"}",
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Dynamic Barcode Visual / QR Image
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(12.dp)
                .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
              contentAlignment = Alignment.Center
            ) {
              if (tx?.qrImageUrl != null) {
                AsyncImage(
                  model = tx.qrImageUrl,
                  contentDescription = "QRIS Barcode",
                  modifier = Modifier.size(190.dp)
                )
              } else {
                QrisBarcodeVisual()
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Countdown Timer Badge
            val minutes = remainingSeconds / 60
            val seconds = remainingSeconds % 60
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier.padding(vertical = 4.dp)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Timer,
                  contentDescription = null,
                  tint = if (remainingSeconds < 120) QrisRed else MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Batas Waktu Bayar: %02d:%02d".format(minutes, seconds),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (remainingSeconds < 120) QrisRed else MaterialTheme.colorScheme.onSurface
                )
              }
            }

            // Total Amount Card
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
              modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
              Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = "Total Nominal Pembayaran",
                  fontSize = 10.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = idrFormat.format(pkg.priceIdr),
                  fontSize = 22.sp,
                  fontWeight = FontWeight.Black,
                  color = EmeraldAuthentic
                )
                Text(
                  text = "Mendapatkan: ${pkg.coins + pkg.bonusCoins} Koin (Bonus: ${pkg.bonusCoins})",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = GoldCoinDark
                )
              }
            }

            // Copy EMVCo QR String action button
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              horizontalArrangement = Arrangement.Center
            ) {
              OutlinedButton(
                onClick = {
                  val qrisStr = tx?.qrisString ?: "000201010212..."
                  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                  clipboard.setPrimaryClip(ClipData.newPlainText("QRIS Payload", qrisStr))
                  Toast.makeText(context, "String QRIS EMVCo berhasil disalin!", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(10.dp)
              ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Salin String QRIS", fontSize = 10.sp)
              }

              if (tx?.paymentUrl != null) {
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                  onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(tx.paymentUrl))
                    context.startActivity(intent)
                  },
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Buka Web Invoice", fontSize = 10.sp)
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Real-Time Status Check Button
            OutlinedButton(
              onClick = {
                isCheckingStatus = true
                if (paymentGatewayManager != null) {
                  paymentGatewayManager.checkPaymentStatusManually { status ->
                    isCheckingStatus = false
                    if (status == PaymentStatus.SETTLEMENT) {
                      Toast.makeText(context, "Pembayaran berhasil diverifikasi!", Toast.LENGTH_LONG).show()
                    } else {
                      Toast.makeText(context, "Status saat ini: Menunggu Pembayaran dari Bank/E-Wallet", Toast.LENGTH_SHORT).show()
                    }
                  }
                } else {
                  // Fallback simulation check
                  android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    isCheckingStatus = false
                    Toast.makeText(context, "Menunggu notifikasi webhook pembayaran dari ${selectedGateway.name}...", Toast.LENGTH_SHORT).show()
                  }, 1200)
                }
              },
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              if (isCheckingStatus) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Memeriksa Server ${selectedGateway.name}...", fontSize = 12.sp)
              } else {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Periksa Status Pembayaran Real-Time", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Test Scan / Sandbox Confirmation Button
            Button(
              onClick = {
                if (paymentGatewayManager != null && tx != null) {
                  paymentGatewayManager.confirmPaymentSuccess(tx) {
                    activeTransaction = tx.copy(status = PaymentStatus.SETTLEMENT)
                  }
                } else {
                  onConfirmPurchase(pkg)
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = EmeraldAuthentic),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("confirm_qris_payment_button")
            ) {
              Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Simulasi Pembayaran Sukses (Sandbox API)",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = "Mendukung BCA, Mandiri, BRI, BNI, GoPay, OVO, DANA, ShopeePay & LinkAja",
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      }
    }
  }
}
