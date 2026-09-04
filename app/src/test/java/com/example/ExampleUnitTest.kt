package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testLuckRoyaleSyncEvents() {
    val events = com.example.service.LuckRoyaleSyncService.getEvents()
    assertTrue(events.isNotEmpty())

    val freeSpins = com.example.service.LuckRoyaleSyncService.getFreeSpinEvents()
    assertTrue(freeSpins.isNotEmpty())

    // Simulate new button update from server
    val newEvent = com.example.service.LuckRoyaleSyncService.simulateServerButtonUpdate()
    assertTrue(newEvent.isFreeSpinAvailable)

    val updatedEvents = com.example.service.LuckRoyaleSyncService.getEvents()
    assertTrue(updatedEvents.any { it.id == newEvent.id })
  }
}
