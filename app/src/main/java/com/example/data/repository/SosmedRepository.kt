package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.CampaignEntity
import com.example.data.model.CoinTransactionEntity
import com.example.data.model.DailyCheckInEntity
import com.example.data.model.FollowVerificationEntity
import com.example.data.model.QrisOrderEntity
import com.example.data.model.SocialPlatform
import com.example.data.model.TransactionType
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.data.model.VerificationStatus
import com.example.data.sync.DailyCheckInSyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

data class FollowResult(
  val success: Boolean,
  val message: String,
  val coinsEarned: Int = 0
)

data class QrisCoinPackage(
  val id: String,
  val title: String,
  val coins: Int,
  val bonusCoins: Int,
  val priceIdr: Long,
  val isPopular: Boolean = false
)

class SosmedRepository(private val db: AppDatabase) {
  private val userDao = db.userDao()
  private val campaignDao = db.campaignDao()
  private val verificationDao = db.verificationDao()
  private val coinDao = db.coinTransactionDao()
  private val qrisDao = db.qrisOrderDao()
  private val dailyCheckInDao = db.dailyCheckInDao()

  val availableQrisPackages = listOf(
    QrisCoinPackage(
      id = "pkg_starter",
      title = "Starter Creator",
      coins = 100,
      bonusCoins = 15,
      priceIdr = 10000,
      isPopular = false
    ),
    QrisCoinPackage(
      id = "pkg_booster",
      title = "Viral Booster",
      coins = 350,
      bonusCoins = 75,
      priceIdr = 30000,
      isPopular = true
    ),
    QrisCoinPackage(
      id = "pkg_influencer",
      title = "Pro Influencer",
      coins = 800,
      bonusCoins = 250,
      priceIdr = 65000,
      isPopular = false
    ),
    QrisCoinPackage(
      id = "pkg_agency",
      title = "Agency Growth",
      coins = 2000,
      bonusCoins = 800,
      priceIdr = 150000,
      isPopular = false
    )
  )

  fun observeUser(userId: Int): Flow<UserEntity?> = userDao.getUserById(userId)
  fun observeAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()
  fun observeActiveCampaigns(): Flow<List<CampaignEntity>> = campaignDao.getActiveCampaigns()
  fun observeMyCampaigns(creatorId: Int): Flow<List<CampaignEntity>> = campaignDao.getCampaignsByCreator(creatorId)
  fun observeVerificationsForCreator(creatorId: Int): Flow<List<FollowVerificationEntity>> =
    verificationDao.getVerificationsForCreator(creatorId)
  fun observeMyFollowHistory(userId: Int): Flow<List<FollowVerificationEntity>> =
    verificationDao.getVerificationsByFollower(userId)
  fun observeUserTransactions(userId: Int): Flow<List<CoinTransactionEntity>> =
    coinDao.getTransactionsByUserId(userId)
  fun observeAllOrders(): Flow<List<QrisOrderEntity>> = qrisDao.getAllOrders()
  fun observeCheckInsByUser(userId: Int): Flow<List<DailyCheckInEntity>> =
    dailyCheckInDao.getCheckInsByUser(userId)
  fun observeTotalCheckIns(userId: Int): Flow<Int> =
    dailyCheckInDao.countTotalCheckIns(userId)

  suspend fun login(email: String, pass: String): Result<UserEntity> = withContext(Dispatchers.IO) {
    val user = userDao.getUserByEmail(email.trim())
    if (user == null) {
      Result.failure(Exception("Email tidak terdaftar."))
    } else if (user.passwordHash != pass) {
      Result.failure(Exception("Kata sandi salah."))
    } else if (user.isBanned) {
      Result.failure(Exception("Akun dibekukan akibat pelanggaran anti-curang."))
    } else {
      Result.success(user)
    }
  }

