package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.UserRole
import com.example.ui.dialogs.AdminAddCoinDialog
import com.example.ui.dialogs.AiJobDetectorDialog
import com.example.ui.dialogs.AntiCheatInfoDialog
import com.example.ui.dialogs.CreateCampaignDialog
import com.example.ui.dialogs.DailyCheckInDialog
import com.example.ui.dialogs.FollowActionDialog
import com.example.ui.dialogs.LaravelDatabaseSyncDialog
import com.example.ui.dialogs.NotificationCenterDialog
import com.example.ui.dialogs.QrisShopDialog
import com.example.ui.dialogs.ThemeSelectionDialog
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.BoostlyHomeScreen
import com.example.ui.screens.BoostlyOnboardingScreen
import com.example.ui.screens.BoostlyProfileDetailScreen
import com.example.ui.screens.BoostlyTasksScreen
import com.example.ui.screens.BoostlyWalletScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ValidationAuditScreen
import com.example.ui.theme.BoostlyAppBg
import com.example.ui.theme.BoostlyDarkBg
import com.example.ui.theme.BoostlyDarkCard
import com.example.ui.theme.BoostlyDarkGreenBtn
import com.example.ui.theme.BoostlyForestGreen
import com.example.ui.theme.BoostlyGold
import com.example.ui.theme.BoostlyGoldDark
import com.example.ui.theme.BoostlyGoldLight
import com.example.ui.theme.BoostlyNeonLime
import com.example.ui.theme.BoostlyTextLightGray
import com.example.ui.theme.BoostlyTextMuted
import com.example.ui.theme.isThemeDark
import com.example.ui.theme.BoostlyTextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SosmedMainScreen(viewModel: SosmedViewModel) {
  val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
  val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
  val selectedDetailHandle by viewModel.selectedDetailHandle.collectAsStateWithLifecycle()

  val allCampaigns by viewModel.allCampaigns.collectAsStateWithLifecycle()
  val myCampaigns by viewModel.myCampaigns.collectAsStateWithLifecycle()
  val myFollowHistory by viewModel.myFollowHistory.collectAsStateWithLifecycle()
  val creatorVerifications by viewModel.creatorVerifications.collectAsStateWithLifecycle()
  val transactions by viewModel.userTransactions.collectAsStateWithLifecycle()

  // Admin Data
  val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
  val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()

  // Dialog states
  val activeFollowCampaign by viewModel.activeFollowCampaign.collectAsStateWithLifecycle()
  val showExchangeDialog by viewModel.showExchangeDialog.collectAsStateWithLifecycle()
  val showQrisShop by viewModel.showQrisShop.collectAsStateWithLifecycle()
  val selectedQrisPackage by viewModel.selectedQrisPackage.collectAsStateWithLifecycle()
  val adminSelectedUser by viewModel.adminSelectedUser.collectAsStateWithLifecycle()
  val showAntiCheatInfo by viewModel.showAntiCheatInfo.collectAsStateWithLifecycle()
  val dailyEvaluation by viewModel.dailyCheckInEvaluation.collectAsStateWithLifecycle()
  val totalCheckInsCount by viewModel.totalCheckInsCount.collectAsStateWithLifecycle()
  val showDailyCheckInDialog by viewModel.showDailyCheckInDialog.collectAsStateWithLifecycle()
  val showLaravelSyncDialog by viewModel.showLaravelSyncDialog.collectAsStateWithLifecycle()
  val laravelConnectionStatus by viewModel.laravelConnectionStatus.collectAsStateWithLifecycle()
  val laravelSyncStatus by viewModel.laravelSyncStatus.collectAsStateWithLifecycle()
  val laravelBaseUrl by viewModel.laravelBaseUrl.collectAsStateWithLifecycle()
  val laravelApiToken by viewModel.laravelApiToken.collectAsStateWithLifecycle()

  val showAiJobDialog by viewModel.showAiJobDialog.collectAsStateWithLifecycle()
  val isAiJobDetecting by viewModel.isAiJobDetecting.collectAsStateWithLifecycle()
  val aiJobAnalysisResult by viewModel.aiJobAnalysisResult.collectAsStateWithLifecycle()

  val showNotificationCenter by viewModel.showNotificationCenter.collectAsStateWithLifecycle()
  val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
  val showThemeDialog by viewModel.showThemeDialog.collectAsStateWithLifecycle()

  val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(snackbarMessage) {
    snackbarMessage?.let {
      snackbarHostState.showSnackbar(it)
      viewModel.clearSnackbar()
    }
  }

  // Handle back navigation for secondary screens
  BackHandler(enabled = currentTab != MainTab.HOME) {
    viewModel.setTab(MainTab.HOME)
  }

  val authError by viewModel.authError.collectAsStateWithLifecycle()
  val isAuthLoading by viewModel.isAuthLoading.collectAsStateWithLifecycle()

  // If user is not logged in, show Boostly Onboarding Screen (matching top right phone mockup)
  if (currentUser == null) {
    BoostlyOnboardingScreen(
      errorMessage = authError,
      isLoading = isAuthLoading,
      onLogin = { email, pass, onError -> viewModel.login(email, pass, onError) },
      onRegister = { name, email, pass, handle, onError ->
        viewModel.register(name, email, pass, instagramHandle = handle, onError = onError)
      },
      onGoogleSignIn = { viewModel.signInWithGoogle() },
      onQuickLoginUser = { viewModel.quickLoginAsUser() },
      onQuickLoginAdmin = { viewModel.quickLoginAsAdmin() },
      onClearError = { viewModel.clearAuthError() }
    )
    return
  }

  val user = currentUser!!

  val isDark = isThemeDark()
  val navItemColors = NavigationBarItemDefaults.colors(
    selectedIconColor = if (isDark) BoostlyNeonLime else BoostlyDarkGreenBtn,
    selectedTextColor = if (isDark) BoostlyNeonLime else BoostlyDarkGreenBtn,
    unselectedIconColor = if (isDark) Color(0xFFA3B8AB) else BoostlyTextLightGray,
    unselectedTextColor = if (isDark) Color(0xFFA3B8AB) else BoostlyTextLightGray,
    indicatorColor = Color.Transparent
  )

  Scaffold(
    containerColor = MaterialTheme.colorScheme.background,
    bottomBar = {
      // Boostly Bottom Navigation matching Phone 1 in the mockup (Theme Adaptive with Floating Coin)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surface)
          .border(androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF1B3D2A) else Color(0xFFF3F4F6)))
          .windowInsetsPadding(WindowInsets.navigationBars)
      ) {
        NavigationBar(
          containerColor = MaterialTheme.colorScheme.surface,
          tonalElevation = 0.dp,
          modifier = Modifier.fillMaxWidth()
        ) {
          // Tab 1: Beranda (Home)
          NavigationBarItem(
            selected = currentTab == MainTab.HOME,
            onClick = { viewModel.setTab(MainTab.HOME) },
            icon = {
              Icon(
                imageVector = if (currentTab == MainTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                contentDescription = "Beranda"
              )
            },
            label = {
              Text(
                "Beranda",
                fontSize = 10.sp,
                fontWeight = if (currentTab == MainTab.HOME) FontWeight.Bold else FontWeight.Medium
              )
            },
            colors = navItemColors,
            modifier = Modifier.testTag("nav_beranda")
          )

          // Tab 2: Tugas (Tasks)
          NavigationBarItem(
            selected = currentTab == MainTab.TASKS || currentTab == MainTab.DETAIL,
            onClick = { viewModel.setTab(MainTab.TASKS) },
            icon = {
              Icon(
                imageVector = if (currentTab == MainTab.TASKS) Icons.Filled.Assignment else Icons.Outlined.Assignment,
                contentDescription = "Tugas"
              )
            },
            label = {
              Text(
                "Tugas",
                fontSize = 10.sp,
                fontWeight = if (currentTab == MainTab.TASKS) FontWeight.Bold else FontWeight.Medium
              )
            },
            colors = navItemColors,
            modifier = Modifier.testTag("nav_tugas")
          )

          // Tab 3: Center Floating Coin Button (Tukar Koin / Dompet)
          NavigationBarItem(
            selected = currentTab == MainTab.WALLET,
            onClick = { viewModel.setTab(MainTab.WALLET) },
            icon = {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
                  .background(
                    androidx.compose.ui.graphics.Brush.linearGradient(
                      listOf(BoostlyGoldLight, BoostlyGold, BoostlyGoldDark)
                    )
                  )
                  .border(2.5.dp, BoostlyNeonLime, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.MonetizationOn,
                  contentDescription = "Koin",
                  tint = Color(0xFF78350F),
                  modifier = Modifier.size(24.dp)
                )
              }
            },
            label = {
              Text(
                "",
                fontSize = 1.sp
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = BoostlyGold,
              selectedTextColor = BoostlyGold,
              unselectedIconColor = BoostlyGold,
              unselectedTextColor = BoostlyGold,
              indicatorColor = Color.Transparent
            ),
            modifier = Modifier.testTag("nav_koin_center")
          )

          // Tab 4: Teman / Analitik
          NavigationBarItem(
            selected = currentTab == MainTab.ANALYTICS,
            onClick = { viewModel.setTab(MainTab.ANALYTICS) },
            icon = {
              Icon(
                imageVector = if (currentTab == MainTab.ANALYTICS) Icons.Filled.Group else Icons.Outlined.Group,
                contentDescription = "Teman"
              )
            },
            label = {
              Text(
                "Teman",
                fontSize = 10.sp,
                fontWeight = if (currentTab == MainTab.ANALYTICS) FontWeight.Bold else FontWeight.Medium
              )
            },
            colors = navItemColors,
            modifier = Modifier.testTag("nav_teman_analytics")
          )

          // Tab 5: Profil / Admin
          val isProfileActive = currentTab == MainTab.PROFILE || currentTab == MainTab.ADMIN
          NavigationBarItem(
            selected = isProfileActive,
            onClick = {
              if (user.role == UserRole.ADMIN) {
                viewModel.setTab(MainTab.ADMIN)
              } else {
                viewModel.setTab(MainTab.PROFILE)
              }
            },
            icon = {
              Icon(
                imageVector = if (user.role == UserRole.ADMIN) Icons.Default.AdminPanelSettings else if (isProfileActive) Icons.Filled.AccountCircle else Icons.Outlined.AccountCircle,
                contentDescription = "Profil"
              )
            },
            label = {
              Text(
                if (user.role == UserRole.ADMIN) "Admin" else "Profil",
                fontSize = 10.sp,
                fontWeight = if (isProfileActive) FontWeight.Bold else FontWeight.Medium
              )
            },
            colors = navItemColors,
            modifier = Modifier.testTag("nav_profil")
          )
        }
      }
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
    modifier = Modifier.fillMaxSize()
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      Crossfade(targetState = currentTab, label = "boostly_tab_crossfade") { tab ->
        when (tab) {
          MainTab.HOME -> {
            BoostlyHomeScreen(
              user = user,
              campaigns = allCampaigns,
              dailyEvaluation = dailyEvaluation,
              totalCheckInsCount = totalCheckInsCount,
              transactions = transactions,
              laravelConnectionStatus = laravelConnectionStatus,
              laravelSyncStatus = laravelSyncStatus,
              onNavigateToTasks = { viewModel.setTab(MainTab.TASKS) },
              onNavigateToWallet = { viewModel.setTab(MainTab.WALLET) },
              onNavigateToDetail = { handle -> viewModel.navigateToDetail(handle) },
              onShowAntiCheatInfo = { viewModel.setAntiCheatInfoVisible(true) },
              onOpenQrisShop = { viewModel.openQrisShop() },
              onClaimDailyBonus = { viewModel.claimDailyBonus() },
              onOpenDailyCheckInDialog = { viewModel.openDailyCheckInDialog() },
              onOpenLaravelSyncDialog = { viewModel.openLaravelSyncDialog() },
              onOpenAiJobDetector = { viewModel.openAiJobDialog() },
              onOpenNotificationCenter = { viewModel.openNotificationCenter() },
              onToggleTheme = { viewModel.toggleTheme() },
              onOpenThemeDialog = { viewModel.openThemeDialog() }
            )
          }
          MainTab.TASKS -> {
            BoostlyTasksScreen(
              campaigns = allCampaigns,
              history = myFollowHistory,
              onBack = { viewModel.setTab(MainTab.HOME) },
              onOpenFollowTask = { campaign -> viewModel.openFollowDialog(campaign) },
              onOpenProfileDetail = { handle -> viewModel.navigateToDetail(handle) }
            )
          }
          MainTab.DETAIL -> {
            BoostlyProfileDetailScreen(
              handle = selectedDetailHandle,
              category = "Fashion & Lifestyle",
              onBack = { viewModel.setTab(MainTab.TASKS) },
              onFollowAction = { campaign -> viewModel.openFollowDialog(campaign) }
            )
          }
          MainTab.WALLET -> {
            BoostlyWalletScreen(
              user = user,
              transactions = transactions,
              onBack = { viewModel.setTab(MainTab.HOME) },
              onOpenExchangeFollowers = { viewModel.openExchangeDialog() },
              onOpenQrisShop = { viewModel.openQrisShop() },
              onWithdraw = { amount, method, account ->
                viewModel.submitWithdrawal(amount, method, account)
              },
              onClaimDailyBonus = { viewModel.claimDailyBonus() }
            )
          }
          MainTab.ANALYTICS -> {
            Column(modifier = Modifier.fillMaxSize()) {
              AnalyticsScreen(
                user = user,
                myCampaigns = myCampaigns
              )
            }
          }
          MainTab.ADMIN -> {
            AdminDashboardScreen(
              users = allUsers,
              campaigns = allCampaigns,
              orders = allOrders,
              onSelectUserToAdjustCoins = { viewModel.openAdminUserDialog(it) },
              onToggleUserBan = { viewModel.adminToggleBan(it) },
              onOpenLaravelSync = { viewModel.openLaravelSyncDialog() }
            )
          }
          MainTab.PROFILE -> {
            ProfileScreen(
              user = user,
              transactions = transactions,
              onOpenQrisShop = { viewModel.openQrisShop() },
              onShowAntiCheatInfo = { viewModel.setAntiCheatInfoVisible(true) },
              onSwitchToAdminDemo = { viewModel.quickLoginAsAdmin() },
              onLogout = { viewModel.logout() },
              onOpenLaravelSync = { viewModel.openLaravelSyncDialog() },
              onOpenAiJobDetector = { viewModel.openAiJobDialog() },
              themeMode = themeMode,
              onToggleTheme = { viewModel.toggleTheme() },
              onOpenThemeDialog = { viewModel.openThemeDialog() }
            )
          }
        }
      }
    }
  }

  // Active Dialogs & Modals
  activeFollowCampaign?.let { campaign ->
    FollowActionDialog(
      campaign = campaign,
      defaultHandle = when (campaign.platform) {
        com.example.data.model.SocialPlatform.INSTAGRAM -> user.instagramHandle
        com.example.data.model.SocialPlatform.TIKTOK -> user.tiktokHandle
        else -> "@${user.name.lowercase().replace(" ", "_")}"
      },
      onDismiss = { viewModel.dismissFollowDialog() },
      onVerifyAndClaim = { handle -> viewModel.submitFollowVerification(handle) }
    )
  }

  if (showExchangeDialog) {
    CreateCampaignDialog(
      user = user,
      onDismiss = { viewModel.dismissExchangeDialog() },
      onConfirmExchange = { platform, targetUrl, accountHandle, category, targetFollowers, coinReward ->
        viewModel.executeCoinExchange(
          platform,
          targetUrl,
          accountHandle,
          category,
          targetFollowers,
          coinReward
        )
      }
    )
  }

  if (showQrisShop) {
    QrisShopDialog(
      packages = viewModel.repository.availableQrisPackages,
      initiallySelectedPackage = selectedQrisPackage,
      paymentGatewayManager = viewModel.paymentGatewayManager,
      onDismiss = { viewModel.dismissQrisShop() },
      onConfirmPurchase = { pkg -> viewModel.confirmQrisPayment(pkg) }
    )
  }

  adminSelectedUser?.let { selectedUser ->
    AdminAddCoinDialog(
      user = selectedUser,
      onDismiss = { viewModel.dismissAdminUserDialog() },
      onConfirmAdjust = { amount, reason ->
        viewModel.adminAdjustUserCoins(selectedUser.id, amount, reason)
      }
    )
  }

  if (showAntiCheatInfo) {
    AntiCheatInfoDialog(
      onDismiss = { viewModel.setAntiCheatInfoVisible(false) }
    )
  }

  if (showDailyCheckInDialog) {
    DailyCheckInDialog(
      evaluation = dailyEvaluation,
      totalCheckInsCount = totalCheckInsCount,
      onClaim = { viewModel.claimDailyBonus() },
      onDismiss = { viewModel.dismissDailyCheckInDialog() }
    )
  }

  if (showLaravelSyncDialog) {
    LaravelDatabaseSyncDialog(
      connectionStatus = laravelConnectionStatus,
      syncStatus = laravelSyncStatus,
      savedBaseUrl = laravelBaseUrl,
      savedApiToken = laravelApiToken,
      syncManager = viewModel.laravelSyncManager,
      onDismiss = { viewModel.dismissLaravelSyncDialog() },
      onTestConnection = { url, token -> viewModel.testLaravelConnection(url, token) },
      onSyncDatabase = { viewModel.syncWithLaravel() },
      onSaveConfig = { url, token -> viewModel.updateLaravelConfig(url, token) },
      onResetSyncStatus = { viewModel.resetLaravelSyncStatus() }
    )
  }

  if (showAiJobDialog) {
    AiJobDetectorDialog(
      user = user,
      isLoading = isAiJobDetecting,
      analysisResult = aiJobAnalysisResult,
      onDetect = { desc -> viewModel.detectUserJobWithAi(desc) },
      onApplyResult = { result -> viewModel.applyDetectedJobToProfile(result) },
      onDismiss = { viewModel.dismissAiJobDialog() }
    )
  }

  if (showNotificationCenter) {
    NotificationCenterDialog(
      onDismiss = { viewModel.dismissNotificationCenter() }
    )
  }

  if (showThemeDialog) {
    ThemeSelectionDialog(
      currentMode = themeMode,
      onSelectMode = { mode -> viewModel.setThemeMode(mode) },
      onDismiss = { viewModel.dismissThemeDialog() }
    )
  }
}
