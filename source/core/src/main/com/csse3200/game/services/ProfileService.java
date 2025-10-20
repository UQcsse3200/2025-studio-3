package com.csse3200.game.services;

import com.csse3200.game.persistence.Persistence;
import com.csse3200.game.persistence.Savefile;
import com.csse3200.game.progression.Profile;
import java.util.Arrays;
import java.util.List;
import net.dermetfan.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for managing user profiles.
 *
 * <p>This service provides a centralized way to manage profile operations including creating,
 * loading, saving, and deleting profiles. It acts as a wrapper around the existing Persistence
 * class to provide a service-based interface.
 */
public class ProfileService {
  private static final Logger logger = LoggerFactory.getLogger(ProfileService.class);
  // Ordered mainline levels used to infer progression without storing unlock/completion state
  private static final List<String> MAINLINE_LEVELS =
      Arrays.asList("levelOne", "levelTwo", "levelThree", "levelFour", "levelFive", "end");
  private Profile profile;
  private boolean isActive;
  private int currentSlot;

  /** Creates a new ProfileService instance. */
  public ProfileService() {
    this.profile = new Profile(); // Initialize with a dummy profile
    this.isActive = false;
    this.currentSlot = 0;
    logger.debug("[ProfileService] ProfileService initialized");
  }

  /**
   * Gets the current active profile.
   *
   * @return the current profile
   */
  public Profile getProfile() {
    return profile;
  }

  /**
   * Gets the current slot number.
   *
   * @return the current slot (1-3), or 0 if no slot is active
   */
  public int getCurrentSlot() {
    return currentSlot;
  }

  /**
   * Checks if a profile is currently loaded.
   *
   * @return true if a profile is loaded, false otherwise
   */
  public boolean isActive() {
    return isActive;
  }

  /**
   * Creates a new profile with the specified name and slot.
   *
   * @param profileName the name for the new profile
   * @param slot the slot to save the profile to (1-3)
   */
  public void createProfile(String profileName, int slot) {
    logger.info("[ProfileService] Creating new profile '{}' in slot {}", profileName, slot);
    Pair<Profile, Integer> pair = Persistence.create(profileName, slot);
    this.profile = pair.getKey();
    this.currentSlot = pair.getValue();
    this.isActive = true;
    Persistence.save(this.currentSlot, this.profile);
  }

  /**
   * Loads a profile from the specified savefile.
   *
   * @param savefile the savefile to load
   */
  public void loadProfile(Savefile savefile) {
    logger.info("[ProfileService] Loading profile from savefile: {}", savefile);
    Pair<Profile, Integer> pair = Persistence.load(savefile);
    this.profile = pair.getKey();
    this.currentSlot = savefile.getSlot();
    this.isActive = true;
  }

  /**
   * Saves the current profile to the current slot.
   *
   * @throws IllegalStateException if no profile is currently loaded
   */
  public void saveCurrentProfile() {
    if (!isActive) {
      throw new IllegalStateException("No profile loaded to save");
    }
    logger.info("[ProfileService] Saving current profile to slot {}", currentSlot);
    Persistence.save(currentSlot, profile);
  }

  /**
   * Returns the next mainline level key after the provided current key, or null if there is no
   * next.
   */
  private String nextMainlineOf(String currentKey) {
    if (currentKey == null) {
      return null;
    }
    int idx = MAINLINE_LEVELS.indexOf(currentKey);
    if (idx == -1 || idx + 1 >= MAINLINE_LEVELS.size()) {
      return null; // not in mainline or already at last level
    }
    return MAINLINE_LEVELS.get(idx + 1);
  }

