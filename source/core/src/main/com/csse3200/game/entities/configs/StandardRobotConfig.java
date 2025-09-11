package com.csse3200.game.entities.configs;

/** Defines the stats for a standard robot */
public class StandardRobotConfig extends BaseEnemyConfig {
  public String getName() {
    return "Standard Robot";
  }

  public String getAtlasFile() {
    return "images/robot_placeholder.atlas";
  }

  public String getDefaultSprite() {
    return "images/default_enemy_image.png";
  }

  public int getHealth() {
    return 60;
  }

  public int getAttack() {
    return 10;
  }

  public float getMovementSpeed() {
    return 20;
  }

  public float getScale() {
    return 1f;
  }
}
