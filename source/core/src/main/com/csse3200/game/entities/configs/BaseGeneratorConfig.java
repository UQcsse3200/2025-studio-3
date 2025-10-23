package com.csse3200.game.entities.configs;

/** Defines a set of properties for all generators. */
public class BaseGeneratorConfig extends BaseEntityConfig {
  /** Time between currency being spawned */
  private int interval;

  /** Amount of currency the generator will give */
  private int scrapValue;

  private int cost;
  private String soundPath;
  private String level;

  /** Creates a new BaseGeneratorConfig with default values. */
  public BaseGeneratorConfig() {
    // Default constructor with default field values
  }

  /**
   * Gets the interval of the generator.
   *
   * @return the interval of the generator
   */
  public int getInterval() {
    return interval;
  }

  /**
   * Gets the scrap value of the generator.
   *
   * @return the scrap value of the generator
   */
  public int getScrapValue() {
    return scrapValue;
  }

  /**
   * Gets the cost of the generator.
   *
   * @return the cost of the generator
   */
  public int getCost() {
    return cost;
  }

  /**
   * Gets the path to the sound that plays on placement
   *
   * @return the path to the sound
   */
  public String getSoundPath() {
    return soundPath;
  }

  /**
   * Gets the first level that the defence entity is playable on
   *
   * @return the level key
   */
  public String getLevelUnlockedOn() {
    return level;
  }
}
