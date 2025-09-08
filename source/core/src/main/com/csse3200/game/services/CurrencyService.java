package com.csse3200.game.services;

import com.csse3200.game.components.currency.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurrencyService {
  private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);
  private final Currency currency;

  /**
   * @param initialAmount starting currency; @param maxAmount cap
   */
  public CurrencyService(int initialAmount, int maxAmount) {
    this.currency = new Currency(initialAmount, maxAmount);
    logger.debug("Currency service created.");
  }

  /**
   * @return internal currency model
   */
  public Currency getCurrency() {
    return currency;
  }

  /**
   * @return current amount
   */
  public int get() {
    return currency.getSunlight();
  }

  /** set absolute amount (clamped >=0) */
  public void set(int amount) {
    currency.setSunlight(amount);
  }

  /** add positive delta (clamped to max) */
  public void add(int amount) {
    currency.addSunshine(amount);
  }

  /** spend if affordable @return true */
  public boolean spend(int amount) {
    return currency.spendSunshine(amount);
  }

  /**
   * @return whether the amount is affordable
   */
  public boolean canAfford(int amount) {
    return currency.canAffordSunshine(amount);
  }
}
