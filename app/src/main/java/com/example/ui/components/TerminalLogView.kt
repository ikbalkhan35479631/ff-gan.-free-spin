package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LogType
import com.example.model.TerminalLog
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.PrimeGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TerminalLogView(
  logs: List<TerminalLog>,
  modifier: Modifier = Modifier
) {
  val listState = rememberLazyListState()

  LaunchedEffect(logs.size) {
    if (logs.isNotEmpty()) {
      listState.animateScrollToItem(logs.size - 1)
    }
  }

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("terminal_log_view"),
    shape = RoundedCornerShape(16.dp),
    color = DarkSurface,
    border = BorderStroke(1.dp, DarkBorder)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Terminal,
            contentDescription = "Terminal",
            tint = NeonCyan,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "LIVE PROTOCOL TERMINAL",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NeonCyan,
            letterSpacing = 0.5.sp
          )
        }

        Text(
          text = "${logs.size} events",
          fontSize = 10.sp,
          color = TextMuted
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(130.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(DarkBg)
          .padding(8.dp)
      ) {
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        LazyColumn(
          state = listState,
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          items(logs, key = { it.id }) { log ->
            val color = when (log.type) {
              LogType.SUCCESS -> NeonGreen
              LogType.ERROR -> DangerRed
              LogType.PRIME_DROP -> PrimeGold
              LogType.NORMAL_DROP -> NeonCyan
              LogType.SPINNING -> AmberGold
              LogType.INFO -> TextSecondary
            }

            val timeStr = timeFormat.format(Date(log.timestamp))

            Row(verticalAlignment = Alignment.Top) {
              Text(
                text = "[$timeStr] ",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = TextMuted,
                fontWeight = FontWeight.Medium
              )
              Text(
                text = log.message,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = color,
                lineHeight = 13.sp
              )
            }
          }
        }
      }
    }
  }
}
