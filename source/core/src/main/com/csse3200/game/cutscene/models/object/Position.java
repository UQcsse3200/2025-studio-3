package com.csse3200.game.cutscene.models.object;

/** The different positions a character can be displayed on screen. */
public enum Position {
  /** Renders default image on the left side of the screen */
  LEFT,

  /** Renders flipped image on the right side of the screen */
  RIGHT;

  /**
   * Maps JSON values {@code "left"|"right"} to their {@code Position} counterparts. Default to null
   * because invalid options are caught in validation checker.
   *
   * @param value - String value to be converted
   * @return {@code Position} value corresponding to the input string
   */
  public static Position fromString(String value) {
    return switch (value) {
      case "left" -> Position.LEFT;
      case "right" -> Position.RIGHT;
      default -> null;
    };
  }
}
