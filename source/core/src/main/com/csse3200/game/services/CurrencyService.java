package com.csse3200.game.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Service for managing in-level currency. */
public class CurrencyService {
  private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);
  private int amount;
  private int maxAmount;

  /**
   * Creates a new currency service with the specified initial amount and maximum limit.
   *
   * @param initialAmount starting currency amount
   * @param maxAmount maximum currency cap
   */
  public CurrencyService(int initialAmount, int maxAmount) {
    this.amount = initialAmount;
    this.maxAmount = maxAmount;
    logger.debug("[CurrencyService] Currency service created.");
  }

  /**
   * Gets the currency amount.
   *
   * @return the currency amount
   */
  public int get() {
    return amount;
  }

  /**
   * Sets the currency amount.
   *
   * @param amount the new amount
   */
  public void set(int amount) {
    this.amount = amount;
    if (this.amount > maxAmount) {
      this.amount = maxAmount;
    }
  }

  /**
   * Adds a positive amount to the currency.
   *
   * @param amount the amount to add
   */
  public void add(int amount) {
    if (this.amount + amount > maxAmount) {
      this.amount = maxAmount;
    } else {
      this.amount += amount;
    }
  }

  /**
   * Attempts to spend the specified amount if affordable.
   *
   * @param amount the amount to spend
   * @return true if successful, false if insufficient funds
   */
  public boolean spend(int amount) {
    if (this.amount >= amount) {
      this.amount -= amount;
      return true;
    }
    return false;
  }

  /**
   * Checks if the specified amount is affordable.
   *
   * @param amount the amount to check
   * @return true if affordable, false otherwise
   */
  public boolean canAfford(int amount) {
    return this.amount >= amount;
  }
}
