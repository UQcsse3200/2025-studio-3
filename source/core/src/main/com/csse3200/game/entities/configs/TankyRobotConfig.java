package com.csse3200.game.entities.configs;

/** Defines the stats for a tanky robot */
public class TankyRobotConfig extends BaseEntityConfig {
  public int getHealth() {
    return 100;
  }

  public int getAttack() {
    return 15;
  }

  public float getMovementSpeed() {
    return 1;
  }
}
