package com.csse3200.game.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Graphics.Monitor;
import com.csse3200.game.persistence.DeserializedSettings;
import com.csse3200.game.persistence.FileLoader;
import com.csse3200.game.persistence.Settings;
import com.csse3200.game.utils.EnvironmentUtils;
import java.io.File;
import net.dermetfan.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Makes the settings into a service available throughout the game, instead of a singleton. */
public class SettingsService {
  private static final Logger logger = LoggerFactory.getLogger(SettingsService.class);
  private Settings settings;
  private static final String PATH = "The Day We Fought Back" + File.separator + "settings.json";

  /** Constructor for the SettingsService. */
  public SettingsService() {
    logger.info("[SettingsService] SettingsService created");
    DeserializedSettings deserializedSettings =
        FileLoader.readClass(DeserializedSettings.class, PATH, FileLoader.Location.EXTERNAL);
    settings = deserializedSettings != null ? new Settings(deserializedSettings) : new Settings();
    Gdx.graphics.setForegroundFPS(settings.getFps());
    Gdx.graphics.setVSync(settings.isVsync());
    changeDisplayMode(settings.getCurrentMode());
    saveSettings();
    logger.info("[SettingsService] SettingsService initialized");
    String settingsString = settings.toString();
    logger.info(settingsString);
  }

  /**
   * Get the settings.
   *
   * @return the settings.
   */
  public Settings getSettings() {
    return settings;
  }

  /** Change the display mode. */
  public void changeDisplayMode(Settings.Mode mode) {
    Gdx.graphics.setResizable(false);
    DisplayMode displayMode;
    switch (mode) {
      case FULLSCREEN:
        settings.setCurrentMode(Settings.Mode.FULLSCREEN);
        displayMode = Gdx.graphics.getDisplayMode(settings.getCurrentMonitor());
        Gdx.graphics.setFullscreenMode(displayMode);
        settings.setCurrentResolution(
            EnvironmentUtils.getResolutionFromDisplayMode(settings.getCurrentMonitor()));
        break;
      case BORDERLESS:
        settings.setCurrentMode(Settings.Mode.BORDERLESS);
        Gdx.graphics.setUndecorated(true);
        Gdx.graphics.setResizable(false);
        displayMode = Gdx.graphics.getDisplayMode(settings.getCurrentMonitor());
        Gdx.graphics.setWindowedMode(displayMode.width, displayMode.height);
        settings.setCurrentResolution(
            EnvironmentUtils.getResolutionFromDisplayMode(settings.getCurrentMonitor()));
        break;
      case WINDOWED:
        settings.setCurrentMode(Settings.Mode.WINDOWED);
        Gdx.graphics.setWindowedMode(
            settings.getWindowedResolution().getKey(), settings.getWindowedResolution().getValue());
        settings.setCurrentResolution(
            EnvironmentUtils.getResolutionFromDisplayMode(settings.getCurrentMonitor()));
        break;
      default:
        logger.warn("[SettingsService] Invalid display mode");
        break;
    }
  }

  /**
   * Change the display settings.
   *
   * @param fps the FPS to set.
   * @param vsync the VSync to set.
   * @param uiScale the UI Scale to set.
   * @param quality the Quality to set.
   */
  public void changeDisplaySettings(
      int fps, boolean vsync, Settings.UIScale uiScale, Settings.Quality quality) {
    if (fps < 30 || fps > 240) {
      logger.warn("[SettingsService] FPS must be between 30 and 240");
      return;
    }
    settings.setFps(fps);
    settings.setVsync(vsync);
    settings.setCurrentUIScale(uiScale);
    settings.setQuality(quality);
    Gdx.graphics.setForegroundFPS(fps);
    Gdx.graphics.setVSync(vsync);
  }

  /**
   * Switch to a different monitor.
   *
   * @param monitor the monitor to switch to.
   */
  public void switchMonitor(Monitor monitor) {
    DisplayMode[] displayMode = Gdx.graphics.getDisplayModes(monitor);
    Gdx.graphics.setFullscreenMode(displayMode[0]);
    settings.setCurrentResolution(new Pair<>(displayMode[0].width, displayMode[0].height));
    settings.setAvailableResolutions();
    settings.setCurrentMonitor(monitor);
    settings.setCurrentMode(Settings.Mode.FULLSCREEN);
    settings.setRefreshRate(displayMode[0].refreshRate);
  }

  /**
   * Switch to a different resolution if in windowed mode.
   *
   * @param resolution the resolution to switch to.
   */
  public void switchResolution(Pair<Integer, Integer> resolution) {
    if (settings.getCurrentMode() != Settings.Mode.WINDOWED) {
      logger.warn("[SettingsService] Cannot switch resolution in fullscreen mode");
    } else {
      settings.setCurrentResolution(resolution);
      settings.setWindowedResolution(resolution);
      Gdx.graphics.setWindowedMode(resolution.getKey(), resolution.getValue());
    }
  }

  /**
   * Change the audio settings.
   *
   * @param musicVolume the music volume to set.
   * @param soundVolume the sound volume to set.
   * @param voiceVolume the voice volume to set.
   * @param masterVolume the master volume to set.
   */
  public void changeAudioSettings(
      float musicVolume, float soundVolume, float voiceVolume, float masterVolume) {
    if (musicVolume < 0 || musicVolume > 1) {
      logger.warn("[SettingsService] Music volume must be between 0 and 1");
      return;
    }
    if (soundVolume < 0 || soundVolume > 1) {
      logger.warn("[SettingsService] Sound volume must be between 0 and 1");
      return;
    }
    if (voiceVolume < 0 || voiceVolume > 1) {
      logger.warn("[SettingsService] Voice volume must be between 0 and 1");
      return;
    }
    if (masterVolume < 0 || masterVolume > 1) {
      logger.warn("[SettingsService] Master volume must be between 0 and 1");
      return;
    }
    settings.setMusicVolume(musicVolume);
    settings.setSoundVolume(soundVolume);
    settings.setVoiceVolume(voiceVolume);
    settings.setMasterVolume(masterVolume);
  }

  /**
   * Change the keybinds.
   *
   * @param pauseButton the pause button to set.
   * @param skipButton the skip button to set.
   * @param interactionButton the interaction button to set.
   * @param upButton the up button to set.
   * @param downButton the down button to set.
   * @param leftButton the left button to set.
   * @param rightButton the right button to set.
   */
  public void changeKeybinds(
      int pauseButton,
      int skipButton,
      int interactionButton,
      int upButton,
      int downButton,
      int leftButton,
      int rightButton) {
    settings.setPauseButton(pauseButton);
    settings.setSkipButton(skipButton);
    settings.setInteractionButton(interactionButton);
    settings.setUpButton(upButton);
    settings.setDownButton(downButton);
    settings.setLeftButton(leftButton);
    settings.setRightButton(rightButton);
  }

  /**
   * Get the sound volume.
   *
   * @return the sound volume.
   */
  public float getSoundVolume() {
    return settings.getSoundVolume() * settings.getMasterVolume();
  }

  /**
   * Get the voice volume.
   *
   * @return the voice volume.
   */
  public float getVoiceVolume() {
    return settings.getVoiceVolume() * settings.getMasterVolume();
  }

  /**
   * Get the music volume.
   *
   * @return the music volume.
   */
  public float getMusicVolume() {
    return settings.getMusicVolume() * settings.getMasterVolume();
  }

  /** Save the settings. */
  public void saveSettings() {
    DeserializedSettings deserializedSettings = new DeserializedSettings(settings);
    FileLoader.writeClass(deserializedSettings, PATH, FileLoader.Location.EXTERNAL);
  }
}
