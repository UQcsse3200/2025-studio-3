package com.csse3200.game.entities.configs;

/** Defines the stats for a fast robot */
public class FastRobotConfig extends BaseEnemyConfig {
  public String getName() {
    return "Fast Robot";
  }

  public String getAtlasFile() {
    return "images/robot_placeholder.atlas";
  }

  public String getDefaultSprite() {
    return "images/default_enemy_image.png";
  }

  public int getHealth() {
    return 40;
  }

  public int getAttack() {
    return 10;
  }

  public float getMovementSpeed() {
    return 50;
  }

  public float getScale() {
    return 0.75f;
  }
}
