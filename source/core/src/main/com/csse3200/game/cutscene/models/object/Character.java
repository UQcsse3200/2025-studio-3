package com.csse3200.game.cutscene.models.object;

import java.util.Map;

/** Stores character data. */
public class Character {
  private String id;
  private String name;
  private Map<String, String> poses;

  /**
   * Creates a {@code Character} object with specified id, name, and a map of poses.
   *
   * @param id The ID of the character
   * @param name The name of the character (to be displayed in chat box)
   * @param poses A {@code Map<String, String>} of poses (where the key is the pose name, and the
   *     value is the file name)
   */
  public Character(String id, String name, Map<String, String> poses) {
    this.id = id;
    this.name = name;
    this.poses = poses;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Map<String, String> getPoses() {
    return Map.copyOf(poses);
  }
}
