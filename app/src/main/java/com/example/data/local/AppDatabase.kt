package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CampaignEntity
import com.example.data.model.CoinTransactionEntity
import com.example.data.model.DailyCheckInEntity
import com.example.data.model.FollowVerificationEntity
import com.example.data.model.QrisOrderEntity
import com.example.data.model.SocialPlatform
import com.example.data.model.TaskItemEntity
import com.example.data.model.TaskType
import com.example.data.model.TaskVerificationRecordEntity
import com.example.data.model.TaskVerificationStatus
import com.example.data.model.TransactionType
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.data.model.VerificationMethod
import com.example.data.model.VerificationStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Converters {
  @TypeConverter
  fun fromUserRole(value: UserRole): String = value.name

  @TypeConverter
  fun toUserRole(value: String): UserRole = try {
    UserRole.valueOf(value)
  } catch (e: Exception) {
    UserRole.USER
  }

  @TypeConverter
  fun fromSocialPlatform(value: SocialPlatform): String = value.name

  @TypeConverter
  fun toSocialPlatform(value: String): SocialPlatform = try {
    SocialPlatform.valueOf(value)
  } catch (e: Exception) {
    SocialPlatform.INSTAGRAM
  }

  @TypeConverter
  fun fromVerificationStatus(value: VerificationStatus): String = value.name

  @TypeConverter
  fun toVerificationStatus(value: String): VerificationStatus = try {
    VerificationStatus.valueOf(value)
  } catch (e: Exception) {
    VerificationStatus.VERIFIED_ACTIVE
  }

  @TypeConverter
  fun fromTransactionType(value: TransactionType): String = value.name

  @TypeConverter
  fun toTransactionType(value: String): TransactionType = try {
    TransactionType.valueOf(value)
  } catch (e: Exception) {
    TransactionType.EARNED_FOLLOW
  }

  @TypeConverter
  fun fromTaskType(value: TaskType): String = value.name

  @TypeConverter
  fun toTaskType(value: String): TaskType = try {
    TaskType.valueOf(value)
  } catch (e: Exception) {
    TaskType.FOLLOW_SOCIAL
  }

  @TypeConverter
  fun fromVerificationMethod(value: VerificationMethod): String = value.name

  @TypeConverter
  fun toVerificationMethod(value: String): VerificationMethod = try {
    VerificationMethod.valueOf(value)
  } catch (e: Exception) {
    VerificationMethod.MANUAL_AUDIT
  }

  @TypeConverter
  fun fromTaskVerificationStatus(value: TaskVerificationStatus): String = value.name

  @TypeConverter
  fun toTaskVerificationStatus(value: String): TaskVerificationStatus = try {
    TaskVerificationStatus.valueOf(value)
  } catch (e: Exception) {
    TaskVerificationStatus.PENDING
  }
}