  /**
   * Marks the given level as completed and advances the mainline pointer solely based on the
   * current level. This method no longer records unlocked/completed nodes; it only updates {@code
   * currentLevel} to the next mainline level (if any within five levels).
   *
   * <p>Rules:
   *
   * <ul>
   *   <li>If {@code currentKey} is one of levelOne..levelFive and not the last, set {@code
   *       currentLevel} to the next one.
   *   <li>If {@code currentKey} is levelFive or not a recognised mainline key, leave {@code
   *       currentLevel} unchanged.
   *   <li>No writes to unlockedNodes or completedNodes.
   * </ul>
   *
   * After updating, the profile is saved immediately to the active slot.
   *
   * @param currentKey the key of the level just completed (e.g., {@code levelTwo})
   */
  public void markLevelComplete(String currentKey) {
    if (profile == null) {
      logger.warn("Attempted to mark level complete but profile is null.");
      return;
    }

    // Do not regress progression: only advance if this completion moves the mainline forward.
    String currentPtr = profile.getCurrentLevel();
    int curIdx = MAINLINE_LEVELS.indexOf(currentPtr);
    if (curIdx < 0) curIdx = 0; // safety for unexpected values

    final String nextOfKey = nextMainlineOf(currentKey);
    int candIdx = (nextOfKey != null) ? MAINLINE_LEVELS.indexOf(nextOfKey) : curIdx;
    if (candIdx < 0) candIdx = curIdx;

    if (candIdx > curIdx) {
      String newLevel = MAINLINE_LEVELS.get(candIdx);
      profile.setCurrentLevel(newLevel);
      logger.info(
          "[ProfileService] Progress advanced: '{}' complete -> pointer '{}' -> '{}'",
          currentKey,
          currentPtr,
          newLevel);
    } else {
      logger.debug(
          "[ProfileService] Ignoring replay completion '{}' (pointer at '{}' not regressed)",
          currentKey,
          currentPtr);
    }

    // Persist only the updated currentLevel
    saveCurrentProfile();
  }

  /**
   * Saves the current profile to a specific slot.
   *
   * @param slot the slot to save to (1-3)
   * @throws IllegalStateException if no profile is currently loaded
   * @throws IllegalArgumentException if slot is not between 1 and 3
   */
  public void saveProfileToSlot(int slot) {
    if (!isActive) {
      throw new IllegalStateException("No profile loaded to save");
    }
    if (slot < 1 || slot > 3) {
      throw new IllegalArgumentException("Slot must be between 1 and 3, got: " + slot);
    }
    logger.info("[ProfileService] Saving current profile to slot {}", slot);
    Persistence.save(slot, profile);
    this.currentSlot = slot;
  }

  /**
   * Gets all available savefiles organized by slot.
   *
   * @return a list of savefiles (null entries indicate empty slots)
   */
  public List<Savefile> getAllSaves() {
    return Persistence.fetch();
  }

  /**
   * Gets a specific savefile from a slot.
   *
   * @param slot the slot to get the savefile from (1-3)
   * @return the savefile in that slot, or null if the slot is empty
   * @throws IllegalArgumentException if slot is not between 1 and 3
   */
  public Savefile getSaveFromSlot(int slot) {
    if (slot < 1 || slot > 3) {
      throw new IllegalArgumentException("Slot must be between 1 and 3, got: " + slot);
    }

    List<Savefile> saves = getAllSaves();
    if (slot <= saves.size()) {
      return saves.get(slot - 1);
    }
    return null;
  }

  /** Clears the current profile. */
  public void clear() {
    logger.info("Clearing current profile.");
    this.profile = new Profile();
    this.currentSlot = 0;
    this.isActive = false;
  }

  /**
   * Checks if a slot is empty.
   *
   * @param slot the slot to check (1-3)
   * @return true if the slot is empty, false otherwise
   * @throws IllegalArgumentException if slot is not between 1 and 3
   */
  public boolean isSlotEmpty(int slot) {
    return getSaveFromSlot(slot) == null;
  }

  /**
   * Gets the number of used slots.
   *
   * @return the number of slots that contain savefiles
   */
  public int getUsedSlotCount() {
    List<Savefile> saves = getAllSaves();
    int count = 0;
    for (Savefile save : saves) {
      if (save != null) {
        count++;
      }
    }
    return count;
  }
}
