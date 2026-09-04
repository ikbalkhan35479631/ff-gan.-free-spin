package com.example.service

import com.example.model.AccountStatus
import com.example.model.GameItem
import com.example.model.GeneratorConfig
import com.example.model.GuestAccount
import com.example.model.ItemCategory
import com.example.model.ItemRarity
import com.example.model.SpinType
import java.util.UUID
import kotlin.random.Random

object FreeFireSpinEngine {

  // Prime Bundles & Mythic Items
  val PRIME_ITEMS = listOf(
    GameItem(
      name = "🌸 Sakura Blossom Season 1 Bundle",
      category = ItemCategory.PRIME_BUNDLE,
      rarity = ItemRarity.MYTHIC_PRIME,
      iconEmoji = "🌸",
      description = "Ultra Rare Season 1 Samurai & Cherry Blossom Elite Pass Bundle"
    ),
    GameItem(
      name = "🎧 Hip Hop Season 2 Elite Bundle",
      category = ItemCategory.PRIME_BUNDLE,
      rarity = ItemRarity.MYTHIC_PRIME,
      iconEmoji = "🎧",
      description = "Legendary Season 2 Golden Mic & Graffiti Hip Hop Outfit"
    ),
    GameItem(
      name = "🔴 Red Criminal Special Outfit",
      category = ItemCategory.PRIME_BUNDLE,
      rarity = ItemRarity.MYTHIC_PRIME,
      iconEmoji = "🔴",
      description = "Top Tier Criminal Squad Red Jumpsuit with Demon Mask"
    ),
    GameItem(
      name = "🐍 Cobra Rage Legendary Bundle",
      category = ItemCategory.PRIME_BUNDLE,
      rarity = ItemRarity.MYTHIC_PRIME,
      iconEmoji = "🐍",
      description = "Evolutionary 4-Color Shift Cobra Mech Costume"
    ),
    GameItem(
      name = "❄️ Arctic Blue Warrior Bundle",
      category = ItemCategory.PRIME_BUNDLE,
      rarity = ItemRarity.MYTHIC_PRIME,
      iconEmoji = "❄️",
      description = "Diamond Royale Mythic Cyber Frost Flame Samurai"
    ),
    GameItem(
      name = "🥋 Breakdancer Street Style Bundle",
      category = ItemCategory.PRIME_BUNDLE,
      rarity = ItemRarity.LEGENDARY,
      iconEmoji = "🥋",
      description = "Iconic Gold Royale Vintage Breakdance Outfit"
    ),
    GameItem(
      name = "💎 Magic Cube + Prime Bundle Token",
      category = ItemCategory.PRIME_BUNDLE,
      rarity = ItemRarity.MYTHIC_PRIME,
      iconEmoji = "💎",
      description = "Instant 100% Exchange for Any Prime Legendary Bundle in Store"
    ),
    GameItem(
      name = "🟡 Yellow Criminal Squad Jumpsuit",
      category = ItemCategory.PRIME_BUNDLE,
      rarity = ItemRarity.MYTHIC_PRIME,
      iconEmoji = "🟡",
      description = "Extremely Rare Heist Jumpsuit with Cartoon Mask"
    ),
    GameItem(
      name = "⚡ Rampage New Dawn Titan Bundle",
      category = ItemCategory.PRIME_BUNDLE,
      rarity = ItemRarity.LEGENDARY,
      iconEmoji = "⚡",
      description = "Exclusive Rampage Campaign Thunder Warrior Costume"
    ),
    GameItem(
      name = "🐉 Blue Flame Draco MP40 Skin (Evo Max)",
      category = ItemCategory.WEAPON_SKIN,
      rarity = ItemRarity.MYTHIC_PRIME,
      iconEmoji = "🐉",
      description = "Evolution Dragon Weapon with Blue Flame Bullet FX"
    )
  )

