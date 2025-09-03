package com.csse3200.game.persistence;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.csse3200.game.progression.Profile;

/**
 * Class for loading and saving the user profile / savefile.
 * 
 * Save files should be in the format <profilename>$<unixtime>.json.
 */
public class Persistence {
  private static Logger logger = LoggerFactory.getLogger(Persistence.class);
  private static Profile profile;
  private static final String ROOT_DIR = "The Day We Fought Back" + File.separator + "saves";

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
    return ROOT_DIR + File.separator + save.toString() + ".json";
  }

  /**
   * Load a user profile from a savefile.
   *
   * @param save the savefile object
   */
  public static void load(Savefile save) {
    String path = getPath(save);
    Profile savedProfile = FileLoader.readClass(
        Profile.class, path, FileLoader.Location.EXTERNAL);
    if (savedProfile != null) {
      profile = savedProfile;
    } else {
      logger.error("Failed to load profile, creating new one.");
      profile = new Profile();
    }
  }

  /**
   * Creates a new user profile, for use when a new game is started.
   */
  public static void load() {
    profile = new Profile();
    logger.info("Created new profile");
  }

  /**
   * Ensures that the save directory exists.
   */
  private static void ensureDirectoryExists() {
    FileHandle dir = Gdx.files.external(ROOT_DIR);
    if (!dir.exists()) {
        dir.mkdirs();
        logger.info("Created save directory: " + ROOT_DIR);
    }
}
  
  /**
   * Fetch the latest three profile names from the file system.
   */
  public static List<Savefile> fetch() {
    List<Savefile> saves = new ArrayList<>();

    // Search the saves directory for savefiles
    Pattern filePattern = Pattern.compile("^(.+?)\\$(\\d{10,13})\\.json$");
    ensureDirectoryExists();
    FileHandle rootDir = Gdx.files.external(ROOT_DIR);
    FileHandle[] files = rootDir.list(".json");
    if (files.length == 0) {
      return saves;
    }

    // Iterate over files and extract profile names and timestamps
    for (FileHandle file : files) {
      String filename = file.name();
      Matcher matcher = filePattern.matcher(filename);
      if (matcher.matches()) {
        String profileName = matcher.group(1);
        String timestampStr = matcher.group(2);
        try {
          long timestamp = Long.parseLong(timestampStr);
          saves.add(new Savefile(profileName, timestamp));
        } catch (NumberFormatException e) {
          continue;
        }
      }
    }

    // Sort on date and return
    saves.sort((a, b) -> b.getDate().compareTo(a.getDate()));
    if (saves.size() > 3) {
      return saves.subList(0, 3);
    }
    return saves;
  }

  /**
   * Save the current user profile to the savefile.
   */
  public static void save() {
    List<Savefile> saves = fetch();
    for (Savefile save : saves) {
      if (save.getName().equals(profile.getName())) {
        delete(save);
      }
    }
    String path = ROOT_DIR + File.separator + profile.getName() + "$" + System.currentTimeMillis() + ".json";
    FileLoader.writeClass(profile, path, FileLoader.Location.EXTERNAL);
  }

  /**
   * Deletes a savefile from the filesystem.
   */
  private static void delete(Savefile save) {
    String path = getPath(save);
    FileHandle file = Gdx.files.external(path);
    if (file.exists()) {
      boolean success = file.delete();
      if (success) {
        logger.info("Deleted savefile: " + path);
      } else {
        logger.error("Failed to delete savefile: " + path);
      }
    } else {
      logger.warn("Savefile does not exist: " + path);
    }
  }

  /**
   * Get the current user profile.
   * 
   * @return the current user profile.
   */
  public static Profile profile() {
    if (profile == null) {
      return null;
    }
    return profile;
  }
}
