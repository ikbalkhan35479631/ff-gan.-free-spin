package com.example.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.AccountFilter
import com.example.model.AccountStatus
import com.example.model.GeneratorConfig
import com.example.model.GeneratorStats
import com.example.model.GuestAccount
import com.example.model.LogType
import com.example.model.SpinType
import com.example.model.TerminalLog
import com.example.service.FreeFireSpinEngine
import com.example.service.ZipExportService
import java.io.File
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GeneratorUiState(
  val config: GeneratorConfig = GeneratorConfig(),
  val stats: GeneratorStats = GeneratorStats(),
  val accounts: List<GuestAccount> = emptyList(),
  val isGenerating: Boolean = false,
  val isPaused: Boolean = false,
  val currentProgress: Float = 0f,
  val currentStepStatus: String = "Ready to generate guest accounts",
  val terminalLogs: List<TerminalLog> = listOf(
    TerminalLog(message = "System initialized. Configure limit & spin location, then tap START.", type = LogType.INFO)
  ),
  val activeFilter: AccountFilter = AccountFilter.ALL,
  val searchQuery: String = "",
  val activeTab: Int = 0, // 0: Luck Royale, 1: Generator, 2: Accounts, 3: ZIP Export
  val isDarkMode: Boolean = true,
  val luckRoyaleEvents: List<com.example.model.LuckRoyaleEvent> = com.example.service.LuckRoyaleSyncService.getEvents(),
  val selectedRoyaleEventId: String = "gold_vault",
  val isLiveSyncing: Boolean = false,
  val lastSyncTimeText: String = "Just now",
  val activeFreeSpinCount: Int = com.example.service.LuckRoyaleSyncService.getFreeSpinEvents().size,
  val lastExportedZip: File? = null,
  val toastMessage: String? = null,
  val previewFileTitle: String? = null,
  val previewFileContent: String? = null,
  val showPreviewDialog: Boolean = false,
  val inspectAccount: GuestAccount? = null
)

class GeneratorViewModel(application: Application) : AndroidViewModel(application) {

  private val _uiState = MutableStateFlow(GeneratorUiState())
  val uiState: StateFlow<GeneratorUiState> = _uiState.asStateFlow()

  private var generationJob: Job? = null

  fun setTab(index: Int) {
    _uiState.update { it.copy(activeTab = index) }
  }

  fun setFilter(filter: AccountFilter) {
    _uiState.update { it.copy(activeFilter = filter) }
  }

  fun setSearchQuery(query: String) {
    _uiState.update { it.copy(searchQuery = query) }
  }

  fun updateLimit(limit: Int) {
    val clamped = limit.coerceIn(1, 500)
    _uiState.update { it.copy(config = it.config.copy(limit = clamped)) }
  }

  fun updateNamePrefix(prefix: String) {
    _uiState.update { it.copy(config = it.config.copy(namePrefix = prefix)) }
  }

  fun toggleSequentialName(sequential: Boolean) {
    _uiState.update { it.copy(config = it.config.copy(isSequentialName = sequential)) }
  }

  fun updateCustomPassword(password: String) {
    _uiState.update { it.copy(config = it.config.copy(customPassword = password)) }
  }

  fun toggleAutoPassword(isAuto: Boolean) {
    _uiState.update { it.copy(config = it.config.copy(isAutoPassword = isAuto)) }
  }

  fun selectSpin(spinType: SpinType) {
    _uiState.update {
      it.copy(config = it.config.copy(selectedSpin = spinType))
    }
    addLog("Selected free spin location: ${spinType.title} (${spinType.freeLabel})", LogType.INFO)
  }

  fun selectServer(server: String) {
    _uiState.update { it.copy(config = it.config.copy(selectedServer = server)) }
    addLog("Target server region set to: $server", LogType.INFO)
  }

  fun toggleSimulateFailures(simulate: Boolean) {
    _uiState.update { it.copy(config = it.config.copy(simulateFailures = simulate)) }
  }

  fun clearToast() {
    _uiState.update { it.copy(toastMessage = null) }
  }

  fun setInspectAccount(account: GuestAccount?) {
    _uiState.update { it.copy(inspectAccount = account) }
  }

  private fun addLog(message: String, type: LogType = LogType.INFO) {
    val newLog = TerminalLog(message = message, type = type)
    _uiState.update { state ->
      val updated = (state.terminalLogs + newLog).takeLast(60)
      state.copy(terminalLogs = updated)
    }
  }

