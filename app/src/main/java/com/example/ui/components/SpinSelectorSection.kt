package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SpinType
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DangerRed
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.LocalAppColors
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.PrimeGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SpinSelectorSection(
  selectedSpin: SpinType,
  onSpinSelected: (SpinType) -> Unit,
  isEnabled: Boolean = true,
  modifier: Modifier = Modifier
) {
  val colors = LocalAppColors.current

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("spin_selector_section"),
    shape = RoundedCornerShape(16.dp),
    color = colors.surface,
    border = BorderStroke(1.dp, colors.border)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Casino,
            contentDescription = "Spin Selector",
            tint = AmberGold,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "CHOOSE FREE SPIN LOCATION",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = AmberGold,
            letterSpacing = 0.5.sp
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(NeonGreen.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "FREE SPIN AUTO",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = NeonGreen
          )
        }
      }

      Text(
        text = "Every generated Guest ID will automatically execute the selected Free Spin to collect rewards:",
        fontSize = 11.sp,
        color = TextSecondary,
        modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
      )

      // Spin buttons grid (2 items per row)
      val spinTypes = SpinType.values()
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        for (i in spinTypes.indices step 2) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            val spin1 = spinTypes[i]
            val isSelected1 = selectedSpin == spin1
            SpinButtonCard(
              spin = spin1,
              isSelected = isSelected1,
              onClick = { if (isEnabled) onSpinSelected(spin1) },
              modifier = Modifier.weight(1f)
            )

            if (i + 1 < spinTypes.size) {
              val spin2 = spinTypes[i + 1]
              val isSelected2 = selectedSpin == spin2
              SpinButtonCard(
                spin = spin2,
                isSelected = isSelected2,
                onClick = { if (isEnabled) onSpinSelected(spin2) },
                modifier = Modifier.weight(1f)
              )
            } else {
              Spacer(modifier = Modifier.weight(1f))
            }
          }
        }
      }

      // Selected spin details strip
      Spacer(modifier = Modifier.height(10.dp))
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(colors.surfaceElevated)
          .padding(8.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = selectedSpin.emoji, fontSize = 16.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "Target: ${selectedSpin.title} • ${selectedSpin.freeLabel}",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
            Text(
              text = selectedSpin.description,
              fontSize = 10.sp,
              color = TextMuted
            )
          }
        }
      }
    }
  }
}

@Composable
private fun SpinButtonCard(
  spin: SpinType,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = LocalAppColors.current
  val borderColor = if (isSelected) AmberGold else colors.border
  val bgColor = if (isSelected) {
    if (colors.isDark) colors.surfaceElevated else Color(0xFFFEF3C7)
  } else colors.surfaceElevated.copy(alpha = 0.5f)

  Surface(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .clickable(onClick = onClick)
      .testTag("spin_btn_${spin.id}"),
    shape = RoundedCornerShape(10.dp),
    color = bgColor,
    border = BorderStroke(if (isSelected) 1.8.dp else 0.8.dp, borderColor)
  ) {
    Column(
      modifier = Modifier.padding(8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = spin.emoji, fontSize = 16.sp)

        if (isSelected) {
          Box(
            modifier = Modifier
              .size(16.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(AmberGold),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Selected",
              tint = Color(0xFF1E1200),
              modifier = Modifier.size(12.dp)
            )
          }
        } else {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(FlameOrange.copy(alpha = 0.15f))
              .padding(horizontal = 4.dp, vertical = 1.dp)
          ) {
            Text(
              text = "FREE",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = FlameOrange
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = spin.title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = if (isSelected) AmberGold else TextPrimary,
        maxLines = 1
      )

      Text(
        text = spin.freeLabel,
        fontSize = 10.sp,
        color = if (isSelected) PrimeGold else TextMuted,
        fontWeight = FontWeight.Medium
      )
    }
  }
}
