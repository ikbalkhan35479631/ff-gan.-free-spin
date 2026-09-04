package com.example.service

import com.example.model.GameItem
import com.example.model.ItemCategory
import com.example.model.ItemRarity
import com.example.model.LuckRoyaleEvent
import com.example.model.SpinType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object LuckRoyaleSyncService {

  private val initialEvents = listOf(
    LuckRoyaleEvent(
      id = "gold_vault",
      banglaName = "গোল্ড ভল্ট",
      englishName = "Gold Vault Royale",
      badgeText = "HOT",
      duration = "24 জুন - 15 সেপ্টেম্বর",
      daysRemaining = "10d 21h",
      dailyRefreshTimer = "18:30:56",
      grandPrizeTitle = "গোল্ড ভল্ট 24 জুন - 15 সেপ্টেম্বর",
      grandPrizeSubtitle = "বান্ডেলটিতে থাকা সকল আইটেম গ্রহণ করতে খুলে নিন!",
      grandPrizeItems = listOf(
        "ইলেক্ট্রো মরফোসিস বান্ডেল (Electro Morphosis)",
        "গ্রোজা - ফ্যান্টম প্রিভেটর (Groza Phantom)"
      ),
      isFreeSpinAvailable = true,
      freeSpinLabel = "1 টি স্পিন (FREE)",
      singleSpinCostText = "FREE / 1000",
      tenSpinsCostText = "10000",
      guaranteedSpins = 113,
      bannerEmoji = "⚡",
      spinType = SpinType.GOLD_ROYALE
    ),
    LuckRoyaleEvent(
      id = "booyah_pass_ring",
      banglaName = "BOOYAH PASS রিং",
      englishName = "Booyah Pass Ring",
      badgeText = "-11%",
      duration = "01 সেপ্টেম্বর - 30 সেপ্টেম্বর",
      daysRemaining = "25d 10h",
      dailyRefreshTimer = "18:30:56",
      grandPrizeTitle = "BOOYAH PASS রিং ইউনিভার্সাল",
      grandPrizeSubtitle = "সিজন এক্সক্লুসিভ বান্ডেল ও প্রিমিয়াম ইমোটস সংগ্রহ করুন!",
      grandPrizeItems = listOf(
        "বুয়াহ পাস সিজনাল রিং বান্ডেল",
        "বুয়াহ পাস গোল্ডেন ব্যাজ বান্ডেল"
      ),
      isFreeSpinAvailable = true,
      freeSpinLabel = "ফ্রি টোকেন স্পিন",
      singleSpinCostText = "FREE (1x Token)",
      tenSpinsCostText = "90 💎",
      guaranteedSpins = 90,
      bannerEmoji = "🎖️",
      spinType = SpinType.PRIME_EVENT_WHEEL
    ),
    LuckRoyaleEvent(
      id = "mag7_awm_universal",
      banglaName = "MAG-7 x AWM ইউনিভার্সাল",
      englishName = "MAG-7 x AWM Universal",
      badgeText = "-10%",
      duration = "28 আগস্ট - 18 সেপ্টেম্বর",
      daysRemaining = "13d 08h",
      dailyRefreshTimer = "18:30:56",
      grandPrizeTitle = "MAG-7 x AWM ইউনিভার্সাল রিং",
      grandPrizeSubtitle = "লিজেন্ডারি স্নিপার এবং রাশ গান স্কিনের মহাসম্মেলন!",
      grandPrizeItems = listOf(
        "MAG-7 Lethal Cyber Neon",
        "AWM Precision Cyber Striker"
      ),
      isFreeSpinAvailable = false,
      freeSpinLabel = "20 💎 স্পিন",
      singleSpinCostText = "20 💎",
      tenSpinsCostText = "200 💎",
      guaranteedSpins = 150,
      bannerEmoji = "🔫",
      spinType = SpinType.WEAPON_ROYALE
    ),
    LuckRoyaleEvent(
      id = "horse_skin",
      banglaName = "হর্স স্কিন",
      englishName = "Mythic Pegasus Horse Skin",
      badgeText = "NEW",
      duration = "02 সেপ্টেম্বর - 22 সেপ্টেম্বর",
      daysRemaining = "17d 12h",
      dailyRefreshTimer = "18:30:56",
      grandPrizeTitle = "পেগাসাস উইংড হর্স স্কিন",
      grandPrizeSubtitle = "আকাশে উড়ন্ত জাদুকরী ঘোড়া এবং অ্যারাইভাল ড্রপ অ্যানিমেশন!",
      grandPrizeItems = listOf(
        "Mythic Pegasus Flying Mount",
        "Skyrider Celestial Wings"
      ),
      isFreeSpinAvailable = false,
      freeSpinLabel = "40 💎 স্পিন",
      singleSpinCostText = "40 💎",
      tenSpinsCostText = "400 💎",
      guaranteedSpins = 180,
      bannerEmoji = "🦄",
      spinType = SpinType.PRIME_EVENT_WHEEL
    ),
    LuckRoyaleEvent(
      id = "scarlet_ring",
      banglaName = "স্কারলেট রিং ইউনিভার্সাল",
      englishName = "Scarlet Ring Universal",
      badgeText = "-10%",
      duration = "20 আগস্ট - 10 সেপ্টেম্বর",
      daysRemaining = "5d 14h",
      dailyRefreshTimer = "18:30:56",
      grandPrizeTitle = "স্কারলেট রিং ইউনিভার্সাল স্পেশাল",
      grandPrizeSubtitle = "রক্তলাল ক্রিমসন স্যুট এবং ডেমন ব্লাড সাইথ!",
      grandPrizeItems = listOf(
        "Scarlet Phantom Demon Outfit",
        "Blood Scythe Melee Weapon"
      ),
      isFreeSpinAvailable = false,
      freeSpinLabel = "20 💎 স্পিন",
      singleSpinCostText = "20 💎",
      tenSpinsCostText = "200 💎",
      guaranteedSpins = 120,
      bannerEmoji = "🔴",
      spinType = SpinType.PRIME_EVENT_WHEEL
    ),
    LuckRoyaleEvent(
      id = "full_animation",
      banglaName = "ফুল অ্যানিমেশন",
      englishName = "Full Animation Arrival",
      badgeText = "HOT",
      duration = "30 আগস্ট - 14 সেপ্টেম্বর",
      daysRemaining = "9d 05h",
      dailyRefreshTimer = "18:30:56",
      grandPrizeTitle = "ফুল অ্যানিমেশন রয়্যাল",
      grandPrizeSubtitle = "স্কোয়াড লবি এবং এন্ট্রি স্পেশাল ফুল মোশন অ্যানিমেশন!",
      grandPrizeItems = listOf(
        "Lightning Dragon Entrance Animation",
        "Thunder Clap Emote Special"
      ),
      isFreeSpinAvailable = false,
      freeSpinLabel = "25 💎 স্পিন",
      singleSpinCostText = "25 💎",
      tenSpinsCostText = "250 💎",
      guaranteedSpins = 100,
      bannerEmoji = "✨",
      spinType = SpinType.PRIME_EVENT_WHEEL
    ),
    LuckRoyaleEvent(
      id = "prime_faded_wheel",
      banglaName = "প্রাইম ফেডেড হুইল",
      englishName = "Prime Faded Wheel",
      badgeText = "⭐ 1ST FREE",
      duration = "স্পেশাল ইভেন্ট",
      daysRemaining = "7d 00h",
      dailyRefreshTimer = "18:30:56",
      grandPrizeTitle = "প্রাইম বান্ডেল ফেডেড হুইল",
      grandPrizeSubtitle = "সাকুরা, হিপহপ, রেড ক্রিমিনাল ও কোবরা রেইজ বান্ডেল!",
      grandPrizeItems = listOf(
        "🌸 Sakura Blossom Season 1 Bundle",
        "🔴 Red Criminal Special Outfit"
      ),
      isFreeSpinAvailable = true,
      freeSpinLabel = "১ম স্পিন 100% FREE",
      singleSpinCostText = "FREE (1st Spin)",
      tenSpinsCostText = "199 💎",
      guaranteedSpins = 8,
      bannerEmoji = "👑",
      spinType = SpinType.PRIME_EVENT_WHEEL
    ),
    LuckRoyaleEvent(
      id = "weapon_royale_live",
      banglaName = "ওয়েপন রয়্যাল",
      englishName = "Weapon Royale",
      badgeText = "DAILY",
      duration = "চলমান",
      daysRemaining = "12d 00h",
      dailyRefreshTimer = "18:30:56",
      grandPrizeTitle = "ওয়েপন রয়্যাল - ড্রাগন একে",
      grandPrizeSubtitle = "দৈনিক ফ্রি টিকিট দিয়ে গান স্কিন আনলক করুন!",
      grandPrizeItems = listOf(
        "Dragon AK47 Max Skin",
        "Blue Flame Draco MP40"
      ),
      isFreeSpinAvailable = true,
      freeSpinLabel = "দৈনিক ফ্রি টিকিট",
      singleSpinCostText = "FREE (1x Ticket)",
      tenSpinsCostText = "400 💎",
      guaranteedSpins = 100,
      bannerEmoji = "🎯",
      spinType = SpinType.WEAPON_ROYALE
    )
  )

  private val _eventsState = MutableStateFlow(initialEvents)
  val eventsState: StateFlow<List<LuckRoyaleEvent>> = _eventsState.asStateFlow()

  private val _isLiveConnected = MutableStateFlow(true)
  val isLiveConnected: StateFlow<Boolean> = _isLiveConnected.asStateFlow()

  private val _lastSyncTime = MutableStateFlow(System.currentTimeMillis())
  val lastSyncTime: StateFlow<Long> = _lastSyncTime.asStateFlow()

  fun getEvents(): List<LuckRoyaleEvent> = _eventsState.value

  fun getEventById(id: String): LuckRoyaleEvent? = _eventsState.value.find { it.id == id }

  fun getFreeSpinEvents(): List<LuckRoyaleEvent> = _eventsState.value.filter { it.isFreeSpinAvailable }

  /**
   * Toggles whether a specific event currently offers a Free Spin.
   */
  fun toggleFreeSpin(eventId: String, isFree: Boolean) {
    _eventsState.update { list ->
      list.map { ev ->
        if (ev.id == eventId) {
          ev.copy(
            isFreeSpinAvailable = isFree,
            freeSpinLabel = if (isFree) "1 টি স্পিন (FREE)" else "পেইড স্পিন",
            lastSyncedAt = System.currentTimeMillis()
          )
        } else ev
      }
    }
    _lastSyncTime.value = System.currentTimeMillis()
  }

  /**
   * Simulates real-time server update: Free Fire added a new Luck Royale button or updated live event!
   */
  fun simulateServerButtonUpdate(): LuckRoyaleEvent {
    val sampleNewEvents = listOf(
      LuckRoyaleEvent(
        id = "cyber_ring_${System.currentTimeMillis() % 1000}",
        banglaName = "সাইবার রিং ফেস্টিভাল",
        englishName = "Cyber Ring Festival",
        badgeText = "-15%",
        duration = "লাইভ ইভেন্ট",
        daysRemaining = "14d 00h",
        grandPrizeTitle = "সাইবার সামুরাই মেক বান্ডেল",
        grandPrizeSubtitle = "সার্ভার লাইভ আপডেট! লিমিটেড টাইম ফ্রি স্পিন সক্রিয়!",
        grandPrizeItems = listOf("Cyber Samurai Katana", "Neon Ninja Mask"),
        isFreeSpinAvailable = true,
        freeSpinLabel = "1 টি স্পিন (FREE)",
        singleSpinCostText = "FREE",
        tenSpinsCostText = "85 💎",
        guaranteedSpins = 80,
        bannerEmoji = "🤖",
        spinType = SpinType.PRIME_EVENT_WHEEL,
        isCustomOrLive = true
      ),
      LuckRoyaleEvent(
        id = "rampage_ring_${System.currentTimeMillis() % 1000}",
        banglaName = "র‍্যাম্পেজ ফায়ার রিং",
        englishName = "Rampage Fire Ring",
        badgeText = "NEW",
        duration = "সীমিত সময়",
        daysRemaining = "6d 18h",
        grandPrizeTitle = "র‍্যাম্পেজ ফায়ার টাইটান",
        grandPrizeSubtitle = "সার্ভার ইভেন্ট রিফ্রেশ! সকল গেস্ট আইডিতে ফ্রি ড্রপ!",
        grandPrizeItems = listOf("Rampage Thunder Claw", "Fiery Wings Parachute"),
        isFreeSpinAvailable = true,
        freeSpinLabel = "1 টি স্পিন (FREE)",
        singleSpinCostText = "FREE",
        tenSpinsCostText = "190 💎",
        guaranteedSpins = 100,
        bannerEmoji = "🔥",
        spinType = SpinType.PRIME_EVENT_WHEEL,
        isCustomOrLive = true
      )
    )

    val newEvent = sampleNewEvents.random()
    _eventsState.update { current ->
      listOf(newEvent) + current.filter { it.id != newEvent.id }
    }
    _lastSyncTime.value = System.currentTimeMillis()
    return newEvent
  }

  /**
   * Syncs with live game server
   */
  fun triggerLiveSync(): Int {
    _lastSyncTime.value = System.currentTimeMillis()
    _isLiveConnected.value = true
    return _eventsState.value.count { it.isFreeSpinAvailable }
  }

  /**
   * Generates spin prize from a Luck Royale Event
   */
  fun spinFromEvent(event: LuckRoyaleEvent): Pair<GameItem, Boolean> {
    return FreeFireSpinEngine.rollSpinItem(event.spinType)
  }
}
