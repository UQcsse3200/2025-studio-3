package com.csse3200.game.services;

import static org.junit.jupiter.api.Assertions.*;

import com.csse3200.game.components.currency.Currency;
import org.junit.jupiter.api.Test;

/** Unit tests for CurrencyService facade. Covers: get/set/add/spend behavior and edge cases. */
public class CurrencyServiceTest {

  @Test
  void constructor_get_shouldExposeInitial() {
    CurrencyService svc = new CurrencyService(50, 200);
    assertEquals(50, svc.get());
  }

  @Test
  void set_shouldWriteThroughAndClampToZero() {
    CurrencyService svc = new CurrencyService(50, 200);
    svc.set(120);
    assertEquals(120, svc.get());

    svc.set(-5); // clamp to 0 in model
    assertEquals(0, svc.get());
  }

  @Test
  void add_shouldClampToMaxAndIgnoreNonPositive() {
    CurrencyService svc = new CurrencyService(90, 100);
    svc.add(15); // -> clamp to 100
    assertEquals(100, svc.get());

    svc.add(0); // ignore
    assertEquals(100, svc.get());

    svc.add(-5); // ignore
    assertEquals(100, svc.get());
  }

  @Test
  void spend_shouldReturnTrueAndDeduct_whenEnough() {
    CurrencyService svc = new CurrencyService(80, 100);
    assertTrue(svc.spend(60));
    assertEquals(20, svc.get());
  }

  @Test
  void spend_shouldReturnFalseAndNoChange_whenInsufficientOrInvalid() {
    CurrencyService svc = new CurrencyService(10, 100);
    assertFalse(svc.spend(60)); // insufficient
    assertEquals(10, svc.get());

    assertFalse(svc.spend(0)); // invalid amount
    assertEquals(10, svc.get());

    assertFalse(svc.spend(-3)); // invalid amount
    assertEquals(10, svc.get());
  }

  @Test
  void getCurrency_shouldExposeUnderlyingModel() {
    CurrencyService svc = new CurrencyService(5, 10);
    Currency model = svc.getCurrency();
    assertNotNull(model);
    model.addSunshine(3);
    assertEquals(8, svc.get());
  }
}
