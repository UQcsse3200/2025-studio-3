package com.csse3200.game.components.currency;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for Currency data model. Covers: constructor state, clamping on set/add,
 * affordability, spend success/failure, no-op for invalid inputs.
 */
class CurrencyTest {

  @Test
  void constructor_shouldSetInitialAndMax() {
    Currency c1 = new Currency(); // default 0, Integer.MAX_VALUE
    assertEquals(0, c1.getScrap());

    Currency c2 = new Currency(50);
    assertEquals(50, c2.getScrap());

    Currency c3 = new Currency(10, 123);
    assertEquals(10, c3.getScrap());
    c3.addScrap(1000);
    assertEquals(123, c3.getScrap());
  }

  @Test
  void setSunlight_shouldClampToZeroWhenNegative() {
    Currency c = new Currency(50, 9999);
    c.setScrap(-10);
    assertEquals(0, c.getScrap());
  }

  @Test
  void addSunshine_shouldClampToMaxAndIgnoreNonPositive() {
    Currency c = new Currency(95, 100);
    c.addScrap(10);
    assertEquals(100, c.getScrap());

    c.addScrap(0);
    assertEquals(100, c.getScrap());

    c.addScrap(-5);
    assertEquals(100, c.getScrap());
  }

  @Test
  void canAffordSunshine_shouldReflectBalance() {
    Currency c = new Currency(50, 100);
    assertTrue(c.canAffordScrap(50));
    assertTrue(c.canAffordScrap(1));
    assertFalse(c.canAffordScrap(51));
  }

  @Test
  void spendSunshine_shouldDeductAndReturnTrue_whenEnough() {
    Currency c = new Currency(60, 100);
    boolean ok = c.spendScrap(25);
    assertTrue(ok);
    assertEquals(35, c.getScrap());
  }

  @Test
  void spendSunshine_shouldNotChangeAndReturnFalse_whenInsufficientOrInvalid() {
    Currency c = new Currency(10, 100);
    assertFalse(c.spendScrap(25));
    assertEquals(10, c.getScrap());

    assertFalse(c.spendScrap(0));
    assertEquals(10, c.getScrap());

    assertFalse(c.spendScrap(-3));
    assertEquals(10, c.getScrap());
  }
}
