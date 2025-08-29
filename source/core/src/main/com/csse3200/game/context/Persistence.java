package com.csse3200.game.context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.csse3200.game.progression.Profile;

/**
 * Class for loading and saving the user profile / savefile.
 * 
 * Save files should be in the format <profilename><unixtime>.json.
 */
public class Persistence {
  private static Profile profile;
  private static final String ROOT_DIR = "The Day We Fought Back" + File.separator + "saves";

  public static class Savefile {
    public String name;
    public Long date;

    public Savefile(String name, Long date) {
      this.name = name;
      this.date = date;
    }
  }
 
  private Persistence() {
    throw new IllegalStateException("Instantiating static util class");
  }

  /**
   * Load the user profile. For the purposes of sprint one, there is only one
   * profile. Later, this could be extended to allow multiple profiles.
   *
   * Loads from the savefile, or returns a new profile if it doesn't exist.
   */
  public static void load(Savefile save) {
    String path = ROOT_DIR + File.separator + save.name + save.date + ".json";
    Profile savedProfile = FileLoader.readClass(
        Profile.class, path, FileLoader.Location.EXTERNAL);
    if (savedProfile != null) {
      profile = savedProfile;
    } else {
      //...
    }
  }
  
  /**
   * Fetch the latest three profile names from the file system.
   */
  public static List<Savefile> fetch() {
    List<Savefile> saves = new ArrayList<>();

    // Search the saves directory for savefiles
    Pattern filePattern = Pattern.compile("^(.+?)(\\d{10,13})\\.json$");
    FileHandle rootDir = Gdx.files.internal(ROOT_DIR);
    if (!rootDir.exists() || !rootDir.isDirectory()) {
      return saves;
    }
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
          // String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
          //     .format(new java.util.Date(timestamp));
          saves.add(new Savefile(profileName, timestamp));
        } catch (NumberFormatException e) {
          continue;
        }
      }
    }

    // Sort on date and return
    saves.sort((a, b) -> b.date.compareTo(a.date));
    if (saves.size() > 3) {
      return saves.subList(0, 3);
    }
    return saves;
  }

  /**
   * Save the current user profile to the savefile.
   */
  public static void save() {
    String path = ROOT_DIR + File.separator + profile.getName() + System.currentTimeMillis() + ".json";
    FileLoader.writeClass(profile, path, FileLoader.Location.EXTERNAL);
  }

  /**
   * Get the current user profile.
   * 
   * @return the current user profile.
   */
  public static Profile get() {
    return profile;
  }
}
