package com.csse3200.game.components.currency;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for Currency data model. Covers: constructor state, clamping on set/add,
 * affordability, spend success/failure, no-op for invalid inputs.
 */
public class CurrencyTest {

  @Test
  void constructor_shouldSetInitialAndMax() {
    Currency c1 = new Currency(); // default 0, Integer.MAX_VALUE
    assertEquals(0, c1.getSunlight());

    Currency c2 = new Currency(50);
    assertEquals(50, c2.getSunlight());

    Currency c3 = new Currency(10, 123);
    assertEquals(10, c3.getSunlight());
    c3.addSunshine(1000);
    assertEquals(123, c3.getSunlight());
  }

  @Test
  void setSunlight_shouldClampToZeroWhenNegative() {
    Currency c = new Currency(50, 9999);
    c.setSunlight(-10);
    assertEquals(0, c.getSunlight());
  }

  @Test
  void addSunshine_shouldClampToMaxAndIgnoreNonPositive() {
    Currency c = new Currency(95, 100);
    c.addSunshine(10);
    assertEquals(100, c.getSunlight());

    c.addSunshine(0);
    assertEquals(100, c.getSunlight());

    c.addSunshine(-5);
    assertEquals(100, c.getSunlight());
  }

  @Test
  void canAffordSunshine_shouldReflectBalance() {
    Currency c = new Currency(50, 100);
    assertTrue(c.canAffordSunshine(50));
    assertTrue(c.canAffordSunshine(1));
    assertFalse(c.canAffordSunshine(51));
  }

  @Test
  void spendSunshine_shouldDeductAndReturnTrue_whenEnough() {
    Currency c = new Currency(60, 100);
    boolean ok = c.spendSunshine(25);
    assertTrue(ok);
    assertEquals(35, c.getSunlight());
  }

  @Test
  void spendSunshine_shouldNotChangeAndReturnFalse_whenInsufficientOrInvalid() {
    Currency c = new Currency(10, 100);
    assertFalse(c.spendSunshine(25));
    assertEquals(10, c.getSunlight());

    assertFalse(c.spendSunshine(0));
    assertEquals(10, c.getSunlight());

    assertFalse(c.spendSunshine(-3));
    assertEquals(10, c.getSunlight());
  }
}
