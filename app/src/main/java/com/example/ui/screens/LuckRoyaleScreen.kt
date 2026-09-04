package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.GeneratorConfig
import com.example.model.LuckRoyaleEvent
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberGoldDark
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.LocalAppColors
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.PrimeGold
import com.example.ui.theme.PrimePurple

@Composable
fun LuckRoyaleScreen(
  events: List<LuckRoyaleEvent>,
  selectedEventId: String,
  config: GeneratorConfig,
  isLiveSyncing: Boolean,
  lastSyncTimeText: String,
  onSelectEvent: (String) -> Unit,
  onSyncLive: () -> Unit,
  onSimulateServerUpdate: () -> Unit,
  onToggleFreeSpin: (String, Boolean) -> Unit,
  onSetTargetSpin: (String) -> Unit,
  onTestSpin: () -> Unit,
  onGoToGenerator: () -> Unit
) {
  val colors = LocalAppColors.current
  val selectedEvent = events.find { it.id == selectedEventId } ?: events.firstOrNull()
  val freeEventsCount = events.count { it.isFreeSpinAvailable }

  val infiniteTransition = rememberInfiniteTransition(label = "glow")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.96f,
    targetValue = 1.04f,
    animationSpec = infiniteRepeatable(
      animation = tween(900, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse"
  )

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(colors.bg)
      .padding(horizontal = 14.dp),
    contentPadding = PaddingValues(top = 10.dp, bottom = 28.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Live Sync & Server Header
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.border)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(10.dp)
                  .clip(CircleShape)
                  .background(NeonGreen)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "লাক রয়্যাল লাইভ সিঙ্ক",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colors.textPrimary
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (colors.isDark) Color(0xFF0F3A22) else Color(0xFFDCFCE7))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = "$freeEventsCount টি অপশনে ফ্রি স্পিন",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (colors.isDark) NeonGreen else Color(0xFF15803D)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "ফ্রি ফায়ার গেমের লাক রয়্যাল ইভেন্ট বাটন আপডেট হলে সাথে সাথে এখানে রিয়েল-টাইম আপডেট হবে।",
            fontSize = 12.sp,
            color = colors.textSecondary,
            lineHeight = 16.sp
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = onSyncLive,
              enabled = !isLiveSyncing,
              modifier = Modifier
                .weight(1f)
                .testTag("live_sync_button"),
              colors = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = Color(0xFF1A1200)),
              shape = RoundedCornerShape(8.dp)
            ) {
              if (isLiveSyncing) {
                CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = Color.Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text("সিঙ্ক হচ্ছে...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              } else {
                Icon(imageVector = Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("লাইভ সিঙ্ক করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }

            OutlinedButton(
              onClick = onSimulateServerUpdate,
              modifier = Modifier
                .weight(1f)
                .testTag("simulate_new_button"),
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary)
            ) {
              Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = FlameOrange, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("নতুন বাটন যোগ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // 2. Free Fire Luck Royale Sidebar / Tab Bar (Matching screenshot items)
    item {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "লাক রয়্যাল বাটন সমূহ (LUCK ROYALE)",
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = AmberGold,
            letterSpacing = 0.5.sp
          )
          Text(
            text = "বাটনে ক্লিক করে দেখুন",
            fontSize = 11.sp,
            color = colors.textMuted
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
          items(events) { ev ->
            val isSelected = ev.id == selectedEvent?.id
            val isTarget = config.targetLuckRoyaleId == ev.id

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(
                  if (isSelected) {
                    if (colors.isDark) Color(0xFF2A2000) else Color(0xFFFEF3C7)
                  } else colors.surface
                )
                .border(
                  width = if (isSelected) 1.8.dp else 1.dp,
                  color = if (isSelected) AmberGold else colors.border,
                  shape = RoundedCornerShape(10.dp)
                )
                .clickable { onSelectEvent(ev.id) }
                .padding(horizontal = 12.dp, vertical = 9.dp)
                .testTag("royale_button_${ev.id}")
            ) {
              Column(horizontalAlignment = Alignment.Start) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = ev.bannerEmoji,
                    fontSize = 14.sp
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = ev.banglaName,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                    color = if (isSelected) AmberGold else colors.textPrimary
                  )

                  ev.badgeText?.let { badge ->
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(FlameOrange)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                      Text(text = badge, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                  }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                  if (ev.isFreeSpinAvailable) {
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (colors.isDark) Color(0xFF0F3A22) else Color(0xFFDCFCE7))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                          modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(NeonGreen)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                          text = "FREE SPIN",
                          fontSize = 9.sp,
                          fontWeight = FontWeight.ExtraBold,
                          color = if (colors.isDark) NeonGreen else Color(0xFF15803D)
                        )
                      }
                    }
                  } else {
                    Text(
                      text = ev.singleSpinCostText,
                      fontSize = 10.sp,
                      color = colors.textMuted
                    )
                  }

                  if (isTarget) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "🎯 টার্গেট",
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      color = AmberGold
                    )
                  }
                }
              }
            }
          }
        }
      }
    }

    // 3. Center Showcase Banner (Mirrors Free Fire screenshot!)
    if (selectedEvent != null) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("royale_showcase_card"),
          colors = CardDefaults.cardColors(containerColor = colors.surface),
          shape = RoundedCornerShape(16.dp),
          border = androidx.compose.foundation.BorderStroke(1.2.dp, if (selectedEvent.isFreeSpinAvailable) AmberGold else colors.border)
        ) {
          Column(modifier = Modifier.fillMaxWidth()) {
            // Header bar of the Luck Royale event
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .background(
                  Brush.horizontalGradient(
                    colors = listOf(
                      if (colors.isDark) Color(0xFF332000) else Color(0xFFFEF3C7),
                      colors.surface
                    )
                  )
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = selectedEvent.grandPrizeTitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AmberGold
                  )
                  Text(
                    text = "সময়সীমা: ${selectedEvent.daysRemaining} • দৈনিক রিফ্রেশ: ${selectedEvent.dailyRefreshTimer}",
                    fontSize = 11.sp,
                    color = colors.textSecondary
                  )
                }

                if (selectedEvent.isFreeSpinAvailable) {
                  Box(
                    modifier = Modifier
                      .scale(pulseScale)
                      .clip(RoundedCornerShape(8.dp))
                      .background(FlameOrange)
                      .padding(horizontal = 10.dp, vertical = 5.dp)
                  ) {
                    Text(
                      text = "★ FREE SPIN ★",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.ExtraBold,
                      color = Color.White
                    )
                  }
                }
              }
            }

            // Hero Image
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .background(Color.Black)
            ) {
              Image(
                painter = painterResource(id = R.drawable.img_luck_royale_hero),
                contentDescription = "Luck Royale Hero Banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
              )

              // Dark gradient overlay for text legibility
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(
                    Brush.verticalGradient(
                      colors = listOf(Color.Transparent, Color(0xCC050810)),
                      startY = 60f
                    )
                  )
              )

              // Overlaid grand prizes label
              Column(
                modifier = Modifier
                  .align(Alignment.BottomStart)
                  .padding(12.dp)
              ) {
                Text(
                  text = "গ্র্যান্ড প্রাইজ আইটেমসমূহ:",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = AmberGold
                )
                selectedEvent.grandPrizeItems.forEach { item ->
                  Text(
                    text = "• $item",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              }
            }

            // Subtitle banner from screenshot
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .background(if (colors.isDark) Color(0xFF192233) else Color(0xFFE2E8F0))
                .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = selectedEvent.grandPrizeSubtitle,
                  fontSize = 11.sp,
                  color = colors.textSecondary,
                  modifier = Modifier.weight(1f)
                )
                Text(
                  text = "প্রাইজগুলো দেখুন >",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = AmberGold
                )
              }
            }

            // Bottom Buttons & Guaranteed counter (matching screenshot!)
            Column(modifier = Modifier.padding(14.dp)) {
              // Guaranteed Prize Banner
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (colors.isDark) Color(0xFF22190A) else Color(0xFFFFFBEB))
                  .border(0.8.dp, AmberGold, RoundedCornerShape(8.dp))
                  .padding(horizontal = 12.dp, vertical = 6.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(imageVector = Icons.Default.Stars, contentDescription = null, tint = AmberGold, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "${selectedEvent.guaranteedSpins} টি স্পিনে গ্যারান্টেড গ্র্যান্ড প্রাইজ!",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberGold
                  )
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              // Spin Buttons: 1 Spin (FREE or cost) & 10 Spins
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                // Button 1: 1 Spin
                Button(
                  onClick = onTestSpin,
                  modifier = Modifier
                    .weight(1.2f)
                    .height(48.dp)
                    .testTag("royale_free_spin_button"),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedEvent.isFreeSpinAvailable) AmberGold else colors.surfaceElevated,
                    contentColor = if (selectedEvent.isFreeSpinAvailable) Color(0xFF1E1200) else colors.textPrimary
                  ),
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                      text = if (selectedEvent.isFreeSpinAvailable) "1 টি স্পিন (FREE)" else "1 টি স্পিন",
                      fontSize = 13.sp,
                      fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                      text = if (selectedEvent.isFreeSpinAvailable) "ফ্রি স্পিন সক্রিয়" else selectedEvent.singleSpinCostText,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Normal
                    )
                  }
                }

                // Button 2: 10 Spins
                OutlinedButton(
                  onClick = onTestSpin,
                  modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                  shape = RoundedCornerShape(10.dp),
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary)
                ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "10 টি স্পিন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = selectedEvent.tenSpinsCostText, fontSize = 9.sp, color = colors.textMuted)
                  }
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              // Event Controls: Set as Generator Target & Toggle Free Spin
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = "ফ্রি স্পিন সক্রিয়তা",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                  )
                  Text(
                    text = if (selectedEvent.isFreeSpinAvailable) "বর্তমানে ফ্রি স্পিন অপশনে রয়েছে" else "পেইড স্পিন (ডায়মন্ড/টোকেন)",
                    fontSize = 10.sp,
                    color = colors.textMuted
                  )
                }

                Switch(
                  checked = selectedEvent.isFreeSpinAvailable,
                  onCheckedChange = { onToggleFreeSpin(selectedEvent.id, it) },
                  colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = NeonGreen,
                    uncheckedThumbColor = colors.textMuted,
                    uncheckedTrackColor = colors.border
                  )
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              Button(
                onClick = { onSetTargetSpin(selectedEvent.id) },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("set_royale_target_button"),
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (config.targetLuckRoyaleId == selectedEvent.id) NeonGreen else colors.surfaceElevated,
                  contentColor = if (config.targetLuckRoyaleId == selectedEvent.id) Color.Black else colors.textPrimary
                ),
                shape = RoundedCornerShape(8.dp)
              ) {
                Icon(
                  imageVector = if (config.targetLuckRoyaleId == selectedEvent.id) Icons.Default.CheckCircle else Icons.Default.Casino,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (config.targetLuckRoyaleId == selectedEvent.id) {
                    "✓ এই ইভেন্টটি জেনারেটরের টার্গেট হিসেবে সক্রিয়"
                  } else {
                    "এই ইভেন্টে গেস্ট আইডিদের স্পিন করান"
                  },
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    }

    // 4. Quick Jump to Generator
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (colors.isDark) Color(0xFF1E1700) else Color(0xFFFEF3C7)),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AmberGold)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "গেস্ট আইডি তৈরি করুন",
              fontSize = 13.sp,
              fontWeight = FontWeight.ExtraBold,
              color = if (colors.isDark) AmberGold else Color(0xFF92400E)
            )
            Text(
              text = "সিলেক্ট করা লাক রয়্যাল ফ্রি স্পিন সহ অটোমেটিক আনলিমিটেড গেস্ট আইডি তৈরি করুন।",
              fontSize = 11.sp,
              color = colors.textSecondary
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          Button(
            onClick = onGoToGenerator,
            colors = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = Color.Black),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("জেনারেটরে যান >", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
