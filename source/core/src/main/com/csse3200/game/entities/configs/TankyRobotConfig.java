package com.csse3200.game.entities.configs;

/** Defines the stats for a tanky robot */
public class TankyRobotConfig extends BaseEnemyConfig {
  public String getName() {
    return "Tanky Robot";
  }

  public String getAtlasFile() {
    return "images/robot_placeholder.atlas";
  }

  public String getDefaultSprite() {
    return "images/default_enemy_image.png";
  }

  public int getHealth() {
    return 100;
  }

  public int getAttack() {
    return 15;
  }

  public float getMovementSpeed() {
    return 10;
  }

  public float getScale() {
    return 1.25f;
  }
}
