package com.csse3200.game.services;

import com.csse3200.game.entities.configs.BaseAchievementConfig;
import com.csse3200.game.entities.configs.BaseAchievementConfig.DeserializedAchievementConfig;
import com.csse3200.game.entities.configs.BaseEnemyConfig.DeserializedEnemyConfig;
import com.csse3200.game.entities.configs.BaseDefenderConfig;
import com.csse3200.game.entities.configs.BaseEnemyConfig;
import com.csse3200.game.entities.configs.BaseGeneratorConfig;
import com.csse3200.game.entities.configs.BaseItemConfig;
import com.csse3200.game.entities.configs.DeserializedDefencesConfig;
import com.csse3200.game.entities.configs.BaseItemConfig.DeserializedItemConfig;
import com.csse3200.game.persistence.FileLoader;
import net.dermetfan.utils.Pair;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Service for managing the config files and data loading of the game. */
public class ConfigService {
  private static final Logger logger = LoggerFactory.getLogger(ConfigService.class);
  private Map<String, BaseDefenderConfig> defendersConfigs;
  private Map<String, BaseGeneratorConfig> generatorsConfigs;
  private Map<String, BaseEnemyConfig> enemyConfigs;
  private Map<String, BaseItemConfig> itemConfigs;
  private Map<String, BaseAchievementConfig> achievementConfigs;
  private static final String DEFENCE_CONFIG_FILE = "configs/defences.json";
  private static final String ENEMY_CONFIG_FILE = "configs/enemies.json";
  private static final String ITEM_CONFIG_FILE = "configs/items.json";
  private static final String ACHIEVEMENT_CONFIG_FILE = "configs/achievements.json";

