package com.csse3200.game.components.currency;

/** Represents a currency system with sunlight as the primary currency. */
public class Currency {
  private int sunlight;
  private int maxSunlight;

  /** Creates a new currency with zero sunlight and no maximum limit. */
  public Currency() {
    this.sunlight = 0;
    this.maxSunlight = Integer.MAX_VALUE;
  }

  /**
   * Creates a new currency with the specified initial amount.
   *
   * @param initialAmount the initial sunlight amount
   */
  public Currency(int initialAmount) {
    this.sunlight = initialAmount;
    this.maxSunlight = Integer.MAX_VALUE;
  }

  /**
   * Creates a new currency with the specified initial amount and maximum limit.
   *
   * @param initialAmount the initial sunlight amount
   * @param maxSunshine the maximum sunlight limit
   */
  public Currency(int initialAmount, int maxSunshine) {
    this.sunlight = initialAmount;
    this.maxSunlight = maxSunshine;
  }

  /**
   * Gets the current sunlight amount.
   *
   * @return the current sunlight amount
   */
  public int getSunlight() {
    return sunlight;
  }

  /**
   * Sets the sunlight amount.
   *
   * @param amount the new sunlight amount
   */
  public void setSunlight(int amount) {
    this.sunlight = Math.max(0, amount);
  }

  /**
   * Adds sunlight to the current amount.
   *
   * @param amount the amount of sunlight to add
   */
  public void addSunshine(int amount) {
    if (amount > 0) {
      long sum = (long) sunlight + amount;
      if (sum > maxSunlight) {
        sunlight = maxSunlight;
      } else {
        sunlight += amount;
      }
    }
  }

  /**
   * Checks if the current sunlight amount can afford the specified cost.
   *
   * @param amount the cost to check
   * @return true if affordable, false otherwise
   */
  public boolean canAffordSunshine(int amount) {
    return sunlight >= amount;
  }

  /**
   * Attempts to spend the specified amount of sunlight.
   *
   * @param amount the amount to spend
   * @return true if successful, false if insufficient funds
   */
  public boolean spendSunshine(int amount) {
    if (amount > 0 && canAffordSunshine(amount)) {
      sunlight -= amount;
      return true;
    }
    return false;
  }
}
