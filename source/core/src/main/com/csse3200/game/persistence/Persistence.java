package com.csse3200.game.persistence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.csse3200.game.progression.Profile;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dermetfan.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Persistence utilities for loading and saving user profiles.
 *
 * <p>Save files follow the format: {@code <profilename>$<unixtime>$<slot>.json}
 */
public class Persistence {
  private static final Logger logger = LoggerFactory.getLogger(Persistence.class);
  private static final String ROOT_DIR = "The Day We Fought Back" + File.separator + "saves";
  private static final String SAVE_FILE_PATTERN = "^(.+?)\\$(\\d{10,13})(?:\\$(\\d+))?\\.json$";
  private static final String FILE_EXTENSION = ".json";

  /** Prevent instantiation of this static utility class. */
  private Persistence() {
    throw new IllegalStateException("Instantiating static util class");
  }

  /** Builds the absolute external path for the given savefile. */
  private static String getPath(Savefile save) {
    return ROOT_DIR + File.separator + save.toString() + FILE_EXTENSION;
  }
  
  /**
   * Load a user profile from a savefile.
   *
   * @param save the savefile object
   * @return the profile and the slot
   */
  public static Pair<Profile, Integer> load(Savefile save) {
    String path = getPath(save);
    Profile savedProfile = FileLoader.readClass(Profile.class, path, FileLoader.Location.EXTERNAL);
    if (savedProfile != null) {
      return new Pair<>(savedProfile, save.getSlot());
    } else {
      throw new IllegalStateException("Failed to load profile.");
    }
  }


  /**
   * Creates a new user profile and immediately persists it to the given slot.
   *
   * @param profileName Optional profile name (null = use default)
   * @param slot Slot number (1-3)
   * @return Pair of (new Profile, slot)
   */
  public static Pair<Profile, Integer> create(String profileName, int slot) {
    Profile profile = new Profile();
    if (profileName != null) {
      profile.setName(profileName);
    }
    save(slot, profile);
    return new Pair<>(profile, slot);
  }

  /** Ensures the save directory exists. */
  private static void ensureDirectoryExists() {
    FileHandle dir = Gdx.files.external(ROOT_DIR);
    if (!dir.exists()) {
      dir.mkdirs();
      logger.info("Created save directory: {}", ROOT_DIR);
    }
  }

  /**
   * Scans the save directory and returns the latest save per slot (1..3).
   *
   * @return List of 3 entries (null = empty slot)
   */
  public static List<Savefile> fetch() {
    List<Savefile> saves = new ArrayList<>(3);
    for (int i = 0; i < 3; i++) {
      saves.add(null);
    }

    ensureDirectoryExists();
    FileHandle rootDir = Gdx.files.external(ROOT_DIR);
    FileHandle[] files = rootDir.list(FILE_EXTENSION);
    if (files.length == 0) {
      return saves;
    }

    for (FileHandle file : files) {
      Savefile savefile = parseSavefile(file);
      if (savefile != null) {
        saves.set(savefile.getSlot() - 1, savefile);
      }
    }

    return saves;
  }

  /**
   * Parses a savefile descriptor from a filename.
   *
   * @param file File handle to parse
   * @return Savefile or null if filename is invalid
   */
  private static Savefile parseSavefile(FileHandle file) {
    Pattern filePattern = Pattern.compile(SAVE_FILE_PATTERN);
    Matcher matcher = filePattern.matcher(file.name());
    if (!matcher.matches()) {
      logger.error("Failed to parse savefile");
      return null;
    }

    String profileName = matcher.group(1);
    String timestampStr = matcher.group(2);
    String slotStr = matcher.group(3);
    long timestamp;
    int slot;

    try {
      timestamp = Long.parseLong(timestampStr);
      slot = Integer.parseInt(slotStr);
      if (slot > 3 || slot < 1) throw new NumberFormatException();
    } catch (NumberFormatException e) {
      logger.error("Failed to parse savefile");
      return null;
    }

    return new Savefile(profileName, timestamp, slot);
  }

  /**
   * Save the current user profile to a specific slot.
   *
   * @param slot the slot to save the profile to
   * @param profile the profile to save
   */
  public static void save(int slot, Profile profile) {
    if (slot > 3 || slot < 1) {
      logger.error("Invalid slot: {}", slot);
      return;
    }

    // Delete any existing save in this slot
    List<Savefile> saves = fetch();
    try {
      if (saves.get(slot - 1) != null) {
        delete(saves.get(slot - 1));
      }
    } catch (IndexOutOfBoundsException e) {
      // slot is empty
    }

    // Create filename with slot information
    String filename =
        profile.getName() + "$" + System.currentTimeMillis() + "$" + slot + FILE_EXTENSION;
    String path = ROOT_DIR + File.separator + filename;
    FileLoader.writeClass(profile, path, FileLoader.Location.EXTERNAL);
    logger.info("Saved profile to slot {}: {}", slot, filename);
  }

  /**
   * Deletes the given savefile from disk.
   *
   * @param save Savefile descriptor
   */
  private static void delete(Savefile save) {
    String path = getPath(save);
    FileHandle file = Gdx.files.external(path);
    if (file.exists()) {
      boolean success = file.delete();
      if (success) {
        logger.info("Deleted savefile: {}", path);
      } else {
        logger.error("Failed to delete savefile: {}", path);
      }
    } else {
      logger.warn("Savefile does not exist: {}", path);
    }
  }
}
