package com.csse3200.game.cutscene.models.object;

import java.util.ArrayList;
import java.util.List;

/** The different transitions that can happen to image (character sprites and backgrounds) */
public enum Transition {
  /** Fades into or out of existence by a defined time (in MS) */
  FADE,

  /** Slides into or out of frame (only for character sprites) depending on the position defined. */
  SLIDE,

  /** "Pops" or moves the sprite when creating, destroying, or replacing it */
  POP,

  /** In place replace with no animation */
  REPLACE;

  /**
   * Maps JSON values {@code "fade"|"slide"|"pop"|"replace"} to their {@code Transition}
   * counterparts.
   *
   * @param value - String value to be converted
   * @return {@code Transition} value corresponding to the input string
   */
  public static Transition fromString(String value) {
    return switch (value) {
      case "fade" -> Transition.FADE;
      case "slide" -> Transition.SLIDE;
      case "pop" -> Transition.POP;
      case "replace" -> Transition.REPLACE;
      default -> null;
    };
  }

  /**
   * {@inheritDoc}
   *
   * <p>Converts each enum value to their string versions
   *
   * @return The string version of each enum
   */
  @Override
  public String toString() {
    return switch (this) {
      case FADE -> "fade";
      case SLIDE -> "slide";
      case POP -> "pop";
      case REPLACE -> "replace";
    };
  }

  /**
   * Converts each element to its string version to be returned as a list of strings.
   *
   * @return A list of strings containing the string values for each enum element
   */
  public static List<String> displayNames() {
    List<String> list = new ArrayList<>();
    for (Transition transition : values()) {
      String string = transition.toString();
      list.add(string);
    }
    return list;
  }
}
