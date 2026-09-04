package com.example.model

enum class AccountStatus {
  SUCCESS,
  FAILED
}

enum class ItemRarity(val label: String) {
  MYTHIC_PRIME("Mythic Prime"),
  LEGENDARY("Legendary"),
  EPIC("Epic"),
  NORMAL("Normal")
}

enum class ItemCategory {
  PRIME_BUNDLE,
  SPECIAL_OUTFIT,
  WEAPON_SKIN,
  NORMAL_ITEM,
  VOUCHER
}

data class GameItem(
  val name: String,
  val category: ItemCategory,
  val rarity: ItemRarity,
  val iconEmoji: String,
  val description: String
)

enum class SpinType(
  val id: String,
  val title: String,
  val shortName: String,
  val emoji: String,
  val description: String,
  val freeLabel: String
) {
  GOLD_ROYALE(
    id = "gold_royale",
    title = "Gold Royale",
    shortName = "Gold Spin",
    emoji = "🎯",
    description = "Daily 1st Free Spin for Gold & Rare Bundles",
    freeLabel = "1x Daily Free"
  ),
  PRIME_EVENT_WHEEL(
    id = "prime_event",
    title = "Prime Bundle Wheel",
    shortName = "Prime Wheel",
    emoji = "👑",
    description = "Special Event Free Spin for Sakura & Criminal Bundles",
    freeLabel = "Free Event Spin"
  ),
  WEAPON_ROYALE(
    id = "weapon_royale",
    title = "Weapon Royale",
    shortName = "Weapon Spin",
    emoji = "🔫",
    description = "Daily Free Spin for Legendary Dragon AK & Gun Crates",
    freeLabel = "Daily Free Spin"
  ),
  DIAMOND_ROYALE(
    id = "diamond_royale",
    title = "Diamond Royale",
    shortName = "Diamond Spin",
    emoji = "💎",
    description = "Free Ticket Spin for Arctic Blue & Magic Cube",
    freeLabel = "1x Free Ticket"
  ),
  INCUBATOR(
    id = "incubator",
    title = "Incubator Wheel",
    shortName = "Incubator",
    emoji = "🌀",
    description = "Free Daily Blueprint & Evolution Stone Spin",
    freeLabel = "Free Blueprint Spin"
  ),
  WELCOME_SPIN(
    id = "welcome_spin",
    title = "Newbie Starter Spin",
    shortName = "Welcome Spin",
    emoji = "🎁",
    description = "Guaranteed Free Welcome Gift Spin for New Guest IDs",
    freeLabel = "100% Free Starter"
  )
}

data class LuckRoyaleEvent(
  val id: String,
  val banglaName: String,
  val englishName: String,
  val badgeText: String? = null,
  val duration: String,
  val daysRemaining: String,
  val dailyRefreshTimer: String = "18:30:56",
  val grandPrizeTitle: String,
  val grandPrizeSubtitle: String = "বান্ডেলটিতে থাকা সকল আইটেম গ্রহণ করতে খুলে নিন!",
  val grandPrizeItems: List<String>,
  val isFreeSpinAvailable: Boolean = false,
  val freeSpinLabel: String = "1 টি স্পিন FREE",
  val singleSpinCostText: String = "1000",
  val tenSpinsCostText: String = "10000",
  val guaranteedSpins: Int = 113,
  val bannerEmoji: String = "🎯",
  val spinType: SpinType = SpinType.GOLD_ROYALE,
  val isCustomOrLive: Boolean = false,
  val lastSyncedAt: Long = System.currentTimeMillis()
)

data class GuestAccount(
  val id: String,
  val uid: String,
  val accountName: String,
  val password: String,
  val serverRegion: String,
  val status: AccountStatus,
  val failureReason: String? = null,
  val spinType: SpinType,
  val wonItem: GameItem? = null,
  val wonLuckRoyaleEventName: String? = null,
  val isPrimeBundle: Boolean = false,
  val timestamp: Long = System.currentTimeMillis()
)

data class GeneratorConfig(
  val limit: Int = 10,
  val namePrefix: String = "FF_Guest_",
  val isSequentialName: Boolean = true,
  val customPassword: String = "FF@Guest2026",
  val isAutoPassword: Boolean = false,
  val selectedSpin: SpinType = SpinType.PRIME_EVENT_WHEEL,
  val selectedServer: String = "Bangladesh (BD)",
  val simulateFailures: Boolean = true,
  val targetLuckRoyaleId: String = "all_free",
  val isAutoSpinAllFreeEvents: Boolean = true
)

enum class LogType {
  INFO,
  SUCCESS,
  PRIME_DROP,
  NORMAL_DROP,
  ERROR,
  SPINNING
}

data class TerminalLog(
  val id: Long = System.nanoTime(),
  val message: String,
  val type: LogType = LogType.INFO,
  val timestamp: Long = System.currentTimeMillis()
)

data class GeneratorStats(
  val totalRequested: Int = 0,
  val totalProcessed: Int = 0,
  val successCount: Int = 0,
  val failedCount: Int = 0,
  val primeBundleCount: Int = 0,
  val normalItemCount: Int = 0
) {
  val successPercentage: Int
    get() = if (totalProcessed > 0) ((successCount * 100) / totalProcessed) else 0

  val primePercentage: Int
    get() = if (successCount > 0) ((primeBundleCount * 100) / successCount) else 0
}

enum class AccountFilter(val label: String) {
  ALL("All IDs"),
  SUCCESS_ONLY("Success Passed"),
  PRIME_BUNDLES("Prime Bundles ⭐"),
  NORMAL_ITEMS("Normal Loot 📦"),
  FAILED_ONLY("Failed ⚠️")
}