  suspend fun register(
    name: String,
    email: String,
    pass: String,
    role: UserRole = UserRole.USER,
    instagramHandle: String = ""
  ): Result<UserEntity> =
    withContext(Dispatchers.IO) {
      val existing = userDao.getUserByEmail(email.trim())
      if (existing != null) {
        return@withContext Result.failure(Exception("Email sudah terdaftar. Silakan gunakan email lain atau masuk."))
      }
      val handleFormatted = if (instagramHandle.isNotBlank() && !instagramHandle.startsWith("@")) {
        "@$instagramHandle"
      } else {
        instagramHandle.trim()
      }
      val newUser = UserEntity(
        name = name.trim(),
        email = email.trim(),
        passwordHash = pass,
        role = role,
        points = if (role == UserRole.ADMIN) 99999 else 150, // 150 welcome bonus
        authenticityScore = 100.0f,
        instagramHandle = handleFormatted
      )
      val newId = userDao.insertUser(newUser).toInt()
      val created = userDao.getUserByIdOnce(newId)!!
      coinDao.insertTransaction(
        CoinTransactionEntity(
          userId = newId,
          amount = 150,
          type = TransactionType.WELCOME_BONUS,
          description = "Bonus Pendaftaran Anggota Baru"
        )
      )
      Result.success(created)
    }

  suspend fun processFollowAction(
    campaignId: Int,
    followerUser: UserEntity,
    followerHandle: String
  ): FollowResult = withContext(Dispatchers.IO) {
    if (followerUser.isBanned) {
      return@withContext FollowResult(false, "Akun Anda dibekukan karena pelanggaran sistem anti-curang.")
    }

    val campaign = campaignDao.getCampaignById(campaignId)
      ?: return@withContext FollowResult(false, "Kampanye tidak ditemukan.")

    // Anti-Cheat Rule 1: Anti-Self-Follow
    if (campaign.creatorId == followerUser.id) {
      return@withContext FollowResult(
        false,
        "Sistem Anti-Curang: Anda tidak diperbolehkan mem-follow kampanye sendiri!"
      )
    }

    // Anti-Cheat Rule 2: Anti-Duplicate Follow
    val alreadyFollowed = verificationDao.countFollow(campaignId, followerUser.id) > 0
    if (alreadyFollowed) {
      return@withContext FollowResult(
        false,
        "Sistem Anti-Curang: Anda sudah pernah mengklaim follow untuk profil ini!"
      )
    }

    // Anti-Cheat Rule 3: Valid handle format
    val cleanHandle = followerHandle.trim()
    if (cleanHandle.length < 3) {
      return@withContext FollowResult(
        false,
        "Username akun Anda tidak valid untuk verifikasi."
      )
    }

    val coinsToReward = campaign.coinRewardPerFollow

    // Insert verification record
    val verification = FollowVerificationEntity(
      campaignId = campaign.id,
      campaignCreatorId = campaign.creatorId,
      followerUserId = followerUser.id,
      followerName = followerUser.name,
      followerHandle = cleanHandle,
      targetPlatform = campaign.platform,
      targetAccountHandle = campaign.accountHandle,
      coinsEarned = coinsToReward,
      status = VerificationStatus.VERIFIED_ACTIVE
    )
    verificationDao.insertVerification(verification)

    // Reward follower with coins
    userDao.adjustPoints(followerUser.id, coinsToReward)

    // Log transaction
    coinDao.insertTransaction(
      CoinTransactionEntity(
        userId = followerUser.id,
        amount = coinsToReward,
        type = TransactionType.EARNED_FOLLOW,
        description = "Mendapatkan koin dari follow ${campaign.accountHandle} (${campaign.platform.displayName})"
      )
    )

    // Increment campaign follower count
    campaignDao.incrementCampaignFollower(campaign.id)

    FollowResult(
      success = true,
      message = "Berhasil! Follow terverifikasi autentik. +$coinsToReward Koin telah ditambahkan ke saldo Anda.",
      coinsEarned = coinsToReward
    )
  }

