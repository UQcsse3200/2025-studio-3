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
 * Class for loading and saving the user profile / savefile.
 *
 * <p>Save files should be in the format {@code <profilename>$<unixtime>$<slot>.json}.
 */
public class Persistence {
  private static Logger logger = LoggerFactory.getLogger(Persistence.class);
  private static final String ROOT_DIR = "The Day We Fought Back" + File.separator + "saves";
  private static final String SAVE_FILE_PATTERN = "^(.+?)\\$(\\d{10,13})(?:\\$(\\d+))?\\.json$";
  private static final String FILE_EXTENSION = ".json";

  /** Private constructor to prevent instantiation. */
  private Persistence() {
    throw new IllegalStateException("Instantiating static util class");
  }

  /**
   * Get the file path for a savefile.
   *
   * @param save the savefile object
   * @return the file path as a string
   */
  private static String getPath(Savefile save) {
    return ROOT_DIR + File.separator + save.toString() + FILE_EXTENSION;
  }

  /**
   * Load a user profile from a savefile.
   *
   * @param save the savefile object
   * @return the profile and the slot
   */
  public static net.dermetfan.utils.Pair<Profile, Integer> load(Savefile save) {
    String path = getPath(save);
    Profile savedProfile = FileLoader.readClass(Profile.class, path, FileLoader.Location.EXTERNAL);
    if (savedProfile != null) {
      return new Pair<>(savedProfile, save.getSlot());
    } else {
      throw new IllegalStateException("Failed to load profile, creating new one.");
    }
  }

  /**
   * Create a new user profile.
   *
   * @param profileName the name of the profile, or null to use the default name
   * @param slot the slot to save the profile to
   * @return the profile and the slot
   */
  public static Pair<Profile, Integer> create(String profileName, int slot) {
    Profile profile = new Profile();
    if (profileName != null) {
      profile.setName(profileName);
    }
    save(slot, profile);
    return new Pair<>(profile, slot);
  }

  /** Ensures that the save directory exists. */
  private static void ensureDirectoryExists() {
    FileHandle dir = Gdx.files.external(ROOT_DIR);
    if (!dir.exists()) {
      dir.mkdirs();
      logger.info("Created save directory: {}", ROOT_DIR);
    }
  }

  /**
   * Fetch saves organized by slot.
   *
   * @return the list of savefiles
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
   * Parse a savefile from a file handle.
   *
   * @param file the file handle to parse
   * @return the savefile, or null if the file is invalid
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
   * Deletes a savefile from the filesystem.
   *
   * @param save the savefile to delete
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
