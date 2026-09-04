package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GeneratorStats
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.PrimeGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun StatsCardGrid(
  stats: GeneratorStats,
  progress: Float,
  isGenerating: Boolean,
  currentStatus: String,
  modifier: Modifier = Modifier
) {
  val animatedProgress by animateFloatAsState(
    targetValue = progress.coerceIn(0f, 1f),
    label = "generator_progress"
  )

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("stats_card_grid"),
    shape = RoundedCornerShape(16.dp),
    color = DarkSurface,
    border = BorderStroke(1.dp, DarkBorder)
  ) {
    Column(
      modifier = Modifier.padding(14.dp)
    ) {
      // Top header with status
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(if (isGenerating) NeonGreen else AmberGold)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isGenerating) "GENERATING LIVE" else "LIVE STATUS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isGenerating) NeonGreen else AmberGold,
            letterSpacing = 1.sp
          )
        }

        Text(
          text = "${stats.totalProcessed} / ${stats.totalRequested}",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = TextSecondary
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Current step text
      Text(
        text = currentStatus,
        fontSize = 12.sp,
        color = TextPrimary,
        maxLines = 1,
        fontWeight = FontWeight.Medium
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Progress bar
      LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = AmberGold,
        trackColor = DarkSurfaceElevated
      )

      Spacer(modifier = Modifier.height(14.dp))

      // 4 Metric Counters Grid
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Success Card
        StatBox(
          modifier = Modifier.weight(1f),
          title = "Passed",
          value = "${stats.successCount}",
          subtitle = "${stats.successPercentage}%",
          accentColor = NeonGreen,
          icon = Icons.Default.CheckCircle,
          testTag = "stat_passed"
        )

        // Failed Card
        StatBox(
          modifier = Modifier.weight(1f),
          title = "Failed",
          value = "${stats.failedCount}",
          subtitle = if (stats.totalProcessed > 0) "${(stats.failedCount * 100) / stats.totalProcessed}%" else "0%",
          accentColor = DangerRed,
          icon = Icons.Default.Cancel,
          testTag = "stat_failed"
        )

        // Prime Bundle Card
        StatBox(
          modifier = Modifier.weight(1f),
          title = "Prime ⭐",
          value = "${stats.primeBundleCount}",
          subtitle = "${stats.primePercentage}% hit",
          accentColor = PrimeGold,
          icon = Icons.Default.Star,
          testTag = "stat_prime"
        )

        // Normal Items Card
        StatBox(
          modifier = Modifier.weight(1f),
          title = "Normal",
          value = "${stats.normalItemCount}",
          subtitle = "Loot",
          accentColor = NeonCyan,
          icon = Icons.Default.Inventory2,
          testTag = "stat_normal"
        )
      }
    }
  }
}

@Composable
private fun StatBox(
  title: String,
  value: String,
  subtitle: String,
  accentColor: Color,
  icon: ImageVector,
  testTag: String,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(DarkSurfaceElevated)
      .border(BorderStroke(0.8.dp, accentColor.copy(alpha = 0.35f)), RoundedCornerShape(10.dp))
      .padding(vertical = 8.dp, horizontal = 6.dp)
      .testTag(testTag)
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = accentColor,
          modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = title,
          fontSize = 11.sp,
          color = TextSecondary,
          fontWeight = FontWeight.SemiBold
        )
      }

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = value,
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold,
        color = accentColor
      )

      Text(
        text = subtitle,
        fontSize = 10.sp,
        color = TextMuted,
        fontWeight = FontWeight.Normal
      )
    }
  }
}
