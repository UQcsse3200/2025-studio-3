package com.csse3200.game.cutscene.models.object;

/** Stores sound data. */
public class Sound {
  private String id;
  private String file;

  /**
   * Creates a {@code Sound} object with specified id and file
   *
   * @param id The ID of the sound
   * @param file The path of the sound file
   */
  public Sound(String id, String file) {
    this.id = id;
    this.file = file;
  }

  public String getId() {
    return id;
  }

  public String getFile() {
    return file;
  }
}
