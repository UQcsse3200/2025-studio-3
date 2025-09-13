package com.csse3200.game.entities.configs;

/**
 * Defines a basic set of properties stored in item config files to be loaded by Item Factories.
 */
public class BaseItemConfig {
  private String name;
  private String description;
  private String eventName;
  private int cost;

  /**
   * Creates a new BaseItemConfig.
   */
  public BaseItemConfig() {
    // Used for JSON deserialization
  }

  /**
   * Gets the name of the item.
   * 
   * @return the name of the item
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the description of the item.
   * 
   * @return the description of the item
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the event name of the item.
   * 
   * @return the event name of the item
   */
  public String getEventName() {
    return eventName;
  }

  /**
   * Gets the cost of the item.
   * 
   * @return the cost of the item
   */
  public int getCost() {
    return cost;
  }
}
