package com.csse3200.game.persistence;

import java.util.EnumMap;
import java.util.List;
import java.util.Arrays;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Graphics.Monitor;

import net.dermetfan.utils.Pair;

public class Settings {
  public static final String OPERATING_SYSTEM = System.getProperty("os.name");

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

  /**
   * Quality settings of the game.
   */
  enum Quality {
    /** Low quality. */
    LOW,
    /** High quality. */
    HIGH
  }

  /**
   * UI scale settings of the game.
   */
  enum UIScale {
    /** Small UI scale. */
    SMALL,
    /** Medium UI scale. */
    MEDIUM,
    /** Large UI scale. */
    LARGE
  }

  static final List<Pair<Integer, Integer>> RESOLUTIONS = Arrays.asList(
    new Pair<>(3840, 2160),
    new Pair<>(2560, 1440),
    new Pair<>(1920, 1080),
    new Pair<>(1600, 900),
    new Pair<>(1366, 768),
    new Pair<>(1280, 720)
  );

  /*
   * Display settings of the game.
   */
  private Monitor[] availableMonitors;
  private Monitor currentMonitor;
  private EnumMap<Mode, String> availableModes = new EnumMap<>(Mode.class);
  private Mode currentMode;
  private List<Pair<Integer, Integer>> availableResolutions;
  private Pair<Integer, Integer> currentResolution;
  private boolean vsync;
  private int refreshRate;
  private int fps;
  private UIScale currentUIScale;
  private EnumMap<UIScale, String> availableUIScales = new EnumMap<>(UIScale.class);
  private Quality quality;

  /*
   * Gameplay settings of the game.
   */
  private Difficulty difficulty;
  private int pauseButton;
  private int skipButton;
  private int interactionButton;
  private int upButton;
  private int downButton;
  private int leftButton;
  private int rightButton;

  /*
   * Audio settings of the game.
   */
  private float musicVolume;
  private float soundVolume;
  private float voiceVolume;
  private float masterVolume;

  /**
   * Constructor for the Settings class.
   * 
   * Initializes the display settings to default settings.
   */
  public Settings() {
    // Volume settings
    musicVolume = 1.0f;
    soundVolume = 1.0f;
    voiceVolume = 1.0f;
    masterVolume = 1.0f;

    // Gameplay settings
    difficulty = Difficulty.NORMAL;
    pauseButton = Input.Keys.ESCAPE;
    skipButton = Input.Keys.SPACE;
    interactionButton = Input.Keys.E;
    upButton = Input.Keys.W;
    downButton = Input.Keys.S;
    leftButton = Input.Keys.A;
    rightButton = Input.Keys.D;
    
    // Display settings
    availableModes.put(Mode.WINDOWED, "Windowed");
    availableModes.put(Mode.FULLSCREEN, "Fullscreen");
    if (OPERATING_SYSTEM.startsWith("Windows")) {
      availableModes.put(Mode.BORDERLESS, "Windowed Borderless");
    }
    currentMode = Mode.WINDOWED;
    availableMonitors = Gdx.graphics.getMonitors();
    currentMonitor = Gdx.graphics.getPrimaryMonitor();
    DisplayMode displayMode = Gdx.graphics.getDisplayMode(currentMonitor);
    currentResolution = new Pair<>(displayMode.width, displayMode.height);
    for (Pair<Integer, Integer> resolution : RESOLUTIONS) {
      if (displayMode.width >= resolution.getKey() && displayMode.height >= resolution.getValue()) {
        availableResolutions.add(resolution);
      }
    }
    refreshRate = displayMode.refreshRate;
    fps = displayMode.refreshRate;
    vsync = false;
    currentUIScale = UIScale.MEDIUM;
    availableUIScales.put(UIScale.SMALL, "Small");
    availableUIScales.put(UIScale.MEDIUM, "Medium");
    availableUIScales.put(UIScale.LARGE, "Large");
    quality = Quality.HIGH;
  }


}

