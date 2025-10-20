package com.csse3200.game.entities.configs;

public class BaseBossConfig {
  public String name = "Boss";
  public String atlasFilePath = "";
  public int health = 500;
  public int attack = 50;
  public float scale = 4.0f;
  public float speed = 0.5f;
  public String attackType = "";
  public float range = 0f;
  public int coinsRewarded = 20;

  public float getRange() {
    return range;
  }

  public int getCoinsRewarded() {
    return coinsRewarded;
  }
}
