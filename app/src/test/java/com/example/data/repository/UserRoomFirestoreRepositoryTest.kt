package com.example.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.model.TransactionType
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class UserRoomFirestoreRepositoryTest {

  private lateinit var database: AppDatabase
  private lateinit var repository: UserRoomFirestoreRepository

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    // Instantiated without Firestore instance for pure offline/Room testing
    repository = UserRoomFirestoreRepository(database, firestoreInstance = null)
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun testSaveAndObserveUserProfile() = runBlocking {
    val user = UserEntity(
      id = 101,
      name = "Ahmad Pratama",
      email = "ahmad@example.com",
      passwordHash = "hash123",
      role = UserRole.USER,
      points = 250,
      instagramHandle = "@ahmad.pra"
    )

    val saveResult = repository.saveUserProfile(user)
    assertTrue("Save user profile should succeed", saveResult.isSuccess)

    val observedUser = repository.observeUserProfile(101).first()
    assertNotNull(observedUser)
    assertEquals("Ahmad Pratama", observedUser?.name)
    assertEquals(250, observedUser?.points)
    assertEquals("@ahmad.pra", observedUser?.instagramHandle)
  }

  @Test
  fun testUpdateProfileDetails() = runBlocking {
    val user = UserEntity(
      id = 102,
      name = "Bella Dev",
      email = "bella@example.com",
      passwordHash = "hash456",
      points = 150
    )
    repository.saveUserProfile(user)

    val updateResult = repository.updateProfileDetails(
      userId = 102,
      name = "Bella Update",
      instagram = "@bella.ig",
      tiktok = "@bella.tt",
      twitter = "@bella.tw",
      youtube = "@bella.yt"
    )

    assertTrue("Update details should succeed", updateResult.isSuccess)
    val updated = repository.observeUserProfile(102).first()
    assertEquals("Bella Update", updated?.name)
    assertEquals("@bella.ig", updated?.instagramHandle)
    assertEquals("@bella.tt", updated?.tiktokHandle)
    assertEquals("@bella.tw", updated?.twitterHandle)
    assertEquals("@bella.yt", updated?.youtubeHandle)
  }

  @Test
  fun testCoinBalanceAdjustmentAndTransactions() = runBlocking {
    val user = UserEntity(
      id = 103,
      name = "Chandra Coins",
      email = "chandra@example.com",
      passwordHash = "hash789",
      points = 100
    )
    repository.saveUserProfile(user)

    // Earn 50 coins
    val earnResult = repository.adjustCoinBalance(
      userId = 103,
      delta = 50,
      description = "Bonus Follow Task",
      type = TransactionType.EARNED_TASK
    )
    assertTrue("Earn coins should succeed", earnResult.isSuccess)
    assertEquals(150, earnResult.getOrNull())

    val currentPoints = repository.observeCoinBalance(103).first()
    assertEquals(150, currentPoints)

    // Spend 30 coins
    val spendResult = repository.adjustCoinBalance(
      userId = 103,
      delta = -30,
      description = "Tukar Follower",
      type = TransactionType.EXCHANGED_CAMPAIGN
    )
    assertTrue("Spend coins should succeed", spendResult.isSuccess)
    assertEquals(120, spendResult.getOrNull())

    val updatedPoints = repository.observeCoinBalance(103).first()
    assertEquals(120, updatedPoints)

    // Verify transactions
    val transactions = repository.observeUserTransactions(103).first()
    assertEquals(2, transactions.size)
    assertEquals(50, transactions[1].amount)
    assertEquals(-30, transactions[0].amount)
  }

  @Test
  fun testInsufficientBalanceFailsGracefully() = runBlocking {
    val user = UserEntity(
      id = 104,
      name = "Dani Low Balance",
      email = "dani@example.com",
      passwordHash = "hash000",
      points = 20
    )
    repository.saveUserProfile(user)

    // Attempt to spend 50 coins when only 20 available
    val result = repository.adjustCoinBalance(
      userId = 104,
      delta = -50,
      description = "Tukar Follower Terlalu Banyak"
    )

    assertTrue("Should fail on insufficient coins", result.isFailure)
    val pointsAfter = repository.observeCoinBalance(104).first()
    assertEquals(20, pointsAfter)
  }
}
