package com.example.data.remote.laravel

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Retrofit REST API interface defining standard Laravel API endpoints:
 * - GET  /api/health
 * - POST /api/auth/login
 * - POST /api/auth/register
 * - GET  /api/sync/pull
 * - POST /api/sync/push
 * - POST /api/daily-checkin
 * - POST /api/campaigns
 * - POST /api/coins/adjust
 */
typealias ApiService = com.example.data.remote.ApiService

interface LaravelApiService {

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

  @POST("coins/adjust")
  suspend fun adjustCoins(
    @Header("Authorization") bearerToken: String? = null,
    @Body body: Map<String, Any>
  ): Response<LaravelApiResponse<Map<String, Any>>>
}
