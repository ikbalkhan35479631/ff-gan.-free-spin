package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.model.AccountFilter
import com.example.model.AccountStatus
import com.example.model.GuestAccount
import com.example.ui.components.AccountItemCard
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
fun AccountsListScreen(
  accounts: List<GuestAccount>,
  activeFilter: AccountFilter,
  searchQuery: String,
  onFilterChange: (AccountFilter) -> Unit,
  onSearchChange: (String) -> Unit,
  onCopyUid: (String) -> Unit,
  onCopyPassword: (String) -> Unit,
  onCopyCombo: (String) -> Unit,
  onCopyAllSuccess: () -> Unit,
  onCopyPrimeIds: () -> Unit,
  onGoToExport: () -> Unit,
  onGoToGenerator: () -> Unit,
  modifier: Modifier = Modifier
) {
  val filteredAccounts = accounts.filter { acc ->
    val matchesFilter = when (activeFilter) {
      AccountFilter.ALL -> true
      AccountFilter.SUCCESS_ONLY -> acc.status == AccountStatus.SUCCESS
      AccountFilter.PRIME_BUNDLES -> acc.isPrimeBundle
      AccountFilter.NORMAL_ITEMS -> acc.status == AccountStatus.SUCCESS && !acc.isPrimeBundle
      AccountFilter.FAILED_ONLY -> acc.status == AccountStatus.FAILED
    }
    val matchesSearch = if (searchQuery.isBlank()) true else {
      acc.uid.contains(searchQuery, ignoreCase = true) ||
          acc.accountName.contains(searchQuery, ignoreCase = true) ||
          (acc.wonItem?.name?.contains(searchQuery, ignoreCase = true) == true)
    }
    matchesFilter && matchesSearch
  }

  val totalSuccess = accounts.count { it.status == AccountStatus.SUCCESS }
  val totalPrime = accounts.count { it.isPrimeBundle }
  val totalNormal = accounts.count { it.status == AccountStatus.SUCCESS && !it.isPrimeBundle }
  val totalFailed = accounts.count { it.status == AccountStatus.FAILED }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 10.dp)
  ) {
    // Search Box
    OutlinedTextField(
      value = searchQuery,
      onValueChange = onSearchChange,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("accounts_search_field"),
      placeholder = { Text("Search by UID, name, or reward...", fontSize = 12.sp) },
      leadingIcon = {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = "Search",
          tint = AmberGold,
          modifier = Modifier.size(18.dp)
        )
      },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { onSearchChange("") }) {
            Icon(
              imageVector = Icons.Default.Clear,
              contentDescription = "Clear",
              tint = TextSecondary,
              modifier = Modifier.size(16.dp)
            )
          }
        }
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
      shape = RoundedCornerShape(10.dp)
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Filter Chips Horizontal Row
    val filterScroll = rememberScrollState()
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(filterScroll),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      FilterPill(
        title = "All (${accounts.size})",
        isSelected = activeFilter == AccountFilter.ALL,
        onClick = { onFilterChange(AccountFilter.ALL) }
      )
      FilterPill(
        title = "Success ($totalSuccess)",
        isSelected = activeFilter == AccountFilter.SUCCESS_ONLY,
        accentColor = NeonGreen,
        onClick = { onFilterChange(AccountFilter.SUCCESS_ONLY) }
      )
      FilterPill(
        title = "Prime ⭐ ($totalPrime)",
        isSelected = activeFilter == AccountFilter.PRIME_BUNDLES,
        accentColor = PrimeGold,
        onClick = { onFilterChange(AccountFilter.PRIME_BUNDLES) }
      )
      FilterPill(
        title = "Normal 📦 ($totalNormal)",
        isSelected = activeFilter == AccountFilter.NORMAL_ITEMS,
        accentColor = NeonCyan,
        onClick = { onFilterChange(AccountFilter.NORMAL_ITEMS) }
      )
      FilterPill(
        title = "Failed ⚠️ ($totalFailed)",
        isSelected = activeFilter == AccountFilter.FAILED_ONLY,
        accentColor = DangerRed,
        onClick = { onFilterChange(AccountFilter.FAILED_ONLY) }
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Quick Action Bar: Copy All Success, Copy Prime, Go to ZIP
    if (accounts.isNotEmpty()) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        OutlinedButton(
          onClick = onCopyAllSuccess,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonGreen),
          border = BorderStroke(0.8.dp, NeonGreen.copy(alpha = 0.5f))
        ) {
          Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = null,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text("Copy Success", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        if (totalPrime > 0) {
          OutlinedButton(
            onClick = onCopyPrimeIds,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimeGold),
            border = BorderStroke(0.8.dp, PrimeGold.copy(alpha = 0.5f))
          ) {
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = null,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Copy Prime", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }

        Button(
          onClick = onGoToExport,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = FlameOrange)
        ) {
          Icon(
            imageVector = Icons.Default.Archive,
            contentDescription = null,
            modifier = Modifier.size(13.dp),
            tint = Color.White
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text("ZIP Export", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
    }

    // Accounts list or Empty State
    if (accounts.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(bottom = 60.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(24.dp)
        ) {
          Box(
            modifier = Modifier
              .size(64.dp)
              .clip(RoundedCornerShape(32.dp))
              .background(DarkSurfaceElevated),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Group,
              contentDescription = null,
              tint = AmberGold,
              modifier = Modifier.size(32.dp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = "No Guest IDs Generated Yet",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )

          Text(
            text = "Go to Generator tab, set your account limit and choose your free spin location to begin batch creation.",
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 6.dp, bottom = 18.dp)
          )

          Button(
            onClick = onGoToGenerator,
            colors = ButtonDefaults.buttonColors(containerColor = AmberGold)
          ) {
            Text("Open Generator", color = Color(0xFF1E1200), fontWeight = FontWeight.Bold)
          }
        }
      }
    } else if (filteredAccounts.isEmpty()) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "No accounts match current filter / search",
          fontSize = 13.sp,
          color = TextMuted
        )
      }
    } else {
      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        itemsIndexed(filteredAccounts, key = { _, acc -> acc.id }) { index, account ->
          AccountItemCard(
            account = account,
            index = index,
            onCopyUid = onCopyUid,
            onCopyPassword = onCopyPassword,
            onCopyCombo = onCopyCombo
          )
        }
        item {
          Spacer(modifier = Modifier.height(20.dp))
        }
      }
    }
  }
}

@Composable
private fun FilterPill(
  title: String,
  isSelected: Boolean,
  accentColor: Color = AmberGold,
  onClick: () -> Unit
) {
  val bg = if (isSelected) accentColor else DarkSurfaceElevated
  val textCol = if (isSelected) Color(0xFF111622) else TextSecondary

  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(bg)
      .clickable(onClick = onClick)
      .padding(horizontal = 10.dp, vertical = 6.dp)
  ) {
    Text(
      text = title,
      fontSize = 11.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
      color = textCol
    )
  }
}
