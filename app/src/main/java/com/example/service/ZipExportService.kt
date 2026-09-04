package com.example.service

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.model.AccountStatus
import com.example.model.GeneratorConfig
import com.example.model.GeneratorStats
import com.example.model.GuestAccount
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ZipExportService {

  data class GeneratedFilesContent(
    val successContent: String,
    val failedContent: String,
    val primeBundleContent: String,
    val normalItemsContent: String,
    val comboRawContent: String,
    val summaryReportContent: String
  )

  fun buildFileContents(
    accounts: List<GuestAccount>,
    config: GeneratorConfig,
    stats: GeneratorStats
  ): GeneratedFilesContent {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val now = dateFormat.format(Date())

    val successAccounts = accounts.filter { it.status == AccountStatus.SUCCESS }
    val failedAccounts = accounts.filter { it.status == AccountStatus.FAILED }
    val primeAccounts = successAccounts.filter { it.isPrimeBundle }
    val normalAccounts = successAccounts.filter { !it.isPrimeBundle }

    // 1. success_ids.txt
    val successSb = StringBuilder()
    successSb.appendLine("=========================================================================")
    successSb.appendLine("        FREE FIRE GUEST ID GENERATOR - ALL SUCCESSFUL IDS")
    successSb.appendLine("=========================================================================")
    successSb.appendLine("Generated At: $now")
    successSb.appendLine("Total Successful IDs: ${successAccounts.size}")
    successSb.appendLine("Server Region: ${config.selectedServer}")
    successSb.appendLine("Spin Location: ${config.selectedSpin.title} (${config.selectedSpin.freeLabel})")
    successSb.appendLine("-------------------------------------------------------------------------")
    successSb.appendLine("FORMAT: [INDEX] UID | PASSWORD | ACCOUNT_NAME | WON_ITEM | SPIN_LOCATION")
    successSb.appendLine("-------------------------------------------------------------------------")
    if (successAccounts.isEmpty()) {
      successSb.appendLine("(No successful IDs generated in this batch)")
    } else {
      successAccounts.forEachIndexed { idx, acc ->
        val itemName = acc.wonItem?.name ?: "Standard Reward"
        val eventLabel = acc.wonLuckRoyaleEventName ?: acc.spinType.title
        successSb.appendLine("[${idx + 1}] UID: ${acc.uid} | PASSWORD: ${acc.password} | NAME: ${acc.accountName} | ITEM: $itemName | ROYALE: $eventLabel | REGION: ${acc.serverRegion}")
      }
    }
    successSb.appendLine("=========================================================================")

    // 2. failed_ids.txt
    val failedSb = StringBuilder()
    failedSb.appendLine("=========================================================================")
    failedSb.appendLine("         FREE FIRE GUEST ID GENERATOR - FAILED IDS & ATTEMPTS")
    failedSb.appendLine("=========================================================================")
    failedSb.appendLine("Generated At: $now")
    failedSb.appendLine("Total Failed Attempts: ${failedAccounts.size}")
    failedSb.appendLine("-------------------------------------------------------------------------")
    failedSb.appendLine("FORMAT: [INDEX] ATTEMPT_NAME | STATUS | REASON | TIMESTAMP")
    failedSb.appendLine("-------------------------------------------------------------------------")
    if (failedAccounts.isEmpty()) {
      failedSb.appendLine("(No failed accounts! 100% success rate achieved)")
    } else {
      failedAccounts.forEachIndexed { idx, acc ->
        val reason = acc.failureReason ?: "Unknown Gateway Error"
        failedSb.appendLine("[${idx + 1}] NAME: ${acc.accountName} | UID_ATTEMPT: ${acc.uid} | STATUS: FAILED | ERROR: $reason")
      }
    }
    failedSb.appendLine("=========================================================================")

    // 3. prime_bundle_special_ids.txt
    val primeSb = StringBuilder()
    primeSb.appendLine("=========================================================================")
    primeSb.appendLine("    FREE FIRE SPECIAL PRIME BUNDLE & MYTHIC REWARDS ACCOUNTS")
    primeSb.appendLine("=========================================================================")
    primeSb.appendLine("Generated At: $now")
    primeSb.appendLine("Total Prime Bundle Hits: ${primeAccounts.size}")
    primeSb.appendLine("Spin Location: ${config.selectedSpin.title}")
    primeSb.appendLine("-------------------------------------------------------------------------")
    primeSb.appendLine("FORMAT: [INDEX] UID | PASSWORD | ACCOUNT_NAME | PRIME_BUNDLE | RARITY")
    primeSb.appendLine("-------------------------------------------------------------------------")
    if (primeAccounts.isEmpty()) {
      primeSb.appendLine("(No Prime Bundles won in this batch. Try spinning Prime Wheel or increase limit!)")
    } else {
      primeAccounts.forEachIndexed { idx, acc ->
        val item = acc.wonItem
        val itemName = item?.name ?: "Special Prime Item"
        val rarity = item?.rarity?.label ?: "Mythic Prime"
        primeSb.appendLine("[${idx + 1}] UID: ${acc.uid} | PASSWORD: ${acc.password} | NAME: ${acc.accountName} | PRIME_ITEM: $itemName | RARITY: $rarity | SERVER: ${acc.serverRegion}")
      }
    }
    primeSb.appendLine("=========================================================================")

    // 4. normal_items_ids.txt
    val normalSb = StringBuilder()
    normalSb.appendLine("=========================================================================")
    normalSb.appendLine("          FREE FIRE NORMAL / COMMON ITEMS ACCOUNTS")
    normalSb.appendLine("=========================================================================")
    normalSb.appendLine("Generated At: $now")
    normalSb.appendLine("Total Normal Item IDs: ${normalAccounts.size}")
    normalSb.appendLine("-------------------------------------------------------------------------")
    normalSb.appendLine("FORMAT: [INDEX] UID | PASSWORD | ACCOUNT_NAME | NORMAL_ITEM")
    normalSb.appendLine("-------------------------------------------------------------------------")
    if (normalAccounts.isEmpty()) {
      normalSb.appendLine("(No normal item accounts in this batch)")
    } else {
      normalAccounts.forEachIndexed { idx, acc ->
        val itemName = acc.wonItem?.name ?: "Normal Loot"
        normalSb.appendLine("[${idx + 1}] UID: ${acc.uid} | PASSWORD: ${acc.password} | NAME: ${acc.accountName} | ITEM: $itemName")
      }
    }
    normalSb.appendLine("=========================================================================")

    // 5. all_accounts_combo.txt (Single-line raw combo format)
    val comboSb = StringBuilder()
    comboSb.appendLine("# FREE FIRE GUEST COMBO FORMAT (UID:PASSWORD:NAME:ITEM)")
    successAccounts.forEach { acc ->
      val itemName = acc.wonItem?.name?.replace(" ", "_") ?: "Reward"
      comboSb.appendLine("${acc.uid}:${acc.password}:${acc.accountName}:$itemName:${acc.serverRegion}")
    }

    // 6. summary_report.txt
    val summarySb = StringBuilder()
    summarySb.appendLine("=========================================================================")
    summarySb.appendLine("              FREE FIRE GUEST ID BATCH GENERATION REPORT")
    summarySb.appendLine("=========================================================================")
    summarySb.appendLine("Date & Time        : $now")
    summarySb.appendLine("Server Region      : ${config.selectedServer}")
    summarySb.appendLine("Spin Location      : ${config.selectedSpin.title} (${config.selectedSpin.freeLabel})")
    summarySb.appendLine("Total Requested    : ${config.limit}")
    summarySb.appendLine("Total Processed    : ${accounts.size}")
    summarySb.appendLine("Successful (Passed): ${successAccounts.size} (${stats.successPercentage}%)")
    summarySb.appendLine("Failed Attempts    : ${failedAccounts.size}")
    summarySb.appendLine("Prime Bundle Hits  : ${primeAccounts.size} (${stats.primePercentage}% of passed)")
    summarySb.appendLine("Normal Item Hits   : ${normalAccounts.size}")
    summarySb.appendLine("Name Prefix Used   : ${config.namePrefix}")
    summarySb.appendLine("Password Config    : ${if (config.isAutoPassword) "Auto-Secure" else config.customPassword}")
    summarySb.appendLine("-------------------------------------------------------------------------")
    summarySb.appendLine("ZIP ARCHIVE FILE CONTENTS:")
    summarySb.appendLine(" 1. success_ids.txt               - All successfully created IDs with password & items")
    summarySb.appendLine(" 2. failed_ids.txt                - Failed attempts with error details")
    summarySb.appendLine(" 3. prime_bundle_special_ids.txt  - High-tier Sakura/HipHop/Cobra Prime accounts")
    summarySb.appendLine(" 4. normal_items_ids.txt          - Standard gold/voucher/weapon crate accounts")
    summarySb.appendLine(" 5. all_accounts_combo.txt        - Raw combo format UID:PASS:NAME:ITEM")
    summarySb.appendLine(" 6. summary_report.txt            - This batch statistical report")
    summarySb.appendLine("=========================================================================")

    return GeneratedFilesContent(
      successContent = successSb.toString(),
      failedContent = failedSb.toString(),
      primeBundleContent = primeSb.toString(),
      normalItemsContent = normalSb.toString(),
      comboRawContent = comboSb.toString(),
      summaryReportContent = summarySb.toString()
    )
  }

  /**
   * Generates a standard .zip archive containing all categorized files
   */
  fun createZipArchive(
    context: Context,
    accounts: List<GuestAccount>,
    config: GeneratorConfig,
    stats: GeneratorStats
  ): File {
    val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val zipFile = File(exportDir, "FF_Guest_IDs_${timestamp}.zip")

    val contents = buildFileContents(accounts, config, stats)

    ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { zos ->
      fun addEntry(fileName: String, content: String) {
        val entry = ZipEntry(fileName)
        zos.putNextEntry(entry)
        zos.write(content.toByteArray(Charsets.UTF_8))
        zos.closeEntry()
      }

      addEntry("success_ids.txt", contents.successContent)
      addEntry("failed_ids.txt", contents.failedContent)
      addEntry("prime_bundle_special_ids.txt", contents.primeBundleContent)
      addEntry("normal_items_ids.txt", contents.normalItemsContent)
      addEntry("all_accounts_combo.txt", contents.comboRawContent)
      addEntry("summary_report.txt", contents.summaryReportContent)
    }

    return zipFile
  }

  /**
   * Triggers an Android system share intent for the exported ZIP file
   */
  fun shareZipArchive(context: Context, zipFile: File) {
    val uri = FileProvider.getUriForFile(
      context,
      "${context.packageName}.fileprovider",
      zipFile
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
      type = "application/zip"
      putExtra(Intent.EXTRA_STREAM, uri)
      putExtra(Intent.EXTRA_SUBJECT, "Free Fire Guest IDs - Batch Export")
      putExtra(Intent.EXTRA_TEXT, "Here is the ZIP archive containing categorized Free Fire Guest IDs, Passwords, and Won Spin Items.")
      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    val chooser = Intent.createChooser(intent, "Share Free Fire Guest IDs ZIP")
    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(chooser)
  }

  fun formatFileSize(bytes: Long): String {
    return when {
      bytes < 1024 -> "$bytes B"
      bytes < 1024 * 1024 -> String.format(Locale.US, "%.1f KB", bytes / 1024f)
      else -> String.format(Locale.US, "%.2f MB", bytes / (1024f * 1024f))
    }
  }
}
