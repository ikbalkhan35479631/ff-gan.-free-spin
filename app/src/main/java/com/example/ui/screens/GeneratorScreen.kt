package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GeneratorConfig
import com.example.model.GeneratorStats
import com.example.model.SpinType
import com.example.model.TerminalLog
import com.example.ui.components.SpinSelectorSection
import com.example.ui.components.StatsCardGrid
import com.example.ui.components.TerminalLogView
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberGoldDark
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratorScreen(
  config: GeneratorConfig,
  stats: GeneratorStats,
  progress: Float,
  isGenerating: Boolean,
  isPaused: Boolean,
  currentStatus: String,
  terminalLogs: List<TerminalLog>,
  onUpdateLimit: (Int) -> Unit,
  onUpdateNamePrefix: (String) -> Unit,
  onToggleSequentialName: (Boolean) -> Unit,
  onUpdatePassword: (String) -> Unit,
  onToggleAutoPassword: (Boolean) -> Unit,
  onSelectSpin: (SpinType) -> Unit,
  onSelectServer: (String) -> Unit,
  onStart: () -> Unit,
  onPause: () -> Unit,
  onStop: () -> Unit,
  onReset: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {

    // 1. Live Stats & Progress
    StatsCardGrid(
      stats = stats,
      progress = progress,
      isGenerating = isGenerating && !isPaused,
      currentStatus = currentStatus
    )

    // 2. Control Action Bar (START, PAUSE, STOP, RESET)
    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      color = DarkSurface,
      border = BorderStroke(1.dp, DarkBorder)
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Primary Start / Resume Button
          Button(
            onClick = {
              if (isGenerating && !isPaused) onPause() else onStart()
            },
            modifier = Modifier
              .weight(1.5f)
              .height(52.dp)
              .testTag("start_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isGenerating && !isPaused) AmberGoldDark else FlameOrange
            )
          ) {
            Icon(
              imageVector = if (isGenerating && !isPaused) Icons.Default.Pause else Icons.Default.PlayArrow,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = when {
                isGenerating && !isPaused -> "PAUSE"
                isPaused -> "RESUME"
                else -> "START GENERATE"
              },
              fontSize = 14.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White
            )
          }

          // Stop Button
          if (isGenerating || isPaused) {
            Button(
              onClick = onStop,
              modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .testTag("stop_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
            ) {
              Icon(
                imageVector = Icons.Default.Stop,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("STOP", fontWeight = FontWeight.Bold, color = Color.White)
            }
          } else {
            // Reset Button
            OutlinedButton(
              onClick = onReset,
              modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .testTag("reset_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
              border = BorderStroke(1.dp, DarkBorder)
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("RESET", fontWeight = FontWeight.SemiBold)
            }
          }
        }
      }
    }

    // 3. Generation Settings Card (Limit, Name Prefix, Password Setup, Server)
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("generator_config_card"),
      shape = RoundedCornerShape(16.dp),
      color = DarkSurface,
      border = BorderStroke(1.dp, DarkBorder)
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Settings,
              contentDescription = null,
              tint = AmberGold,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "GENERATOR & ID CONFIGURATION",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = AmberGold,
              letterSpacing = 0.5.sp
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(DarkSurfaceElevated)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = "EDITABLE LIMIT",
              fontSize = 10.sp,
              color = TextSecondary,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- Limit Configuration ---
        Text(
          text = "Account Limit (Target Count):",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Decrement button
          IconButton(
            onClick = { onUpdateLimit((config.limit - 5).coerceAtLeast(1)) },
            modifier = Modifier
              .size(44.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(DarkSurfaceElevated)
          ) {
            Icon(
              imageVector = Icons.Default.Remove,
              contentDescription = "Decrease",
              tint = AmberGold
            )
          }

          // Editable text field for Limit
          OutlinedTextField(
            value = config.limit.toString(),
            onValueChange = { str ->
              val num = str.filter { it.isDigit() }.toIntOrNull()
              if (num != null) onUpdateLimit(num)
            },
            modifier = Modifier
              .weight(1f)
              .testTag("limit_input_field"),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = AmberGold,
              unfocusedBorderColor = DarkBorder,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary,
              focusedContainerColor = DarkSurfaceElevated,
              unfocusedContainerColor = DarkSurfaceElevated
            ),
            shape = RoundedCornerShape(8.dp)
          )

          // Increment button
          IconButton(
            onClick = { onUpdateLimit((config.limit + 5).coerceAtMost(500)) },
            modifier = Modifier
              .size(44.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(DarkSurfaceElevated)
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Increase",
              tint = AmberGold
            )
          }
        }

        // Quick Limit Preset Chips
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          val presets = listOf(5, 10, 20, 50, 100)
          presets.forEach { preset ->
            val isSelected = config.limit == preset
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) AmberGold else DarkSurfaceElevated)
                .clickable { onUpdateLimit(preset) }
                .padding(vertical = 5.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "$preset IDs",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color(0xFF1E1200) else TextSecondary
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- ID Name Option Setup ---
        Text(
          text = "ID Name Prefix & Setup:",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
          value = config.namePrefix,
          onValueChange = onUpdateNamePrefix,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("name_prefix_field"),
          label = { Text("Base Name Prefix", fontSize = 11.sp) },
          placeholder = { Text("e.g. FF_Guest_, Sniper_, Hunter_") },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Person,
              contentDescription = null,
              tint = AmberGold,
              modifier = Modifier.size(18.dp)
            )
          },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AmberGold,
            unfocusedBorderColor = DarkBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = DarkSurfaceElevated,
            unfocusedContainerColor = DarkSurfaceElevated
          ),
          shape = RoundedCornerShape(8.dp)
        )

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Sequential numbering (e.g. ${config.namePrefix}001, 002)",
            fontSize = 11.sp,
            color = TextSecondary
          )
          Switch(
            checked = config.isSequentialName,
            onCheckedChange = onToggleSequentialName,
            colors = SwitchDefaults.colors(
              checkedThumbColor = AmberGold,
              checkedTrackColor = AmberGoldDark.copy(alpha = 0.5f)
            )
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- Password Setup & Rename ---
        Text(
          text = "Password Setup & Preset:",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
          value = config.customPassword,
          onValueChange = onUpdatePassword,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("password_setup_field"),
          label = { Text("Preset Password for IDs", fontSize = 11.sp) },
          enabled = !config.isAutoPassword,
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Key,
              contentDescription = null,
              tint = AmberGold,
              modifier = Modifier.size(18.dp)
            )
          },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AmberGold,
            unfocusedBorderColor = DarkBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = DarkSurfaceElevated,
            unfocusedContainerColor = DarkSurfaceElevated
          ),
          shape = RoundedCornerShape(8.dp)
        )

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Auto-generate strong unique passwords",
            fontSize = 11.sp,
            color = TextSecondary
          )
          Switch(
            checked = config.isAutoPassword,
            onCheckedChange = onToggleAutoPassword,
            colors = SwitchDefaults.colors(
              checkedThumbColor = AmberGold,
              checkedTrackColor = AmberGoldDark.copy(alpha = 0.5f)
            )
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- Server / Region Dropdown ---
        Text(
          text = "Game Server Region:",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))

        var serverDropdownExpanded by remember { mutableStateOf(false) }
        val serverList = listOf(
          "Bangladesh (BD)",
          "India (IND)",
          "Singapore (SG)",
          "Indonesia (ID)",
          "Brazil (BR)",
          "Europe (EU)",
          "Middle East (ME)",
          "Global"
        )

        ExposedDropdownMenuBox(
          expanded = serverDropdownExpanded,
          onExpandedChange = { serverDropdownExpanded = !serverDropdownExpanded },
          modifier = Modifier.fillMaxWidth()
        ) {
          OutlinedTextField(
            value = config.selectedServer,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
              .fillMaxWidth()
              .menuAnchor(),
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Dns,
                contentDescription = null,
                tint = AmberGold,
                modifier = Modifier.size(18.dp)
              )
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = serverDropdownExpanded) },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = AmberGold,
              unfocusedBorderColor = DarkBorder,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary,
              focusedContainerColor = DarkSurfaceElevated,
              unfocusedContainerColor = DarkSurfaceElevated
            ),
            shape = RoundedCornerShape(8.dp)
          )

          ExposedDropdownMenu(
            expanded = serverDropdownExpanded,
            onDismissRequest = { serverDropdownExpanded = false }
          ) {
            serverList.forEach { server ->
              DropdownMenuItem(
                text = { Text(server) },
                onClick = {
                  onSelectServer(server)
                  serverDropdownExpanded = false
                }
              )
            }
          }
        }
      }
    }

    // 4. Spin Selection Section (User explicitly requested buttons for each spin type!)
    SpinSelectorSection(
      selectedSpin = config.selectedSpin,
      onSpinSelected = onSelectSpin,
      isEnabled = !isGenerating
    )

    // 5. Live Terminal Logs View
    TerminalLogView(logs = terminalLogs)

    Spacer(modifier = Modifier.height(16.dp))
  }
}