  fun startGeneration() {
    if (_uiState.value.isGenerating && !_uiState.value.isPaused) return

    if (_uiState.value.isPaused) {
      _uiState.update { it.copy(isPaused = false, currentStepStatus = "Resuming generation...") }
      addLog("▶ Generation resumed.", LogType.INFO)
      runGenerationLoop()
      return
    }

    // New generation run
    val limit = _uiState.value.config.limit
    _uiState.update {
      it.copy(
        accounts = emptyList(),
        stats = GeneratorStats(totalRequested = limit),
        isGenerating = true,
        isPaused = false,
        currentProgress = 0f,
        currentStepStatus = "Starting generation of $limit guest accounts..."
      )
    }
    addLog("=== STARTING BATCH GENERATION (Target: $limit IDs) ===", LogType.INFO)
    addLog("Spin Target: ${_uiState.value.config.selectedSpin.title}", LogType.SPINNING)

    runGenerationLoop()
  }

  private fun runGenerationLoop() {
    generationJob?.cancel()
    generationJob = viewModelScope.launch {
      val config = _uiState.value.config
      val target = config.limit
      val startingIndex = _uiState.value.accounts.size + 1

      for (i in startingIndex..target) {
        if (!_uiState.value.isGenerating) break
        while (_uiState.value.isPaused) {
          delay(200)
          if (!_uiState.value.isGenerating) break
        }
        if (!_uiState.value.isGenerating) break

        val accountName = FreeFireSpinEngine.generateAccountName(config, i)
        _uiState.update {
          it.copy(
            currentStepStatus = "[$i/$target] Handshaking with ${config.selectedServer} for $accountName..."
          )
        }
        delay(180)

        // Generate the account
        val account = FreeFireSpinEngine.generateSingleAccount(config, i)

        if (account.status == AccountStatus.SUCCESS) {
          val wonItem = account.wonItem
          val isPrime = account.isPrimeBundle
          val dropType = if (isPrime) LogType.PRIME_DROP else LogType.NORMAL_DROP
          val dropTag = if (isPrime) "⭐ PRIME BUNDLE" else "📦 NORMAL LOOT"

          addLog("[$i/$target] SUCCESS: UID ${account.uid} ($accountName) -> Spin: ${config.selectedSpin.shortName} -> Won: ${wonItem?.name ?: "Reward"} [$dropTag]", dropType)
        } else {
          addLog("[$i/$target] FAILED: $accountName -> Reason: ${account.failureReason}", LogType.ERROR)
        }

        // Update state
        _uiState.update { state ->
          val newAccounts = state.accounts + account
          val successCount = newAccounts.count { it.status == AccountStatus.SUCCESS }
          val failedCount = newAccounts.count { it.status == AccountStatus.FAILED }
          val primeCount = newAccounts.count { it.isPrimeBundle }
          val normalCount = newAccounts.count { it.status == AccountStatus.SUCCESS && !it.isPrimeBundle }

          val newStats = GeneratorStats(
            totalRequested = target,
            totalProcessed = newAccounts.size,
            successCount = successCount,
            failedCount = failedCount,
            primeBundleCount = primeCount,
            normalItemCount = normalCount
          )

          state.copy(
            accounts = newAccounts,
            stats = newStats,
            currentProgress = newAccounts.size.toFloat() / target.toFloat(),
            currentStepStatus = "[$i/$target] Completed: ${account.accountName}"
          )
        }

        delay(180)
      }

      if (_uiState.value.isGenerating && !_uiState.value.isPaused) {
        val totalSuccess = _uiState.value.stats.successCount
        val totalPrime = _uiState.value.stats.primeBundleCount
        val totalFailed = _uiState.value.stats.failedCount

        addLog("=== GENERATION COMPLETED ===", LogType.SUCCESS)
        addLog("Summary: $totalSuccess Passed, $totalFailed Failed, $totalPrime Prime Bundles won!", LogType.SUCCESS)
        addLog("Ready to export all accounts into ZIP file.", LogType.INFO)

        _uiState.update {
          it.copy(
            isGenerating = false,
            isPaused = false,
            currentProgress = 1f,
            currentStepStatus = "Completed! $totalSuccess Success, $totalFailed Failed, $totalPrime Prime Bundles."
          )
        }

        // Auto create initial export zip in background
        try {
          val zipFile = ZipExportService.createZipArchive(
            getApplication(),
            _uiState.value.accounts,
            _uiState.value.config,
            _uiState.value.stats
          )
          _uiState.update { it.copy(lastExportedZip = zipFile) }
        } catch (_: Exception) {}
      }
    }
  }

  fun pauseGeneration() {
    if (_uiState.value.isGenerating && !_uiState.value.isPaused) {
      _uiState.update { it.copy(isPaused = true, currentStepStatus = "Generation paused by user.") }
      addLog("⏸ Generation paused.", LogType.INFO)
    }
  }