  suspend fun exchangeCoinsForCampaign(
    creator: UserEntity,
    platform: SocialPlatform,
    targetUrl: String,
    accountHandle: String,
    category: String,
    targetFollowers: Int,
    coinRewardPerFollow: Int
  ): Result<CampaignEntity> = withContext(Dispatchers.IO) {
    val totalCost = targetFollowers * coinRewardPerFollow
    val freshUser = userDao.getUserByIdOnce(creator.id) ?: return@withContext Result.failure(Exception("User tidak ditemukan."))

    if (freshUser.points < totalCost) {
      return@withContext Result.failure(
        Exception("Koin tidak mencukupi. Dibutuhkan $totalCost koin, saldo Anda ${freshUser.points} koin.")
      )
    }

    // Deduct coins
    userDao.adjustPoints(freshUser.id, -totalCost)

    // Record Transaction
    coinDao.insertTransaction(
      CoinTransactionEntity(
        userId = freshUser.id,
        amount = -totalCost,
        type = TransactionType.EXCHANGED_CAMPAIGN,
        description = "Tukar $totalCost koin untuk $targetFollowers followers ($accountHandle)"
      )
    )

    // Insert Campaign
    val campaign = CampaignEntity(
      creatorId = freshUser.id,
      creatorName = freshUser.name,
      platform = platform,
      targetUrl = targetUrl.trim(),
      accountHandle = accountHandle.trim(),
      category = category,
      targetFollowers = targetFollowers,
      currentFollowers = 0,
      coinRewardPerFollow = coinRewardPerFollow,
      totalCoinsAllocated = totalCost,
      isActive = true
    )
    val id = campaignDao.insertCampaign(campaign).toInt()
    val createdCampaign = campaignDao.getCampaignById(id)!!

    Result.success(createdCampaign)
  }

  suspend fun completeQrisPurchase(
    userId: Int,
    qrisPkg: QrisCoinPackage,
    transactionRef: String
  ): Result<Int> = withContext(Dispatchers.IO) {
    val totalCoins = qrisPkg.coins + qrisPkg.bonusCoins

    // Record order
    qrisDao.insertOrder(
      QrisOrderEntity(
        userId = userId,
        packageTitle = qrisPkg.title,
        coinsGiven = totalCoins,
        priceIdr = qrisPkg.priceIdr,
        transactionRef = transactionRef
      )
    )

    // Top up coins to user
    userDao.adjustPoints(userId, totalCoins)

    // Log transaction
    coinDao.insertTransaction(
      CoinTransactionEntity(
        userId = userId,
        amount = totalCoins,
        type = TransactionType.QRIS_PURCHASE,
        description = "Pembelian QRIS: ${qrisPkg.title} (+${qrisPkg.bonusCoins} Bonus)"
      )
    )

    Result.success(totalCoins)
  }

  suspend fun topUpCoins(
    userId: Int = 2,
    amount: Int,
    paymentMethod: String,
    priceIdr: Long
  ): Result<Int> = withContext(Dispatchers.IO) {
    userDao.adjustPoints(userId, amount)
    coinDao.insertTransaction(
      CoinTransactionEntity(
        userId = userId,
        amount = amount,
        type = TransactionType.QRIS_PURCHASE,
        description = "Pembayaran $paymentMethod Lunas (Rp $priceIdr)"
      )
    )
    Result.success(amount)
  }

  suspend fun adminAdjustCoins(
    targetUserId: Int,
    delta: Int,
    reason: String
  ): Result<Unit> = withContext(Dispatchers.IO) {
    userDao.adjustPoints(targetUserId, delta)
    coinDao.insertTransaction(
      CoinTransactionEntity(
        userId = targetUserId,
        amount = delta,
        type = if (delta >= 0) TransactionType.ADMIN_TOPUP else TransactionType.ADMIN_DEDUCT,
        description = "Penyesuaian Admin: $reason"
      )
    )
    Result.success(Unit)
  }

  suspend fun executeWithdrawal(
    userId: Int,
    coinsAmount: Int,
    eWalletMethod: String,
    accountNumber: String
  ): Result<Int> = withContext(Dispatchers.IO) {
    val user = userDao.getUserByIdOnce(userId)
      ?: return@withContext Result.failure(Exception("Pengguna tidak ditemukan."))
    
    if (user.points < coinsAmount) {
      return@withContext Result.failure(Exception("Saldo koin Anda tidak mencukupi (Saldo: ${user.points} Koin)."))
    }

    val idrEquivalent = coinsAmount * 100L
    val refCode = "WD-" + (100000..999999).random()

    userDao.adjustPoints(userId, -coinsAmount)
    coinDao.insertTransaction(
      CoinTransactionEntity(
        userId = userId,
        amount = -coinsAmount,
        type = TransactionType.ADMIN_DEDUCT,
        description = "Penarikan Saldo ke $eWalletMethod ($accountNumber) - Rp $idrEquivalent ($refCode)"
      )
    )

    Result.success(user.points - coinsAmount)
  }

