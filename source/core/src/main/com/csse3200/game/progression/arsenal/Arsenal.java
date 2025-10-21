package com.csse3200.game.progression.arsenal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.csse3200.game.entities.configs.BaseDefenderConfig;
import com.csse3200.game.entities.configs.BaseGeneratorConfig;
import com.csse3200.game.entities.configs.DefenceAndGeneratorConfig;

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
  public static final Map<String, BaseDefenderConfig> ALL_DEFENCES = new HashMap<>();
  /* The same as above for generators*/
  public static final Map<String, BaseGeneratorConfig> ALL_GENERATORS = new HashMap<>();

  static {
      loadFromConfig();
  }

  private static void loadFromConfig() {
      try {
          FileHandle file = Gdx.files.internal("configs/defences.json");
          Json json = new Json();

          DefenceAndGeneratorConfig data = json.fromJson(DefenceAndGeneratorConfig.class, file);

          for (Map.Entry<String, BaseDefenderConfig> entry : data.config.defenders.entrySet()) {
              if (!entry.getKey().equals("wall")) {
                  ALL_DEFENCES.put(entry.getKey(), entry.getValue());
              }
          }

          for (Map.Entry<String, BaseGeneratorConfig> entry : data.config.generators.entrySet()) {
              ALL_GENERATORS.put(entry.getKey(), entry.getValue());
          }
          System.out.println("Loaded defenders: " + data.config.defenders);
      } catch (Exception e) {
          Gdx.app.error("Arsenal", "Failed to load defenders/generators config", e);
      }
  }

  /** Constructor for the Arsenal class. */
  public Arsenal() {
    defences = new ArrayList<>();
    // Adds all default defences to the arsenal

    for (Map.Entry<String, BaseDefenderConfig> entry : ALL_DEFENCES.entrySet()) {
      if (entry.getValue().getLevelUnlockedOn().equals(INITIAL_DEFENCE)) {
        defences.add(entry.getKey());
      }
    }

    generators = new ArrayList<>();
    // Adds all default generators to the arsenal
    for (Map.Entry<String, BaseGeneratorConfig> entry : ALL_GENERATORS.entrySet()) {
      if (entry.getValue().getLevelUnlockedOn().equals(INITIAL_DEFENCE)) {
        generators.add(entry.getKey());
      }
    }
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
