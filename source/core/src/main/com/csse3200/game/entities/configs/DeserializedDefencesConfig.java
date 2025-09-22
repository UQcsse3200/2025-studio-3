package com.csse3200.game.entities.configs;

import java.util.HashMap;
import java.util.Map;

/**
 * DeserializedDefencesConfig is a wrapper class for the BaseDefenderConfig and BaseGeneratorConfig
 * classes.
 */
public class DeserializedDefencesConfig {
  private DefenceConfigWrapper config;

  /** Creates a new DeserializedDefencesConfig. */
  public DeserializedDefencesConfig() {
    this.config = new DefenceConfigWrapper();
  }

  /**
   * Gets the config map for the defenders.
   *
   * @return the config map for the defenders
   */
  public Map<String, BaseDefenderConfig> getDefenders() {
    return config != null ? config.defenders : new HashMap<>();
  }

  /**
   * Gets the config map for the generators.
   *
   * @return the config map for the generators
   */
  public Map<String, BaseGeneratorConfig> getGenerators() {
    return config != null ? config.generators : new HashMap<>();
  }

  /** Inner class to match the JSON structure with "config" wrapper */
  public static class DefenceConfigWrapper {
    private HashMap<String, BaseDefenderConfig> defenders;
    private HashMap<String, BaseGeneratorConfig> generators;

    /** Creates a new DefenceConfigWrapper. */
    public DefenceConfigWrapper() {
      this.defenders = new HashMap<>();
      this.generators = new HashMap<>();
    }
  }
}