  // Normal Items & Standard Free Drops
  val NORMAL_ITEMS = listOf(
    GameItem(
      name = "🪙 2,500 Gold Coins",
      category = ItemCategory.NORMAL_ITEM,
      rarity = ItemRarity.NORMAL,
      iconEmoji = "🪙",
      description = "Standard in-game gold currency used for basic upgrades"
    ),
    GameItem(
      name = "📦 Dragon AK47 Weapon Loot Crate x3",
      category = ItemCategory.NORMAL_ITEM,
      rarity = ItemRarity.EPIC,
      iconEmoji = "📦",
      description = "Crates containing chance for Dragon AK Weapon skins"
    ),
    GameItem(
      name = "🎫 Weapon Royale Free Voucher (7-Day)",
      category = ItemCategory.VOUCHER,
      rarity = ItemRarity.NORMAL,
      iconEmoji = "🎫",
      description = "Usable in Weapon Royale for one free spin"
    ),
    GameItem(
      name = "🎫 Diamond Royale Free Ticket",
      category = ItemCategory.VOUCHER,
      rarity = ItemRarity.NORMAL,
      iconEmoji = "🎫",
      description = "Ticket for free spin in Diamond Royale section"
    ),
    GameItem(
      name = "👖 Classic Camo Combat Pants",
      category = ItemCategory.NORMAL_ITEM,
      rarity = ItemRarity.NORMAL,
      iconEmoji = "👖",
      description = "Desert camouflage tactical pants for battle royale"
    ),
    GameItem(
      name = "🥾 Street Runner Speed Sneakers",
      category = ItemCategory.NORMAL_ITEM,
      rarity = ItemRarity.NORMAL,
      iconEmoji = "🥾",
      description = "Red and white speed boots for guest avatars"
    ),
    GameItem(
      name = "🧩 Universal Character Memory Fragments x500",
      category = ItemCategory.NORMAL_ITEM,
      rarity = ItemRarity.NORMAL,
      iconEmoji = "🧩",
      description = "Memory fragments used to level up character skills"
    ),
    GameItem(
      name = "🎒 Cyber Hunter Level 3 Backpack",
      category = ItemCategory.NORMAL_ITEM,
      rarity = ItemRarity.EPIC,
      iconEmoji = "🎒",
      description = "Neon accented tactical backpack skin"
    ),
    GameItem(
      name = "🛹 Fiery Lava Surfboard",
      category = ItemCategory.NORMAL_ITEM,
      rarity = ItemRarity.EPIC,
      iconEmoji = "🛹",
      description = "Parachute drop surfboard with orange fire trail"
    ),
    GameItem(
      name = "🪙 5,000 Gold Bounty Pack",
      category = ItemCategory.NORMAL_ITEM,
      rarity = ItemRarity.NORMAL,
      iconEmoji = "🪙",
      description = "Bonus gold pack for completing first daily spin"
    ),
    GameItem(
      name = "🍖 Pet Food Crate x5",
      category = ItemCategory.NORMAL_ITEM,
      rarity = ItemRarity.NORMAL,
      iconEmoji = "🍖",
      description = "Nutritious pet food for upgrading pet skills"
    ),
    GameItem(
      name = "🪂 Skyline Glider Chute Skin",
      category = ItemCategory.NORMAL_ITEM,
      rarity = ItemRarity.NORMAL,
      iconEmoji = "🪂",
      description = "Standard skydiving parachute skin"
    )
  )

  private val FAILURE_REASONS = listOf(
    "Gateway Rate Limited (HTTP 429 - Too Many Requests)",
    "Server Handshake Timeout (Garena BD Relay 504)",
    "Device Fingerprint Invalidation / Captcha Challenge",
    "Account Creation IP Cooldown - Wait 15s",
    "Region Gateway Handshake Aborted (SSL Reset)"
  )

  /**
   * Generates a 10-digit Free Fire UID with realistic prefix (20xxxxxxxx or 31xxxxxxxx)
   */
  fun generateRealisticUID(): String {
    val prefix = if (Random.nextBoolean()) "2" else "3"
    val rest = StringBuilder()
    repeat(9) {
      rest.append(Random.nextInt(0, 10))
    }
    return "$prefix$rest"
  }

  /**
   * Generates a guest account name based on user config
   */
  fun generateAccountName(config: GeneratorConfig, index: Int): String {
    val prefix = config.namePrefix.trim().ifEmpty { "FF_Guest_" }
    return if (config.isSequentialName) {
      val numStr = String.format("%03d", index)
      "$prefix$numStr"
    } else {
      val chars = "abcdefghijklmnopqrstuvwxyz0123456789"
      val randomSuffix = (1..4).map { chars.random() }.joinToString("")
      "$prefix$randomSuffix"
    }
  }

  /**
   * Generates or retrieves the password based on user config
   */
  fun resolvePassword(config: GeneratorConfig, index: Int): String {
    if (!config.isAutoPassword) {
      return config.customPassword.trim().ifEmpty { "FF@Guest2026" }
    }
    val special = listOf("@", "#", "$", "!", "*").random()
    val num = Random.nextInt(100, 999)
    return "FF${special}Pass$num"
  }

