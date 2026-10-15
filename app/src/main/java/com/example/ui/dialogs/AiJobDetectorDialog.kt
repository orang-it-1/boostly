package com.example.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.ai.AiJobAnalysisResult
import com.example.data.model.UserEntity
import com.example.ui.theme.BoostlyDarkGreenBtn
import com.example.ui.theme.BoostlyDarkGreenCard
import com.example.ui.theme.BoostlyEmerald
import com.example.ui.theme.BoostlyGold
import com.example.ui.theme.BoostlyGoldDark
import com.example.ui.theme.BoostlyTextDark
import com.example.ui.theme.BoostlyTextGray
import com.example.ui.theme.BoostlyTextMuted
import com.example.ui.theme.ElectricIndigo

@Composable
fun AiJobDetectorDialog(
  user: UserEntity?,
  isLoading: Boolean,
  analysisResult: AiJobAnalysisResult?,
  onDetect: (description: String) -> Unit,
  onApplyResult: (AiJobAnalysisResult) -> Unit,
  onDismiss: () -> Unit
) {
  var inputDescription by remember {
    mutableStateOf(
      user?.jobAnalysisSummary?.ifBlank {
        "Saya aktif membuat konten video ulasan teknologi, unboxing gadget terbaru, dan berbagi tips seputar dunia digital."
      } ?: ""
    )
  }

  val infiniteTransition = rememberInfiniteTransition(label = "ai_spin")
  val spinAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "ai_spin_angle"
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(26.dp),
      color = Color.White,
      shadowElevation = 12.dp,
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .padding(vertical = 20.dp)
        .testTag("ai_job_detector_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Header Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(
                  Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF3B82F6)))
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
              )
            }
            Column {
              Text(
                text = "Deteksi Pekerjaan AI ✨",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = BoostlyTextDark
              )
              Text(
                text = "Gemini AI Job & Creator Niche Detector",
                fontSize = 11.sp,
                color = BoostlyTextMuted
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(36.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Tutup",
              tint = BoostlyTextGray
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Body Content
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 520.dp)
            .verticalScroll(rememberScrollState())
        ) {
          // Connected Handles Preview
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                text = "Data Profil yang Dianalisis AI:",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = BoostlyTextMuted
              )
              Spacer(modifier = Modifier.height(6.dp))
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                if (!user?.instagramHandle.isNullOrBlank()) {
                  SocialBadge(icon = "📸", text = user?.instagramHandle ?: "")
                }
                if (!user?.tiktokHandle.isNullOrBlank()) {
                  SocialBadge(icon = "🎵", text = user?.tiktokHandle ?: "")
                }
                if (!user?.youtubeHandle.isNullOrBlank()) {
                  SocialBadge(icon = "▶️", text = user?.youtubeHandle ?: "")
                }
                SocialBadge(icon = "👤", text = user?.name ?: "Creator")
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Description Input
          Text(
            text = "Ceritakan Pekerjaan, Konten, atau Bisnis Anda:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = BoostlyTextDark
          )
          Spacer(modifier = Modifier.height(4.dp))
          OutlinedTextField(
            value = inputDescription,
            onValueChange = { inputDescription = it },
            placeholder = { Text("Contoh: Saya sering review gadget dan unboxing laptop...") },
            minLines = 3,
            maxLines = 4,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = Color(0xFF8B5CF6),
              unfocusedBorderColor = Color(0xFFE2E8F0)
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("ai_job_description_input")
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Preset Chips for Instant Testing
          Text(
            text = "Contoh Cepat (Klik untuk mencoba):",
            fontSize = 11.sp,
            color = BoostlyTextMuted
          )
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            PresetChip(
              label = "Gadget & Tech",
              onClick = {
                inputDescription = "Saya sering review smartphone, gadget terbaru, unboxing laptop, dan tutorial software di YouTube dan TikTok."
              }
            )
            PresetChip(
              label = "Fashion & OOTD",
              onClick = {
                inputDescription = "Saya punya brand hijab lokal sendiri dan suka sharing tips OOTD busana muslimah kekinian di Instagram."
              }
            )
            PresetChip(
              label = "Kuliner & Cafe",
              onClick = {
                inputDescription = "Food vlogger kuliner nusantara, review makanan viral, dan hunting cafe aesthetic di kota."
              }
            )
            PresetChip(
              label = "Gaming & Streamer",
              onClick = {
                inputDescription = "Live streamer Mobile Legends dan Valorant setiap malam, suka bikin klip gameplay epic montage."
              }
            )
            PresetChip(
              label = "Pebisnis Online",
              onClick = {
                inputDescription = "Seller e-commerce aktif di Shopee dan TikTok Live, jualan pakaian dan produk skincare."
              }
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Action Button: Deteksi dengan AI
          Button(
            onClick = { onDetect(inputDescription) },
            enabled = !isLoading && inputDescription.isNotBlank(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF6366F1),
              disabledContainerColor = Color(0xFFE2E8F0)
            ),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("detect_job_with_ai_button")
          ) {
            if (isLoading) {
              CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = Color.White,
                strokeWidth = 2.dp
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text("Gemini AI Sedang Menganalisis...", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            } else {
              Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text("Deteksi Pekerjaan dengan AI ✨", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
          }

          // Result Card
          AnimatedVisibility(visible = analysisResult != null) {
            analysisResult?.let { result ->
              Spacer(modifier = Modifier.height(16.dp))
              Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFC084FC)),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("ai_job_result_card")
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF8B5CF6))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                      Text(
                        text = "HASIL DETEKSI AI",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                      )
                    }

                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFDCFCE7))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                      Text(
                        text = "Akurasi ${(result.confidence * 100).toInt()}%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF15803D)
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(10.dp))

                  // Job Title
                  Text(
                    text = result.jobTitle,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF4C1D95)
                  )

                  Text(
                    text = "Niche: ${result.niche}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF7C3AED)
                  )

                  Spacer(modifier = Modifier.height(8.dp))

                  // Summary
                  Text(
                    text = result.summary,
                    fontSize = 11.sp,
                    color = BoostlyTextDark,
                    lineHeight = 16.sp
                  )

                  Spacer(modifier = Modifier.height(10.dp))

                  // Key skills
                  Text(
                    text = "Keahlian Kunci Terdeteksi:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BoostlyTextDark
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    result.keySkills.forEach { skill ->
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(6.dp))
                          .background(Color.White)
                          .border(1.dp, Color(0xFFDDD6FE), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                      ) {
                        Text(text = skill, fontSize = 10.sp, color = Color(0xFF6D28D9), fontWeight = FontWeight.Medium)
                      }
                    }
                  }

                  Spacer(modifier = Modifier.height(10.dp))

                  // Earning Potential
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.MonetizationOn,
                      contentDescription = null,
                      tint = BoostlyGoldDark,
                      modifier = Modifier.size(16.dp)
                    )
                    Text(
                      text = "Potensi Koin: ${result.earningPotential}",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = BoostlyTextDark
                    )
                  }

                  Spacer(modifier = Modifier.height(14.dp))

                  // Button apply to profile
                  Button(
                    onClick = { onApplyResult(result) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BoostlyDarkGreenBtn),
                    modifier = Modifier
                      .fillMaxWidth()
                      .testTag("apply_detected_job_btn")
                  ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Terapkan ke Profil Saya ✨", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Dismiss button
        Button(
          onClick = onDismiss,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F4F6)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Tutup", color = BoostlyTextDark, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
      }
    }
  }
}

@Composable
private fun SocialBadge(icon: String, text: String) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(Color.White)
      .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
      .padding(horizontal = 8.dp, vertical = 3.dp)
  ) {
    Text(text = "$icon $text", fontSize = 10.sp, color = BoostlyTextDark, fontWeight = FontWeight.Medium)
  }
}

@Composable
private fun PresetChip(label: String, onClick: () -> Unit) {
  FilterChip(
    selected = false,
    onClick = onClick,
    label = { Text(label, fontSize = 10.sp) },
    colors = FilterChipDefaults.filterChipColors(
      containerColor = Color(0xFFF1F5F9),
      labelColor = BoostlyTextDark
    )
  )
}
