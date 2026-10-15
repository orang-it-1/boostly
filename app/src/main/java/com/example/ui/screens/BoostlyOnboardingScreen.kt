package com.example.ui.screens

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BoostlyIconMark
import com.example.ui.components.GoogleLogoIcon
import com.example.ui.components.LiveClockMiniBadge
import com.example.ui.components.rememberLiveClock
import com.example.ui.theme.BoostlyDarkBg
import com.example.ui.theme.BoostlyDarkCard
import com.example.ui.theme.BoostlyDarkGreenBtn
import com.example.ui.theme.BoostlyEmerald
import com.example.ui.theme.BoostlyForestGreen
import com.example.ui.theme.BoostlyGold
import com.example.ui.theme.BoostlyNeonLime
import com.example.ui.theme.BoostlyTextMuted
import com.example.ui.theme.BoostlyTextWhite

@Composable
fun BoostlyOnboardingScreen(
  errorMessage: String? = null,
  isLoading: Boolean = false,
  onLogin: (email: String, pass: String, onError: (String) -> Unit) -> Unit,
  onRegister: (name: String, email: String, pass: String, handle: String, onError: (String) -> Unit) -> Unit,
  onGoogleSignIn: () -> Unit = {},
  onQuickLoginUser: () -> Unit,
  onQuickLoginAdmin: () -> Unit,
  onClearError: () -> Unit = {}
) {
  val clockState = rememberLiveClock()

  // 0 = Masuk (Login), 1 = Daftar (Register)
  var selectedTab by remember { mutableStateOf(0) }

  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var isPasswordVisible by remember { mutableStateOf(false) }

  // Register Fields
  var name by remember { mutableStateOf("") }
  var instagramHandle by remember { mutableStateOf("") }

  // Local Form Validation Errors
  var localError by remember { mutableStateOf<String?>(null) }
  var localSuccess by remember { mutableStateOf<String?>(null) }

  val activeError = errorMessage ?: localError

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(
            Color(0xFF0F2619),
            BoostlyDarkBg,
            Color(0xFF08140D)
          )
        )
      )
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top Live Clock & Greeting Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = clockState.greetingIcon,
            contentDescription = null,
            tint = BoostlyGold,
            modifier = Modifier.size(16.dp)
          )
          Text(
            text = "${clockState.greeting}!",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = BoostlyTextWhite
          )
        }

        LiveClockMiniBadge(clockState = clockState, isDarkTheme = true)
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Boostly Hero Logo Mark
      BoostlyIconMark(size = 72.dp)

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "Boostly",
        fontSize = 32.sp,
        fontWeight = FontWeight.Black,
        color = BoostlyTextWhite,
        letterSpacing = 0.5.sp
      )

      Text(
        text = "Follow. Earn. Grow.",
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = BoostlyNeonLime
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Platform pertukaran follow dan analitik akun sosial media dengan verifikasi keaslian anti-curang.",
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        color = BoostlyTextMuted,
        textAlign = TextAlign.Center,
        lineHeight = 18.sp
      )

      Spacer(modifier = Modifier.height(20.dp))

      // MAIN AUTHENTICATION CARD (Real, Interactive, Validated)
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BoostlyDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, BoostlyForestGreen),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("auth_main_card")
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          // Pill Tabs: [ Masuk ] vs [ Daftar Akun ]
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0F2417),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1B4028)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(4.dp),
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              // Tab Masuk
              Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (selectedTab == 0) BoostlyNeonLime else Color.Transparent,
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(16.dp))
                  .clickable {
                    selectedTab = 0
                    localError = null
                    localSuccess = null
                    onClearError()
                  }
                  .testTag("tab_masuk")
              ) {
                Box(
                  modifier = Modifier.padding(vertical = 10.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "Masuk Akun",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTab == 0) Color(0xFF0A1910) else BoostlyTextMuted
                  )
                }
              }

              // Tab Daftar
              Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (selectedTab == 1) BoostlyNeonLime else Color.Transparent,
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(16.dp))
                  .clickable {
                    selectedTab = 1
                    localError = null
                    localSuccess = null
                    onClearError()
                  }
                  .testTag("tab_daftar")
              ) {
                Box(
                  modifier = Modifier.padding(vertical = 10.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "Daftar Baru",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTab == 1) Color(0xFF0A1910) else BoostlyTextMuted
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Error Banner (if any)
          AnimatedVisibility(
            visible = activeError != null,
            enter = fadeIn(),
            exit = fadeOut()
          ) {
            activeError?.let { msg ->
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF2E1212),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF5350).copy(alpha = 0.5f)),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(bottom = 12.dp)
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = Color(0xFFEF5350),
                    modifier = Modifier.size(18.dp)
                  )
                  Text(
                    text = msg,
                    fontSize = 12.sp,
                    color = Color(0xFFFFCDD2),
                    lineHeight = 16.sp
                  )
                }
              }
            }
          }

          // Register Bonus Banner
          if (selectedTab == 1) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color(0xFF132B1D),
              border = androidx.compose.foundation.BorderStroke(1.dp, BoostlyNeonLime.copy(alpha = 0.35f)),
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.MonetizationOn,
                  contentDescription = null,
                  tint = BoostlyGold,
                  modifier = Modifier.size(18.dp)
                )
                Text(
                  text = "Daftar sekarang & dapatkan Bonus +150 Koin Gratis!",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = BoostlyNeonLime
                )
              }
            }
          }

          // Form Field: Full Name (Only on Register)
          if (selectedTab == 1) {
            OutlinedTextField(
              value = name,
              onValueChange = {
                name = it
                localError = null
                onClearError()
              },
              label = { Text("Nama Lengkap", color = BoostlyTextMuted, fontSize = 12.sp) },
              placeholder = { Text("misal: Ahmad Fauzi", color = Color.Gray, fontSize = 12.sp) },
              leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null, tint = BoostlyNeonLime, modifier = Modifier.size(20.dp))
              },
              singleLine = true,
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = BoostlyNeonLime,
                unfocusedBorderColor = BoostlyForestGreen,
                focusedContainerColor = Color(0xFF0F2417),
                unfocusedContainerColor = Color(0xFF0F2417)
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_nama_lengkap")
            )
            Spacer(modifier = Modifier.height(14.dp))

          // Divider "atau"
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Color(0xFF1E3A28))
            )
            Text(
              text = "  atau masuk dengan  ",
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium,
              color = BoostlyTextMuted
            )
            Box(
              modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Color(0xFF1E3A28))
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Google Sign-In Button (Official Clean Layout)
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shadowElevation = 2.dp,
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .clip(RoundedCornerShape(16.dp))
              .clickable(enabled = !isLoading, onClick = onGoogleSignIn)
              .testTag("google_sign_in_button")
          ) {
            Row(
              modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              GoogleLogoIcon(size = 20.dp)
              Spacer(modifier = Modifier.width(12.dp))
              Text(
                text = if (selectedTab == 0) "Lanjutkan dengan Google" else "Daftar dengan Google",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))
          }

          // Form Field: Email
          OutlinedTextField(
            value = email,
            onValueChange = {
              email = it
              localError = null
              onClearError()
            },
            label = { Text("Alamat Email", color = BoostlyTextMuted, fontSize = 12.sp) },
            placeholder = { Text(if (selectedTab == 0) "rian@gmail.com" else "nama@email.com", color = Color.Gray, fontSize = 12.sp) },
            leadingIcon = {
              Icon(Icons.Default.Email, contentDescription = null, tint = BoostlyNeonLime, modifier = Modifier.size(20.dp))
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedBorderColor = BoostlyNeonLime,
              unfocusedBorderColor = BoostlyForestGreen,
              focusedContainerColor = Color(0xFF0F2417),
              unfocusedContainerColor = Color(0xFF0F2417)
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_email")
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Form Field: Password with Toggle Visibility
          OutlinedTextField(
            value = password,
            onValueChange = {
              password = it
              localError = null
              onClearError()
            },
            label = { Text("Kata Sandi", color = BoostlyTextMuted, fontSize = 12.sp) },
            placeholder = { Text(if (selectedTab == 0) "password123" else "Minimal 6 karakter", color = Color.Gray, fontSize = 12.sp) },
            leadingIcon = {
              Icon(Icons.Default.Lock, contentDescription = null, tint = BoostlyNeonLime, modifier = Modifier.size(20.dp))
            },
            trailingIcon = {
              IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(
                  imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                  contentDescription = if (isPasswordVisible) "Sembunyikan sandi" else "Tampilkan sandi",
                  tint = BoostlyTextMuted,
                  modifier = Modifier.size(20.dp)
                )
              }
            },
            singleLine = true,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedBorderColor = BoostlyNeonLime,
              unfocusedBorderColor = BoostlyForestGreen,
              focusedContainerColor = Color(0xFF0F2417),
              unfocusedContainerColor = Color(0xFF0F2417)
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_password")
          )

          // Form Field: Instagram Handle (Only on Register)
          if (selectedTab == 1) {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
              value = instagramHandle,
              onValueChange = {
                instagramHandle = it
                localError = null
                onClearError()
              },
              label = { Text("Username Instagram (Opsional)", color = BoostlyTextMuted, fontSize = 12.sp) },
              placeholder = { Text("@username_kamu", color = Color.Gray, fontSize = 12.sp) },
              leadingIcon = {
                Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = BoostlyGold, modifier = Modifier.size(20.dp))
              },
              singleLine = true,
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = BoostlyNeonLime,
                unfocusedBorderColor = BoostlyForestGreen,
                focusedContainerColor = Color(0xFF0F2417),
                unfocusedContainerColor = Color(0xFF0F2417)
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_instagram_handle")
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Submit Action Button
          Button(
            onClick = {
              localError = null
              onClearError()

              // Validate Form
              val trimmedEmail = email.trim()
              val trimmedPass = password.trim()

              if (selectedTab == 0) {
                // Login validation
                if (trimmedEmail.isBlank()) {
                  localError = "Silakan masukkan alamat email Anda."
                  return@Button
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
                  localError = "Format email tidak valid (contoh: user@gmail.com)."
                  return@Button
                }
                if (trimmedPass.isBlank()) {
                  localError = "Silakan masukkan kata sandi Anda."
                  return@Button
                }

                onLogin(trimmedEmail, trimmedPass) { err ->
                  localError = err
                }
              } else {
                // Register validation
                val trimmedName = name.trim()
                if (trimmedName.length < 2) {
                  localError = "Nama lengkap minimal 2 karakter."
                  return@Button
                }
                if (trimmedEmail.isBlank()) {
                  localError = "Silakan masukkan alamat email Anda."
                  return@Button
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
                  localError = "Format email tidak valid (contoh: user@gmail.com)."
                  return@Button
                }
                if (trimmedPass.length < 6) {
                  localError = "Kata sandi minimal 6 karakter demi keamanan akun."
                  return@Button
                }

                onRegister(trimmedName, trimmedEmail, trimmedPass, instagramHandle.trim()) { err ->
                  localError = err
                }
              }
            },
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
              containerColor = BoostlyNeonLime,
              contentColor = Color(0xFF0A1910),
              disabledContainerColor = BoostlyForestGreen,
              disabledContentColor = BoostlyTextMuted
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("auth_submit_button")
          ) {
            if (isLoading) {
              CircularProgressIndicator(
                color = Color(0xFF0A1910),
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text("Memproses...", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            } else {
              Text(
                text = if (selectedTab == 0) "Masuk Sekarang" else "Daftar & Dapatkan 150 Koin",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Bottom Switch Text
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (selectedTab == 0) "Belum memiliki akun?" else "Sudah memiliki akun?",
              fontSize = 12.sp,
              color = BoostlyTextMuted
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (selectedTab == 0) "Daftar di sini" else "Masuk di sini",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = BoostlyNeonLime,
              modifier = Modifier
                .clickable {
                  selectedTab = if (selectedTab == 0) 1 else 0
                  localError = null
                  onClearError()
                }
                .padding(4.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // QUICK DEMO ACCOUNTS HELPER PANEL
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1F13)),
        border = androidx.compose.foundation.BorderStroke(1.dp, BoostlyForestGreen),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = BoostlyEmerald,
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = "Akun Pengujian Demo (Sekali Klik):",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = BoostlyTextWhite
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Button 1: User Demo Rian
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = BoostlyDarkCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1B4028)),
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                email = "rian@gmail.com"
                password = "password123"
                onQuickLoginUser()
              }
              .testTag("quick_login_user_chip")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(BoostlyForestGreen),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = BoostlyNeonLime,
                    modifier = Modifier.size(18.dp)
                  )
                }
                Column {
                  Text(
                    text = "Pengguna Komunitas: Rian Pratama",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BoostlyTextWhite
                  )
                  Text(
                    text = "rian@gmail.com • pass: password123",
                    fontSize = 10.sp,
                    color = BoostlyTextMuted
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = BoostlyNeonLime.copy(alpha = 0.15f)
              ) {
                Text(
                  text = "Masuk →",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = BoostlyNeonLime,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Button 2: Admin Demo
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = BoostlyDarkCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1B4028)),
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                email = "admin@sosmedgrow.com"
                password = "admin123"
                onQuickLoginAdmin()
              }
              .testTag("quick_login_admin_chip")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(BoostlyGold.copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = BoostlyGold,
                    modifier = Modifier.size(18.dp)
                  )
                }
                Column {
                  Text(
                    text = "Administrator Sistem",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BoostlyGold
                  )
                  Text(
                    text = "admin@sosmedgrow.com • pass: admin123",
                    fontSize = 10.sp,
                    color = BoostlyTextMuted
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = BoostlyGold.copy(alpha = 0.15f)
              ) {
                Text(
                  text = "Admin →",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = BoostlyGold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // 4 Feature Pillars
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FeaturePillarItem(
          icon = Icons.Default.Group,
          title = "Saling Follow",
          desc = "Follow akun target komunitas",
          modifier = Modifier.weight(1f)
        )
        FeaturePillarItem(
          icon = Icons.Default.MonetizationOn,
          title = "Dapatkan Koin",
          desc = "Verifikasi tugas sah & autentik",
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FeaturePillarItem(
          icon = Icons.Default.NearMe,
          title = "Tukar Koin",
          desc = "Dapatkan ribuan follower asli",
          modifier = Modifier.weight(1f)
        )
        FeaturePillarItem(
          icon = Icons.Default.TrendingUp,
          title = "Analitik Nyata",
          desc = "Pantau pertumbuhan real-time",
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}

@Composable
fun FeaturePillarItem(
  icon: ImageVector,
  title: String,
  desc: String,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = BoostlyDarkCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1B3826)),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(BoostlyForestGreen),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = BoostlyNeonLime,
          modifier = Modifier.size(18.dp)
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = BoostlyTextWhite
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = desc,
        fontSize = 10.sp,
        color = BoostlyTextMuted,
        lineHeight = 13.sp
      )
    }
  }
}