  fun stopGeneration() {
    generationJob?.cancel()
    _uiState.update {
      it.copy(
        isGenerating = false,
        isPaused = false,
        currentStepStatus = "Generation stopped by user."
      )
    }
    addLog("⏹ Generation stopped.", LogType.ERROR)
  }

  fun resetAll() {
    generationJob?.cancel()
    _uiState.update {
      GeneratorUiState(
        config = it.config,
        terminalLogs = listOf(
          TerminalLog(message = "Session reset. Ready to generate new batch.", type = LogType.INFO)
        )
      )
    }
  }

  fun exportAndShareZip(context: Context) {
    val accounts = _uiState.value.accounts
    if (accounts.isEmpty()) {
      _uiState.update { it.copy(toastMessage = "No generated accounts to export yet!") }
      return
    }

    try {
      val zipFile = ZipExportService.createZipArchive(
        context,
        accounts,
        _uiState.value.config,
        _uiState.value.stats
      )
      _uiState.update {
        it.copy(
          lastExportedZip = zipFile,
          toastMessage = "ZIP created: ${zipFile.name} (${ZipExportService.formatFileSize(zipFile.length())})"
        )
      }
      ZipExportService.shareZipArchive(context, zipFile)
    } catch (e: Exception) {
      _uiState.update { it.copy(toastMessage = "Export failed: ${e.localizedMessage}") }
    }
  }

  fun showFilePreview(fileType: String) {
    val accounts = _uiState.value.accounts
    val contents = ZipExportService.buildFileContents(
      accounts,
      _uiState.value.config,
      _uiState.value.stats
    )

    val (title, text) = when (fileType) {
      "success" -> Pair("success_ids.txt", contents.successContent)
      "failed" -> Pair("failed_ids.txt", contents.failedContent)
      "prime" -> Pair("prime_bundle_special_ids.txt", contents.primeBundleContent)
      "normal" -> Pair("normal_items_ids.txt", contents.normalItemsContent)
      "combo" -> Pair("all_accounts_combo.txt", contents.comboRawContent)
      "summary" -> Pair("summary_report.txt", contents.summaryReportContent)
      else -> Pair("File Preview", "No content")
    }

    _uiState.update {
      it.copy(
        previewFileTitle = title,
        previewFileContent = text,
        showPreviewDialog = true
      )
    }
  }

  fun dismissPreviewDialog() {
    _uiState.update { it.copy(showPreviewDialog = false) }
  }

  fun copyToClipboard(context: Context, text: String, label: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
    _uiState.update { it.copy(toastMessage = "Copied $label to clipboard!") }
  }

  fun copyAllSuccessCombo(context: Context) {
    val successAccounts = _uiState.value.accounts.filter { it.status == AccountStatus.SUCCESS }
    if (successAccounts.isEmpty()) {
      _uiState.update { it.copy(toastMessage = "No success accounts to copy!") }
      return
    }
    val sb = StringBuilder()
    successAccounts.forEach { acc ->
      sb.appendLine("UID: ${acc.uid} | PASS: ${acc.password} | ITEM: ${acc.wonItem?.name ?: "Reward"}")
    }
    copyToClipboard(context, sb.toString(), "All Success IDs (${successAccounts.size})")
  }

  fun copyAllPrimeBundleIds(context: Context) {
    val primeAccounts = _uiState.value.accounts.filter { it.isPrimeBundle }
    if (primeAccounts.isEmpty()) {
      _uiState.update { it.copy(toastMessage = "No Prime Bundle accounts found!") }
      return
    }
    val sb = StringBuilder()
    primeAccounts.forEach { acc ->
      sb.appendLine("UID: ${acc.uid} | PASS: ${acc.password} | PRIME: ${acc.wonItem?.name} | SERVER: ${acc.serverRegion}")
    }
    copyToClipboard(context, sb.toString(), "Prime Bundle Accounts (${primeAccounts.size})")
  }

  // --- Dark Mode / Light Mode Management ---
  fun toggleDarkMode() {
    val newMode = !_uiState.value.isDarkMode
    _uiState.update { it.copy(isDarkMode = newMode) }
    addLog("UI theme switched to: ${if (newMode) "Dark Mode (Cyber Gold)" else "Light Mode (Snow Platinum)"}", LogType.INFO)
  }

  fun setDarkMode(dark: Boolean) {
    _uiState.update { it.copy(isDarkMode = dark) }
  }

  // --- Luck Royale Live Sync & Event Management ---
  fun selectRoyaleEvent(eventId: String) {
    _uiState.update { it.copy(selectedRoyaleEventId = eventId) }
  }

