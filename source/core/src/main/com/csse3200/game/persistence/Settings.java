package com.csse3200.game.persistence;

import java.util.EnumMap;
import com.badlogic.gdx.Graphics.Monitor;

public class Settings {
  /**
   * Display mode of the game.
   */
  enum Mode {
    /** Windowed display mode. */
    WINDOWED,
    /** Fullscreen display mode. */
    FULLSCREEN,
    /** Windowed Borderless display mode. */
    BORDERLESS
  }

  /**
   * Difficulty of the game.
   */
  enum Difficulty {
    /** Easy difficulty. */
    EASY,
    /** Normal difficulty. */
    NORMAL,
    /** Hard difficulty. */
    HARD
  }

  /*
   * Display settings of the game.
   */
  public static final String OPERATING_SYSTEM = System.getProperty("os.name");
  private EnumMap<Mode, String> availableModes = new EnumMap<Mode, String>(Mode.class);
  private Monitor[] availableMonitors;
  private Monitor currentMonitor;
  private boolean vsync;
  private Mode mode;
  private int width;
  private int height;
  private int refreshRate;
  private int fps;
  private float uiScale;


  public Settings() {
    this.width = 1280;
    this.height = 800;
    this.refreshRate = 60;


    availableModes.put(Mode.WINDOWED, "Windowed");
    availableModes.put(Mode.FULLSCREEN, "Fullscreen");
    if (Settings.OPERATING_SYSTEM.startsWith("Windows")) {
      availableModes.put(Mode.BORDERLESS, "Borderless");
    }
  }

  
}

