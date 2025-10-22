package com.csse3200.game.progression.arsenal;

import com.csse3200.game.entities.configs.BaseDefenderConfig;
import com.csse3200.game.entities.configs.BaseGeneratorConfig;
import com.csse3200.game.services.ConfigService;
import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Arsenal class to manage the player's unlocked defences. */
public class Arsenal {
  private final List<String> defences;
  private final List<String> generators;

  /* Constant list of all unlockable defences coupled with the level that they are unlocked on
   * 0 indicates level 1 is initialised with three defences. This will be used to track which
   * defences should be unlocked on each level */
  private static final String INITIAL_DEFENCE = "levelOne";
  private static final Map<String, BaseDefenderConfig> ALL_DEFENCES = new HashMap<>();
  /* The same as above for generators*/
  private static final Map<String, BaseGeneratorConfig> ALL_GENERATORS = new HashMap<>();

  /**
   * Gets a record of all defences in the defender config file
   *
   * @return a map of defender keys to the respective defender config
   */
  public static Map<String, BaseDefenderConfig> getAllDefences() {
    return ALL_DEFENCES;
  }

  /**
   * Gets a record of all generators in the defender config file
   *
   * @return a map of generator keys to respective generator config
   */
  public static Map<String, BaseGeneratorConfig> getAllGenerators() {
    return ALL_GENERATORS;
  }

  /**
   * Sets the all defences map to be a given map
   *
   * @param allDefences the map that will replace the previous allDefences map
   */
  public static void setAllDefences(Map<String, BaseDefenderConfig> allDefences) {
    for (Map.Entry<String, BaseDefenderConfig> entry : allDefences.entrySet()) {
      if (!entry.getKey().equals("wall")) {
        ALL_DEFENCES.put(entry.getKey(), entry.getValue());
      }
    }
  }

  /**
   * Sets the all generators map to be a given map
   *
   * @param allGenerators the map that will replace the previous allGenerators map
   */
  public static void setAllGenerators(Map<String, BaseGeneratorConfig> allGenerators) {
    ALL_GENERATORS.putAll(allGenerators);
  }

  /** Loads the defence and generator maps from the defender config file */
  private static void loadFromConfig() {
    ConfigService configService = ServiceLocator.getConfigService();

    if (configService == null) return;
    ALL_DEFENCES.clear();
    ALL_GENERATORS.clear();

    setAllDefences(configService.getDefenderConfigs());
    setAllGenerators(configService.getGeneratorConfigs());
  }

  /** Constructor for the Arsenal class. */
  public Arsenal() {
    defences = new ArrayList<>();
    generators = new ArrayList<>();

    ConfigService configService = ServiceLocator.getConfigService();
    if (configService == null) return;

    initialiseArsenal();
  }

  /**
   * Adds a defence to the arsenal.
   *
   * @param defenceKey The key of the defence to add.
   */
  public void unlockDefence(String defenceKey) {
    defences.add(defenceKey);
  }

  /**
   * Removes a defence from the arsenal.
   *
   * @param defenceKey The key of the defence to remove.
   */
  public void lockDefence(String defenceKey) {
    defences.remove(defenceKey);
  }

  /**
   * Adds a defence to the arsenal.
   *
   * @param defenceKey The key of the defence to add.
   */
  public void unlockGenerator(String defenceKey) {
    generators.add(defenceKey);
  }

  /**
   * Removes a defence from the arsenal.
   *
   * @param defenceKey The key of the defence to remove.
   */
  public void lockGenerator(String defenceKey) {
    generators.remove(defenceKey);
  }

  /** Unlocks all initial entities to initialise the arsenal */
  public void initialiseArsenal() {
    loadFromConfig();

    for (Map.Entry<String, BaseDefenderConfig> entry : getAllDefences().entrySet()) {
      if (entry.getValue().getLevelUnlockedOn().equals(INITIAL_DEFENCE)
          && !entry.getKey().equals("wall") && !defences.contains(entry.getKey())) {
        defences.add(entry.getKey());
      }
    }

    for (Map.Entry<String, BaseGeneratorConfig> entry : getAllGenerators().entrySet()) {
      if (entry.getValue().getLevelUnlockedOn().equals(INITIAL_DEFENCE) && !generators.contains(entry.getKey())) {
        generators.add(entry.getKey());
      }
    }
  }

  /**
   * Gets the list of defences in the arsenal.
   *
   * @return The list of keys of the defences in the arsenal.
   */
  public List<String> getDefenders() {
    return defences;
  }

  /**
   * Gets the list of generators in the arsenal.
   *
   * @return The list of keys of the generators in the arsenal.
   */
  public List<String> getGenerators() {
    return generators;
  }

  /**
   * Checks if the arsenal contains the specified defence key.
   *
   * @param key The key of the defence to check.
   * @return True if the arsenal contains the defence key, false otherwise.
   */
  public boolean contains(String key) {
    return defences.contains(key) || generators.contains(key);
  }
}
