package com.csse3200.game.entities.configs;

/**
 * Defines a set of properties for all generators.
 */
public class BaseGeneratorConfig extends BaseEntityConfig {
  /** Time between currency being spawned */
  private int interval;
    /** Amount of currency the generator will give */
  private int scrapValue;
  private int cost;

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
}
