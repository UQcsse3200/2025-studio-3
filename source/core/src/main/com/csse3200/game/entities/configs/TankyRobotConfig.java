package com.csse3200.game.entities.configs;

/** Defines the stats for a tanky robot */
public class TankyRobotConfig extends BaseEnemyConfig {

  @Override
  public String getName() {
    return "Tanky Robot";
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
    return 100;
  }

  @Override
  public int getAttack() {
    return 15;
  }

  @Override
  public float getMovementSpeed() {
    return 10;
  }

  @Override
  public float getScale() {
    return 1.25f;
  }
}
