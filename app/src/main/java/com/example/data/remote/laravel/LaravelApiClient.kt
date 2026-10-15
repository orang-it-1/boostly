package com.example.data.remote.laravel

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object LaravelApiClient {

  private var currentBaseUrl: String = "http://10.0.2.2:8000/api/"
  private var currentApiToken: String = ""

  private val moshi: Moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .build()

  private val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
  }

  private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
    .connectTimeout(6, TimeUnit.SECONDS)
    .readTimeout(10, TimeUnit.SECONDS)
    .writeTimeout(10, TimeUnit.SECONDS)
    .addInterceptor(loggingInterceptor)
    .addInterceptor { chain ->
      val original = chain.request()
      val requestBuilder = original.newBuilder()
        .header("Accept", "application/json")
        .header("User-Agent", "Boostly-Android-Client/1.0")

      if (currentApiToken.isNotBlank()) {
        requestBuilder.header("Authorization", "Bearer $currentApiToken")
      }
      chain.proceed(requestBuilder.build())
    }
    .build()

  private var cachedService: LaravelApiService? = null
  private var cachedUrl: String = ""

  fun getBaseUrl(): String = currentBaseUrl

  fun getApiToken(): String = currentApiToken

  fun setBaseUrl(url: String) {
    var formatted = url.trim()
    if (!formatted.startsWith("http://") && !formatted.startsWith("https://")) {
      formatted = "http://$formatted"
    }
    if (!formatted.endsWith("/")) {
      formatted = "$formatted/"
    }
    if (!formatted.contains("/api/")) {
      formatted = if (formatted.endsWith("/")) "${formatted}api/" else "$formatted/api/"
    }
    if (formatted != currentBaseUrl) {
      currentBaseUrl = formatted
      cachedService = null
    }
  }

  fun setApiToken(token: String) {
    currentApiToken = token.trim()
  }

  fun getApiService(): LaravelApiService {
    val url = currentBaseUrl
    if (cachedService != null && cachedUrl == url) {
      return cachedService!!
    }

    val retrofit = Retrofit.Builder()
      .baseUrl(url)
      .client(okHttpClient)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

    val service = retrofit.create(LaravelApiService::class.java)
    cachedService = service
    cachedUrl = url
    return service
  }

  private var cachedRetrofitApiService: com.example.data.remote.ApiService? = null

  fun getRetrofitApiService(): com.example.data.remote.ApiService {
    val url = currentBaseUrl
    if (cachedRetrofitApiService != null && cachedUrl == url) {
      return cachedRetrofitApiService!!
    }

    val retrofit = Retrofit.Builder()
      .baseUrl(url)
      .client(okHttpClient)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

    val service = retrofit.create(com.example.data.remote.ApiService::class.java)
    cachedRetrofitApiService = service
    cachedUrl = url
    return service
  }
}
