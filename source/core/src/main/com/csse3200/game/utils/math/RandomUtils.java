package com.csse3200.game.utils.math;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Utility class for generating random numbers and vectors.
 */
public class RandomUtils {
  
  /**
   * Generates a random Vector2 between two given vectors.
   *
   * @param start The starting vector
   * @param end The ending vector
   * @return A random Vector2 between the two given vectors
   */
  public static Vector2 random(Vector2 start, Vector2 end) {
    return new Vector2(MathUtils.random(start.x, end.x), MathUtils.random(start.y, end.y));
  }

  /**
   * Generates a random GridPoint2 between two given GridPoint2s.
   *
   * @param start The starting GridPoint2
   * @param end The ending GridPoint2
   * @return A random GridPoint2 between the two given GridPoint2s
   */
  public static GridPoint2 random(GridPoint2 start, GridPoint2 end) {
    return new GridPoint2(MathUtils.random(start.x, end.x), MathUtils.random(start.y, end.y));
  }

  /**
   * Constructor for the RandomUtils class.
   */
  private RandomUtils() {
    throw new IllegalStateException("Instantiating static util class");
  }
}
