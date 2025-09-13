package com.csse3200.game.components;

public class GeneratorStatsComponent extends CombatStatsComponent {

  /** Integer identifier for the type of defender (e.g., tower, trap, etc.). */
  private int interval;

  public GeneratorStatsComponent(int health, int interval) {
    super(health, 0); // no attack stat

    setInterval(interval);
  }

  public int getInterval() {
    return interval;
  }

  public void setInterval(int interval) {
    this.interval = interval;
  }
}
