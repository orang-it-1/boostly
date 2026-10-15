package com.example.data.payment

enum class PaymentGateway(val displayName: String, val badgeColorHex: Long, val tagline: String) {
  MIDTRANS("Midtrans QRIS", 0xFF002B49, "GoPay / National QRIS (Bank Indonesia)"),
  XENDIT("Xendit QRIS", 0xFF145FF5, "Dynamic QRIS Real-time Settlement"),
  TRIPAY("Tripay QRIS", 0xFFEE4D2D, "Instant Multi-Payment Gateway")
}

enum class PaymentStatus(val label: String) {
  PENDING("Menunggu Pembayaran"),
  SETTLEMENT("Berhasil / Lunas"),
  EXPIRED("Kedaluwarsa"),
  FAILED("Gagal")
}

data class PaymentTransaction(
  val orderId: String,
  val gateway: PaymentGateway,
  val amountIdr: Long,
  val coins: Int,
  val bonusCoins: Int,
  val qrisString: String,
  val qrImageUrl: String? = null,
  val status: PaymentStatus = PaymentStatus.PENDING,
  val createdAt: Long = System.currentTimeMillis(),
  val expiresAt: Long = System.currentTimeMillis() + (15 * 60 * 1000), // 15 mins
  val paymentUrl: String? = null,
  val merchantName: String = "SosmedGrow Official (NMID: ID1029384756201)",
  val isRealGateway: Boolean = true
)

data class PaymentGatewayConfig(
  val activeGateway: PaymentGateway = PaymentGateway.MIDTRANS,
  val isSandbox: Boolean = true,
  val midtransServerKey: String = "SB-Mid-server-SosmedGrowDemo2026",
  val midtransClientKey: String = "SB-Mid-client-SosmedGrowDemo2026",
  val xenditApiKey: String = "xnd_development_SosmedGrowDemo2026",
  val tripayApiKey: String = "DEV-SosmedGrowTripayDemo2026",
  val tripayMerchantCode: String = "T12345"
)
