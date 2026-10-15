package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class UserRole {
  USER,
  ADMIN
}

@Entity(tableName = "users")
data class UserEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val name: String,
  val email: String,
  val passwordHash: String,
  val role: UserRole = UserRole.USER,
  val points: Int = 100,
  val authenticityScore: Float = 100.0f, // 0 - 100%
  val violationsCount: Int = 0,
  val isBanned: Boolean = false,
  val instagramHandle: String = "",
  val tiktokHandle: String = "",
  val twitterHandle: String = "",
  val youtubeHandle: String = "",
  val detectedJob: String = "",
  val jobConfidence: Float = 0f,
  val jobAnalysisSummary: String = "",
  val jobKeySkills: String = "",
  val recommendedNiche: String = "",
  val createdAt: Long = System.currentTimeMillis()
)

enum class SocialPlatform(val displayName: String, val iconName: String) {
  INSTAGRAM("Instagram", "instagram"),
  TIKTOK("TikTok", "tiktok"),
  YOUTUBE("YouTube", "youtube"),
  X_TWITTER("X / Twitter", "twitter"),
  THREADS("Threads", "threads")
}

@Entity(tableName = "campaigns")
data class CampaignEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val creatorId: Int,
  val creatorName: String,
  val platform: SocialPlatform,
  val targetUrl: String,
  val accountHandle: String,
  val category: String,
  val targetFollowers: Int,
  val currentFollowers: Int = 0,
  val coinRewardPerFollow: Int = 10,
  val totalCoinsAllocated: Int = 100,
  val isActive: Boolean = true,
  val createdAt: Long = System.currentTimeMillis()
)

enum class VerificationStatus {
  PENDING_AUDIT,
  VERIFIED_ACTIVE,
  FLAGGED_CHEATING
}

@Entity(tableName = "follow_verifications")
data class FollowVerificationEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val campaignId: Int,
  val campaignCreatorId: Int,
  val followerUserId: Int,
  val followerName: String,
  val followerHandle: String,
  val targetPlatform: SocialPlatform,
  val targetAccountHandle: String,
  val coinsEarned: Int,
  val status: VerificationStatus = VerificationStatus.VERIFIED_ACTIVE,
  val cheatReportReason: String? = null,
  val createdAt: Long = System.currentTimeMillis(),
  val verifiedAt: Long = System.currentTimeMillis()
)

// Task Verification System Enums & Entities
enum class TaskType(val displayName: String) {
  FOLLOW_SOCIAL("Follow Akun"),
  ENGAGE_CONTENT("Like & Interaksi"),
  VISIT_AND_EXPLORE("Kunjungi & Jelajahi"),
  SHARE_INVITE("Bagikan Link")
}

enum class VerificationMethod(val displayName: String, val description: String) {
  OFFICIAL_API(
    "API Resmi Platform",
    "Verifikasi langsung via token API resmi platform sosial media."
  ),
  CHALLENGE_TOKEN(
    "Challenge Token Server",
    "Validasi token penjelajahan dan durasi interaksi nyata di server."
  ),
  MANUAL_AUDIT(
    "Audit Bukti Alternatif",
    "Pemeriksaan handle dan bukti tangkapan layar jika API resmi platform belum terhubung."
  )
}

enum class TaskVerificationStatus(val label: String) {
  PENDING("Menunggu Verifikasi"),
  VERIFIED("Terverifikasi"),
  REJECTED("Ditolak")
}

@Entity(tableName = "tasks")
data class TaskItemEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val title: String,
  val taskType: TaskType,
  val platform: SocialPlatform,
  val targetHandle: String,
  val targetUrl: String,
  val category: String,
  val rewardCoins: Int,
  val verificationMethod: VerificationMethod,
  val instructions: String,
  val officialApiSupported: Boolean = false,
  val requiredDurationSeconds: Int = 0,
  val isActive: Boolean = true,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "task_verifications")
data class TaskVerificationRecordEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val taskId: Int,
  val userId: Int,
  val taskType: TaskType,
  val platform: SocialPlatform,
  val targetHandle: String,
  val verificationMethod: VerificationMethod,
  val submittedProof: String, // e.g. username, profile url, or challenge token
  val proofNotes: String? = null,
  val status: TaskVerificationStatus = TaskVerificationStatus.PENDING,
  val rejectionReason: String? = null,
  val rewardCoins: Int,
  val isRewardClaimed: Boolean = false,
  val submittedAt: Long = System.currentTimeMillis(),
  val verifiedAt: Long? = null,
  val apiAuditLog: String? = null
)

enum class TransactionType {
  WELCOME_BONUS,
  EARNED_FOLLOW,
  EARNED_TASK,
  EXCHANGED_CAMPAIGN,
  QRIS_PURCHASE,
  ADMIN_TOPUP,
  ADMIN_DEDUCT,
  CHEAT_PENALTY,
  DAILY_CHECKIN_BONUS
}

@Entity(tableName = "coin_transactions")
data class CoinTransactionEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val userId: Int,
  val amount: Int,
  val type: TransactionType,
  val description: String,
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "qris_orders")
data class QrisOrderEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val userId: Int,
  val packageTitle: String,
  val coinsGiven: Int,
  val priceIdr: Long,
  val transactionRef: String,
  val status: String = "SUCCESS",
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(
  tableName = "daily_check_ins",
  indices = [Index(value = ["userId", "checkInDateKey"], unique = true)]
)
data class DailyCheckInEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val userId: Int,
  val checkInDateKey: String, // Calendar date in local timezone e.g. "2026-10-11"
  val streakDay: Int,         // Consecutive day counter (1..7)
  val rewardCoins: Int,       // Coins awarded for this check-in
  val timestamp: Long = System.currentTimeMillis(),
  val timezoneId: String = "WIB",
  val timeFormatted: String = ""
)

