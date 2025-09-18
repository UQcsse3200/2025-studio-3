package com.csse3200.game.entities.configs;

import java.util.HashMap;
import java.util.Map;

/**
 * DeserializedDefencesConfig is a wrapper class for the BaseDefenderConfig and BaseGeneratorConfig classes.
 */
public class DeserializedDefencesConfig {
  private Map<String, BaseDefenderConfig> defenders;
  private Map<String, BaseGeneratorConfig> generators;

  /**
   * Creates a new DeserializedDefencesConfig.
   */
  public DeserializedDefencesConfig() {
    this.defenders = new HashMap<>();
    this.generators = new HashMap<>();
  }

  /**
   * Sets the config map for the defenders and generators.
   *
   * @param defenders the config map for the defenders
   * @param generators the config map for the generators
   */
  public void setConfig(Map<String, BaseDefenderConfig> defenders, Map<String, BaseGeneratorConfig> generators) {
    this.defenders = new HashMap<>(defenders);
    this.generators = new HashMap<>(generators);
  }

  /**
   * Gets the config map for the defenders.
   *
   * @return the config map for the defenders
   */
  public Map<String, BaseDefenderConfig> getDefenders() {
    return defenders;
  }

  /**
   * Gets the config map for the generators.
   *
   * @return the config map for the generators
   */
  public Map<String, BaseGeneratorConfig> getGenerators() {
    return generators;
  }
}