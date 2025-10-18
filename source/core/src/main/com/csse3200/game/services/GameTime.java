package com.csse3200.game.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Controls the game time */
public class GameTime {
  private static Logger logger = LoggerFactory.getLogger(GameTime.class);
  private final long startTime;
  private float timeScale = 1f;

  /** Constructor for the GameTime class. */
  public GameTime() {
    startTime = TimeUtils.millis();
    logger.debug("Setting game start time to {}", startTime);
  }

  /**
   * Set the speed of time passing. This affects getDeltaTime()
   *
   * @param timeScale Time scale, where normal speed is 1.0, no time passing is 0.0
   */
  public void setTimeScale(float timeScale) {
    float clamped = Math.max(0f, timeScale);
    if (clamped != timeScale) {
      logger.warn("Requested time scale {} below zero. Clamping to {}", timeScale, clamped);
    }
    logger.debug("Setting time scale to {}", clamped);
    this.timeScale = clamped;
  }

  /**
   * Get the current timescale applied to delta calculations. A value of 1.0 represents normal
   * speed, while 0.0 represents a frozen game.
   *
   * @return the timescale in effect.
   */
  public float getTimeScale() {
    return timeScale;
  }

  /**
   * Get the time since the last frame in seconds, scaled by time scale
   *
   * @return time passed since the last frame in seconds, scaled by time scale.
   */
  public float getDeltaTime() {
    return Gdx.graphics.getDeltaTime() * timeScale;
  }

  /**
   * Get the raw time since the last frame in seconds
   *
   * @return time passed since the last frame in seconds, not affected by time scale.
   */
  public float getRawDeltaTime() {
    return Gdx.graphics.getDeltaTime();
  }

  /**
   * Get the time since the game started in milliseconds
   *
   * @return time passed since the game started in milliseconds
   */
  public long getTime() {
    return TimeUtils.timeSinceMillis(startTime);
  }

  /**
   * Get the time since the last time in milliseconds
   *
   * @param lastTime the time to get the time since
   * @return the time since the last time
   */
  public long getTimeSince(long lastTime) {
    return getTime() - lastTime;
  }
}
