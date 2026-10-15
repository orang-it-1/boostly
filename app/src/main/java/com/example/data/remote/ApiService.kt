package com.example.data.remote

import com.example.data.remote.laravel.CoinBalanceResponse
import com.example.data.remote.laravel.CoinOperationResponse
import com.example.data.remote.laravel.CreateCoinTransactionRequest
import com.example.data.remote.laravel.CreateFollowRequest
import com.example.data.remote.laravel.DeleteResponse
import com.example.data.remote.laravel.FollowRecordDto
import com.example.data.remote.laravel.LaravelApiResponse
import com.example.data.remote.laravel.LaravelAuthResponse
import com.example.data.remote.laravel.LaravelCampaignDto
import com.example.data.remote.laravel.LaravelDailyCheckInDto
import com.example.data.remote.laravel.LaravelHealthDto
import com.example.data.remote.laravel.LaravelSyncPayload
import com.example.data.remote.laravel.LaravelSyncResultDto
import com.example.data.remote.laravel.LaravelTransactionDto
import com.example.data.remote.laravel.UpdateCoinBalanceRequest
import com.example.data.remote.laravel.UpdateFollowRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface named 'ApiService' to map endpoints for connecting
 * to the Laravel backend and handling CRUD operations for user coins and follows.
 */
interface ApiService {

  // ================= 1. HEALTH & BACKEND STATUS =================
  @GET("health")
  suspend fun checkHealth(
    @Header("Authorization") bearerToken: String? = null
  ): Response<LaravelApiResponse<LaravelHealthDto>>

  @POST("auth/login")
  suspend fun login(
    @Body credentials: Map<String, String>
  ): Response<LaravelApiResponse<LaravelAuthResponse>>

  @POST("auth/register")
  suspend fun register(
    @Body data: Map<String, String>
  ): Response<LaravelApiResponse<LaravelAuthResponse>>

  // ================= 2. USER COINS CRUD OPERATIONS =================

  /**
   * CREATE: Record/insert a new coin transaction in Laravel database.
   * Endpoint: POST /api/coins/transactions
   */
  @POST("coins/transactions")
  suspend fun createCoinTransaction(
    @Header("Authorization") bearerToken: String? = null,
    @Body request: CreateCoinTransactionRequest
  ): Response<LaravelApiResponse<LaravelTransactionDto>>

  /**
   * READ: Get current user coin balance.
   * Endpoint: GET /api/users/{userId}/coins
   */
  @GET("users/{userId}/coins")
  suspend fun getUserCoinBalance(
    @Path("userId") userId: Int,
    @Header("Authorization") bearerToken: String? = null
  ): Response<LaravelApiResponse<CoinBalanceResponse>>

  /**
   * READ: Get list of coin transactions for a user.
   * Endpoint: GET /api/users/{userId}/coins/transactions
   */
  @GET("users/{userId}/coins/transactions")
  suspend fun getUserCoinTransactions(
    @Path("userId") userId: Int,
    @Header("Authorization") bearerToken: String? = null
  ): Response<LaravelApiResponse<List<LaravelTransactionDto>>>

  /**
   * READ: Get all coin transactions across system (Admin/Audit).
   * Endpoint: GET /api/coins/transactions
   */
  @GET("coins/transactions")
  suspend fun getAllCoinTransactions(
    @Header("Authorization") bearerToken: String? = null,
    @Query("limit") limit: Int = 50
  ): Response<LaravelApiResponse<List<LaravelTransactionDto>>>

  /**
   * READ: Get single coin transaction detail by ID.
   * Endpoint: GET /api/coins/transactions/{id}
   */
  @GET("coins/transactions/{id}")
  suspend fun getCoinTransactionById(
    @Path("id") transactionId: Int,
    @Header("Authorization") bearerToken: String? = null
  ): Response<LaravelApiResponse<LaravelTransactionDto>>

  /**
   * UPDATE: Adjust/update user's coin balance (+/- delta).
   * Endpoint: PUT /api/users/{userId}/coins
   */
  @PUT("users/{userId}/coins")
  suspend fun updateUserCoins(
    @Path("userId") userId: Int,
    @Body request: UpdateCoinBalanceRequest,
    @Header("Authorization") bearerToken: String? = null
  ): Response<LaravelApiResponse<CoinOperationResponse>>

  /**
   * UPDATE: Admin coin adjustment endpoint.
   * Endpoint: POST /api/coins/adjust
   */
  @POST("coins/adjust")
  suspend fun adjustCoins(
    @Body body: Map<String, Any>,
    @Header("Authorization") bearerToken: String? = null
  ): Response<LaravelApiResponse<CoinOperationResponse>>