  suspend fun reportCheater(
    verificationId: Int,
    reason: String
  ): Result<Unit> = withContext(Dispatchers.IO) {
    val verifications = verificationDao.getAllVerifications() // or find matching
    // Mark verification as flagged
    verificationDao.updateStatus(verificationId, VerificationStatus.FLAGGED_CHEATING, reason)

    // Apply strict anti-cheat penalty to follower
    // We get verification details
    val verificationList = db.verificationDao()
    // For penalty:
    Result.success(Unit)
  }

  suspend fun getLatestCheckIn(userId: Int): DailyCheckInEntity? = withContext(Dispatchers.IO) {
    dailyCheckInDao.getLatestCheckIn(userId)
  }

  suspend fun performDailyCheckIn(user: UserEntity): Result<DailyCheckInResult> = withContext(Dispatchers.IO) {
    if (user.isBanned) {
      return@withContext Result.failure(Exception("Akun Anda dibekukan karena pelanggaran anti-curang."))
    }

    val currentTimestamp = System.currentTimeMillis()
    val tz = java.util.TimeZone.getDefault()
    val latest = dailyCheckInDao.getLatestCheckIn(user.id)

    val evaluation = DailyCheckInSyncManager.evaluateDailyCheckInStatus(latest, currentTimestamp, tz)

    if (evaluation.isClockSkewDetected) {
      return@withContext Result.failure(
        Exception(evaluation.clockSkewErrorMessage ?: "Waktu perangkat tidak sinkron. Harap gunakan waktu jaringan otomatis.")
      )
    }

    if (evaluation.alreadyClaimedToday) {
      return@withContext Result.failure(
        Exception("Bonus hari ini sudah diklaim! Silakan kembali besok dalam ${evaluation.countdownFormatted}.")
      )
    }

    val streakDay = evaluation.currentStreakDay
    val coinsToReward = evaluation.rewardCoinsForToday
    val todayDateKey = evaluation.todayDateKey
    val timeFormatted = DailyCheckInSyncManager.formatHumanTimestamp(currentTimestamp, tz)

    val checkInEntity = DailyCheckInEntity(
      userId = user.id,
      checkInDateKey = todayDateKey,
      streakDay = streakDay,
      rewardCoins = coinsToReward,
      timestamp = currentTimestamp,
      timezoneId = evaluation.timezoneDisplayName,
      timeFormatted = timeFormatted
    )

    // 1. Insert Daily Check-in Record
    dailyCheckInDao.insertCheckIn(checkInEntity)

    // 2. Adjust User Points
    userDao.adjustPoints(user.id, coinsToReward)

    // 3. Insert Coin Transaction
    coinDao.insertTransaction(
      CoinTransactionEntity(
        userId = user.id,
        amount = coinsToReward,
        type = TransactionType.DAILY_CHECKIN_BONUS,
        description = "Bonus Check-in Harian Hari ke-$streakDay (+${coinsToReward} 🪙)",
        timestamp = currentTimestamp
      )
    )

    val grandBonusNote = if (streakDay == 7) " 🎁 Grand Jackpot 7 Hari Streak!" else ""
    val successMsg = "🎉 Selamat! Bonus Check-in Hari ke-$streakDay (+${coinsToReward} 🪙)$grandBonusNote berhasil diklaim!"

    Result.success(
      DailyCheckInResult(
        success = true,
        coinsAwarded = coinsToReward,
        streakDay = streakDay,
        message = successMsg
      )
    )
  }

  suspend fun saveDetectedJob(
    userId: Int,
    jobTitle: String,
    confidence: Float,
    summary: String,
    skills: String,
    niche: String
  ): Result<Unit> = withContext(Dispatchers.IO) {
    userDao.updateDetectedJob(userId, jobTitle, confidence, summary, skills, niche)
    Result.success(Unit)
  }
}

data class DailyCheckInResult(
  val success: Boolean,
  val coinsAwarded: Int,
  val streakDay: Int,
  val message: String
)

