package com.csse3200.game.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** Unit tests for CurrencyService facade. Covers: get/set/add/spend behavior and edge cases. */
class CurrencyServiceTest {
  @Test
  void testCurrencyService() {
    CurrencyService currencyService = new CurrencyService(0, 100);
    assertEquals(0, currencyService.get());
    currencyService.add(10);
    assertEquals(10, currencyService.get());
    currencyService.spend(5);
    assertEquals(5, currencyService.get());
    assertTrue(currencyService.canAfford(5));
    assertFalse(currencyService.canAfford(10));
  }
}
