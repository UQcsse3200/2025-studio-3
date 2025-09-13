package com.csse3200.game.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csse3200.game.entities.configs.BaseDefenceConfig;
import com.csse3200.game.entities.configs.BaseEntityConfig;
import com.csse3200.game.entities.configs.BaseItemConfig;
import com.csse3200.game.persistence.FileLoader;

import java.util.Map;
import java.util.HashMap;

/**
 * Service for managing the config files and data loading of the game.
 */
public class ConfigService {
  private static final Logger logger = LoggerFactory.getLogger(ConfigService.class);
  private Map<String, BaseDefenceConfig> defenceConfigs;
  private Map<String, BaseEntityConfig> enemyConfigs;
  private Map<String, BaseItemConfig> itemConfigs;
  private static final String DEFENCE_CONFIG_FILE = "configs/Defences.json";
  private static final String ENEMY_CONFIG_FILE = "configs/Enemies.json";
  private static final String ITEM_CONFIG_FILE = "configs/Items.json";

  /**
   * On registration, the config service will use the FileLoader to load all config files.
   */
  public ConfigService() {
    this.defenceConfigs = loadConfigs(DEFENCE_CONFIG_FILE);
    this.enemyConfigs = loadConfigs(ENEMY_CONFIG_FILE);
    this.itemConfigs = loadConfigs(ITEM_CONFIG_FILE);
  }
  
  /**
   * Generic method to load any config type from any JSON file
   * 
   * @param filename The JSON file to load from
   * @param <T> The type of config objects
   * @return Map of config name to config object
   */
  @SuppressWarnings("unchecked")
  public <T> Map<String, T> loadConfigs(String filename) {
    try {
      Map<String, T> configs = FileLoader.readClass(Map.class, filename);
      if (configs != null) {
        logger.info("Loaded {} entities from {}", configs.size(), filename);
        return configs;
      }


      
      // DeserializedConfig<T> configs = FileLoader.readClass(DeserializedConfig.class, filename);
      // if (configs != null && configs.getConfigs() != null) {
      //   logger.info("Loaded {} entities from {}", configs.getConfigs().size(), filename);
      //   return configs.getConfigs();
      // }
    } catch (Exception e) {
      logger.error("Failed to load configurations from {}", filename, e);
    }
    return new HashMap<>();
  }

  /**
   * Gets a particular defence config.
   * 
   * @param key The identifier or name of the defence config to get.
   * @return The defence config for the given key.
   */
  public BaseDefenceConfig getDefenceConfig(String key) {
    return defenceConfigs.get(key);
  }

  /**
   * Gets a particular enemy config.
   * 
   * @param key The identifier or name of the enemy config to get.
   * @return The enemy config for the given key.
   */
  public BaseEntityConfig getEnemyConfig(String key) {
    return enemyConfigs.get(key);
  }

  /**
   * Gets a particular item config.
   * 
   * @param key The identifier or name of the item config to get.
   * @return The item config for the given key.
   */
  public BaseItemConfig getItemConfig(String key) {
    return itemConfigs.get(key);
  }

  /**
   * Gets all the defence configs.
   * 
   * @return All the defence configs.
   */
  public BaseDefenceConfig[] getDefenceConfigs() {
    return defenceConfigs.values().toArray(new BaseDefenceConfig[0]);
  }

  /**
   * Gets all the enemy configs.
   * 
   * @return All the enemy configs.
   */
  public BaseEntityConfig[] getEnemyConfigs() {
    return enemyConfigs.values().toArray(new BaseEntityConfig[0]);
  }

  /**
   * Gets all the item configs.
   * 
   * @return All the item configs.
   */
  public BaseItemConfig[] getItemConfigs() {
    return itemConfigs.values().toArray(new BaseItemConfig[0]);
  }

  /**
   * Generic data-class for the FileLoader to load config files into.
   * 
   * @param <T> The base type of config objects
   */
  public static class DeserializedConfig<T> {
    private Map<String, T> configs;

    /**
     * Creates a new DeserializedConfig.
     */
    public DeserializedConfig() {
      this.configs = new HashMap<>();
    }
  
    /**
     * Returns the deserialized configs.
     * 
     * @return the deserialized configs
     */
    public Map<String, T> getConfigs() {
      return configs;
    }
  }
}
