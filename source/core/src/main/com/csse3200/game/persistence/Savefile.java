package com.csse3200.game.persistence;

import java.util.UUID;

/** Represents a savefile in the game. */
public class Savefile {
  private String name;
  private Long date;
  private int slot;

  /**
   * Creates a Savefile object with the given name, date, and slot.
   *
   * @param name the name of the savefile
   * @param date the date of the savefile
   * @param slot the slot of the savefile
   */
  public Savefile(String name, Long date, int slot) {
    this.name = name;
    this.date = date;
    this.slot = slot;
  }

  /**
   * Creates a Savefile object from a string representation.
   *
   * @param savefileString the string representation of the savefile
   * @return the Savefile object, or null if the string is invalid
   */
  public static Savefile fromString(String savefileString) {
    String[] parts = savefileString.split("\\$");
    if (parts.length != 3) {
      return null;
    }

    try {
      String name = parts[0];
      Long date = Long.parseLong(parts[1]);
      int slot = Integer.parseInt(parts[2]);
      return new Savefile(name, date, slot);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  /**
   * Converts the Savefile object to a string representation.
   *
   * @return the string representation of the savefile.
   */
  public String toString() {
    return name + "$" + date + "$" + slot;
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
   * Get the slot number of the savefile.
   *
   * @return the slot number of the savefile.
   */
  public int getSlot() {
    return slot;
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
    return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(date));
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
