package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AccountFilter
import com.example.ui.components.FilePreviewDialog
import com.example.ui.screens.AccountsListScreen
import com.example.ui.screens.GeneratorScreen
import com.example.ui.screens.LuckRoyaleScreen
import com.example.ui.screens.ZipExportScreen
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.LocalAppColors
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.PrimeGold
import com.example.viewmodel.GeneratorViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: GeneratorViewModel by viewModels()

  @OptIn(ExperimentalMaterial3Api::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      val uiState by viewModel.uiState.collectAsState()

      MyApplicationTheme(darkTheme = uiState.isDarkMode) {
        val colors = LocalAppColors.current
        val context = LocalContext.current
        val snackbarHostState = remember { SnackbarHostState() }
        var showHelpDialog by remember { mutableStateOf(false) }

        // Trigger snackbar on toastMessage
        LaunchedEffect(uiState.toastMessage) {
          uiState.toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
          }
        }

        Scaffold(
          modifier = Modifier.fillMaxSize(),
          containerColor = colors.bg,
          snackbarHost = { SnackbarHost(snackbarHostState) },
          topBar = {
            TopAppBar(
              title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  // Premium App Logo
                  Box(
                    modifier = Modifier
                      .size(38.dp)
                      .clip(RoundedCornerShape(9.dp))
                      .background(colors.surfaceElevated)
                      .border(1.2.dp, AmberGold, RoundedCornerShape(9.dp)),
                    contentAlignment = Alignment.Center
                  ) {
                    Image(
                      painter = painterResource(R.drawable.img_ff_premium_logo),
                      contentDescription = "FF Premium Logo",
                      modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp)),
                      contentScale = ContentScale.Crop
                    )
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = "FF LUCK ROYALE",
                      fontSize = 15.sp,
                      fontWeight = FontWeight.ExtraBold,
                      color = AmberGold,
                      letterSpacing = 0.5.sp
                    )
                    Text(
                      text = "গেস্ট আইডি • লাইভ সিঙ্ক • ${uiState.config.selectedServer}",
                      fontSize = 10.sp,
                      color = colors.textMuted
                    )
                  }
                }
              },
              actions = {
                // Live Sync Action Button
                IconButton(
                  onClick = { viewModel.syncLuckRoyaleNow() },
                  modifier = Modifier.testTag("live_sync_action")
                ) {
                  Icon(
                    imageVector = Icons.Default.CloudSync,
                    contentDescription = "Live Sync Luck Royale",
                    tint = AmberGold,
                    modifier = Modifier.size(22.dp)
                  )
                }

                // Dark / Light Mode Toggle Button
                IconButton(
                  onClick = { viewModel.toggleDarkMode() },
                  modifier = Modifier.testTag("theme_toggle_button")
                ) {
                  Icon(
                    imageVector = if (uiState.isDarkMode) Icons.Default.Brightness7 else Icons.Default.Brightness4,
                    contentDescription = if (uiState.isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode",
                    tint = if (uiState.isDarkMode) AmberGold else FlameOrange,
                    modifier = Modifier.size(20.dp)
                  )
                }

                IconButton(onClick = { showHelpDialog = true }) {
                  Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = "Help Info",
                    tint = colors.textSecondary,
                    modifier = Modifier.size(20.dp)
                  )
                }
                IconButton(onClick = { viewModel.resetAll() }) {
                  Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset Session",
                    tint = colors.textSecondary,
                    modifier = Modifier.size(20.dp)
                  )
                }
              },
              colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colors.surface,
                titleContentColor = AmberGold
              )
            )
          },
          bottomBar = {
            NavigationBar(
              containerColor = colors.surface,
              modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .testTag("bottom_nav_bar")
            ) {
              // Tab 0: Luck Royale
              NavigationBarItem(
                selected = uiState.activeTab == 0,
                onClick = { viewModel.setTab(0) },
                icon = {
                  BadgedBox(
                    badge = {
                      if (uiState.activeFreeSpinCount > 0) {
                        Badge(
                          containerColor = NeonGreen,
                          contentColor = Color(0xFF0F3A22)
                        ) {
                          Text(text = "FREE", fontSize = 8.sp, fontWeight = FontWeight.ExtraBold)
                        }
                      }
                    }
                  ) {
                    Icon(
                      imageVector = Icons.Default.Casino,
                      contentDescription = "Luck Royale"
                    )
                  }
                },
                label = { Text("লাক রয়্যাল", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(
                  selectedIconColor = if (colors.isDark) Color(0xFF121824) else Color.White,
                  selectedTextColor = AmberGold,
                  indicatorColor = AmberGold,
                  unselectedIconColor = colors.textMuted,
                  unselectedTextColor = colors.textMuted
                )
              )

              // Tab 1: Generator
              NavigationBarItem(
                selected = uiState.activeTab == 1,
                onClick = { viewModel.setTab(1) },
                icon = {
                  Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Generator"
                  )
                },
                label = { Text("জেনারেটর", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(
                  selectedIconColor = if (colors.isDark) Color(0xFF121824) else Color.White,
                  selectedTextColor = AmberGold,
                  indicatorColor = AmberGold,
                  unselectedIconColor = colors.textMuted,
                  unselectedTextColor = colors.textMuted
                )
              )

              // Tab 2: Accounts List
              NavigationBarItem(
                selected = uiState.activeTab == 2,
                onClick = { viewModel.setTab(2) },
                icon = {
                  Icon(
                    imageVector = Icons.Default.ListAlt,
                    contentDescription = "Accounts"
                  )
                },
                label = {
                  Text(
                    text = if (uiState.accounts.isNotEmpty()) "আইডি (${uiState.accounts.size})" else "আইডি সমূহ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                },
                colors = NavigationBarItemDefaults.colors(
                  selectedIconColor = if (colors.isDark) Color(0xFF121824) else Color.White,
                  selectedTextColor = AmberGold,
                  indicatorColor = AmberGold,
                  unselectedIconColor = colors.textMuted,
                  unselectedTextColor = colors.textMuted
                )
              )

              // Tab 3: ZIP Export
              NavigationBarItem(
                selected = uiState.activeTab == 3,
                onClick = { viewModel.setTab(3) },
                icon = {
                  Icon(
                    imageVector = Icons.Default.FolderZip,
                    contentDescription = "ZIP Export"
                  )
                },
                label = { Text("জিপ এক্সপোর্ট", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(
                  selectedIconColor = if (colors.isDark) Color(0xFF121824) else Color.White,
                  selectedTextColor = AmberGold,
                  indicatorColor = AmberGold,
                  unselectedIconColor = colors.textMuted,
                  unselectedTextColor = colors.textMuted
                )
              )
            }
          }
        ) { paddingValues ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(paddingValues)
          ) {
            when (uiState.activeTab) {
              0 -> LuckRoyaleScreen(
                events = uiState.luckRoyaleEvents,
                selectedEventId = uiState.selectedRoyaleEventId,
                config = uiState.config,
                isLiveSyncing = uiState.isLiveSyncing,
                lastSyncTimeText = uiState.lastSyncTimeText,
                onSelectEvent = { viewModel.selectRoyaleEvent(it) },
                onSyncLive = { viewModel.syncLuckRoyaleNow() },
                onSimulateServerUpdate = { viewModel.simulateLiveServerButtonUpdate() },
                onToggleFreeSpin = { id, isFree -> viewModel.toggleRoyaleFreeSpin(id, isFree) },
                onSetTargetSpin = { viewModel.setTargetLuckRoyale(it) },
                onTestSpin = { viewModel.testSpinCurrentRoyale() },
                onGoToGenerator = { viewModel.setTab(1) }
              )

              1 -> GeneratorScreen(
                config = uiState.config,
                stats = uiState.stats,
                progress = uiState.currentProgress,
                isGenerating = uiState.isGenerating,
                isPaused = uiState.isPaused,
                currentStatus = uiState.currentStepStatus,
                terminalLogs = uiState.terminalLogs,
                onUpdateLimit = { viewModel.updateLimit(it) },
                onUpdateNamePrefix = { viewModel.updateNamePrefix(it) },
                onToggleSequentialName = { viewModel.toggleSequentialName(it) },
                onUpdatePassword = { viewModel.updateCustomPassword(it) },
                onToggleAutoPassword = { viewModel.toggleAutoPassword(it) },
                onSelectSpin = { viewModel.selectSpin(it) },
                onSelectServer = { viewModel.selectServer(it) },
                onStart = { viewModel.startGeneration() },
                onPause = { viewModel.pauseGeneration() },
                onStop = { viewModel.stopGeneration() },
                onReset = { viewModel.resetAll() }
              )

              2 -> AccountsListScreen(
                accounts = uiState.accounts,
                activeFilter = uiState.activeFilter,
                searchQuery = uiState.searchQuery,
                onFilterChange = { viewModel.setFilter(it) },
                onSearchChange = { viewModel.setSearchQuery(it) },
                onCopyUid = { viewModel.copyToClipboard(context, it, "UID: $it") },
                onCopyPassword = { viewModel.copyToClipboard(context, it, "Password") },
                onCopyCombo = { viewModel.copyToClipboard(context, it, "Account details") },
                onCopyAllSuccess = { viewModel.copyAllSuccessCombo(context) },
                onCopyPrimeIds = { viewModel.copyAllPrimeBundleIds(context) },
                onGoToExport = { viewModel.setTab(3) },
                onGoToGenerator = { viewModel.setTab(1) }
              )

              3 -> ZipExportScreen(
                accounts = uiState.accounts,
                config = uiState.config,
                stats = uiState.stats,
                lastExportedZip = uiState.lastExportedZip,
                onExportAndShare = { viewModel.exportAndShareZip(context) },
                onShowPreview = { viewModel.showFilePreview(it) },
                onCopyText = { text, label -> viewModel.copyToClipboard(context, text, label) },
                onCopyAllSuccess = { viewModel.copyAllSuccessCombo(context) },
                onCopyPrimeIds = { viewModel.copyAllPrimeBundleIds(context) }
              )
            }
          }

          // File Preview Dialog
          if (uiState.showPreviewDialog && uiState.previewFileTitle != null && uiState.previewFileContent != null) {
            FilePreviewDialog(
              title = uiState.previewFileTitle!!,
              content = uiState.previewFileContent!!,
              onDismiss = { viewModel.dismissPreviewDialog() },
              onCopy = { viewModel.copyToClipboard(context, it, uiState.previewFileTitle!!) }
            )
          }

          // Help & Instructions Dialog
          if (showHelpDialog) {
            AlertDialog(
              onDismissRequest = { showHelpDialog = false },
              title = {
                Text(
                  text = "FF Guest Generator Help",
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  color = AmberGold
                )
              },
              text = {
                Column {
                  Text(
                    text = "How to use:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = colors.textPrimary
                  )
                  Spacer(modifier = Modifier.size(6.dp))
                  Text(
                    text = "1. Limit: Set how many accounts you want to create (e.g. 10, 20, 50, 100).\n" +
                        "2. Name & Password: Customize your guest ID name prefix and preset password.\n" +
                        "3. Free Spin Selection: Tap on the spin button (Prime Bundle Wheel, Gold Royale, Weapon Royale, etc.). The app will automatically execute that free spin on all generated IDs.\n" +
                        "4. Passed & Failed Stats: Live tracking of how many IDs passed and failed.\n" +
                        "5. ZIP Export: Tap 'Save & Share ZIP' to export all accounts into a ZIP file with categorized text files:\n" +
                        "   - success_ids.txt\n" +
                        "   - failed_ids.txt\n" +
                        "   - prime_bundle_special_ids.txt\n" +
                        "   - normal_items_ids.txt\n" +
                        "   - all_accounts_combo.txt",
                    fontSize = 12.sp,
                    color = colors.textSecondary,
                    lineHeight = 16.sp
                  )
                }
              },
              confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                  Text("Got it", color = AmberGold, fontWeight = FontWeight.Bold)
                }
              },
              containerColor = colors.surfaceElevated
            )
          }
        }
      }
    }
  }
}
