package com.csse3200.game.components;

public class GeneratorStatsComponent extends CombatStatsComponent {

  /** Integer identifier for the type of defender (e.g., tower, trap, etc.). */
  private int interval;

  private int scrapValue;

  public GeneratorStatsComponent(int health, int interval, int scrapValue) {
    super(health, 0); // no attack stat

    setInterval(interval);
    setScrapValue(scrapValue);
  }

  public int getInterval() {
    return interval;
  }

  public void setInterval(int interval) {
    if (interval < 0) {
      this.interval = 0;
    } else {
      this.interval = interval;
    }
  }

  public int getScrapValue() {
    return scrapValue;
  }

  public void setScrapValue(int scrapValue) {
    if (scrapValue < 0) {
      this.scrapValue = 0;
    } else {
      this.scrapValue = scrapValue;
    }
  }
}
