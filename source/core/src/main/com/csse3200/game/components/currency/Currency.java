package com.csse3200.game.components.currency;

/** Represents a currency system with sunlight as the primary currency. */
public class Currency {
  private int scrap;
  private final int maxScrap;

  /** Creates a new currency with zero sunlight and no maximum limit. */
  public Currency() {
    this.scrap = 0;
    this.maxScrap = Integer.MAX_VALUE;
  }

  /**
   * Creates a new currency with the specified initial amount.
   *
   * @param initialAmount the initial sunlight amount
   */
  public Currency(int initialAmount) {
    this.scrap = initialAmount;
    this.maxScrap = Integer.MAX_VALUE;
  }

  /**
   * Creates a new currency with the specified initial amount and maximum limit.
   *
   * @param initialAmount the initial sunlight amount
   * @param maxSunshine the maximum sunlight limit
   */
  public Currency(int initialAmount, int maxSunshine) {
    this.scrap = initialAmount;
    this.maxScrap = maxSunshine;
  }

  /**
   * Gets the current sunlight amount.
   *
   * @return the current sunlight amount
   */
  public int getScrap() {
    return scrap;
  }

  /**
   * Sets the sunlight amount.
   *
   * @param amount the new sunlight amount
   */
  public void setScrap(int amount) {
    this.scrap = Math.max(0, amount);
  }

  /**
   * Adds sunlight to the current amount.
   *
   * @param amount the amount of sunlight to add
   */
  public void addScrap(int amount) {
    if (amount > 0) {
      long sum = (long) scrap + amount;
      if (sum > maxScrap) {
        scrap = maxScrap;
      } else {
        scrap += amount;
      }
    }
  }

  /**
   * Checks if the current sunlight amount can afford the specified cost.
   *
   * @param amount the cost to check
   * @return true if affordable, false otherwise
   */
  public boolean canAffordScrap(int amount) {
    return scrap >= amount;
  }

  /**
   * Attempts to spend the specified amount of sunlight.
   *
   * @param amount the amount to spend
   * @return true if successful, false if insufficient funds
   */
  public boolean spendScrap(int amount) {
    if (amount > 0 && canAffordScrap(amount)) {
      scrap -= amount;
      return true;
    }
    return false;
  }
}
