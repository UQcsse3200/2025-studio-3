package com.csse3200.game.components;

public class GeneratorStatsComponent extends CombatStatsComponent {

  private int interval;

  private int scrapValue;

  /**
   * Creates a new GeneratorStatsComponent with the given stats
   * 
   * @param health the maximum health of the generator
   * @param interval the currency generation rate
   * @param scrapValue the value of the scrap generated
   */
  public GeneratorStatsComponent(int health, int interval, int scrapValue) {
    super(health, 0); // no attack stat

    setInterval(interval);
    setScrapValue(scrapValue);
  }

  /**
   * Returns the current interval value.
   *
   * @return the interval
   */
  public int getInterval() {
    return interval;
  }

  /**
   * Sets the interval value.
   * If the provided value is negative, the interval is set to 0
   *
   * @param interval new interval
   */
  public void setInterval(int interval) {
    if (interval < 0) {
      this.interval = 0;
    } else {
      this.interval = interval;
    }
  }

  /**
   * Returns the current scrap value.
   *
   * @return the scrap value
   */
  public int getScrapValue() {
    return scrapValue;
  }

  /**
   * Sets the scrap value.
   * if the provided value is negative, the scrap value is set to 0
   *
   * @param scrapValue new scrap value
   */
  public void setScrapValue(int scrapValue) {
    if (scrapValue < 0) {
      this.scrapValue = 0;
    } else {
      this.scrapValue = scrapValue;
    }
  }
}