  /**
   * Rolls a spin for the selected spin type.
   * Prime Event Wheel has higher chance for Prime items (~30%),
   * other wheels have standard realistic distribution (~12-18%).
   */
  fun rollSpinItem(spinType: SpinType): Pair<GameItem, Boolean> {
    val primeChance = when (spinType) {
      SpinType.PRIME_EVENT_WHEEL -> 0.35 // 35% chance for Prime in prime wheel
      SpinType.GOLD_ROYALE -> 0.15
      SpinType.WEAPON_ROYALE -> 0.20
      SpinType.DIAMOND_ROYALE -> 0.25
      SpinType.INCUBATOR -> 0.22
      SpinType.WELCOME_SPIN -> 0.18
    }

    val isPrime = Random.nextDouble() < primeChance
    val wonItem = if (isPrime) {
      when (spinType) {
        SpinType.GOLD_ROYALE -> {
          // Breakdancer or special gold prime
          listOf(PRIME_ITEMS[5], PRIME_ITEMS[8], PRIME_ITEMS[6]).random()
        }
        SpinType.PRIME_EVENT_WHEEL -> {
          // Top tier prime bundles: Sakura, Hip Hop, Red Criminal, Cobra
          listOf(
            PRIME_ITEMS[0], // Sakura
            PRIME_ITEMS[1], // Hip Hop
            PRIME_ITEMS[2], // Red Criminal
            PRIME_ITEMS[3], // Cobra
            PRIME_ITEMS[6], // Magic Cube
            PRIME_ITEMS[7]  // Yellow Criminal
          ).random()
        }
        SpinType.WEAPON_ROYALE -> {
          // Dragon AK or Blue flame
          listOf(PRIME_ITEMS[9], PRIME_ITEMS[2]).random()
        }
        SpinType.DIAMOND_ROYALE -> {
          // Arctic Blue or Magic Cube
          listOf(PRIME_ITEMS[4], PRIME_ITEMS[6]).random()
        }
        SpinType.INCUBATOR -> {
          listOf(PRIME_ITEMS[3], PRIME_ITEMS[6], PRIME_ITEMS[8]).random()
        }
        SpinType.WELCOME_SPIN -> {
          listOf(PRIME_ITEMS[5], PRIME_ITEMS[4]).random()
        }
      }
    } else {
      NORMAL_ITEMS.random()
    }

    return Pair(wonItem, isPrime)
  }

  /**
   * Generates a single guest account outcome
   */
  fun generateSingleAccount(
    config: GeneratorConfig,
    index: Int
  ): GuestAccount {
    val accountName = generateAccountName(config, index)
    val password = resolvePassword(config, index)
    val uid = generateRealisticUID()

    // Failure simulation (approx 12% failure rate if enabled)
    val isFailed = config.simulateFailures && (Random.nextDouble() < 0.12)

    return if (isFailed) {
      GuestAccount(
        id = UUID.randomUUID().toString(),
        uid = uid,
        accountName = accountName,
        password = password,
        serverRegion = config.selectedServer,
        status = AccountStatus.FAILED,
        failureReason = FAILURE_REASONS.random(),
        spinType = config.selectedSpin,
        wonItem = null,
        wonLuckRoyaleEventName = null,
        isPrimeBundle = false
      )
    } else {
      // Determine spin target: from Luck Royale event if specified, or spinType
      val targetedEvent = if (config.targetLuckRoyaleId == "all_free") {
        val freeEvents = LuckRoyaleSyncService.getFreeSpinEvents()
        if (freeEvents.isNotEmpty()) freeEvents.random() else null
      } else {
        LuckRoyaleSyncService.getEventById(config.targetLuckRoyaleId)
      }

      val actualSpinType = targetedEvent?.spinType ?: config.selectedSpin
      val (item, isPrime) = rollSpinItem(actualSpinType)
      val eventName = targetedEvent?.let { "${it.banglaName} (${it.englishName})" } ?: actualSpinType.title

      GuestAccount(
        id = UUID.randomUUID().toString(),
        uid = uid,
        accountName = accountName,
        password = password,
        serverRegion = config.selectedServer,
        status = AccountStatus.SUCCESS,
        failureReason = null,
        spinType = actualSpinType,
        wonItem = item,
        wonLuckRoyaleEventName = eventName,
        isPrimeBundle = isPrime
      )
    }
  }
}