@Database(
  entities = [
    UserEntity::class,
    CampaignEntity::class,
    FollowVerificationEntity::class,
    TaskItemEntity::class,
    TaskVerificationRecordEntity::class,
    CoinTransactionEntity::class,
    QrisOrderEntity::class,
    DailyCheckInEntity::class
  ],
  version = 4,
  exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun userDao(): UserDao
  abstract fun campaignDao(): CampaignDao
  abstract fun verificationDao(): VerificationDao
  abstract fun taskDao(): TaskDao
  abstract fun taskVerificationDao(): TaskVerificationDao
  abstract fun coinTransactionDao(): CoinTransactionDao
  abstract fun qrisOrderDao(): QrisOrderDao
  abstract fun dailyCheckInDao(): DailyCheckInDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "boostly_db_v4"
        )
          .fallbackToDestructiveMigration()
          .addCallback(object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
              super.onCreate(db)
              CoroutineScope(Dispatchers.IO).launch {
                seedInitialData(getDatabase(context))
              }
            }
          })
          .build()
        INSTANCE = instance
        instance
      }
    }

    suspend fun seedInitialData(database: AppDatabase) {
      val userDao = database.userDao()
      val campaignDao = database.campaignDao()
      val taskDao = database.taskDao()
      val taskVerificationDao = database.taskVerificationDao()
      val coinDao = database.coinTransactionDao()
      val orderDao = database.qrisOrderDao()
      val verificationDao = database.verificationDao()

      // 1. Seed Admin
      val adminId = userDao.insertUser(
        UserEntity(
          name = "Admin Boostly",
          email = "admin@sosmedgrow.com",
          passwordHash = "admin123",
          role = UserRole.ADMIN,
          points = 99999,
          authenticityScore = 100f
        )
      ).toInt()

      // 2. Seed Regular User (Default active user Rian Pratama)
      val demoUserId = userDao.insertUser(
        UserEntity(
          name = "Rian Pratama",
          email = "rian@gmail.com",
          passwordHash = "password123",
          role = UserRole.USER,
          points = 245,
          authenticityScore = 98.5f,
          instagramHandle = "@rian_pratama.tech",
          tiktokHandle = "@rian.tech_id",
          youtubeHandle = "@RianTechVlog",
          detectedJob = "Tech Reviewer & Content Creator",
          jobConfidence = 0.96f,
          jobAnalysisSummary = "Terdeteksi sebagai kreator konten teknologi, gadget reviewer, dan software developer berdasarkan portofolio YouTube dan Instagram.",
          jobKeySkills = "Review Gadget, Video Editing, Software Tutorials, Live Tech Q&A",
          recommendedNiche = "Teknologi & Gadget"
        )
      ).toInt()

      // 3. Seed Other Community Creators
      val user2 = userDao.insertUser(
        UserEntity(
          name = "Siti Aisyah",
          email = "siti@gmail.com",
          passwordHash = "password123",
          points = 180,
          authenticityScore = 96.0f,
          instagramHandle = "@siti.fashion_id",
          detectedJob = "Fashion Stylist & Brand Owner",
          jobConfidence = 0.94f,
          jobAnalysisSummary = "Fokus pada OOTD hijab, busana muslimah kekinian, dan endorsement fashion lokal.",
          jobKeySkills = "OOTD Modeling, Fashion Styling, Product Photography",
          recommendedNiche = "Fashion & Beauty"
        )
      ).toInt()

      val user3 = userDao.insertUser(
        UserEntity(
          name = "Devi Ayu",
          email = "devi@gmail.com",
          passwordHash = "password123",
          points = 520,
          authenticityScore = 99.2f,
          tiktokHandle = "@devi_kuliner",
          detectedJob = "Food Vlogger & Kuliner Hunter",
          jobConfidence = 0.95f,
          jobAnalysisSummary = "Kreator konten kuliner nusantara, review makanan viral, dan rekomendasi resto.",
          jobKeySkills = "Food Tasting, Reel Creation, Foodie Storytelling",
          recommendedNiche = "Kuliner & FnB"
        )
      ).toInt()

      // 4. Seed Initial Transactions
      coinDao.insertTransaction(
        CoinTransactionEntity(
          userId = demoUserId,
          amount = 100,
          type = TransactionType.WELCOME_BONUS,
          description = "Bonus Pengguna Baru Boostly"
        )
      )
      coinDao.insertTransaction(
        CoinTransactionEntity(
          userId = demoUserId,
          amount = 145,
          type = TransactionType.QRIS_PURCHASE,
          description = "Topup QRIS Paket Starter (+Bonus)"
        )
      )

      orderDao.insertOrder(
        QrisOrderEntity(
          userId = demoUserId,
          packageTitle = "Paket Creator Starter",
          coinsGiven = 145,
          priceIdr = 15000,
          transactionRef = "QRIS-BST-994102"
        )
      )

      // 4b. Seed Initial Daily Check-In for Demo User (Yesterday Day 1, so today Day 2 is ready!)
      val dailyCheckInDao = database.dailyCheckInDao()
      val calYesterday = java.util.Calendar.getInstance()
      calYesterday.add(java.util.Calendar.DAY_OF_YEAR, -1)
      val yesterdayYear = calYesterday.get(java.util.Calendar.YEAR)
      val yesterdayMonth = calYesterday.get(java.util.Calendar.MONTH) + 1
      val yesterdayDay = calYesterday.get(java.util.Calendar.DAY_OF_MONTH)
      val yesterdayDateKey = String.format(java.util.Locale.US, "%04d-%02d-%02d", yesterdayYear, yesterdayMonth, yesterdayDay)
      dailyCheckInDao.insertCheckIn(
        DailyCheckInEntity(
          userId = demoUserId,
          checkInDateKey = yesterdayDateKey,
          streakDay = 1,
          rewardCoins = 10,
          timestamp = calYesterday.timeInMillis,
          timezoneId = java.util.TimeZone.getDefault().id,
          timeFormatted = "Kemarin"
        )
      )
      coinDao.insertTransaction(
        CoinTransactionEntity(
          userId = demoUserId,
          amount = 10,
          type = TransactionType.DAILY_CHECKIN_BONUS,
          description = "Bonus Check-in Harian Hari ke-1 (+10 🪙)",
          timestamp = calYesterday.timeInMillis
        )
      )

      // 5. Seed Core Tasks with different TaskTypes and VerificationMethods
      val tasks = listOf(
        TaskItemEntity(
          id = 1,
          title = "Follow Akun Instagram @luna.style",
          taskType = TaskType.FOLLOW_SOCIAL,
          platform = SocialPlatform.INSTAGRAM,
          targetHandle = "@luna.style",
          targetUrl = "https://instagram.com/luna.style",
          category = "Fashion & Lifestyle",
          rewardCoins = 10,
          verificationMethod = VerificationMethod.MANUAL_AUDIT,
          instructions = "Buka link profil target, ikuti akun, lalu kirimkan username Anda untuk audit validasi keaslian.",
          officialApiSupported = false
        ),
        TaskItemEntity(
          id = 2,
          title = "Follow Akun TikTok @tech.daily",
          taskType = TaskType.FOLLOW_SOCIAL,
          platform = SocialPlatform.TIKTOK,
          targetHandle = "@tech.daily",
          targetUrl = "https://tiktok.com/@tech.daily",
          category = "Tech & Gadget",
          rewardCoins = 10,
          verificationMethod = VerificationMethod.MANUAL_AUDIT,
          instructions = "Follow akun TikTok @tech.daily dan ajukan bukti username untuk verifikasi server.",
          officialApiSupported = false
        ),
        TaskItemEntity(
          id = 3,
          title = "Subscribe Channel YouTube @SitiDailyLife",
          taskType = TaskType.FOLLOW_SOCIAL,
          platform = SocialPlatform.YOUTUBE,
          targetHandle = "@SitiDailyLife",
          targetUrl = "https://youtube.com/@SitiDailyLife",
          category = "Vlog & Entertainment",
          rewardCoins = 15,
          verificationMethod = VerificationMethod.OFFICIAL_API,
          instructions = "Subscribe channel YouTube. Jika OAuth Google terhubung, sistem akan memverifikasi via YouTube Data API v3 subscriptions.list.",
          officialApiSupported = true
        ),
        TaskItemEntity(
          id = 4,
          title = "Kunjungi & Jelajahi Profil Kuliner @foodies.id",
          taskType = TaskType.VISIT_AND_EXPLORE,
          platform = SocialPlatform.INSTAGRAM,
          targetHandle = "@foodies.id",
          targetUrl = "https://instagram.com/foodies.id",
          category = "Food & Kuliner",
          rewardCoins = 10,
          verificationMethod = VerificationMethod.CHALLENGE_TOKEN,
          instructions = "Kunjungi halaman target profil selama minimal 15 detik untuk mendapatkan Challenge Token server terverifikasi.",
          requiredDurationSeconds = 15
        ),
        TaskItemEntity(
          id = 5,
          title = "Follow Akun X / Twitter @travelgram",
          taskType = TaskType.FOLLOW_SOCIAL,
          platform = SocialPlatform.X_TWITTER,
          targetHandle = "@travelgram",
          targetUrl = "https://x.com/travelgram",
          category = "Travel & Adventure",
          rewardCoins = 10,
          verificationMethod = VerificationMethod.MANUAL_AUDIT,
          instructions = "Follow akun Twitter @travelgram dan verifikasi username akun Anda.",
          officialApiSupported = false
        ),
        TaskItemEntity(
          id = 6,
          title = "Like & Simpan Konten Terkini @fitnesszone",
          taskType = TaskType.ENGAGE_CONTENT,
          platform = SocialPlatform.INSTAGRAM,
          targetHandle = "@fitnesszone",
          targetUrl = "https://instagram.com/fitnesszone",
          category = "Health & Fitness",
          rewardCoins = 10,
          verificationMethod = VerificationMethod.MANUAL_AUDIT,
          instructions = "Beri Like pada postingan terbaru @fitnesszone dan konfirmasi aktivitas Anda.",
          officialApiSupported = false
        ),
        TaskItemEntity(
          id = 7,
          title = "Follow Akun Gaming @gaminghub",
          taskType = TaskType.FOLLOW_SOCIAL,
          platform = SocialPlatform.TIKTOK,
          targetHandle = "@gaminghub",
          targetUrl = "https://tiktok.com/@gaminghub",
          category = "Gaming",
          rewardCoins = 10,
          verificationMethod = VerificationMethod.MANUAL_AUDIT,
          instructions = "Follow akun TikTok gaming @gaminghub.",
          officialApiSupported = false
        ),
        TaskItemEntity(
          id = 8,
          title = "Follow Akun Kreator Seni @artspace",
          taskType = TaskType.FOLLOW_SOCIAL,
          platform = SocialPlatform.INSTAGRAM,
          targetHandle = "@artspace",
          targetUrl = "https://instagram.com/artspace",
          category = "Art & Creativity",
          rewardCoins = 10,
          verificationMethod = VerificationMethod.MANUAL_AUDIT,
          instructions = "Follow akun seni rupa @artspace.",
          officialApiSupported = false
        )
      )
      taskDao.insertTasks(tasks)

      // 6. Seed Sample Verifications with different states (VERIFIED, PENDING, REJECTED)
      val now = System.currentTimeMillis()
      // One verified task today for demo user (counted in daily progress!)
      taskVerificationDao.insertVerification(
        TaskVerificationRecordEntity(
          taskId = 1,
          userId = demoUserId,
          taskType = TaskType.FOLLOW_SOCIAL,
          platform = SocialPlatform.INSTAGRAM,
          targetHandle = "@luna.style",
          verificationMethod = VerificationMethod.MANUAL_AUDIT,
          submittedProof = "@rian_pratama.tech",
          proofNotes = "Telah di-follow dari aplikasi Instagram resmi.",
          status = TaskVerificationStatus.VERIFIED,
          rewardCoins = 10,
          isRewardClaimed = true,
          submittedAt = now - 3600000,
          verifiedAt = now - 3000000,
          apiAuditLog = "Audit server berhasil: Handle diverifikasi aktif dan tidak terdeteksi bot."
        )
      )

      // One pending task (NOT counted in daily progress, reward cannot be claimed yet)
      taskVerificationDao.insertVerification(
        TaskVerificationRecordEntity(
          taskId = 2,
          userId = demoUserId,
          taskType = TaskType.FOLLOW_SOCIAL,
          platform = SocialPlatform.TIKTOK,
          targetHandle = "@tech.daily",
          verificationMethod = VerificationMethod.MANUAL_AUDIT,
          submittedProof = "@rian.tech_id",
          proofNotes = "Menunggu audit server/kreator.",
          status = TaskVerificationStatus.PENDING,
          rewardCoins = 10,
          isRewardClaimed = false,
          submittedAt = now - 1800000,
          apiAuditLog = "Status verifikasi: PENDING (Antrean audit antarmuka server)."
        )
      )

      // One rejected task (NOT counted in daily progress, reward rejected)
      taskVerificationDao.insertVerification(
        TaskVerificationRecordEntity(
          taskId = 6,
          userId = demoUserId,
          taskType = TaskType.ENGAGE_CONTENT,
          platform = SocialPlatform.INSTAGRAM,
          targetHandle = "@fitnesszone",
          verificationMethod = VerificationMethod.MANUAL_AUDIT,
          submittedProof = "@dummy_bot_acc",
          proofNotes = "Bukti tidak sesuai.",
          status = TaskVerificationStatus.REJECTED,
          rejectionReason = "Akun pengikut tidak ditemukan pada daftar pengikut target (Anti-Unfollow check gagal).",
          rewardCoins = 10,
          isRewardClaimed = false,
          submittedAt = now - 7200000,
          verifiedAt = now - 6500000,
          apiAuditLog = "Pemeriksaan server: Akun target belum diikuti atau terdeteksi unfollow."
        )
      )

      // 7. Seed active campaigns
      val c1 = campaignDao.insertCampaign(
        CampaignEntity(
          creatorId = user2,
          creatorName = "Siti Aisyah",
          platform = SocialPlatform.INSTAGRAM,
          targetUrl = "https://instagram.com/siti.fashion_id",
          accountHandle = "@siti.fashion_id",
          category = "Fashion & Lifestyle",
          targetFollowers = 50,
          currentFollowers = 18,
          coinRewardPerFollow = 15,
          totalCoinsAllocated = 750
        )
      ).toInt()

      val c2 = campaignDao.insertCampaign(
        CampaignEntity(
          creatorId = user3,
          creatorName = "Devi Ayu",
          platform = SocialPlatform.TIKTOK,
          targetUrl = "https://tiktok.com/@devi_kuliner",
          accountHandle = "@devi_kuliner",
          category = "Kuliner & Resep",
          targetFollowers = 100,
          currentFollowers = 64,
          coinRewardPerFollow = 20,
          totalCoinsAllocated = 2000
        )
      ).toInt()

      // 8. Seed Follow Verifications for creator audit
      verificationDao.insertVerification(
        FollowVerificationEntity(
          campaignId = c1,
          campaignCreatorId = user2,
          followerUserId = demoUserId,
          followerName = "Rian Pratama",
          followerHandle = "@rian_pratama.tech",
          targetPlatform = SocialPlatform.INSTAGRAM,
          targetAccountHandle = "@siti.fashion_id",
          coinsEarned = 15,
          status = VerificationStatus.VERIFIED_ACTIVE
        )
      )
    }
  }
}
