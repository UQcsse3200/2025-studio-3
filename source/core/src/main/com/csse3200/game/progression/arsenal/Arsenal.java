package com.csse3200.game.progression.arsenal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/** Arsenal class to manage the player's unlocked defences. */
public class Arsenal {
  private final List<String> defences;
  private final List<String> generators;

  /* Constant list of all unlockable defences coupled with the level that they are unlocked on
   * 0 indicates level 1 is initialised with three defences. This will be used to track which
   * defences should be unlocked on each level */
  private final Integer NUM_DEFENCES = 7;
  private static final HashMap<String, Integer> ALL_DEFENCES = new HashMap<>();

  static {
    // ALL_DEFENCES.put("furnace", 0);
    ALL_DEFENCES.put("slingshooter", 0);
    ALL_DEFENCES.put("shield", 0);
    ALL_DEFENCES.put("armyguy", 1);
    ALL_DEFENCES.put("boxer", 2);
    // ALL_DEFENCES.put("mortar", 3);
    ALL_DEFENCES.put("shadow", 4);
  }

  /** Constructor for the Arsenal class. */
  public Arsenal() {
    defences = new ArrayList<>();

    // Adds all default defences to the arsenal
    for (String key : ALL_DEFENCES.keySet()) {
      if (ALL_DEFENCES.get(key) == 0) {
        defences.add(key);
      }
    }

    generators = new ArrayList<>(Arrays.asList("furnace"));
  }

  //    defences =
  //        new ArrayList<>(
  //            Arrays.asList("slingshooter", "armyguy", "shadow", "boxer", "shield")); // Default
  // defences
  //    generators = new ArrayList<>(Arrays.asList("furnace"));

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
