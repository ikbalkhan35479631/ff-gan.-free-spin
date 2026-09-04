package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AccountStatus
import com.example.model.GuestAccount
import com.example.model.ItemRarity
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DangerRed
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.LocalAppColors
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.PrimeGold
import com.example.ui.theme.PrimePurple

@Composable
fun AccountItemCard(
  account: GuestAccount,
  index: Int,
  onCopyUid: (String) -> Unit,
  onCopyPassword: (String) -> Unit,
  onCopyCombo: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = LocalAppColors.current
  var showPassword by remember { mutableStateOf(false) }

  val isSuccess = account.status == AccountStatus.SUCCESS
  val isPrime = account.isPrimeBundle

  val borderColor = when {
    !isSuccess -> DangerRed.copy(alpha = 0.5f)
    isPrime -> PrimeGold
    else -> colors.border
  }

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("account_card_${account.uid}"),
    shape = RoundedCornerShape(14.dp),
    color = colors.surface,
    border = BorderStroke(if (isPrime) 1.5.dp else 1.dp, borderColor)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      // Top header: Index, Name, Status Badge, Region
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(colors.surfaceElevated)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = "#${index + 1}",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = AmberGold
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = account.accountName,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
          )
        }

        // Status Badge
        if (isSuccess) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(
                if (isPrime) Brush.horizontalGradient(listOf(PrimeGold.copy(alpha = 0.2f), PrimePurple.copy(alpha = 0.2f)))
                else Brush.horizontalGradient(listOf(NeonGreen.copy(alpha = 0.15f), NeonGreen.copy(alpha = 0.15f)))
              )
              .border(
                0.8.dp,
                if (isPrime) PrimeGold else NeonGreen,
                RoundedCornerShape(6.dp)
              )
              .padding(horizontal = 7.dp, vertical = 2.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = if (isPrime) Icons.Default.Star else Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (isPrime) PrimeGold else NeonGreen,
                modifier = Modifier.size(11.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (isPrime) "PRIME BUNDLE ⭐" else "SUCCESS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPrime) PrimeGold else NeonGreen
              )
            }
          }
        } else {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(DangerRed.copy(alpha = 0.15f))
              .border(0.8.dp, DangerRed, RoundedCornerShape(6.dp))
              .padding(horizontal = 7.dp, vertical = 2.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = DangerRed,
                modifier = Modifier.size(11.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "FAILED",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = DangerRed
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Credential Rows: UID & Password
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // UID Block
        Box(
          modifier = Modifier
            .weight(1.1f)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceElevated)
            .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "GAME UID",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textMuted,
                letterSpacing = 0.5.sp
              )
              Text(
                text = account.uid,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = NeonCyan
              )
            }
            IconButton(
              onClick = { onCopyUid(account.uid) },
              modifier = Modifier.size(28.dp).testTag("copy_uid_${account.uid}")
            ) {
              Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy UID",
                tint = colors.textSecondary,
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }

        // Password Block
        Box(
          modifier = Modifier
            .weight(1.1f)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceElevated)
            .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "PASSWORD",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textMuted,
                letterSpacing = 0.5.sp
              )
              Text(
                text = if (showPassword) account.password else "••••••••",
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = AmberGold
              )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
              IconButton(
                onClick = { showPassword = !showPassword },
                modifier = Modifier.size(24.dp)
              ) {
                Icon(
                  imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = "Toggle Password",
                  tint = colors.textMuted,
                  modifier = Modifier.size(13.dp)
                )
              }
              IconButton(
                onClick = { onCopyPassword(account.password) },
                modifier = Modifier.size(24.dp).testTag("copy_pass_${account.uid}")
              ) {
                Icon(
                  imageVector = Icons.Default.ContentCopy,
                  contentDescription = "Copy Password",
                  tint = colors.textSecondary,
                  modifier = Modifier.size(13.dp)
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Won Item / Failure Block
      if (isSuccess && account.wonItem != null) {
        val item = account.wonItem
        val bgBrush = if (isPrime) {
          Brush.horizontalGradient(listOf(Color(0xFF3B2706), Color(0xFF261238)))
        } else {
          Brush.horizontalGradient(listOf(colors.surfaceElevated, colors.surfaceElevated))
        }

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bgBrush)
            .border(
              BorderStroke(0.8.dp, if (isPrime) PrimeGold.copy(alpha = 0.5f) else colors.border),
              RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Text(text = item.iconEmoji, fontSize = 18.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = item.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPrime) PrimeGold else colors.textPrimary,
                    maxLines = 1
                  )
                }
                Text(
                  text = "Spin: ${account.wonLuckRoyaleEventName ?: account.spinType.title} • ${account.serverRegion}",
                  fontSize = 10.sp,
                  color = colors.textMuted
                )
              }
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(if (isPrime) PrimeGold else NeonCyan.copy(alpha = 0.2f))
                .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
              Text(
                text = item.rarity.label,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isPrime) Color(0xFF1E1200) else NeonCyan
              )
            }
          }
        }
      } else if (!isSuccess) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DangerRed.copy(alpha = 0.1f))
            .border(0.8.dp, DangerRed.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(8.dp)
        ) {
          Text(
            text = "Error: ${account.failureReason ?: "Gateway timeout"}",
            fontSize = 11.sp,
            color = DangerRed
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Bottom quick copy row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Server: ${account.serverRegion}",
          fontSize = 10.sp,
          color = colors.textMuted
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable {
              val itemTxt = account.wonItem?.name ?: "Reward"
              onCopyCombo("UID: ${account.uid} | PASS: ${account.password} | NAME: ${account.accountName} | ITEM: $itemTxt")
            }
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = null,
            tint = AmberGold,
            modifier = Modifier.size(11.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Copy Row",
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = AmberGold
          )
        }
      }
    }
  }
}
