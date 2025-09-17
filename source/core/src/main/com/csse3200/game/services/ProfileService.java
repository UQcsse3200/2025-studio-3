package com.csse3200.game.services;

import com.csse3200.game.persistence.Persistence;
import com.csse3200.game.persistence.Savefile;
import com.csse3200.game.progression.Profile;
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