  /** On registration, the config service will use the FileLoader to load all config files. */
  public ConfigService() {
    Pair<Map<String, BaseDefenderConfig>, Map<String, BaseGeneratorConfig>> defenceConfigs = loadDefenceConfigs(DEFENCE_CONFIG_FILE);
    this.defendersConfigs = defenceConfigs.getKey();
    this.generatorsConfigs = defenceConfigs.getValue();
    this.enemyConfigs = loadEnemyConfigs(ENEMY_CONFIG_FILE);
    this.itemConfigs = loadItemConfigs(ITEM_CONFIG_FILE);
    this.achievementConfigs = loadAchievementConfigs(ACHIEVEMENT_CONFIG_FILE);
    logger.info("Loaded {} item configs", itemConfigs.size());
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
   * @return a pair of the defenders and generators configs
   */
  public Pair<Map<String, BaseDefenderConfig>, Map<String, BaseGeneratorConfig>> loadDefenceConfigs(String filename) {
    DeserializedDefencesConfig wrapper =
        FileLoader.readClass(DeserializedDefencesConfig.class, filename);
    if (wrapper == null) {
      logger.warn("Failed to load defence config file: {}", filename);
      return new Pair<>(new HashMap<>(), new HashMap<>());
    }

    Map<String, BaseGeneratorConfig> generators = wrapper.getGenerators();
    if (generators == null) {
      logger.warn("Defence config file {} loaded but contains no generators", filename);
      generators = new HashMap<>();
    }

    Map<String, BaseDefenderConfig> defenders = wrapper.getDefenders();
    if (defenders == null) {
      logger.warn("Defence config file {} loaded but contains no configs", filename);
      defenders = new HashMap<>();
    }

    return new Pair<>(defenders, generators);
  }

  /**
   * Loads enemy configs from a file.
   *
   * @param filename the filename to load from
   * @return the enemy configs
   */
  public Map<String, BaseEnemyConfig> loadEnemyConfigs(String filename) {
    DeserializedEnemyConfig wrapper = FileLoader.readClass(DeserializedEnemyConfig.class, filename);
    if (wrapper == null) {
      logger.warn("Failed to load enemy config file: {}", filename);
      return new HashMap<>();
    }

    Map<String, BaseEnemyConfig> configs = wrapper.getConfig();
    if (configs == null) {
      logger.warn("Enemy config file {} loaded but contains no configs", filename);
      return new HashMap<>();
    }

    return configs;
  }

  /**
   * Loads achievement configs from a file.
   *
   * @param filename the filename to load from
   * @return the achievement configs
   */
  public Map<String, BaseAchievementConfig> loadAchievementConfigs(String filename) {
    DeserializedAchievementConfig wrapper =
        FileLoader.readClass(DeserializedAchievementConfig.class, filename);
    if (wrapper == null) {
      logger.warn("Failed to load achievement config file: {}", filename);
      return new HashMap<>();
    }

    Map<String, BaseAchievementConfig> configs = wrapper.getConfig();
    if (configs == null) {
      logger.warn("Achievement config file {} loaded but contains no configs", filename);
      return new HashMap<>();
    }

    return configs;
  }

  /**
   * Gets a particular generator config.
   *
   * @param key The identifier or name of the generator config to get.
   * @return The generator config for the given key.
   */
  public BaseGeneratorConfig getGeneratorConfig(String key) {
    return generatorsConfigs.get(key);
  }

  /**
   * Gets all the generator configs.
   *
   * @return All the generator configs.
   */
  public BaseGeneratorConfig[] getGeneratorConfigs() {
    return generatorsConfigs.values().toArray(new BaseGeneratorConfig[0]);
  }

  /**
   * Gets all the defender configs.
   *
   * @return All the defender configs.
   */
  public BaseDefenderConfig[] getDefenderConfigs() {
    return defendersConfigs.values().toArray(new BaseDefenderConfig[0]);
  }

  /**
   * Gets a particular defender config.
   *
   * @param key The identifier or name of the defender config to get.
   * @return The defender config for the given key.
   */
  public BaseDefenderConfig getDefenderConfig(String key) {
    return defendersConfigs.get(key);
  }

  /**
   * Gets a particular enemy config.
   *
   * @param key The identifier or name of the enemy config to get.
   * @return The enemy config for the given key.
   */
  public BaseEnemyConfig getEnemyConfig(String key) {
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
   * Gets a particular achievement config.
   *
   * @param key The identifier or name of the achievement config to get.
   * @return The achievement config for the given key.
   */
  public BaseAchievementConfig getAchievementConfig(String key) {
    return achievementConfigs.get(key);
  }

  /**
   * Gets all the enemy configs.
   *
   * @return All the enemy configs.
   */
  public BaseEnemyConfig[] getEnemyConfigs() {
    return enemyConfigs.values().toArray(new BaseEnemyConfig[0]);
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
   * Gets all achievement configs.
   *
   * @return map of achievement key to config
   */
  public Map<String, BaseAchievementConfig> getAchievementConfigs() {
    return achievementConfigs;
  }

  /**
   * Gets all the item keys.
   *
   * @return All the item keys.
   */
  public String[] getItemKeys() {
    return itemConfigs.keySet().toArray(new String[0]);
  }

  /**
   * Gets all the achievement keys.
   *
   * @return All the achievement keys.
   */
  public String[] getAchievementKeys() {
    return achievementConfigs.keySet().toArray(new String[0]);
  }

  /**
   * Gets all the defender keys.
   *
   * @return All the defender keys.
   */
  public String[] getDefenderKeys() {
    return defendersConfigs.keySet().toArray(new String[0]);
  }

  /**
   * Gets all the enemy keys.
   *
   * @return All the enemy keys.
   */
  public String[] getEnemyKeys() {
    return enemyConfigs.keySet().toArray(new String[0]);
  }

  /**
   * Gets all the generator keys.
   *
   * @return All the generator keys.
   */
  public String[] getGeneratorKeys() {
    return generatorsConfigs.keySet().toArray(new String[0]);
  }
}
