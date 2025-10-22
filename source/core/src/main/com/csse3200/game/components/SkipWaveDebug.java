package com.csse3200.game.components;

import com.badlogic.gdx.Input;
import com.csse3200.game.areas.GameArea;
import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.input.InputComponent;
import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registered keys: F5:Force End Wave. Kills all current enemies, triggering the end-of-wave
 * sequence.
 */
public class SkipWaveDebug extends InputComponent {
  private static final Logger logger = LoggerFactory.getLogger(SkipWaveDebug.class);

  public SkipWaveDebug() {
    super(5);
  }

  @Override
  public boolean keyDown(int keycode) {
    if (keycode == Input.Keys.F5) {
      forceEndWave();
      return true;
    }
    return false;
  }

  /**
   * Instantly ends the current wave by killing all active robot entities. If no robots are present,
   * it manually advances the wave state to skip preparation phases.
   */
  private void forceEndWave() {
    logger.info("[DEBUG] F5 pressed: Forcing wave to end.");

    GameArea genericGameArea = ServiceLocator.getGameArea();

    if (!(genericGameArea instanceof LevelGameArea)) {
      logger.warn("[DEBUG] F5 key does nothing on a non-level screen.");
      return;
    }
    LevelGameArea gameArea = (LevelGameArea) genericGameArea;

    ArrayList<Entity> robots = new ArrayList<>(gameArea.getRobots());

    if (robots.isEmpty()) {
      logger.info("[DEBUG] No active enemies to kill. Manually advancing wave.");
      ServiceLocator.getWaveService().endWave();
      return;
    }

    logger.info("[DEBUG] Killing {} enemies to end the wave.", robots.size());
    for (Entity robot : robots) {

      robot.getEvents().trigger("entityDeath");
    }
  }
}
