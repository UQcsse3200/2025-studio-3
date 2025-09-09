package com.csse3200.game.services;

import com.csse3200.game.components.currency.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Service for managing currency operations in the game. */
public class CurrencyService {
  private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);
  private final Currency currency;

  /**
   * Creates a new currency service with the specified initial amount and maximum limit.
   *
   * @param initialAmount starting currency amount
   * @param maxAmount maximum currency cap
   */
  public CurrencyService(int initialAmount, int maxAmount) {
    this.currency = new Currency(initialAmount, maxAmount);
    logger.debug("Currency service created.");
  }

  /**
   * Gets the internal currency model.
   *
   * @return the currency instance
   */
  public Currency getCurrency() {
    return currency;
  }

  /**
   * Gets the current currency amount.
   *
   * @return the current amount
   */
  public int get() {
    return currency.getSunlight();
  }

  /**
   * Sets the absolute currency amount (clamped >= 0).
   *
   * @param amount the new amount
   */
  public void set(int amount) {
    currency.setSunlight(amount);
  }

  /**
   * Adds a positive amount to the currency (clamped to maximum).
   *
   * @param amount the amount to add
   */
  public void add(int amount) {
    currency.addSunshine(amount);
  }

  /**
   * Attempts to spend the specified amount if affordable.
   *
   * @param amount the amount to spend
   * @return true if successful, false if insufficient funds
   */
  public boolean spend(int amount) {
    return currency.spendSunshine(amount);
  }

  /**
   * Checks if the specified amount is affordable.
   *
   * @param amount the amount to check
   * @return true if affordable, false otherwise
   */
  public boolean canAfford(int amount) {
    return currency.canAffordSunshine(amount);
  }
}
