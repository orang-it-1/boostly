package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiJobAnalysisResult
import com.example.data.ai.AiJobDetectionService
import com.example.data.local.AppDatabase
import com.example.data.model.CampaignEntity
import com.example.data.model.CoinTransactionEntity
import com.example.data.model.DailyCheckInEntity
import com.example.data.model.FollowVerificationEntity
import com.example.data.model.QrisOrderEntity
import com.example.data.model.SocialPlatform
import com.example.data.auth.FirebaseAuthManager
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.data.model.VerificationStatus
import com.example.data.notification.PushNotificationManager
import com.example.data.payment.PaymentGatewayManager
import com.example.data.remote.laravel.LaravelConnectionStatus
import com.example.data.remote.laravel.LaravelSyncManager
import com.example.data.remote.laravel.LaravelSyncStatus
import com.example.data.repository.QrisCoinPackage
import com.example.data.repository.SosmedRepository
import com.example.data.repository.UserRoomFirestoreRepository
import com.example.data.sync.DailyCheckInEvaluation
import com.example.data.sync.DailyCheckInSyncManager
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.ThemePreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab {
  HOME,            // Beranda Boostly (Mockup 1)
  TASKS,           // Tugas Follow (Mockup 2)
  DETAIL,          // Preview Profil @luna.style (Mockup 3)
  WALLET,          // Dompet Koin (Mockup 4)
  ANALYTICS,       // Teman / Analitik & Validasi Real-time
  ADMIN,           // Panel Kontrol Admin
  PROFILE          // Akun Pengguna
}

class SosmedViewModel(application: Application) : AndroidViewModel(application) {
  private val database = AppDatabase.getDatabase(application)
  val repository = SosmedRepository(database)
  val userFirestoreRepository = UserRoomFirestoreRepository(database)
  val firebaseAuthManager = FirebaseAuthManager(application, userFirestoreRepository)
  val laravelSyncManager = LaravelSyncManager(application)
  val paymentGatewayManager = PaymentGatewayManager(application, repository)
  val themePreferences = ThemePreferences(application)
  val themeMode = themePreferences.themeMode

  val laravelConnectionStatus = laravelSyncManager.connectionStatus
  val laravelSyncStatus = laravelSyncManager.syncStatus
  val laravelBaseUrl = laravelSyncManager.savedBaseUrl
  val laravelApiToken = laravelSyncManager.savedApiToken

  private val _currentUserId = MutableStateFlow<Int?>(2) // Default to Rian Pratama (Demo Creator)
  val currentUserId = _currentUserId.asStateFlow()

