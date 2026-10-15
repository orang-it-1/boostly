package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CampaignEntity
import com.example.data.model.CoinTransactionEntity
import com.example.data.model.DailyCheckInEntity
import com.example.data.model.FollowVerificationEntity
import com.example.data.model.QrisOrderEntity
import com.example.data.model.TaskItemEntity
import com.example.data.model.TaskVerificationRecordEntity
import com.example.data.model.TaskVerificationStatus
import com.example.data.model.UserEntity
import com.example.data.model.VerificationStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
  @Query("SELECT * FROM users WHERE id = :id")
  fun getUserById(id: Int): Flow<UserEntity?>

  @Query("SELECT * FROM users WHERE id = :id")
  suspend fun getUserByIdOnce(id: Int): UserEntity?

  @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
  suspend fun getUserByEmail(email: String): UserEntity?

  @Query("SELECT * FROM users ORDER BY createdAt DESC")
  fun getAllUsers(): Flow<List<UserEntity>>

  @Query("SELECT * FROM users ORDER BY createdAt DESC")
  suspend fun getAllUsersList(): List<UserEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUser(user: UserEntity): Long

  @Update
  suspend fun updateUser(user: UserEntity)

  @Query("UPDATE users SET points = points + :delta WHERE id = :userId")
  suspend fun adjustPoints(userId: Int, delta: Int)

  @Query("UPDATE users SET authenticityScore = :score, violationsCount = :violations, isBanned = :isBanned WHERE id = :userId")
  suspend fun updateReputation(userId: Int, score: Float, violations: Int, isBanned: Boolean)

  @Query("UPDATE users SET detectedJob = :job, jobConfidence = :confidence, jobAnalysisSummary = :summary, jobKeySkills = :skills, recommendedNiche = :niche WHERE id = :userId")
  suspend fun updateDetectedJob(
    userId: Int,
    job: String,
    confidence: Float,
    summary: String,
    skills: String,
    niche: String
  )
}

@Dao
interface CampaignDao {
  @Query("SELECT * FROM campaigns WHERE isActive = 1 ORDER BY createdAt DESC")
  fun getActiveCampaigns(): Flow<List<CampaignEntity>>

  @Query("SELECT * FROM campaigns WHERE creatorId = :creatorId ORDER BY createdAt DESC")
  fun getCampaignsByCreator(creatorId: Int): Flow<List<CampaignEntity>>

  @Query("SELECT * FROM campaigns ORDER BY createdAt DESC")
  fun getAllCampaigns(): Flow<List<CampaignEntity>>

  @Query("SELECT * FROM campaigns ORDER BY createdAt DESC")
  suspend fun getAllCampaignsList(): List<CampaignEntity>

  @Query("SELECT * FROM campaigns WHERE id = :id LIMIT 1")
  suspend fun getCampaignById(id: Int): CampaignEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCampaign(campaign: CampaignEntity): Long

  @Update
  suspend fun updateCampaign(campaign: CampaignEntity)

  @Query("UPDATE campaigns SET currentFollowers = currentFollowers + 1, isActive = CASE WHEN currentFollowers + 1 >= targetFollowers THEN 0 ELSE 1 END WHERE id = :campaignId")
  suspend fun incrementCampaignFollower(campaignId: Int)
}

@Dao
interface VerificationDao {
  @Query("SELECT * FROM follow_verifications WHERE campaignCreatorId = :creatorId ORDER BY createdAt DESC")
  fun getVerificationsForCreator(creatorId: Int): Flow<List<FollowVerificationEntity>>

  @Query("SELECT * FROM follow_verifications WHERE followerUserId = :userId ORDER BY createdAt DESC")
  fun getVerificationsByFollower(userId: Int): Flow<List<FollowVerificationEntity>>

  @Query("SELECT * FROM follow_verifications ORDER BY createdAt DESC")
  fun getAllVerifications(): Flow<List<FollowVerificationEntity>>

  @Query("SELECT COUNT(*) FROM follow_verifications WHERE campaignId = :campaignId AND followerUserId = :userId")
  suspend fun countFollow(campaignId: Int, userId: Int): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertVerification(verification: FollowVerificationEntity): Long

  @Update
  suspend fun updateVerification(verification: FollowVerificationEntity)

  @Query("UPDATE follow_verifications SET status = :status, cheatReportReason = :reason WHERE id = :id")
  suspend fun updateStatus(id: Int, status: VerificationStatus, reason: String?)
}

@Dao
interface TaskDao {
  @Query("SELECT * FROM tasks WHERE isActive = 1 ORDER BY id ASC")
  fun getAllActiveTasks(): Flow<List<TaskItemEntity>>

  @Query("SELECT * FROM tasks ORDER BY id ASC")
  fun getAllTasks(): Flow<List<TaskItemEntity>>

