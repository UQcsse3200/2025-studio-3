package com.csse3200.game.components.dossier;

/**
 * Represents the configuration for a single enemy entity, directly mapping to the structure of an
 * enemy object in the Enemies.json file.
 */
public class EntityDataConfig {

  public String name;
  public String atlasFilePath;
  public String defaultSprite;
  public int health;
  public int attack;
  public int movementSpeed;
  public double scale;
  public String description;

  /** No-argument constructor required by JSON deserialization libraries. */
  public EntityDataConfig() {}
}
