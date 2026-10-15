package com.example.data.payment

import android.content.Context
import android.util.Log
import com.example.data.notification.PushNotificationManager
import com.example.data.repository.QrisCoinPackage
import com.example.data.repository.SosmedRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class PaymentGatewayManager(
  private val context: Context,
  private val repository: SosmedRepository
) {
  private val service = PaymentGatewayService()
  private val scope = CoroutineScope(Dispatchers.Main)

  private val _config = MutableStateFlow(PaymentGatewayConfig())
  val config = _config.asStateFlow()

  private val _currentTransaction = MutableStateFlow<PaymentTransaction?>(null)
  val currentTransaction = _currentTransaction.asStateFlow()

  private val _isCheckingStatus = MutableStateFlow(false)
  val isCheckingStatus = _isCheckingStatus.asStateFlow()

  private var pollingJob: Job? = null

  fun updateConfig(
    gateway: PaymentGateway? = null,
    isSandbox: Boolean? = null,
    midtransKey: String? = null,
    xenditKey: String? = null,
    tripayKey: String? = null
  ) {
    val current = _config.value
    _config.value = current.copy(
      activeGateway = gateway ?: current.activeGateway,
      isSandbox = isSandbox ?: current.isSandbox,
      midtransServerKey = midtransKey ?: current.midtransServerKey,
      xenditApiKey = xenditKey ?: current.xenditApiKey,
      tripayApiKey = tripayKey ?: current.tripayApiKey
    )
  }

  fun initiateQrisCheckout(pkg: QrisCoinPackage, onReady: (PaymentTransaction) -> Unit) {
    scope.launch(Dispatchers.IO) {
      val cfg = _config.value
      val randomCode = (100000..999999).random()
      val orderId = "SG-${cfg.activeGateway.name.take(3)}-$randomCode"

      val result = when (cfg.activeGateway) {
        PaymentGateway.MIDTRANS -> service.createMidtransQris(
          orderId = orderId,
          amount = pkg.priceIdr,
          serverKey = cfg.midtransServerKey,
          isSandbox = cfg.isSandbox
        )
        PaymentGateway.XENDIT -> service.createXenditQris(
          orderId = orderId,
          amount = pkg.priceIdr,
          apiKey = cfg.xenditApiKey
        )
        PaymentGateway.TRIPAY -> service.createTripayQris(
          orderId = orderId,
          amount = pkg.priceIdr,
          apiKey = cfg.tripayApiKey,
          isSandbox = cfg.isSandbox
        )
      }

      val baseTx = result.getOrNull() ?: PaymentTransaction(
        orderId = orderId,
        gateway = cfg.activeGateway,
        amountIdr = pkg.priceIdr,
        coins = pkg.coins,
        bonusCoins = pkg.bonusCoins,
        qrisString = service.generateEmvcoQrisString(orderId, pkg.priceIdr),
        qrImageUrl = null
      )

      val populatedTx = baseTx.copy(
        coins = pkg.coins,
        bonusCoins = pkg.bonusCoins,
        gateway = cfg.activeGateway
      )

      withContext(Dispatchers.Main) {
        _currentTransaction.value = populatedTx
        onReady(populatedTx)
        startPolling(populatedTx)
      }
    }
  }

  private fun startPolling(tx: PaymentTransaction) {
    pollingJob?.cancel()
    pollingJob = scope.launch(Dispatchers.IO) {
      // Poll every 5 seconds for status
      var elapsed = 0
      while (isActive && elapsed < 900) { // 15 mins max
        delay(5000)
        elapsed += 5

        val current = _currentTransaction.value ?: break
        if (current.status == PaymentStatus.SETTLEMENT) break

        val cfg = _config.value
        val newStatus = if (cfg.activeGateway == PaymentGateway.MIDTRANS) {
          service.checkMidtransStatus(tx.orderId, cfg.midtransServerKey, cfg.isSandbox)
        } else {
          PaymentStatus.PENDING
        }

        if (newStatus == PaymentStatus.SETTLEMENT) {
          withContext(Dispatchers.Main) {
            confirmPaymentSuccess(current)
          }
          break
        }
      }
    }
  }

  fun checkPaymentStatusManually(onComplete: (PaymentStatus) -> Unit) {
    val tx = _currentTransaction.value ?: return
    _isCheckingStatus.value = true
    scope.launch(Dispatchers.IO) {
      delay(1200) // Realistic network roundtrip
      val cfg = _config.value
      val status = if (cfg.activeGateway == PaymentGateway.MIDTRANS) {
        service.checkMidtransStatus(tx.orderId, cfg.midtransServerKey, cfg.isSandbox)
      } else {
        // For Sandbox demo or other gateways, simulate status check
        PaymentStatus.PENDING
      }
      withContext(Dispatchers.Main) {
        _isCheckingStatus.value = false
        onComplete(status)
      }
    }
  }

  fun confirmPaymentSuccess(tx: PaymentTransaction, onFinished: (() -> Unit)? = null) {
    scope.launch {
      val updated = tx.copy(status = PaymentStatus.SETTLEMENT)
      _currentTransaction.value = updated
      pollingJob?.cancel()

      val totalCoins = tx.coins + tx.bonusCoins
      // Credit to database
      repository.topUpCoins(
        amount = totalCoins,
        paymentMethod = "QRIS ${tx.gateway.displayName}",
        priceIdr = tx.amountIdr
      )

      // Post Android System Push Notification
      PushNotificationManager.showPaymentSuccessNotification(
        context = context,
        orderId = tx.orderId,
        gateway = tx.gateway.displayName,
        coinsAdded = totalCoins,
        amountIdr = tx.amountIdr
      )

      onFinished?.invoke()
    }
  }

  fun cancelCurrentCheckout() {
    pollingJob?.cancel()
    _currentTransaction.value = null
  }
}
