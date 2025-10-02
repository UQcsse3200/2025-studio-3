package com.csse3200.game.services;

import com.badlogic.gdx.audio.Music;

/**
 * MusicService manages background music playback across the game. It ensures only one track is
 * playing at a time, avoids overlapping, and keeps track of the currently playing track.
 */
public class MusicService {
  private Music currentMusic;
  private String currentPath;

  public MusicService() {
    // Default constructor, resources are managed at play/stop time
  }

  /**
   * Plays the specified music track. If the requested track is already playing, nothing happens.
   * Otherwise, it stops any previously playing music and starts the new one.
   */
  public void play(String path) {
    if (currentMusic != null && path.equals(currentPath)) {
      return; // Already playing this track
    }

    stop(); // Stop and dispose previous track if any

    float volume = ServiceLocator.getSettingsService().getMusicVolume();
    ResourceService resourceService = ServiceLocator.getResourceService();
    resourceService.loadMusic(new String[] {path});
    resourceService.loadAll();
    currentMusic = resourceService.getAsset(path, Music.class);
    currentMusic.setLooping(true);
    currentMusic.setVolume(volume);
    currentMusic.play();
    currentPath = path;
  }

  /** Pauses the currently playing music, if any. */
  public void pause() {
    if (currentMusic != null && currentMusic.isPlaying()) {
      currentMusic.pause();
    }
  }

  /** Resumes the currently paused music, if any. */
  public void resume() {
    if (currentMusic != null && !currentMusic.isPlaying()) {
      currentMusic.play();
    }
  }

  /** Stops the currently playing music and disposes of it. */
  public void stop() {
    if (currentMusic != null) {
      currentMusic.stop();
      currentMusic.dispose();
      currentMusic = null;
      currentPath = null;
    }
  }

  /** Returns true if a music track is currently playing. */
  public boolean isPlaying() {
    return currentMusic != null && currentMusic.isPlaying();
  }

  /**
   * Updates the volume of the currently playing music to match the current settings.
   */
  public void updateVolume(float volume) {
    if (currentMusic != null) {
      currentMusic.setVolume(volume);
    }
  }
}
