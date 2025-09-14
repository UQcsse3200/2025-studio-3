package com.csse3200.game.services;

import com.csse3200.game.entities.configs.BaseDefenceConfig;
import com.csse3200.game.entities.configs.BaseEntityConfig;
import com.csse3200.game.entities.configs.BaseItemConfig;
import com.csse3200.game.entities.configs.BaseItemConfig.DeserializedItemConfig;
import com.csse3200.game.persistence.FileLoader;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Service for managing the config files and data loading of the game. */
public class ConfigService {
  private static final Logger logger = LoggerFactory.getLogger(ConfigService.class);
  private Map<String, BaseDefenceConfig> defenceConfigs;
  private Map<String, BaseEntityConfig> enemyConfigs;
  private Map<String, BaseItemConfig> itemConfigs;
  private static final String DEFENCE_CONFIG_FILE = "configs/Defences.json";
  private static final String ENEMY_CONFIG_FILE = "configs/Enemies.json";
  private static final String ITEM_CONFIG_FILE = "configs/Items.json";

  /** On registration, the config service will use the FileLoader to load all config files. */
  public ConfigService() {
    this.defenceConfigs = loadDefenceConfigs(DEFENCE_CONFIG_FILE);
    this.enemyConfigs = loadEnemyConfigs(ENEMY_CONFIG_FILE);
    this.itemConfigs = loadItemConfigs(ITEM_CONFIG_FILE);
  }

  /**
   * Loads item configs from a file.
   *
   * @param filename the filename to load from
   * @return the item configs
   */
  public Map<String, BaseItemConfig> loadItemConfigs(String filename) {
    DeserializedItemConfig wrapper = FileLoader.readClass(DeserializedItemConfig.class, filename);

    if (wrapper == null) {
      logger.warn("Failed to load item config file: {}", filename);
      return new HashMap<>();
    }

    Map<String, BaseItemConfig> configs = wrapper.getConfig();
    if (configs == null) {
      logger.warn("Item config file {} loaded but contains no configs", filename);
      return new HashMap<>();
    }

    return configs;
  }

  /**
   * Loads defence configs from a file.
   *
   * @param filename the filename to load from
   * @return the defence configs
   */
  public Map<String, BaseDefenceConfig> loadDefenceConfigs(String filename) {
    DeserializedDefenceConfig wrapper =
        FileLoader.readClass(DeserializedDefenceConfig.class, filename);
    if (wrapper == null) {
      logger.warn("Failed to load defence config file: {}", filename);
      return new HashMap<>();
    }

    Map<String, BaseDefenceConfig> configs = wrapper.getConfig();
    if (configs == null) {
      logger.warn("Defence config file {} loaded but contains no configs", filename);
      return new HashMap<>();
    }

    return configs;
  }

  /**
   * Loads enemy configs from a file.
   *
   * @param filename the filename to load from
   * @return the enemy configs
   */
  public Map<String, BaseEntityConfig> loadEnemyConfigs(String filename) {
    DeserializedEnemyConfig wrapper = FileLoader.readClass(DeserializedEnemyConfig.class, filename);
    if (wrapper == null) {
      logger.warn("Failed to load enemy config file: {}", filename);
      return new HashMap<>();
    }

    Map<String, BaseEntityConfig> configs = wrapper.getConfig();
    if (configs == null) {
      logger.warn("Enemy config file {} loaded but contains no configs", filename);
      return new HashMap<>();
    }

    return configs;
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
   * Gets all the item keys.
   *
   * @return All the item keys.
   */
  public String[] getItemKeys() {
    return itemConfigs.keySet().toArray(new String[0]);
  }

  // TODO: Move these to the Base Config classes for tidyness.
  public static class DeserializedDefenceConfig {
    private HashMap<String, BaseDefenceConfig> config;

    public DeserializedDefenceConfig() {
      this.config = new HashMap<>();
    }

    public void setConfig(Map<String, BaseDefenceConfig> config) {
      this.config = new HashMap<>(config);
    }

    public Map<String, BaseDefenceConfig> getConfig() {
      return config;
    }
  }

  public static class DeserializedEnemyConfig {
    private HashMap<String, BaseEntityConfig> config;

    public DeserializedEnemyConfig() {
      this.config = new HashMap<>();
    }

    public void setConfig(Map<String, BaseEntityConfig> config) {
      this.config = new HashMap<>(config);
    }

    public Map<String, BaseEntityConfig> getConfig() {
      return config;
    }
  }
}
