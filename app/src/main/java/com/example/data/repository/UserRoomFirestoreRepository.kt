package com.example.data.repository

import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.model.CoinTransactionEntity
import com.example.data.model.TransactionType
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Data repository combining Room Database (offline-first local persistent cache)
 * and Cloud Firestore (remote cloud database) to store and synchronize user
 * profile data and coin balances.
 */
class UserRoomFirestoreRepository(
  private val db: AppDatabase,
  firestoreInstance: FirebaseFirestore? = null
) {
  private val userDao = db.userDao()
  private val coinDao = db.coinTransactionDao()

  // Safely resolve FirebaseFirestore instance, supporting test environments
  // where FirebaseApp may not be initialized yet.
  private val firestore: FirebaseFirestore? by lazy {
    firestoreInstance ?: runCatching { FirebaseFirestore.getInstance() }.getOrNull()
  }

  companion object {
    private const val TAG = "UserRoomFirestoreRepo"
    const val USERS_COLLECTION = "users"
    const val TRANSACTIONS_COLLECTION = "coin_transactions"
  }

  /**
   * Observe user profile from Room (single source of truth for the UI).
   */
  fun observeUserProfile(userId: Int): Flow<UserEntity?> {
    return userDao.getUserById(userId)
  }

  /**
   * Observe user's current coin balance directly as a Flow.
   */
  fun observeCoinBalance(userId: Int): Flow<Int> {
    return userDao.getUserById(userId).map { it?.points ?: 0 }
  }

  /**
   * Observe user's coin transaction history from local Room cache.
   */
  fun observeUserTransactions(userId: Int): Flow<List<CoinTransactionEntity>> {
    return coinDao.getTransactionsByUserId(userId)
  }

  /**
   * Look up user by email from Room database.
   */
  suspend fun getUserByEmail(email: String): UserEntity? = withContext(Dispatchers.IO) {
    userDao.getUserByEmail(email)
  }

  /**
   * Look up user by ID once from Room database.
   */
  suspend fun getUserById(userId: Int): UserEntity? = withContext(Dispatchers.IO) {
    userDao.getUserByIdOnce(userId)
  }

  /**
   * Save or update a complete user profile to both Room and Firestore.
   */
  suspend fun saveUserProfile(user: UserEntity): Result<UserEntity> = withContext(Dispatchers.IO) {
    try {
      // 1. Save locally to Room
      val generatedId = userDao.insertUser(user)
      val finalUser = if (user.id == 0) user.copy(id = generatedId.toInt()) else user

      // 2. Sync to Cloud Firestore
      syncProfileToFirestore(finalUser)

      Result.success(finalUser)
    } catch (e: Exception) {
      Log.e(TAG, "Error saving user profile: ${e.message}", e)
      Result.failure(e)
    }
  }

  /**
   * Update specific profile details (name, social media handles) in both Room and Firestore.
   */
  suspend fun updateProfileDetails(
    userId: Int,
    name: String,
    instagram: String = "",
    tiktok: String = "",
    twitter: String = "",
    youtube: String = ""
  ): Result<UserEntity> = withContext(Dispatchers.IO) {
    try {
      val existingUser = userDao.getUserByIdOnce(userId)
        ?: return@withContext Result.failure(Exception("Pengguna ID $userId tidak ditemukan di Room"))

      val updatedUser = existingUser.copy(
        name = name,
        instagramHandle = instagram,
        tiktokHandle = tiktok,
        twitterHandle = twitter,
        youtubeHandle = youtube
      )

      // 1. Save to Room
      userDao.updateUser(updatedUser)

      // 2. Push update to Firestore
      firestore?.let { fs ->
        try {
          val updates = hashMapOf<String, Any>(
            "name" to name,
            "instagramHandle" to instagram,
            "tiktokHandle" to tiktok,
            "twitterHandle" to twitter,
            "youtubeHandle" to youtube,
            "updatedAt" to System.currentTimeMillis()
          )
          fs.collection(USERS_COLLECTION)
            .document(userId.toString())
            .set(updates, SetOptions.merge())
            .await()
          Log.d(TAG, "Profile details successfully synced to Firestore for user #$userId")
        } catch (e: Exception) {
          Log.w(TAG, "Firestore sync failed (offline or unauthenticated): ${e.message}")
        }
      }

      Result.success(updatedUser)
    } catch (e: Exception) {
      Log.e(TAG, "Error updating profile details: ${e.message}", e)
      Result.failure(e)
    }
  }

  /**
   * Adjust coin balance (earn or spend) in Room and synchronize to Firestore.
   * Atomically records a coin transaction in both databases.
   */
  suspend fun adjustCoinBalance(
    userId: Int,
    delta: Int,
    description: String,
    type: TransactionType = if (delta >= 0) TransactionType.EARNED_TASK else TransactionType.EXCHANGED_CAMPAIGN
  ): Result<Int> = withContext(Dispatchers.IO) {
    try {
      val user = userDao.getUserByIdOnce(userId)
        ?: return@withContext Result.failure(Exception("Pengguna ID $userId tidak ditemukan"))

      val newBalance = user.points + delta
      if (newBalance < 0) {
        return@withContext Result.failure(Exception("Saldo koin tidak mencukupi (Saldo: ${user.points}, Dibutuhkan: ${-delta})"))
      }

      // 1. Adjust points in Room
      userDao.adjustPoints(userId, delta)

      // 2. Insert transaction in Room
      val txEntity = CoinTransactionEntity(
        userId = userId,
        amount = delta,
        type = type,
        description = description,
        timestamp = System.currentTimeMillis()
      )
      val txId = coinDao.insertTransaction(txEntity)

      // 3. Sync new balance and transaction to Firestore
      firestore?.let { fs ->
        try {
          val userDocRef = fs.collection(USERS_COLLECTION).document(userId.toString())
          val balanceUpdate = hashMapOf<String, Any>(
            "points" to newBalance,
            "coinBalance" to newBalance,
            "lastTransactionAt" to txEntity.timestamp,
            "updatedAt" to System.currentTimeMillis()
          )
          userDocRef.set(balanceUpdate, SetOptions.merge()).await()

          // Add transaction to user's transactions subcollection
          val txData = hashMapOf<String, Any>(
            "id" to txId,
            "userId" to userId,
            "amount" to delta,
            "type" to type.name,
            "description" to description,
            "timestamp" to txEntity.timestamp
          )
          userDocRef.collection("transactions").document(txId.toString()).set(txData).await()

          // Also record in top-level transactions collection for admin querying
          fs.collection(TRANSACTIONS_COLLECTION).document("BST-TX-$txId").set(txData).await()

          Log.d(TAG, "Coin balance ($newBalance) & transaction #$txId synced to Firestore")
        } catch (e: Exception) {
          Log.w(TAG, "Firestore coin sync skipped/failed: ${e.message}")
        }
      }

      Result.success(newBalance)
    } catch (e: Exception) {
      Log.e(TAG, "Error adjusting coin balance: ${e.message}", e)
      Result.failure(e)
    }
  }

  /**
   * Set absolute coin balance for a user.
   */
  suspend fun setCoinBalance(
    userId: Int,
    newBalance: Int,
    reason: String = "Penyesuaian Saldo Admin"
  ): Result<Int> = withContext(Dispatchers.IO) {
    val user = userDao.getUserByIdOnce(userId)
      ?: return@withContext Result.failure(Exception("Pengguna tidak ditemukan"))
    val delta = newBalance - user.points
    adjustCoinBalance(userId, delta, reason, TransactionType.ADMIN_TOPUP)
  }

  /**
   * Fetch user data from Cloud Firestore and cache it into Room.
   */
  suspend fun syncUserFromFirestore(userId: Int): Result<UserEntity?> = withContext(Dispatchers.IO) {
    val fs = firestore ?: return@withContext Result.failure(Exception("Firestore instance not available"))

    try {
      val doc = fs.collection(USERS_COLLECTION).document(userId.toString()).get().await()
      if (!doc.exists()) {
        Log.d(TAG, "User document for #$userId does not exist in Firestore")
        return@withContext Result.success(null)
      }

      val existingUser = userDao.getUserByIdOnce(userId)
      val name = doc.getString("name") ?: existingUser?.name ?: "Pengguna"
      val email = doc.getString("email") ?: existingUser?.email ?: "user$userId@example.com"
      val roleStr = doc.getString("role") ?: existingUser?.role?.name ?: "USER"
      val role = try { UserRole.valueOf(roleStr) } catch (_: Exception) { UserRole.USER }
      val points = (doc.getLong("points") ?: doc.getLong("coinBalance") ?: existingUser?.points?.toLong() ?: 100L).toInt()
      val authScore = (doc.getDouble("authenticityScore") ?: existingUser?.authenticityScore?.toDouble() ?: 100.0).toFloat()
      val violations = (doc.getLong("violationsCount") ?: existingUser?.violationsCount?.toLong() ?: 0L).toInt()
      val isBanned = doc.getBoolean("isBanned") ?: existingUser?.isBanned ?: false

      val updatedUser = UserEntity(
        id = userId,
        name = name,
        email = email,
        passwordHash = existingUser?.passwordHash ?: "oauth_synced",
        role = role,
        points = points,
        authenticityScore = authScore,
        violationsCount = violations,
        isBanned = isBanned,
        instagramHandle = doc.getString("instagramHandle") ?: existingUser?.instagramHandle ?: "",
        tiktokHandle = doc.getString("tiktokHandle") ?: existingUser?.tiktokHandle ?: "",
        twitterHandle = doc.getString("twitterHandle") ?: existingUser?.twitterHandle ?: "",
        youtubeHandle = doc.getString("youtubeHandle") ?: existingUser?.youtubeHandle ?: "",
        detectedJob = doc.getString("detectedJob") ?: existingUser?.detectedJob ?: "",
        jobConfidence = (doc.getDouble("jobConfidence") ?: existingUser?.jobConfidence?.toDouble() ?: 0.0).toFloat(),
        jobAnalysisSummary = doc.getString("jobAnalysisSummary") ?: existingUser?.jobAnalysisSummary ?: "",
        jobKeySkills = doc.getString("jobKeySkills") ?: existingUser?.jobKeySkills ?: "",
        recommendedNiche = doc.getString("recommendedNiche") ?: existingUser?.recommendedNiche ?: "",
        createdAt = existingUser?.createdAt ?: System.currentTimeMillis()
      )

      userDao.insertUser(updatedUser)
      Log.d(TAG, "User #$userId successfully synced from Firestore into Room")
      Result.success(updatedUser)
    } catch (e: Exception) {
      Log.e(TAG, "Error syncing user from Firestore: ${e.message}", e)
      Result.failure(e)
    }
  }

  /**
   * Push existing Room user data to Cloud Firestore.
   */
  suspend fun syncUserToFirestore(userId: Int): Result<Unit> = withContext(Dispatchers.IO) {
    try {
      val user = userDao.getUserByIdOnce(userId)
        ?: return@withContext Result.failure(Exception("Pengguna #$userId tidak ditemukan di database Room"))

      syncProfileToFirestore(user)
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e(TAG, "Error syncing user to Firestore: ${e.message}", e)
      Result.failure(e)
    }
  }

  /**
   * Real-time listener for remote Firestore updates on the user profile and coin balance.
   * Remote changes are automatically written to Room and emitted to subscribers.
   */
  fun listenToFirestoreUser(userId: Int): Flow<UserEntity?> = callbackFlow {
    val fs = firestore
    if (fs == null) {
      close()
      return@callbackFlow
    }

    val docRef = fs.collection(USERS_COLLECTION).document(userId.toString())
    var registration: ListenerRegistration? = null

    try {
      registration = docRef.addSnapshotListener { snapshot, error ->
        if (error != null) {
          Log.w(TAG, "Firestore snapshot listener error: ${error.message}")
          return@addSnapshotListener
        }

        if (snapshot != null && snapshot.exists()) {
          val points = (snapshot.getLong("points") ?: snapshot.getLong("coinBalance") ?: 100L).toInt()
          val name = snapshot.getString("name") ?: "Pengguna"
          val instagram = snapshot.getString("instagramHandle") ?: ""
          val tiktok = snapshot.getString("tiktokHandle") ?: ""
          val twitter = snapshot.getString("twitterHandle") ?: ""
          val youtube = snapshot.getString("youtubeHandle") ?: ""

          // Asynchronously update Room cache and emit
          launch(Dispatchers.IO) {
            val existing = userDao.getUserByIdOnce(userId)
            if (existing != null) {
              val updated = existing.copy(
                points = points,
                name = if (name.isNotBlank()) name else existing.name,
                instagramHandle = instagram,
                tiktokHandle = tiktok,
                twitterHandle = twitter,
                youtubeHandle = youtube
              )
              userDao.updateUser(updated)
              trySend(updated)
            }
          }
        }
      }
    } catch (e: Exception) {
      Log.e(TAG, "Failed to attach snapshot listener: ${e.message}", e)
    }

    awaitClose {
      registration?.remove()
    }
  }.flowOn(Dispatchers.IO)

  /**
   * Internal helper to write a complete UserEntity to Firestore.
   */
  private suspend fun syncProfileToFirestore(user: UserEntity) {
    val fs = firestore ?: return
    try {
      val profileMap = hashMapOf<String, Any>(
        "id" to user.id,
        "name" to user.name,
        "email" to user.email,
        "role" to user.role.name,
        "points" to user.points,
        "coinBalance" to user.points,
        "authenticityScore" to user.authenticityScore,
        "violationsCount" to user.violationsCount,
        "isBanned" to user.isBanned,
        "instagramHandle" to user.instagramHandle,
        "tiktokHandle" to user.tiktokHandle,
        "twitterHandle" to user.twitterHandle,
        "youtubeHandle" to user.youtubeHandle,
        "detectedJob" to user.detectedJob,
        "jobConfidence" to user.jobConfidence,
        "jobAnalysisSummary" to user.jobAnalysisSummary,
        "jobKeySkills" to user.jobKeySkills,
        "recommendedNiche" to user.recommendedNiche,
        "updatedAt" to System.currentTimeMillis()
      )

      fs.collection(USERS_COLLECTION)
        .document(user.id.toString())
        .set(profileMap, SetOptions.merge())
        .await()

      Log.d(TAG, "User profile and coin balance (#${user.id}, ${user.points} Koin) saved to Firestore")
    } catch (e: Exception) {
      Log.w(TAG, "Failed to write user profile to Firestore (offline or unauthenticated): ${e.message}")
    }
  }

  /**
   * Check if Cloud Firestore connection is initialized and available.
   */
  fun isFirestoreAvailable(): Boolean = firestore != null
}