  fun syncLuckRoyaleNow() {
    viewModelScope.launch {
      _uiState.update { it.copy(isLiveSyncing = true) }
      addLog("Connecting to Free Fire Live Event Gateway (BD / SG relay)...", LogType.INFO)
      delay(600)
      com.example.service.LuckRoyaleSyncService.triggerLiveSync()
      val updatedList = com.example.service.LuckRoyaleSyncService.getEvents()
      val freeCount = com.example.service.LuckRoyaleSyncService.getFreeSpinEvents().size
      _uiState.update {
        it.copy(
          isLiveSyncing = false,
          luckRoyaleEvents = updatedList,
          activeFreeSpinCount = freeCount,
          lastSyncTimeText = "Just now",
          toastMessage = "লাক রয়্যাল লাইভ সিঙ্ক সফল! ($freeCount টি ফ্রি স্পিন সক্রিয়)"
        )
      }
      addLog("✓ Live Luck Royale synced! $freeCount Free Spin options available right now.", LogType.SUCCESS)
    }
  }

  fun simulateLiveServerButtonUpdate() {
    viewModelScope.launch {
      _uiState.update { it.copy(isLiveSyncing = true) }
      delay(400)
      val newEvent = com.example.service.LuckRoyaleSyncService.simulateServerButtonUpdate()
      val updatedList = com.example.service.LuckRoyaleSyncService.getEvents()
      val freeCount = com.example.service.LuckRoyaleSyncService.getFreeSpinEvents().size
      _uiState.update {
        it.copy(
          isLiveSyncing = false,
          luckRoyaleEvents = updatedList,
          selectedRoyaleEventId = newEvent.id,
          activeFreeSpinCount = freeCount,
          lastSyncTimeText = "Just now",
          toastMessage = "গেম সার্ভার থেকে নতুন লাক রয়্যাল বাটন আপডেট হয়েছে: ${newEvent.banglaName}!"
        )
      }
      addLog("⚡ NEW GAME BUTTON DETECTED: ${newEvent.banglaName} (${newEvent.englishName}) with FREE SPIN!", LogType.PRIME_DROP)
    }
  }

  fun toggleRoyaleFreeSpin(eventId: String, isFree: Boolean) {
    com.example.service.LuckRoyaleSyncService.toggleFreeSpin(eventId, isFree)
    val updatedList = com.example.service.LuckRoyaleSyncService.getEvents()
    val freeCount = com.example.service.LuckRoyaleSyncService.getFreeSpinEvents().size
    _uiState.update {
      it.copy(
        luckRoyaleEvents = updatedList,
        activeFreeSpinCount = freeCount,
        toastMessage = if (isFree) "ফ্রি স্পিন সক্রিয় করা হয়েছে" else "ফ্রি স্পিন বন্ধ করা হয়েছে"
      )
    }
    val event = com.example.service.LuckRoyaleSyncService.getEventById(eventId)
    addLog("Luck Royale '${event?.banglaName ?: eventId}' Free Spin: ${if (isFree) "ENABLED (FREE)" else "DISABLED"}", LogType.INFO)
  }

  fun setTargetLuckRoyale(eventId: String) {
    val event = com.example.service.LuckRoyaleSyncService.getEventById(eventId)
    _uiState.update {
      it.copy(
        config = it.config.copy(
          targetLuckRoyaleId = eventId,
          selectedSpin = event?.spinType ?: it.config.selectedSpin
        ),
        toastMessage = "জেনারেটরের স্পিন সেট করা হয়েছে: ${event?.banglaName ?: eventId}"
      )
    }
    addLog("Generator target set to: ${event?.banglaName ?: eventId} (${event?.freeSpinLabel ?: ""})", LogType.INFO)
  }

  fun testSpinCurrentRoyale() {
    val currentEvent = _uiState.value.luckRoyaleEvents.find { it.id == _uiState.value.selectedRoyaleEventId }
      ?: _uiState.value.luckRoyaleEvents.firstOrNull() ?: return

    val (wonItem, isPrime) = FreeFireSpinEngine.rollSpinItem(currentEvent.spinType)
    addLog("[TEST SPIN] Event: ${currentEvent.banglaName} -> WON: ${wonItem.name} ${if (isPrime) "(⭐ PRIME BUNDLE)" else "(📦 NORMAL)"}", if (isPrime) LogType.PRIME_DROP else LogType.NORMAL_DROP)
    _uiState.update {
      it.copy(toastMessage = "স্পিনে পেয়েছেন: ${wonItem.name}")
    }
  }
}
