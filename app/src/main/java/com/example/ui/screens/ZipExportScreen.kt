package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GeneratorConfig
import com.example.model.GeneratorStats
import com.example.model.GuestAccount
import com.example.service.ZipExportService
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberGoldDark
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.PrimeGold
import com.example.ui.theme.PrimePurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.io.File

@Composable
fun ZipExportScreen(
  accounts: List<GuestAccount>,
  config: GeneratorConfig,
  stats: GeneratorStats,
  lastExportedZip: File?,
  onExportAndShare: () -> Unit,
  onShowPreview: (String) -> Unit,
  onCopyText: (String, String) -> Unit,
  onCopyAllSuccess: () -> Unit,
  onCopyPrimeIds: () -> Unit,
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

    // Main ZIP Banner Card
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("zip_archive_card"),
      shape = RoundedCornerShape(16.dp),
      color = DarkSurface,
      border = BorderStroke(1.2.dp, AmberGold)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.linearGradient(listOf(AmberGold, FlameOrange))),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.FolderZip,
                contentDescription = null,
                tint = Color(0xFF1E1200),
                modifier = Modifier.size(26.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "ZIP ARCHIVE EXPORTER",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AmberGold
              )
              Text(
                text = if (lastExportedZip != null) {
                  "${lastExportedZip.name} • ${ZipExportService.formatFileSize(lastExportedZip.length())}"
                } else {
                  "Ready to bundle ${accounts.size} accounts into ZIP"
                },
                fontSize = 11.sp,
                color = TextSecondary,
                fontFamily = FontFamily.Monospace
              )
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(NeonGreen.copy(alpha = 0.15f))
              .padding(horizontal = 6.dp, vertical = 3.dp)
          ) {
            Text(
              text = "6 FILES",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = NeonGreen
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "As requested, all accounts are organized into distinct files inside one master ZIP archive: Success IDs, Failed IDs, Special Prime Bundles, Normal items, and Combo raw lists.",
          fontSize = 12.sp,
          color = TextSecondary,
          lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Large Action Button: Save & Share ZIP File
        Button(
          onClick = onExportAndShare,
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("export_share_zip_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = AmberGold)
        ) {
          Icon(
            imageVector = Icons.Default.Share,
            contentDescription = null,
            tint = Color(0xFF1E1200),
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "SAVE & SHARE ZIP FILE",
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1E1200)
          )
        }
      }
    }

    // Quick clipboard shortcuts
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      OutlinedButton(
        onClick = onCopyAllSuccess,
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonGreen),
        border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.5f))
      ) {
        Icon(
          imageVector = Icons.Default.ContentCopy,
          contentDescription = null,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text("Copy Success IDs", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }

      OutlinedButton(
        onClick = onCopyPrimeIds,
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimeGold),
        border = BorderStroke(1.dp, PrimeGold.copy(alpha = 0.5f))
      ) {
        Icon(
          imageVector = Icons.Default.Star,
          contentDescription = null,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text("Copy Prime IDs", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }

    // Files List Header
    Text(
      text = "CATEGORIZED FILES INSIDE ZIP ARCHIVE",
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      color = AmberGold,
      letterSpacing = 0.5.sp,
      modifier = Modifier.padding(top = 6.dp)
    )

    // 1. success_ids.txt
    ZipFileEntryCard(
      fileName = "success_ids.txt",
      description = "All successfully generated accounts with UID, Password, Name, Server, and Won Item.",
      itemCount = stats.successCount,
      accentColor = NeonGreen,
      icon = Icons.Default.CheckCircle,
      onPreview = { onShowPreview("success") }
    )

    // 2. failed_ids.txt
    ZipFileEntryCard(
      fileName = "failed_ids.txt",
      description = "Accounts that failed during registration with specific error reasons & attempts.",
      itemCount = stats.failedCount,
      accentColor = DangerRed,
      icon = Icons.Default.Warning,
      onPreview = { onShowPreview("failed") }
    )

    // 3. prime_bundle_special_ids.txt
    ZipFileEntryCard(
      fileName = "prime_bundle_special_ids.txt",
      description = "Special file containing only rare/legendary Prime Bundles (Sakura, Hip Hop, Criminal, Cobra).",
      itemCount = stats.primeBundleCount,
      accentColor = PrimeGold,
      icon = Icons.Default.Star,
      onPreview = { onShowPreview("prime") }
    )

    // 4. normal_items_ids.txt
    ZipFileEntryCard(
      fileName = "normal_items_ids.txt",
      description = "Standard loot accounts containing gold coins, weapon crates, vouchers, and fragments.",
      itemCount = stats.normalItemCount,
      accentColor = NeonCyan,
      icon = Icons.Default.Description,
      onPreview = { onShowPreview("normal") }
    )

    // 5. all_accounts_combo.txt
    ZipFileEntryCard(
      fileName = "all_accounts_combo.txt",
      description = "Raw colon/pipe format (UID:PASSWORD:NAME:ITEM) for one-click bulk checker import.",
      itemCount = stats.successCount,
      accentColor = AmberGold,
      icon = Icons.Default.ContentCopy,
      onPreview = { onShowPreview("combo") }
    )

    // 6. summary_report.txt
    ZipFileEntryCard(
      fileName = "summary_report.txt",
      description = "Complete statistical audit report including drop rates, timestamps, and generator config.",
      itemCount = stats.totalProcessed,
      accentColor = TextSecondary,
      icon = Icons.Default.Description,
      onPreview = { onShowPreview("summary") }
    )

    Spacer(modifier = Modifier.height(20.dp))
  }
}

@Composable
private fun ZipFileEntryCard(
  fileName: String,
  description: String,
  itemCount: Int,
  accentColor: Color,
  icon: ImageVector,
  onPreview: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("file_entry_$fileName"),
    shape = RoundedCornerShape(12.dp),
    color = DarkSurface,
    border = BorderStroke(0.8.dp, DarkBorder)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurfaceElevated),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(20.dp)
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = fileName,
              fontSize = 13.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(accentColor.copy(alpha = 0.15f))
                .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
              Text(
                text = "$itemCount entries",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
              )
            }
          }

          Text(
            text = description,
            fontSize = 11.sp,
            color = TextMuted,
            maxLines = 2,
            lineHeight = 14.sp
          )
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Preview Button
      OutlinedButton(
        onClick = onPreview,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberGold),
        border = BorderStroke(0.8.dp, AmberGold.copy(alpha = 0.5f))
      ) {
        Icon(
          imageVector = Icons.Default.Visibility,
          contentDescription = null,
          modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text("View", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}
