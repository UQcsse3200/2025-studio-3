package com.csse3200.game.services;

import de.jcm.discordgamesdk.*;
import de.jcm.discordgamesdk.activity.Activity;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for managing the Discord Rich Presence integration.
 *
 * <p>Provides functionality to update Discord status with current game state.
 */
public class DiscordRichPresenceService {
  private static final Logger logger = LoggerFactory.getLogger(DiscordRichPresenceService.class);
  private static final long APPLICATION_ID = 1421050206404739225L;
  Core core;
  ActivityManager activityManager;
  boolean isInitialized = false;
  long startTime;
  Supplier<CreateParams> createParamsSupplier = CreateParams::new;
  Function<CreateParams, Core> coreFactory = Core::new;
  Supplier<Activity> activitySupplier = Activity::new;

  /** Initializes the Discord Rich Presence service. Should be called once when the game starts. */
  public void initialize() {
    try (CreateParams params = createParamsSupplier.get()) {
      params.setClientID(APPLICATION_ID);
      params.setFlags(CreateParams.getDefaultFlags());
      core = coreFactory.apply(params);
      activityManager = core.activityManager();
      startTime = System.currentTimeMillis() / 1000;
      isInitialized = true;
      logger.info("[Discord Rich Presence service] Initialized successfully");
    } catch (Exception e) {
      logger.error("[Discord Rich Presence service] Failed to initialize: {}", e.getMessage());
      isInitialized = false;
    }
  }

  /** Updates the Discord presence with simple game information. */
  public void setPresence(String state) {
    if (!isInitialized || activityManager == null) {
      logger.error("[Discord Rich Presence service] Not initialized, skipping update");
      return;
    }

    try (Activity activity = activitySupplier.get()) {
      // Create activity
      activity.setDetails("Playing The Day We Fought Back");
      if (state != null) {
        activity.setState(state);
      } else {
        activity.setState(null);
      }

      // Set large image
      activity.assets().setLargeImage("game_logo");
      activity.assets().setLargeText("The Day We Fought Back");

      // Set timestamp
      activity.timestamps().setStart(java.time.Instant.ofEpochSecond(startTime));

      // Update presence
      activityManager.updateActivity(
          activity,
          result -> {
            if (result == Result.OK) {
              logger.debug(
                  "[Discord Rich Presence service] Updated presence: Playing The Day We Fought Back");
            } else {
              logger.warn("[Discord Rich Presence service] Failed to update activity: {}", result);
            }
          });

    } catch (Exception e) {
      logger.error("[Discord Rich Presence service] Failed to update presence: {}", e.getMessage());
    }
  }

  /**
   * Updates the Discord presence with specific game state information.
   *
   * @param level Current level
   * @param wave Current wave
   */
  public void updateGamePresence(String level, int wave) {
    if (!isInitialized) {
      return;
    }

    StringBuilder stateBuilder = new StringBuilder();

    if (level != null) {
      stateBuilder.append("Level: ").append(level);
    }

    if (wave != 0) {
      if (!stateBuilder.isEmpty()) {
        stateBuilder.append(" | ");
      }
      stateBuilder.append("Wave: ").append(wave);
    }

    setPresence(stateBuilder.toString());
  }

  /** Shuts down the Discord Rich Presence service. Should be called when the game exits. */
  public void shutdown() {
    if (!isInitialized) {
      return;
    }

    try {
      isInitialized = false;
      if (core != null) {
        core.close();
      }

      logger.info("Discord Rich Presence service shut down");
    } catch (Exception e) {
      logger.error("Failed to shutdown Discord Rich Presence: {}", e.getMessage());
    }
  }

  /**
   * Checks if the service is initialized and ready to use.
   *
   * @return true if initialized, false otherwise
   */
  public boolean isInitialized() {
    return isInitialized;
  }

  /** Runs Discord callbacks. Should be called periodically (e.g., in the main game loop). */
  public void runCallbacks() {
    if (isInitialized && core != null) {
      core.runCallbacks();
    }
  }
}
