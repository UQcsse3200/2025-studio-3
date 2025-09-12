package com.csse3200.game.entities.configs;

/** Defines the stats for a standard robot */
public class StandardRobotConfig extends BaseEnemyConfig {
  @Override
  public String getName() {
    return "Standard Robot";
  }

  @Override
  public String getAtlasFile() {
    return "images/robot_placeholder.atlas";
  }

  @Override
  public String getDefaultSprite() {
    return "images/default_enemy_image.png";
  }

  @Override
  public int getHealth() {
    return 60;
  }

  @Override
  public int getAttack() {
    return 10;
  }

  @Override
  public float getMovementSpeed() {
    return 20;
  }

  @Override
  public float getScale() {
    return 1f;
  }
}
