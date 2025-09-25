package com.csse3200.game.progression.arsenal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Arsenal class to manage the player's unlocked defences. */
public class Arsenal {
  private final List<String> defences;

  /** Constructor for the Arsenal class. */
  public Arsenal() {
    defences =
        new ArrayList<>(Arrays.asList("slingshooter", "furnace", "armyguy")); // Default defences
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
   * Gets the list of defences in the arsenal.
   *
   * @return The list of keys of the defences in the arsenal.
   */
  public List<String> getKeys() {
    return defences;
  }

  /**
   * Checks if the arsenal contains the specified defence key.
   *
   * @param defenceKey The key of the defence to check.
   * @return True if the arsenal contains the defence key, false otherwise.
   */
  public boolean contains(String defenceKey) {
    return defences.contains(defenceKey);
  }
}
