package com.csse3200.game.utils.math;

import com.badlogic.gdx.math.GridPoint2;

/** Contains additional utility constants and functions for common GridPoint2 operations. */
public class GridPoint2Utils {
  public static final GridPoint2 ZERO = new GridPoint2(0, 0);
  
  /**
   * Constructor for the GridPoint2Utils class.
   */
  private GridPoint2Utils() {
    throw new IllegalStateException("Instantiating static util class");
  }
}
