package com.example.ui.auth

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GoogleLogoIcon
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldAuthentic
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.GoldCoinDark
import com.example.ui.theme.GoldCoinLight

@Composable
fun AuthScreen(
  onLogin: (email: String, pass: String) -> Unit,
  onRegister: (name: String, email: String, pass: String) -> Unit,
  onGoogleSignIn: () -> Unit = {},
  onQuickLoginUser: () -> Unit,
  onQuickLoginAdmin: () -> Unit
) {
  var isRegisterMode by remember { mutableStateOf(false) }
  var name by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // App Hero Logo
      Box(
        modifier = Modifier
          .size(72.dp)
          .clip(CircleShape)
          .background(
            Brush.linearGradient(
              listOf(ElectricIndigo, CyanAccent)
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.TrendingUp,
          contentDescription = "SosmedGrow",
          tint = Color.White,
          modifier = Modifier.size(40.dp)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "SosmedGrow",
        fontSize = 28.sp,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onBackground
      )

      Text(
        text = "Komunitas Saling Follow & Validasi Autentik Real-Time",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Main Form Card
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
        ) {
          Text(
            text = if (isRegisterMode) "Daftar Akun Baru (+150 Bonus Koin)" else "Masuk ke Akun Anda",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )

          Spacer(modifier = Modifier.height(16.dp))

          if (isRegisterMode) {
            OutlinedTextField(
              value = name,
              onValueChange = { name = it },
              label = { Text("Nama Lengkap") },
              singleLine = true,
              leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
              modifier = Modifier.fillMaxWidth().testTag("auth_name_input")
            )
            Spacer(modifier = Modifier.height(12.dp))
          }

          OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Alamat Email") },
            placeholder = { Text("contoh: rian@gmail.com") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().testTag("auth_email_input")
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Kata Sandi") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().testTag("auth_password_input")
          )

          if (errorMessage != null) {
            Text(
              text = errorMessage!!,
              color = MaterialTheme.colorScheme.error,
              fontSize = 12.sp,
              modifier = Modifier.padding(top = 8.dp)
            )
          }

          Spacer(modifier = Modifier.height(18.dp))

          Button(
            onClick = {
              if (email.isBlank() || password.isBlank()) {
                errorMessage = "Silakan isi semua bidang."
                return@Button
              }
              if (isRegisterMode) {
                if (name.isBlank()) {
                  errorMessage = "Nama wajib diisi."
                  return@Button
                }
                onRegister(name, email, password)
              } else {
                onLogin(email, password)
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("auth_submit_button")
          ) {
            Text(
              text = if (isRegisterMode) "Daftar & Klaim Koin" else "Masuk",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Divider
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
            )
            Text(
              text = "  atau  ",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Box(
              modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Google Sign-In Button
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .clip(RoundedCornerShape(12.dp))
              .clickable(onClick = onGoogleSignIn)
              .testTag("google_sign_in_button")
          ) {
            Row(
              modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              GoogleLogoIcon(size = 18.dp)
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = if (isRegisterMode) "Daftar dengan Google" else "Lanjutkan dengan Google",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          TextButton(
            onClick = {
              isRegisterMode = !isRegisterMode
              errorMessage = null
            },
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = if (isRegisterMode) "Sudah punya akun? Masuk" else "Belum punya akun? Daftar Baru (+150 Koin)",
              fontSize = 12.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Quick Demo Switcher Section
      Text(
        text = "— ATAU LOGIN CEPAT UJI COBA —",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Quick User Login
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldAuthentic.copy(alpha = 0.5f)),
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onQuickLoginUser)
            .testTag("quick_user_login_button")
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.Person,
              contentDescription = null,
              tint = EmeraldAuthentic,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Login sbg User",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Kreator (350 Koin)",
              fontSize = 10.sp,
              color = EmeraldAuthentic
            )
          }
        }

        // Quick Admin Login
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          border = androidx.compose.foundation.BorderStroke(1.dp, ElectricIndigo.copy(alpha = 0.5f)),
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onQuickLoginAdmin)
            .testTag("quick_admin_login_button")
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.AdminPanelSettings,
              contentDescription = null,
              tint = ElectricIndigo,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Login sbg Admin",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Kelola Koin & Sistem",
              fontSize = 10.sp,
              color = ElectricIndigo
            )
          }
        }
      }
    }
  }
}
