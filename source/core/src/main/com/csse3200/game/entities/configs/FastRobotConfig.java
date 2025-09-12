package com.csse3200.game.entities.configs;

/** Defines the stats for a fast robot */
public class FastRobotConfig extends BaseEnemyConfig {
  @Override
  public String getName() {
    return "Fast Robot";
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
    return 40;
  }

  @Override
  public int getAttack() {
    return 10;
  }

  @Override
  public float getMovementSpeed() {
    return 50;
  }

  @Override
  public float getScale() {
    return 0.75f;
  }
}
