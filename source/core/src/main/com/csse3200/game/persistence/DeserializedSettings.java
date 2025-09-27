package com.csse3200.game.persistence;

import net.dermetfan.utils.Pair;

/** DeserializedSettings is a class that is used to deserialize a user's settings. */
public class DeserializedSettings {
  private float musicVolume;
  private float soundVolume;
  private float voiceVolume;
  private float masterVolume;
  private Settings.Difficulty difficulty;
  private int pauseButton;
  private int skipButton;
  private int interactionButton;
  private int upButton;
  private int downButton;
  private int leftButton;
  private int rightButton;
  private Settings.UIScale currentUIScale;
  private Settings.Quality quality;
  private Settings.Mode currentMode;
  private int fps;
  private boolean vsync;
  private int windowedResolutionWidth;
  private int windowedResolutionHeight;

  /** Constructor for the DeserializedSettings class. */
  public DeserializedSettings() {
    // Default settings
  }

  /**
   * Constructor for the DeserializedSettings class.
   *
   * @param settings the settings to deserialize.
   */
  public DeserializedSettings(Settings settings) {
    this.musicVolume = settings.getMusicVolume();
    this.soundVolume = settings.getSoundVolume();
    this.voiceVolume = settings.getVoiceVolume();
    this.masterVolume = settings.getMasterVolume();
    this.difficulty = settings.getDifficulty();
    this.pauseButton = settings.getPauseButton();
    this.skipButton = settings.getSkipButton();
    this.interactionButton = settings.getInteractionButton();
    this.upButton = settings.getUpButton();
    this.downButton = settings.getDownButton();
    this.leftButton = settings.getLeftButton();
    this.rightButton = settings.getRightButton();
    this.currentUIScale = settings.getCurrentUIScale();
    this.quality = settings.getQuality();
    this.currentMode = settings.getCurrentMode();
    this.fps = settings.getFps();
    this.vsync = settings.isVsync();
    Pair<Integer, Integer> windowedRes = settings.getWindowedResolution();
    this.windowedResolutionWidth = windowedRes.getKey();
    this.windowedResolutionHeight = windowedRes.getValue();
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
  public Settings.Difficulty getDifficulty() {
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

  /**
   * Gets the current UI scale.
   *
   * @return the current UI scale.
   */
  public Settings.UIScale getCurrentUIScale() {
    return currentUIScale;
  }

  /**
   * Gets the current quality.
   *
   * @return the current quality.
   */
  public Settings.Quality getQuality() {
    return quality;
  }

  /**
   * Gets the current mode.
   *
   * @return the current mode.
   */
  public Settings.Mode getCurrentMode() {
    return currentMode;
  }

  /**
   * Gets the FPS.
   *
   * @return the FPS.
   */
  public int getFps() {
    return fps;
  }

  /**
   * Gets whether vsync is enabled.
   *
   * @return whether vsync is enabled.
   */
  public boolean isVsync() {
    return vsync;
  }

  /**
   * Gets the windowed resolution.
   *
   * @return the windowed resolution.
   */
  public Pair<Integer, Integer> getWindowedResolution() {
    return new Pair<>(windowedResolutionWidth, windowedResolutionHeight);
  }

  /**
   * Sets the music volume.
   *
   * @param musicVolume the music volume.
   */
  public void setMusicVolume(float musicVolume) {
    this.musicVolume = musicVolume;
  }

  /**
   * Sets the sound volume.
   *
   * @param soundVolume the sound volume.
   */
  public void setSoundVolume(float soundVolume) {
    this.soundVolume = soundVolume;
  }

  /**
   * Sets the voice volume.
   *
   * @param voiceVolume the voice volume.
   */
  public void setVoiceVolume(float voiceVolume) {
    this.voiceVolume = voiceVolume;
  }

  /**
   * Sets the master volume.
   *
   * @param masterVolume the master volume.
   */
  public void setMasterVolume(float masterVolume) {
    this.masterVolume = masterVolume;
  }

  /**
   * Sets the difficulty.
   *
   * @param difficulty the difficulty.
   */
  public void setDifficulty(Settings.Difficulty difficulty) {
    this.difficulty = difficulty;
  }

  /**
   * Sets the key assigned to the pause button.
   *
   * @param pauseButton the key assigned to the pause button.
   */
  public void setPauseButton(int pauseButton) {
    this.pauseButton = pauseButton;
  }

  /**
   * Sets the key assigned to the skip button.
   *
   * @param skipButton the key assigned to the skip button.
   */
  public void setSkipButton(int skipButton) {
    this.skipButton = skipButton;
  }

  /**
   * Sets the key assigned to the interaction button.
   *
   * @param interactionButton the key assigned to the interaction button.
   */
  public void setInteractionButton(int interactionButton) {
    this.interactionButton = interactionButton;
  }

  /**
   * Sets the key assigned to the up button.
   *
   * @param upButton the key assigned to the up button.
   */
  public void setUpButton(int upButton) {
    this.upButton = upButton;
  }

  /**
   * Sets the key assigned to the down button.
   *
   * @param downButton the key assigned to the down button.
   */
  public void setDownButton(int downButton) {
    this.downButton = downButton;
  }

  /**
   * Sets the key assigned to the left button.
   *
   * @param leftButton the key assigned to the left button.
   */
  public void setLeftButton(int leftButton) {
    this.leftButton = leftButton;
  }

  /**
   * Sets the key assigned to the right button.
   *
   * @param rightButton the key assigned to the right button.
   */
  public void setRightButton(int rightButton) {
    this.rightButton = rightButton;
  }

  /**
   * Sets the current UI scale.
   *
   * @param currentUIScale the current UI scale.
   */
  public void setCurrentUIScale(Settings.UIScale currentUIScale) {
    this.currentUIScale = currentUIScale;
  }

  /**
   * Sets the current quality.
   *
   * @param quality the current quality.
   */
  public void setQuality(Settings.Quality quality) {
    this.quality = quality;
  }

  /**
   * Sets the current mode.
   *
   * @param currentMode the current mode.
   */
  public void setCurrentMode(Settings.Mode currentMode) {
    this.currentMode = currentMode;
  }

  /**
   * Sets the FPS.
   *
   * @param fps the FPS.
   */
  public void setFps(int fps) {
    this.fps = fps;
  }

  /**
   * Sets whether vsync is enabled.
   *
   * @param vsync whether vsync is enabled.
   */
  public void setVsync(boolean vsync) {
    this.vsync = vsync;
  }

  /**
   * Sets the windowed resolution.
   *
   * @param windowedResolution the windowed resolution.
   */
  public void setWindowedResolution(Pair<Integer, Integer> windowedResolution) {
    this.windowedResolutionWidth = windowedResolution.getKey();
    this.windowedResolutionHeight = windowedResolution.getValue();
  }
}