  /**
   * DELETE: Revert or delete a coin transaction record by ID.
   * Endpoint: DELETE /api/coins/transactions/{id}
   */
  @DELETE("coins/transactions/{id}")
  suspend fun deleteCoinTransaction(
    @Path("id") transactionId: Int,
    @Header("Authorization") bearerToken: String? = null
  ): Response<LaravelApiResponse<DeleteResponse>>

  // ================= 3. FOLLOWS CRUD OPERATIONS =================

  /**
   * CREATE: Create/record a new follow interaction.
   * Endpoint: POST /api/follows
   */
  @POST("follows")
  suspend fun createFollow(
    @Body request: CreateFollowRequest,
    @Header("Authorization") bearerToken: String? = null
  ): Response<LaravelApiResponse<FollowRecordDto>>

  /**
   * CREATE: Submit follow proof for verification & coin claim.
   * Endpoint: POST /api/follows/verify
   */
  @POST("follows/verify")
  suspend fun verifyAndClaimFollow(
    @Body request: CreateFollowRequest,
    @Header("Authorization") bearerToken: String? = null
  ): Response<LaravelApiResponse<FollowRecordDto>>

  /**
   * READ: Get all follow verification records.
   * Endpoint: GET /api/follows
   */
  @GET("follows")
  suspend fun getAllFollows(
    @Header("Authorization") bearerToken: String? = null,
    @Query("status") status: String? = null
  ): Response<LaravelApiResponse<List<FollowRecordDto>>>

  /**
   * READ: Get single follow record by ID.
   * Endpoint: GET /api/follows/{id}
   */
  @GET("follows/{id}")
  suspend fun getFollowById(
    @Path("id") followId: Int,
    @Header("Authorization") bearerToken: String? = null
  ): Response<LaravelApiResponse<FollowRecordDto>>

  /**
   * READ: Get follow history submitted by a specific user.
   * Endpoint: GET /api/users/{userId}/follows
   */
  @GET("users/{userId}/follows")
  suspend fun getFollowsByUser(
    @Path("userId") userId: Int,
    @Header("Authorization") bearerToken: String? = null
  ): Response<LaravelApiResponse<List<FollowRecordDto>>>

  /**
   * READ: Get followers received for a creator's campaign.
   * Endpoint: GET /api/campaigns/{campaignId}/follows
   */
  @GET("campaigns/{campaignId}/follows")
  suspend fun getFollowsByCampaign(
    @Path("campaignId") campaignId: Int,
    @Header("Authorization") bearerToken: String? = null
  ): Response<LaravelApiResponse<List<FollowRecordDto>>>

  /**
   * UPDATE: Update status of a follow record (e.g. VERIFIED_ACTIVE or FLAGGED_CHEATING).
   * Endpoint: PUT /api/follows/{id}
   */
  @PUT("follows/{id}")
  suspend fun updateFollowStatus(
    @Path("id") followId: Int,
    @Body request: UpdateFollowRequest,
    @Header("Authorization") bearerToken: String? = null
  ): Response<LaravelApiResponse<FollowRecordDto>>

  /**
   * DELETE: Delete/unfollow record by ID.
   * Endpoint: DELETE /api/follows/{id}
   */
  @DELETE("follows/{id}")
  suspend fun deleteFollow(
    @Path("id") followId: Int,
    @Header("Authorization") bearerToken: String? = null
  ): Response<LaravelApiResponse<DeleteResponse>>

  // ================= 4. DATABASE SYNC & CAMPAIGNS =================

  @GET("sync/pull")
  suspend fun pullData(
    @Header("Authorization") bearerToken: String? = null,
    @Query("since") sinceTimestamp: Long = 0
  ): Response<LaravelApiResponse<LaravelSyncPayload>>

  @POST("sync/push")
  suspend fun pushData(
    @Header("Authorization") bearerToken: String? = null,
    @Body payload: LaravelSyncPayload
  ): Response<LaravelApiResponse<LaravelSyncResultDto>>

  @POST("daily-checkin")
  suspend fun submitDailyCheckIn(
    @Header("Authorization") bearerToken: String? = null,
    @Body checkIn: LaravelDailyCheckInDto
  ): Response<LaravelApiResponse<Map<String, Any>>>

  @POST("campaigns")
  suspend fun createCampaign(
    @Header("Authorization") bearerToken: String? = null,
    @Body campaign: LaravelCampaignDto
  ): Response<LaravelApiResponse<LaravelCampaignDto>>
}
