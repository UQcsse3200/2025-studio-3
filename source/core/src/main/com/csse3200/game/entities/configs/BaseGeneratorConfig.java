package com.csse3200.game.entities.configs;

/** Defines the properties stored in defences.json to be loaded by the NPC Factory. */
public class BaseGeneratorConfig extends BaseEntityConfig {

  /** Creates a new BaseGeneratorConfig with default values. */
  public BaseGeneratorConfig() {
    // Default constructor with default field values
  }

  /** Time between currency being spawned */
  private int interval;

  /** Amount of currency the generator will give */
  private int scrapValue;

  public int getInterval() {
    return interval;
  }

  public int getScrapValue() {
    return scrapValue;
  }
}
