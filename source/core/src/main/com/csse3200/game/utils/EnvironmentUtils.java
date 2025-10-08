package com.csse3200.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Graphics.Monitor;
import net.dermetfan.utils.Pair;

/** Utility class for getting the operating system. */
public class EnvironmentUtils {
  /** Private constructor to prevent instantiation. */
  private EnvironmentUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Get the operating system.
   *
   * @return the operating system
   */
  public static String getOperatingSystem() {
    return System.getProperty("os.name");
  }

  /**
   * Gets the resolution from a specified monitor's display mode.
   *
   * @param monitor the monitor to get the resolution from
   * @return the resolution as a Pair of width and height.
   */
  public static Pair<Integer, Integer> getResolutionFromDisplayMode(Monitor monitor) {
    DisplayMode displayMode = Gdx.graphics.getDisplayMode(monitor);
    return new Pair<>(displayMode.width, displayMode.height);
  }

  /**
   * Gets the refresh rate from a specified monitor's display mode.
   *
   * @param monitor the monitor to get the refresh rate from
   * @return the refresh rate.
   */
  public static int getRefreshRateFromDisplayMode(Monitor monitor) {
    DisplayMode displayMode = Gdx.graphics.getDisplayMode(monitor);
    return displayMode.refreshRate;
  }
}
