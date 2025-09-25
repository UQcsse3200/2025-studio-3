package com.csse3200.game.persistence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Graphics.Monitor;
import com.badlogic.gdx.Input;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.dermetfan.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Settings {
  private static final Logger logger = LoggerFactory.getLogger(Settings.class);
  public static final String OPERATING_SYSTEM = System.getProperty("os.name");

  /** Display mode of the game. */
  public enum Mode {
    /** Windowed display mode. */
    WINDOWED,
    /** Fullscreen display mode. */
    FULLSCREEN,
    /** Windowed Borderless display mode. */
    BORDERLESS
  }

  /** Difficulty of the game. */
  public enum Difficulty {
    /** Easy difficulty. */
    EASY,
    /** Normal difficulty. */
    NORMAL,
    /** Hard difficulty. */
    HARD
  }

  /** Quality settings of the game. */
  public enum Quality {
    /** Low quality. */
    LOW,
    /** High quality. */
    HIGH
  }

  /** UI scale settings of the game. */
  public enum UIScale {
    /** Small UI scale. */
    SMALL,
    /** Medium UI scale. */
    MEDIUM,
    /** Large UI scale. */
    LARGE
  }

  /** Resolutions of the game. */
  static final List<Pair<Integer, Integer>> RESOLUTIONS =
      Arrays.asList(
          new Pair<>(3840, 2160),
          new Pair<>(2560, 1440),
          new Pair<>(1920, 1080),
          new Pair<>(1600, 900),
          new Pair<>(1366, 768),
          new Pair<>(1280, 720));

  /*
   * Display settings of the game.
   */
  private Monitor[] availableMonitors;
  private Monitor currentMonitor;
  private EnumMap<Mode, String> availableModes = new EnumMap<>(Mode.class);
  private Mode currentMode;
  private List<Pair<Integer, Integer>> availableResolutions = new ArrayList<>();
  private Pair<Integer, Integer> currentResolution;
  private Pair<Integer, Integer> windowedResolution;
  private boolean vsync;
  private int refreshRate;
  private int fps;
  private UIScale currentUIScale;
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
   * <p>Initializes the display settings to default settings.
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
    currentMode = Mode.FULLSCREEN;
    availableMonitors = Gdx.graphics.getMonitors();
    currentMonitor = Gdx.graphics.getPrimaryMonitor();
    DisplayMode displayMode = Gdx.graphics.getDisplayMode(currentMonitor);
    Gdx.graphics.setFullscreenMode(displayMode);
    currentResolution = new Pair<>(displayMode.width, displayMode.height);
    windowedResolution = new Pair<>(0, 0);
    for (Pair<Integer, Integer> resolution : RESOLUTIONS) {
      if (displayMode.width >= resolution.getKey() && displayMode.height >= resolution.getValue()) {
        availableResolutions.add(resolution);
        if (resolution.getKey() > windowedResolution.getKey()
            && resolution.getValue() > windowedResolution.getValue()) {
          windowedResolution = resolution;
        }
      }
    }
    refreshRate = displayMode.refreshRate;
    fps = displayMode.refreshRate;
    vsync = false;
    currentUIScale = UIScale.MEDIUM;
    quality = Quality.HIGH;
  }

  /**
   * Constructor for the Settings class, used for saved settings.
   *
   * @param deserializedSettings the deserialized settings.
   */
  public Settings(DeserializedSettings deserializedSettings) {
    // Volume settings
    this.soundVolume = deserializedSettings.getSoundVolume();
    this.voiceVolume = deserializedSettings.getVoiceVolume();
    this.musicVolume = deserializedSettings.getMusicVolume();
    this.masterVolume = deserializedSettings.getMasterVolume();

    // Gameplay settings
    this.difficulty = deserializedSettings.getDifficulty();
    this.pauseButton = deserializedSettings.getPauseButton();
    this.skipButton = deserializedSettings.getSkipButton();
    this.interactionButton = deserializedSettings.getInteractionButton();
    this.upButton = deserializedSettings.getUpButton();
    this.downButton = deserializedSettings.getDownButton();
    this.leftButton = deserializedSettings.getLeftButton();
    this.rightButton = deserializedSettings.getRightButton();

    // Display settings
    availableModes.put(Mode.WINDOWED, "Windowed");
    availableModes.put(Mode.FULLSCREEN, "Fullscreen");
    if (OPERATING_SYSTEM.startsWith("Windows")) {
      availableModes.put(Mode.BORDERLESS, "Windowed Borderless");
    }
    this.currentMode = deserializedSettings.getCurrentMode();
    availableMonitors = Gdx.graphics.getMonitors();
    currentMonitor = Gdx.graphics.getPrimaryMonitor();
    DisplayMode displayMode = Gdx.graphics.getDisplayMode(currentMonitor);
    currentResolution = new Pair<>(displayMode.width, displayMode.height);
    for (Pair<Integer, Integer> resolution : RESOLUTIONS) {
      if (displayMode.width >= resolution.getKey() && displayMode.height >= resolution.getValue()) {
        availableResolutions.add(resolution);
      }
    }
    this.refreshRate = displayMode.refreshRate;
    this.fps =
        deserializedSettings.getFps() > displayMode.refreshRate
            ? displayMode.refreshRate
            : deserializedSettings.getFps();
    this.vsync = deserializedSettings.isVsync();
    this.currentUIScale = deserializedSettings.getCurrentUIScale();
    this.quality = deserializedSettings.getQuality();
  }

  /**
   * Gets the sound volume.
   *
   * @return the sound volume.
   */
  public float getSoundVolume() {
    return soundVolume;
  }

  /**
   * Gets the voice volume.
   *
   * @return the voice volume.
   */
  public float getVoiceVolume() {
    return voiceVolume;
  }

  /**
   * Gets the music volume.
   *
   * @return the music volume.
   */
  public float getMusicVolume() {
    return musicVolume;
  }

  /**
   * Gets the master volume.
   *
   * @return the master volume.
   */
  public float getMasterVolume() {
    return masterVolume;
  }

  /**
   * Gets the difficulty.
   *
   * @return the difficulty.
   */
  public Difficulty getDifficulty() {
    return difficulty;
  }

  /**
   * Gets the key assigned to the pause button.
   *
   * @return the pause button.
   */
  public int getPauseButton() {
    return pauseButton;
  }

  /**
   * Gets the key assigned to the skip button.
   *
   * @return the skip button.
   */
  public int getSkipButton() {
    return skipButton;
  }

  /**
   * Gets the key assigned to the interaction button.
   *
   * @return the interaction button.
   */
  public int getInteractionButton() {
    return interactionButton;
  }

  /**
   * Gets the key assigned to the up button.
   *
   * @return the up button.
   */
  public int getUpButton() {
    return upButton;
  }

  /**
   * Gets the key assigned to the down button.
   *
   * @return the down button.
   */
  public int getDownButton() {
    return downButton;
  }

  /**
   * Gets the key assigned to the left button.
   *
   * @return the left button.
   */
  public int getLeftButton() {
    return leftButton;
  }

  /**
   * Gets the key assigned to the right button.
   *
   * @return the right button.
   */
  public int getRightButton() {
    return rightButton;
  }

  public List<Pair<Integer, Integer>> getAvailableResolutions() {
    return availableResolutions;
  }

  public Pair<Integer, Integer> getCurrentResolution() {
    return currentResolution;
  }

  public int getRefreshRate() {
    return refreshRate;
  }

  public int getFps() {
    return fps;
  }

  public boolean isVsync() {
    return vsync;
  }

  public UIScale getCurrentUIScale() {
    return currentUIScale;
  }

  public Quality getQuality() {
    return quality;
  }

  public Map<Mode, String> getAvailableModes() {
    return availableModes;
  }

  public Monitor[] getAvailableMonitors() {
    return availableMonitors;
  }

  public Monitor getCurrentMonitor() {
    return currentMonitor;
  }

  public Mode getCurrentMode() {
    return currentMode;
  }

  public void setSoundVolume(float soundVolume) {
    this.soundVolume = soundVolume;
  }

  public void setVoiceVolume(float voiceVolume) {
    this.voiceVolume = voiceVolume;
  }

  public void setMusicVolume(float musicVolume) {
    this.musicVolume = musicVolume;
  }

  public void setMasterVolume(float masterVolume) {
    this.masterVolume = masterVolume;
  }

  public void setDifficulty(Difficulty difficulty) {
    this.difficulty = difficulty;
  }

  public void setPauseButton(int pauseButton) {
    this.pauseButton = pauseButton;
  }

  public void setSkipButton(int skipButton) {
    this.skipButton = skipButton;
  }

  public void setInteractionButton(int interactionButton) {
    this.interactionButton = interactionButton;
  }

  public void setUpButton(int upButton) {
    this.upButton = upButton;
  }

  public void setDownButton(int downButton) {
    this.downButton = downButton;
  }

  public void setLeftButton(int leftButton) {
    this.leftButton = leftButton;
  }

  public void setRightButton(int rightButton) {
    this.rightButton = rightButton;
  }

  public Pair<Integer, Integer> getWindowedResolution() {
    return windowedResolution;
  }

  public void setCurrentResolution(Pair<Integer, Integer> currentResolution) {
    this.currentResolution = currentResolution;
    if (currentResolution.equals(windowedResolution)) {
      return;
    }
    this.windowedResolution = new Pair<>(0, 0);
    for (Pair<Integer, Integer> resolution : RESOLUTIONS) {
      if (currentResolution.getKey() >= resolution.getKey()
          && currentResolution.getValue() >= resolution.getValue()
          && currentResolution.getKey() > windowedResolution.getKey()
          && currentResolution.getValue() > windowedResolution.getValue()) {
        windowedResolution = resolution;
      }
    }
  }

  public void setRefreshRate(int refreshRate) {
    this.refreshRate = refreshRate;
  }

  public void setFps(int fps) {
    if (fps > refreshRate) {
      logger.warn("FPS is greater than refresh rate, setting FPS to refresh rate");
      fps = refreshRate;
    }
    this.fps = fps;
  }

  public void setVsync(boolean vsync) {
    this.vsync = vsync;
  }

  public void setCurrentUIScale(UIScale currentUIScale) {
    this.currentUIScale = currentUIScale;
  }

  public void setQuality(Quality quality) {
    this.quality = quality;
  }

  public void setCurrentMode(Mode currentMode) {
    this.currentMode = currentMode;
  }

  public void setCurrentMonitor(Monitor currentMonitor) {
    this.currentMonitor = currentMonitor;
  }

  public void setAvailableResolutions() {
    this.availableResolutions.clear();
    for (Pair<Integer, Integer> resolution : RESOLUTIONS) {
      if (currentResolution.getKey() >= resolution.getKey()
          && currentResolution.getValue() >= resolution.getValue()) {
        availableResolutions.add(resolution);
      }
    }
  }

  public String toString() {
    return "\n"
        + "Settings"
        + "\n"
        + "--------"
        + "\n"
        + "soundVolume = "
        + soundVolume
        + "\n"
        + "voiceVolume = "
        + voiceVolume
        + "\n"
        + "musicVolume = "
        + musicVolume
        + "\n"
        + "masterVolume = "
        + masterVolume
        + "\n"
        + "difficulty = "
        + difficulty
        + "\n"
        + "pauseButton = "
        + Input.Keys.toString(pauseButton)
        + "\n"
        + "skipButton = "
        + Input.Keys.toString(skipButton)
        + "\n"
        + "interactionButton = "
        + Input.Keys.toString(interactionButton)
        + "\n"
        + "upButton = "
        + Input.Keys.toString(upButton)
        + "\n"
        + "downButton = "
        + Input.Keys.toString(downButton)
        + "\n"
        + "leftButton = "
        + Input.Keys.toString(leftButton)
        + "\n"
        + "rightButton = "
        + Input.Keys.toString(rightButton)
        + "\n"
        + "currentResolution = "
        + currentResolution.toString().replace("[", "").replace(" & ", "x").replace("]", "")
        + "\n"
        + "refreshRate = "
        + refreshRate
        + "\n"
        + "fps = "
        + fps
        + "\n"
        + "vsync = "
        + vsync
        + "\n"
        + "currentUIScale = "
        + currentUIScale
        + "\n"
        + "quality = "
        + quality
        + "\n"
        + "currentMode = "
        + currentMode
        + "\n"
        + "currentMonitor = "
        + currentMonitor.name
        + "\n"
        + "--------";
  }
}
