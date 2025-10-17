package com.csse3200.game.services;

import com.badlogic.gdx.utils.Timer;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Coordinates pausing behaviour across the game. Callers register {@link FreezeReason}s when they
 * need gameplay to halt (e.g., intro camera pans or manual pauses). The service freezes the game
 * clock and libGDX {@link Timer} while any reason is active, and restores the previous state once
 * the final reason is cleared.
 *
 * <p>The service also tracks whether user placement interactions should be blocked. Placement locks
 * are managed independently from freeze reasons so callers can decide the scope of each pause.
 */
public class GameStateService {
  private static final Logger logger = LoggerFactory.getLogger(GameStateService.class);

  private final GameTime timeSource;
  private final Timer timer;
  private final EnumSet<FreezeReason> activeReasons = EnumSet.noneOf(FreezeReason.class);

  private float cachedTimeScale;
  private boolean timerStoppedByService;
  private boolean placementLocked;

  /**
   * Reasons that the game can be frozen.
   *
   * <p>This enum intentionally covers a broad set of scenarios so the service can be extended
   * without introducing breaking changes later.
   */
  public enum FreezeReason {
    INTRO_PAN,
    USER_PAUSE,
    GAME_OVER,
    LEVEL_COMPLETE,
    CUTSCENE
  }

  /**
   * Creates the service with the default libGDX timer instance.
   *
   * @param timeSource the game's time source
   */
  public GameStateService(GameTime timeSource) {
    this(timeSource, Timer.instance());
  }

  /**
   * Creates the service with explicit dependencies. Exposed for testing.
   *
   * @param timeSource the game's time source
   * @param timer the timer to pause/resume when freezing/unfreezing
   */
  public GameStateService(GameTime timeSource, Timer timer) {
    this.timeSource = Objects.requireNonNull(timeSource, "timeSource must not be null");
    this.timer = Objects.requireNonNull(timer, "timer must not be null");
    this.cachedTimeScale = timeSource.getTimeScale();
  }

  /**
   * Adds a reason to keep the game frozen. If this is the first active reason, the current time
   * scale is cached, the game clock is frozen, and libGDX timers are paused.
   *
   * @param reason the reason this freeze is required
   */
  public void addFreezeReason(FreezeReason reason) {
    Objects.requireNonNull(reason, "reason must not be null");
    if (activeReasons.add(reason)) {
      logger.debug("Added freeze reason {}. Reasons now: {}", reason, activeReasons);
      if (activeReasons.size() == 1) {
        freezeTime();
      }
    } else {
      logger.trace("Freeze reason {} already active. No state change.", reason);
    }
  }

  /**
   * Removes a freeze reason. When the final reason is cleared, the cached time scale is restored
   * and timers are resumed.
   *
   * @param reason the reason that has finished
   */
  public void removeFreezeReason(FreezeReason reason) {
    Objects.requireNonNull(reason, "reason must not be null");
    if (!activeReasons.remove(reason)) {
      logger.debug("Freeze reason {} was not active. No state change.", reason);
      return;
    }

    logger.debug("Removed freeze reason {}. Remaining: {}", reason, activeReasons);
    if (activeReasons.isEmpty()) {
      unfreezeTime();
    }
  }

  /**
   * Returns an immutable snapshot of the active freeze reasons.
   *
   * @return the set of active freeze reasons
   */
  public Set<FreezeReason> getActiveReasons() {
    if (activeReasons.isEmpty()) {
      return Collections.emptySet();
    }
    return Collections.unmodifiableSet(EnumSet.copyOf(activeReasons));
  }

  /**
   * Indicates whether the game is currently frozen.
   *
   * @return true if any freeze reasons are active
   */
  public boolean isFrozen() {
    return !activeReasons.isEmpty();
  }

  /**
   * Locks placement interactions. This can be used in combination with freeze reasons or
   * independently.
   */
  public void lockPlacement() {
    placementLocked = true;
    logger.trace("Placement locked");
  }

  /** Unlocks placement interactions. */
  public void unlockPlacement() {
    placementLocked = false;
    logger.trace("Placement unlocked");
  }

  /**
   * Indicates whether placement is currently locked.
   *
   * @return true if placement is locked
   */
  public boolean isPlacementLocked() {
    return placementLocked;
  }

  private void freezeTime() {
    cachedTimeScale = timeSource.getTimeScale();
    if (cachedTimeScale > 0f) {
      logger.debug("Caching time scale {} before freezing", cachedTimeScale);
    }
    timeSource.setTimeScale(0f);
    if (!timerStoppedByService) {
      timer.stop();
      timerStoppedByService = true;
      logger.trace("Timer stopped");
    }
  }

  private void unfreezeTime() {
    logger.debug("Restoring time scale to {}", cachedTimeScale);
    timeSource.setTimeScale(cachedTimeScale <= 0f ? 1f : cachedTimeScale);
    if (timerStoppedByService) {
      timer.start();
      timerStoppedByService = false;
      logger.trace("Timer resumed");
    }
  }
}