  val currentUser: StateFlow<UserEntity?> = _currentUserId.flatMapLatest { id ->
    if (id != null) repository.observeUser(id) else flowOf(null)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  private val _currentTab = MutableStateFlow(MainTab.HOME)
  val currentTab = _currentTab.asStateFlow()

  private val _selectedDetailHandle = MutableStateFlow("@luna.style")
  val selectedDetailHandle = _selectedDetailHandle.asStateFlow()

  fun navigateToDetail(handle: String) {
    _selectedDetailHandle.value = handle
    _currentTab.value = MainTab.DETAIL
  }

  private val _selectedPlatformFilter = MutableStateFlow<SocialPlatform?>(null)
  val selectedPlatformFilter = _selectedPlatformFilter.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery = _searchQuery.asStateFlow()

  val allCampaigns: StateFlow<List<CampaignEntity>> = repository.observeActiveCampaigns()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val filteredCampaigns: StateFlow<List<CampaignEntity>> = combine(
    allCampaigns,
    _selectedPlatformFilter,
    _searchQuery
  ) { list, platform, query ->
    list.filter { item ->
      val matchesPlatform = platform == null || item.platform == platform
      val matchesQuery = query.isBlank() ||
        item.accountHandle.contains(query, ignoreCase = true) ||
        item.creatorName.contains(query, ignoreCase = true) ||
        item.category.contains(query, ignoreCase = true)
      matchesPlatform && matchesQuery
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val myCampaigns: StateFlow<List<CampaignEntity>> = _currentUserId.flatMapLatest { id ->
    if (id != null) repository.observeMyCampaigns(id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val creatorVerifications: StateFlow<List<FollowVerificationEntity>> = _currentUserId.flatMapLatest { id ->
    if (id != null) repository.observeVerificationsForCreator(id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val myFollowHistory: StateFlow<List<FollowVerificationEntity>> = _currentUserId.flatMapLatest { id ->
    if (id != null) repository.observeMyFollowHistory(id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val userTransactions: StateFlow<List<CoinTransactionEntity>> = _currentUserId.flatMapLatest { id ->
    if (id != null) repository.observeUserTransactions(id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Daily Check-In Bonus Data & Synchronized Evaluation
  val checkInHistory: StateFlow<List<DailyCheckInEntity>> = _currentUserId.flatMapLatest { id ->
    if (id != null) repository.observeCheckInsByUser(id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val totalCheckInsCount: StateFlow<Int> = _currentUserId.flatMapLatest { id ->
    if (id != null) repository.observeTotalCheckIns(id) else flowOf(0)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  private val _clockTicker = MutableStateFlow(System.currentTimeMillis())

  val dailyCheckInEvaluation: StateFlow<DailyCheckInEvaluation> = combine(
    checkInHistory,
    _clockTicker
  ) { history, now ->
    val latest = history.firstOrNull()
    DailyCheckInSyncManager.evaluateDailyCheckInStatus(latest, now)
  }.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    DailyCheckInSyncManager.evaluateDailyCheckInStatus(null, System.currentTimeMillis())
  )

  private val _showDailyCheckInDialog = MutableStateFlow(false)
  val showDailyCheckInDialog = _showDailyCheckInDialog.asStateFlow()

  private var hasAutoPromptedCheckIn = false

  init {
    // 1. Maintain second-by-second countdown and midnight timestamp sync
    viewModelScope.launch {
      while (true) {
        delay(1000L)
        _clockTicker.value = System.currentTimeMillis()
      }
    }

    // 2. Automatically prompt daily check-in bonus dialog when user opens app if today's bonus is ready
    viewModelScope.launch {
      combine(currentUser, checkInHistory) { user, history ->
        val latest = history.firstOrNull()
        val eval = DailyCheckInSyncManager.evaluateDailyCheckInStatus(latest, System.currentTimeMillis())
        user to eval
      }.collect { (user, eval) ->
        if (user != null && eval.canClaimToday && !hasAutoPromptedCheckIn) {
          hasAutoPromptedCheckIn = true
          delay(400L) // smooth entry animation after screen settles
          _showDailyCheckInDialog.value = true
        }
      }
    }
  }

  // Admin Data
  val allUsers: StateFlow<List<UserEntity>> = repository.observeAllUsers()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allOrders: StateFlow<List<QrisOrderEntity>> = repository.observeAllOrders()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Dialog & Action States
  private val _activeFollowCampaign = MutableStateFlow<CampaignEntity?>(null)
  val activeFollowCampaign = _activeFollowCampaign.asStateFlow()

  private val _showExchangeDialog = MutableStateFlow(false)
  val showExchangeDialog = _showExchangeDialog.asStateFlow()

  private val _showQrisShop = MutableStateFlow(false)
  val showQrisShop = _showQrisShop.asStateFlow()

  private val _selectedQrisPackage = MutableStateFlow<QrisCoinPackage?>(null)
  val selectedQrisPackage = _selectedQrisPackage.asStateFlow()

  private val _adminSelectedUser = MutableStateFlow<UserEntity?>(null)
  val adminSelectedUser = _adminSelectedUser.asStateFlow()

  private val _showAntiCheatInfo = MutableStateFlow(false)
  val showAntiCheatInfo = _showAntiCheatInfo.asStateFlow()

  private val _showLaravelSyncDialog = MutableStateFlow(false)
  val showLaravelSyncDialog = _showLaravelSyncDialog.asStateFlow()

  fun openLaravelSyncDialog() {
    _showLaravelSyncDialog.value = true
  }

  fun dismissLaravelSyncDialog() {
    _showLaravelSyncDialog.value = false
  }

  fun updateLaravelConfig(url: String, token: String) {
    laravelSyncManager.updateConfig(url, token)
  }

  fun testLaravelConnection(url: String? = null, token: String? = null) {
    viewModelScope.launch {
      val status = laravelSyncManager.testConnection(url, token)
      when (status) {
        is LaravelConnectionStatus.Connected -> {
          showMessage("Terhubung ke Laravel API (${status.latencyMs}ms). Database: ${status.health.databaseStatus.uppercase()}")
        }
        is LaravelConnectionStatus.Failed -> {
          showMessage("Uji koneksi gagal: ${status.errorMessage}")
        }
        else -> Unit
      }
    }
  }

  fun syncWithLaravel() {
    viewModelScope.launch {
      val result = laravelSyncManager.performSync(database)
      when (result) {
        is LaravelSyncStatus.Success -> {
          val count = result.syncedUsers + result.syncedCampaigns + result.syncedTransactions + result.syncedCheckIns
          showMessage(if (result.isSimulated) {
            "Database disiapkan ($count data lokal). Siap terhubung online ke Laravel!"
          } else {
            "Sukses sinkronisasi dengan database Laravel! ($count data tersinkron)"
          })
        }
        is LaravelSyncStatus.Error -> {
          showMessage("Sinkronisasi gagal: ${result.message}")
        }
        else -> Unit
      }
    }
  }

  fun resetLaravelSyncStatus() {
    laravelSyncManager.resetSyncStatus()
  }

  // ================= PUSH NOTIFICATIONS =================
  private val _showNotificationCenter = MutableStateFlow(false)
  val showNotificationCenter = _showNotificationCenter.asStateFlow()

  fun openNotificationCenter() {
    _showNotificationCenter.value = true
  }

  fun dismissNotificationCenter() {
    _showNotificationCenter.value = false
  }

  // ================= THEME & ACCESSIBILITY =================
  private val _showThemeDialog = MutableStateFlow(false)
  val showThemeDialog = _showThemeDialog.asStateFlow()

  fun openThemeDialog() {
    _showThemeDialog.value = true
  }

  fun dismissThemeDialog() {
    _showThemeDialog.value = false
  }

  fun setThemeMode(mode: AppThemeMode) {
    themePreferences.setThemeMode(mode)
  }

  fun toggleTheme() {
    themePreferences.toggleTheme()
  }

  // ================= ROOM & FIRESTORE CLOUD DATA =================
  private val _firestoreSyncStatus = MutableStateFlow<String?>(null)
  val firestoreSyncStatus = _firestoreSyncStatus.asStateFlow()

  fun syncUserProfileAndCoinsToFirestore() {
    val uid = _currentUserId.value ?: return
    viewModelScope.launch {
      _firestoreSyncStatus.value = "Menyinkronkan ke Firestore..."
      val result = userFirestoreRepository.syncUserToFirestore(uid)
      result.fold(
        onSuccess = {
          _firestoreSyncStatus.value = "Profil & Saldo Koin tersinkronisasi ke Firestore"
          showMessage("Profil & Saldo Koin berhasil disimpan ke Cloud Firestore!")
        },
        onFailure = { err ->
          _firestoreSyncStatus.value = "Tersimpan di Room (${err.message})"
          showMessage("Penyimpanan: Tersimpan lokal di Room (${err.message ?: "Firestore offline"})")
        }
      )
    }
  }

  fun syncUserProfileAndCoinsFromFirestore() {
    val uid = _currentUserId.value ?: return
    viewModelScope.launch {
      _firestoreSyncStatus.value = "Mengambil data Firestore..."
      val result = userFirestoreRepository.syncUserFromFirestore(uid)
      result.fold(
        onSuccess = { user ->
          if (user != null) {
            _firestoreSyncStatus.value = "Data termutakhir dimuat: ${user.points} Koin"
            showMessage("Data profil & saldo koin diperbarui dari Firestore (${user.points} Koin)")
          } else {
            _firestoreSyncStatus.value = "Data lokal diunggah ke Firestore"
            userFirestoreRepository.syncUserToFirestore(uid)
          }
        },
        onFailure = { err ->
          _firestoreSyncStatus.value = "Gagal memuat: ${err.message}"
        }
      )
    }
  }

  // ================= AI JOB DETECTOR =================
  private val _showAiJobDialog = MutableStateFlow(false)
  val showAiJobDialog = _showAiJobDialog.asStateFlow()

  private val _isAiJobDetecting = MutableStateFlow(false)
  val isAiJobDetecting = _isAiJobDetecting.asStateFlow()

  private val _aiJobAnalysisResult = MutableStateFlow<AiJobAnalysisResult?>(null)
  val aiJobAnalysisResult = _aiJobAnalysisResult.asStateFlow()

  fun openAiJobDialog() {
    _showAiJobDialog.value = true
    val user = currentUser.value
    if (user != null && user.detectedJob.isNotBlank() && _aiJobAnalysisResult.value == null) {
      _aiJobAnalysisResult.value = AiJobAnalysisResult(
        jobTitle = user.detectedJob,
        confidence = user.jobConfidence,
        niche = user.recommendedNiche,
        summary = user.jobAnalysisSummary,
        keySkills = user.jobKeySkills.split(",").map { it.trim() }.filter { it.isNotBlank() },
        suggestedCampaignCategories = listOf("Creator", "Lifestyle"),
        earningPotential = "Tinggi (150-300 Koin per tugas)",
        source = "Profil Terverifikasi AI"
      )
    }
  }

  fun dismissAiJobDialog() {
    _showAiJobDialog.value = false
  }

  fun detectUserJobWithAi(userDescription: String) {
    val user = currentUser.value ?: return
    _isAiJobDetecting.value = true
    viewModelScope.launch {
      val result = AiJobDetectionService.detectUserJob(
        userName = user.name,
        userBioOrDescription = userDescription,
        instagramHandle = user.instagramHandle,
        tiktokHandle = user.tiktokHandle,
        twitterHandle = user.twitterHandle,
        youtubeHandle = user.youtubeHandle
      )
      _isAiJobDetecting.value = false
      _aiJobAnalysisResult.value = result
      showMessage("Pekerjaan berhasil dianalisis AI: ${result.jobTitle} ✨")
    }
  }

  fun applyDetectedJobToProfile(result: AiJobAnalysisResult) {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      repository.saveDetectedJob(
        userId = user.id,
        jobTitle = result.jobTitle,
        confidence = result.confidence,
        summary = result.summary,
        skills = result.keySkills.joinToString(", "),
        niche = result.niche
      )
      _showAiJobDialog.value = false
      showMessage("Pekerjaan terverifikasi AI berhasil disimpan ke profil! ✨")
    }
  }

  private val _authError = MutableStateFlow<String?>(null)
  val authError = _authError.asStateFlow()

  private val _isAuthLoading = MutableStateFlow(false)
  val isAuthLoading = _isAuthLoading.asStateFlow()

  private val _snackbarMessage = MutableStateFlow<String?>(null)
  val snackbarMessage = _snackbarMessage.asStateFlow()

  fun setTab(tab: MainTab) {
    _currentTab.value = tab
  }

  fun setPlatformFilter(platform: SocialPlatform?) {
    _selectedPlatformFilter.value = platform
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun clearSnackbar() {
    _snackbarMessage.value = null
  }

  fun clearAuthError() {
    _authError.value = null
  }

  fun showMessage(msg: String) {
    _snackbarMessage.value = msg
  }

  // Auth Operations
  fun login(
    email: String,
    pass: String,
    onError: (String) -> Unit = {},
    onSuccess: () -> Unit = {}
  ) {
    _authError.value = null
    _isAuthLoading.value = true
    viewModelScope.launch {
      val res = repository.login(email, pass)
      _isAuthLoading.value = false
      res.onSuccess { user ->
        _currentUserId.value = user.id
        _authError.value = null
        showMessage("Selamat datang kembali, ${user.name}!")
        if (user.role == UserRole.ADMIN) {
          _currentTab.value = MainTab.ADMIN
        } else {
          _currentTab.value = MainTab.HOME
        }
        onSuccess()
      }.onFailure { err ->
        val msg = err.message ?: "Login gagal. Periksa kembali email dan kata sandi Anda."
        _authError.value = msg
        showMessage(msg)
        onError(msg)
      }
    }
  }

  fun quickLoginAsUser() {
    login("rian@gmail.com", "password123")
  }

  fun quickLoginAsAdmin() {
    login("admin@sosmedgrow.com", "admin123")
  }

  fun register(
    name: String,
    email: String,
    pass: String,
    instagramHandle: String = "",
    onError: (String) -> Unit = {},
    onSuccess: () -> Unit = {}
  ) {
    _authError.value = null
    _isAuthLoading.value = true
    viewModelScope.launch {
      val res = repository.register(name, email, pass, instagramHandle = instagramHandle)
      _isAuthLoading.value = false
      res.onSuccess { user ->
        _currentUserId.value = user.id
        _authError.value = null
        showMessage("Pendaftaran berhasil! Selamat datang, ${user.name}. Bonus 150 koin ditambahkan.")
        _currentTab.value = MainTab.HOME
        onSuccess()
      }.onFailure { err ->
        val msg = err.message ?: "Pendaftaran gagal."
        _authError.value = msg
        showMessage(msg)
        onError(msg)
      }
    }
  }

  fun openDailyCheckInDialog() {
    _showDailyCheckInDialog.value = true
  }

  fun dismissDailyCheckInDialog() {
    _showDailyCheckInDialog.value = false
  }

  fun claimDailyBonus() {
    val user = currentUser.value
    if (user == null) {
      showMessage("Silakan masuk terlebih dahulu.")
      return
    }

    viewModelScope.launch {
      val result = repository.performDailyCheckIn(user)
      result.onSuccess { checkInRes ->
        showMessage(checkInRes.message)
      }.onFailure { err ->
        val msg = err.message ?: "Gagal mengklaim bonus harian."
        showMessage(msg)
      }
    }
  }

  fun signInWithGoogle(
    onError: (String) -> Unit = {},
    onSuccess: (UserEntity) -> Unit = {}
  ) {
    _authError.value = null
    _isAuthLoading.value = true
    viewModelScope.launch {
      val res = firebaseAuthManager.signInWithGoogle()
      _isAuthLoading.value = false
      res.onSuccess { user ->
        _currentUserId.value = user.id
        _authError.value = null
        showMessage("Berhasil masuk dengan Google sebagai ${user.name}!")
        _currentTab.value = MainTab.HOME
        onSuccess(user)
      }.onFailure { err ->
        val msg = err.message ?: "Login Google dibatalkan atau gagal."
        _authError.value = msg
        showMessage(msg)
        onError(msg)
      }
    }
  }

  fun logout() {
    firebaseAuthManager.signOut()
    _currentUserId.value = null
    _currentTab.value = MainTab.HOME
    showMessage("Anda telah keluar.")
  }

  // Follow Operations (Anti-Cheat Enforced)
  fun openFollowDialog(campaign: CampaignEntity) {
    val user = currentUser.value
    if (user == null) {
      showMessage("Silakan login terlebih dahulu.")
      return
    }
    if (campaign.creatorId == user.id) {
      showMessage("Sistem Anti-Curang: Tidak dapat mem-follow kampanye milik sendiri!")
      return
    }
    _activeFollowCampaign.value = campaign
  }

  fun dismissFollowDialog() {
    _activeFollowCampaign.value = null
  }

  fun submitFollowVerification(handle: String) {
    val campaign = _activeFollowCampaign.value ?: return
    val user = currentUser.value ?: return

    viewModelScope.launch {
      val result = repository.processFollowAction(campaign.id, user, handle)
      showMessage(result.message)
      if (result.success) {
        _activeFollowCampaign.value = null
        // Trigger real Push Notification for new follower
        PushNotificationManager.showNewFollowerNotification(
          context = getApplication(),
          followerUsername = handle.removePrefix("@"),
          platform = campaign.platform.name,
          campaignTitle = campaign.accountHandle
        )
      }
    }
  }

  // Coin Exchange to Launch Campaigns
  fun openExchangeDialog() {
    _showExchangeDialog.value = true
  }

  fun dismissExchangeDialog() {
    _showExchangeDialog.value = false
  }

  fun executeCoinExchange(
    platform: SocialPlatform,
    targetUrl: String,
    accountHandle: String,
    category: String,
    targetFollowers: Int,
    coinReward: Int
  ) {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      val res = repository.exchangeCoinsForCampaign(
        creator = user,
        platform = platform,
        targetUrl = targetUrl,
        accountHandle = accountHandle,
        category = category,
        targetFollowers = targetFollowers,
        coinRewardPerFollow = coinReward
      )
      res.onSuccess {
        _showExchangeDialog.value = false
        showMessage("Kampanye berhasil dibuat! Target link Anda kini aktif di feed komunitas.")
        _currentTab.value = MainTab.TASKS
      }.onFailure { err ->
        showMessage(err.message ?: "Gagal menukar koin.")
      }
    }
  }

  // QRIS Commercial Shop
  fun openQrisShop(pkg: QrisCoinPackage? = null) {
    _selectedQrisPackage.value = pkg
    _showQrisShop.value = true
  }

  fun dismissQrisShop() {
    _showQrisShop.value = false
    _selectedQrisPackage.value = null
  }

  fun selectQrisPackage(pkg: QrisCoinPackage) {
    _selectedQrisPackage.value = pkg
  }

  fun confirmQrisPayment(pkg: QrisCoinPackage) {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      val ref = "QRIS-SG-" + (1000000..9999999).random()
      val res = repository.completeQrisPurchase(user.id, pkg, ref)
      res.onSuccess { coins ->
        _showQrisShop.value = false
        _selectedQrisPackage.value = null
        PushNotificationManager.showPaymentSuccessNotification(
          context = getApplication(),
          orderId = ref,
          gateway = "Midtrans / QRIS",
          coinsAdded = coins,
          amountIdr = pkg.priceIdr
        )
        showMessage("Pembayaran QRIS Berhasil! +$coins Koin telah masuk ke akun Anda. Terima kasih telah mendukung developer!")
      }.onFailure { err ->
        showMessage(err.message ?: "Transaksi QRIS gagal.")
      }
    }
  }

  // Cashout / Withdrawal
  fun submitWithdrawal(coinsAmount: Int, eWalletMethod: String, accountNumber: String, onSuccess: () -> Unit = {}) {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      val res = repository.executeWithdrawal(user.id, coinsAmount, eWalletMethod, accountNumber)
      res.onSuccess {
        showMessage("Penarikan $coinsAmount Koin (Rp ${coinsAmount * 100}) ke $eWalletMethod berhasil diproses!")
        onSuccess()
      }.onFailure { err ->
        showMessage(err.message ?: "Penarikan koin gagal.")
      }
    }
  }

  // Admin Management
  fun openAdminUserDialog(user: UserEntity) {
    _adminSelectedUser.value = user
  }

  fun dismissAdminUserDialog() {
    _adminSelectedUser.value = null
  }

  fun adminAdjustUserCoins(targetUserId: Int, delta: Int, reason: String) {
    viewModelScope.launch {
      val res = repository.adminAdjustCoins(targetUserId, delta, reason)
      res.onSuccess {
        _adminSelectedUser.value = null
        showMessage("Koin pengguna berhasil diperbarui: ${if (delta >= 0) "+$delta" else "$delta"}")
      }.onFailure {
        showMessage("Gagal memperbarui koin.")
      }
    }
  }

  fun adminToggleBan(user: UserEntity) {
    viewModelScope.launch {
      val newBanned = !user.isBanned
      database.userDao().updateReputation(
        userId = user.id,
        score = if (newBanned) 40.0f else 95.0f,
        violations = if (newBanned) user.violationsCount + 1 else user.violationsCount,
        isBanned = newBanned
      )
      showMessage(if (newBanned) "Pengguna telah dibekukan (Anti-Curang)." else "Blokir pengguna dicabut.")
    }
  }

  // Anti-Cheat Info
  fun setAntiCheatInfoVisible(visible: Boolean) {
    _showAntiCheatInfo.value = visible
  }

  fun reportCheater(verification: FollowVerificationEntity) {
    viewModelScope.launch {
      database.verificationDao().updateStatus(
        id = verification.id,
        status = VerificationStatus.FLAGGED_CHEATING,
        reason = "Terdeteksi Unfollow / Bot palsu oleh kreator"
      )
      // Penalize cheater
      database.userDao().adjustPoints(verification.followerUserId, -verification.coinsEarned)
      val follower = database.userDao().getUserByIdOnce(verification.followerUserId)
      if (follower != null) {
        val newScore = (follower.authenticityScore - 15.0f).coerceAtLeast(0f)
        val newViolations = follower.violationsCount + 1
        val shouldBan = newViolations >= 3 || newScore < 70f
        database.userDao().updateReputation(follower.id, newScore, newViolations, shouldBan)
      }
      showMessage("Laporan curang diproses! Koin pelanggar ditarik dan skor keaslian diturunkan.")
    }
  }

  fun confirmFollowerValid(verification: FollowVerificationEntity) {
    viewModelScope.launch {
      database.verificationDao().updateStatus(
        id = verification.id,
        status = VerificationStatus.VERIFIED_ACTIVE,
        reason = null
      )
      showMessage("Pengikut dikonfirmasi aktif & autentik.")
    }
  }
}
