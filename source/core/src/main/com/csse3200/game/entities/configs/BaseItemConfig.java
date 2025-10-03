package com.csse3200.game.entities.configs;

import java.util.HashMap;
import java.util.Map;

/** Defines a basic set of properties stored in item config files to be loaded by Item Factories. */
public class BaseItemConfig {
  private String name;
  private String description;
  private String eventName;
  private String assetPath;
  private int cost;
  private String trigger;

  /** Creates a new BaseItemConfig. */
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
   * Gets the asset path of the item.
   *
   * @return the asset path of the item
   */
  public String getAssetPath() {
    return assetPath;
  }

  /**
   * Gets the trigger name for the item.
   *
   * @return item trigger name
   */
  public String getTrigger() {
    return trigger;
  }

  /**
   * Gets the cost of the item.
   *
   * @return the cost of the item
   */
  public int getCost() {
    return cost;
  }

  /** DeserializedItemConfig is a wrapper class for the BaseItemConfig class. */
  public static class DeserializedItemConfig {
    private HashMap<String, BaseItemConfig> config;

    /** Creates a new DeserializedItemConfig. */
    public DeserializedItemConfig() {
      this.config = new HashMap<>();
    }

    /**
     * Sets the config map for the item configs.
     *
     * @param config the config map for the item configs
     */
    public void setConfig(Map<String, BaseItemConfig> config) {
      this.config = new HashMap<>(config);
    }

    /**
     * Gets the config map for the item configs.
     *
     * @return the config map for the item configs
     */
    public Map<String, BaseItemConfig> getConfig() {
      return config;
    }
  }
}