  @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
  suspend fun getTaskById(id: Int): TaskItemEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTask(task: TaskItemEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTasks(tasks: List<TaskItemEntity>)
}

@Dao
interface TaskVerificationDao {
  @Query("SELECT * FROM task_verifications ORDER BY submittedAt DESC")
  fun getAllVerifications(): Flow<List<TaskVerificationRecordEntity>>

  @Query("SELECT * FROM task_verifications WHERE userId = :userId ORDER BY submittedAt DESC")
  fun getVerificationsForUser(userId: Int): Flow<List<TaskVerificationRecordEntity>>

  @Query("SELECT * FROM task_verifications WHERE status = 'PENDING' ORDER BY submittedAt DESC")
  fun getPendingVerifications(): Flow<List<TaskVerificationRecordEntity>>

  @Query("SELECT * FROM task_verifications WHERE id = :id LIMIT 1")
  suspend fun getVerificationById(id: Int): TaskVerificationRecordEntity?

  @Query("SELECT * FROM task_verifications WHERE taskId = :taskId AND userId = :userId LIMIT 1")
  suspend fun getVerificationByTaskAndUser(taskId: Int, userId: Int): TaskVerificationRecordEntity?

  // Progress Bar: HANYA menghitung tugas yang berstatus VERIFIED, BUKAN pending atau rejected!
  @Query("SELECT COUNT(*) FROM task_verifications WHERE userId = :userId AND status = 'VERIFIED' AND submittedAt >= :startOfDay")
  fun countVerifiedTasksToday(userId: Int, startOfDay: Long): Flow<Int>

  @Query("SELECT COUNT(*) FROM task_verifications WHERE userId = :userId AND status = 'VERIFIED' AND submittedAt >= :startOfDay")
  suspend fun countVerifiedTasksTodayOnce(userId: Int, startOfDay: Long): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertVerification(verification: TaskVerificationRecordEntity): Long

  @Update
  suspend fun updateVerification(verification: TaskVerificationRecordEntity)

  @Query("UPDATE task_verifications SET status = :status, verifiedAt = :verifiedAt, rejectionReason = :rejectionReason, apiAuditLog = :apiAuditLog WHERE id = :id")
  suspend fun updateAuditDecision(
    id: Int,
    status: TaskVerificationStatus,
    verifiedAt: Long?,
    rejectionReason: String?,
    apiAuditLog: String?
  )

  @Query("UPDATE task_verifications SET isRewardClaimed = 1 WHERE id = :id")
  suspend fun markRewardClaimed(id: Int)
}

@Dao
interface CoinTransactionDao {
  @Query("SELECT * FROM coin_transactions WHERE userId = :userId ORDER BY timestamp DESC")
  fun getTransactionsByUserId(userId: Int): Flow<List<CoinTransactionEntity>>

  @Query("SELECT * FROM coin_transactions ORDER BY timestamp DESC")
  fun getAllTransactions(): Flow<List<CoinTransactionEntity>>

  @Query("SELECT * FROM coin_transactions ORDER BY timestamp DESC")
  suspend fun getAllTransactionsList(): List<CoinTransactionEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTransaction(transaction: CoinTransactionEntity): Long
}

@Dao
interface QrisOrderDao {
  @Query("SELECT * FROM qris_orders WHERE userId = :userId ORDER BY timestamp DESC")
  fun getOrdersByUserId(userId: Int): Flow<List<QrisOrderEntity>>

  @Query("SELECT * FROM qris_orders ORDER BY timestamp DESC")
  fun getAllOrders(): Flow<List<QrisOrderEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrder(order: QrisOrderEntity): Long
}

@Dao
interface DailyCheckInDao {
  @Query("SELECT * FROM daily_check_ins WHERE userId = :userId ORDER BY timestamp DESC")
  fun getCheckInsByUser(userId: Int): Flow<List<DailyCheckInEntity>>

  @Query("SELECT * FROM daily_check_ins ORDER BY timestamp DESC")
  suspend fun getAllCheckInsList(): List<DailyCheckInEntity>

  @Query("SELECT * FROM daily_check_ins WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
  suspend fun getLatestCheckIn(userId: Int): DailyCheckInEntity?

  @Query("SELECT * FROM daily_check_ins WHERE userId = :userId AND checkInDateKey = :dateKey LIMIT 1")
  suspend fun getCheckInForDate(userId: Int, dateKey: String): DailyCheckInEntity?

  @Query("SELECT COUNT(*) FROM daily_check_ins WHERE userId = :userId")
  fun countTotalCheckIns(userId: Int): Flow<Int>

  @Query("SELECT COUNT(*) FROM daily_check_ins WHERE userId = :userId")
  suspend fun countTotalCheckInsOnce(userId: Int): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCheckIn(checkIn: DailyCheckInEntity): Long
}

