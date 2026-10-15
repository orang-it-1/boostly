package com.example.data.remote.laravel

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Standard API wrapper matching standard Laravel Response::json([ ... ]) structure.
 */
@JsonClass(generateAdapter = true)
data class LaravelApiResponse<T>(
  @Json(name = "status") val status: String = "success",
  @Json(name = "message") val message: String = "",
  @Json(name = "data") val data: T? = null,
  @Json(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class LaravelHealthDto(
  @Json(name = "status") val status: String = "online",
  @Json(name = "framework") val framework: String = "Laravel 11.x",
  @Json(name = "app_name") val appName: String = "Boostly Laravel API",
  @Json(name = "database_status") val databaseStatus: String = "connected",
  @Json(name = "database_driver") val databaseDriver: String = "mysql",
  @Json(name = "server_time") val serverTime: Long = System.currentTimeMillis(),
  @Json(name = "timezone") val timezone: String = "Asia/Jakarta (WIB)",
  @Json(name = "api_version") val apiVersion: String = "v1.0"
)

@JsonClass(generateAdapter = true)
data class LaravelAuthResponse(
  @Json(name = "token") val token: String = "",
  @Json(name = "token_type") val tokenType: String = "Bearer",
  @Json(name = "user") val user: LaravelUserDto? = null
)

@JsonClass(generateAdapter = true)
data class LaravelUserDto(
  @Json(name = "id") val id: Int = 0,
  @Json(name = "name") val name: String,
  @Json(name = "email") val email: String,
  @Json(name = "role") val role: String = "USER",
  @Json(name = "points") val points: Int = 100,
  @Json(name = "authenticity_score") val authenticityScore: Float = 100.0f,
  @Json(name = "violations_count") val violationsCount: Int = 0,
  @Json(name = "is_banned") val isBanned: Boolean = false,
  @Json(name = "instagram_handle") val instagramHandle: String = "",
  @Json(name = "tiktok_handle") val tiktokHandle: String = "",
  @Json(name = "twitter_handle") val twitterHandle: String = "",
  @Json(name = "youtube_handle") val youtubeHandle: String = "",
  @Json(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class LaravelCampaignDto(
  @Json(name = "id") val id: Int = 0,
  @Json(name = "creator_id") val creatorId: Int,
  @Json(name = "creator_name") val creatorName: String,
  @Json(name = "platform") val platform: String,
  @Json(name = "target_url") val targetUrl: String,
  @Json(name = "account_handle") val accountHandle: String,
  @Json(name = "category") val category: String,
  @Json(name = "target_followers") val targetFollowers: Int,
  @Json(name = "current_followers") val currentFollowers: Int = 0,
  @Json(name = "coin_reward_per_follow") val coinRewardPerFollow: Int = 10,
  @Json(name = "total_coins_allocated") val totalCoinsAllocated: Int = 100,
  @Json(name = "is_active") val isActive: Boolean = true,
  @Json(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class LaravelTransactionDto(
  @Json(name = "id") val id: Int = 0,
  @Json(name = "user_id") val userId: Int,
  @Json(name = "amount") val amount: Int,
  @Json(name = "type") val type: String,
  @Json(name = "description") val description: String,
  @Json(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class LaravelDailyCheckInDto(
  @Json(name = "id") val id: Int = 0,
  @Json(name = "user_id") val userId: Int,
  @Json(name = "check_in_date_key") val checkInDateKey: String,
  @Json(name = "streak_day") val streakDay: Int,
  @Json(name = "reward_coins") val rewardCoins: Int,
  @Json(name = "timestamp") val timestamp: Long = System.currentTimeMillis(),
  @Json(name = "timezone_id") val timezoneId: String = "WIB",
  @Json(name = "time_formatted") val timeFormatted: String = ""
)

@JsonClass(generateAdapter = true)
data class LaravelSyncPayload(
  @Json(name = "users") val users: List<LaravelUserDto> = emptyList(),
  @Json(name = "campaigns") val campaigns: List<LaravelCampaignDto> = emptyList(),
  @Json(name = "transactions") val transactions: List<LaravelTransactionDto> = emptyList(),
  @Json(name = "daily_check_ins") val dailyCheckIns: List<LaravelDailyCheckInDto> = emptyList(),
  @Json(name = "device_timestamp") val deviceTimestamp: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class LaravelSyncResultDto(
  @Json(name = "synced_users_count") val syncedUsersCount: Int = 0,
  @Json(name = "synced_campaigns_count") val syncedCampaignsCount: Int = 0,
  @Json(name = "synced_transactions_count") val syncedTransactionsCount: Int = 0,
  @Json(name = "synced_checkins_count") val syncedCheckInsCount: Int = 0,
  @Json(name = "server_time") val serverTime: Long = System.currentTimeMillis(),
  @Json(name = "pulled_data") val pulledData: LaravelSyncPayload? = null
)

// ================= USER COINS CRUD DTOs =================
@JsonClass(generateAdapter = true)
data class CoinBalanceResponse(
  @Json(name = "user_id") val userId: Int,
  @Json(name = "points") val points: Int,
  @Json(name = "last_updated") val lastUpdated: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class CreateCoinTransactionRequest(
  @Json(name = "user_id") val userId: Int,
  @Json(name = "amount") val amount: Int,
  @Json(name = "type") val type: String,
  @Json(name = "description") val description: String
)

@JsonClass(generateAdapter = true)
data class UpdateCoinBalanceRequest(
  @Json(name = "delta_amount") val deltaAmount: Int,
  @Json(name = "reason") val reason: String = "Adjustment"
)

@JsonClass(generateAdapter = true)
data class CoinOperationResponse(
  @Json(name = "user_id") val userId: Int,
  @Json(name = "previous_balance") val previousBalance: Int = 0,
  @Json(name = "new_balance") val newBalance: Int,
  @Json(name = "amount_changed") val amountChanged: Int = 0,
  @Json(name = "message") val message: String = "Success"
)

// ================= FOLLOWS CRUD DTOs =================
@JsonClass(generateAdapter = true)
data class FollowRecordDto(
  @Json(name = "id") val id: Int = 0,
  @Json(name = "campaign_id") val campaignId: Int,
  @Json(name = "campaign_creator_id") val campaignCreatorId: Int = 0,
  @Json(name = "follower_user_id") val followerUserId: Int,
  @Json(name = "follower_name") val followerName: String = "",
  @Json(name = "follower_handle") val followerHandle: String = "",
  @Json(name = "target_platform") val targetPlatform: String = "INSTAGRAM",
  @Json(name = "target_account_handle") val targetAccountHandle: String = "",
  @Json(name = "coins_earned") val coinsEarned: Int = 10,
  @Json(name = "status") val status: String = "VERIFIED_ACTIVE",
  @Json(name = "cheat_report_reason") val cheatReportReason: String? = null,
  @Json(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
  @Json(name = "verified_at") val verifiedAt: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class CreateFollowRequest(
  @Json(name = "campaign_id") val campaignId: Int,
  @Json(name = "campaign_creator_id") val campaignCreatorId: Int = 0,
  @Json(name = "follower_user_id") val followerUserId: Int,
  @Json(name = "follower_name") val followerName: String,
  @Json(name = "follower_handle") val followerHandle: String,
  @Json(name = "target_platform") val targetPlatform: String,
  @Json(name = "target_account_handle") val targetAccountHandle: String,
  @Json(name = "coins_reward") val coinsReward: Int = 10
)

@JsonClass(generateAdapter = true)
data class UpdateFollowRequest(
  @Json(name = "status") val status: String,
  @Json(name = "cheat_report_reason") val cheatReportReason: String? = null
)

@JsonClass(generateAdapter = true)
data class DeleteResponse(
  @Json(name = "deleted_id") val deletedId: Int,
  @Json(name = "success") val success: Boolean = true,
  @Json(name = "message") val message: String = "Deleted successfully"
)
