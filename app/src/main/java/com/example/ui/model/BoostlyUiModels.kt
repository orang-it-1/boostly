package com.example.ui.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class RecommendedCreator(
  val handle: String,
  val name: String,
  val avatarColor: Color,
  val category: String,
  val coinsReward: Int = 10
)

data class TaskAccountItem(
  val handle: String,
  val category: String,
  val avatarColor: Color,
  val rewardCoins: Int = 10,
  val campaignId: Int = 0,
  val targetUrl: String = ""
)

data class DisplayTransactionItem(
  val id: Int = 0,
  val title: String,
  val subtitle: String = "",
  val timestampText: String,
  val amountText: String,
  val amountValue: Int = 0,
  val isPositive: Boolean,
  val icon: ImageVector,
  val categoryLabel: String = "Tugas",
  val statusText: String = "Selesai",
  val referenceCode: String = ""
)
