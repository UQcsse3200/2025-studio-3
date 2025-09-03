package com.csse3200.game.persistence;

import java.util.UUID;

public class Savefile {
  private String name;
  private Long date;

  public Savefile(String name, Long date) {
    this.name = name;
    this.date = date;
  }

  /**
   * Creates a Savefile object from a string representation.
   *
   * @param savefileString the string representation of the savefile
   * @return the Savefile object, or null if the string is invalid
   */
  public static Savefile fromString(String savefileString) {
    String[] parts = savefileString.split("\\$");
    if (parts.length != 2) {
      return null;
    }
    String name = parts[0];
    Long date;
    try {
      date = Long.parseLong(parts[1]);
    } catch (NumberFormatException e) {
      return null;
    }
    return new Savefile(name, date);
  }

  /**
   * Converts the Savefile object to a string representation.
   */
  public String toString() {
    return name + "$" + date;
  }

  /**
   * Get the name of the savefile.
   *
   * @return the name of the savefile.
   */
  public String getName() {
    return name;
  }

  /**
   * Get the date of the savefile.
   *
   * @return the date of the savefile.
   */
  public Long getDate() {
    return date;
  }

  /**
   * Get the name of the savefile to be displayed.
   *
   * @return the name of the savefile.
   */
  public String getDisplayName() {
    try {
      UUID.fromString(name);
      return "Autosave";
    } catch (IllegalArgumentException e) {
      return name;
    }
  }

  /**
   * Get the date of the savefile to be displayed.
   *
   * @return the date of the savefile.
   */
  public String getDisplayDate() {
    return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        .format(new java.util.Date(date));
  }

  /**
   * Creates a unique name for a new savefile.
   *
   * @return a unique name for the savefile.
   */
  public static String createName() {
    return UUID.randomUUID().toString();
  }
}