package com.csse3200.game.ui.terminal.commands;

import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StopSpawning implements Command {
  private static final Logger logger = LoggerFactory.getLogger(StopSpawning.class);

  /**
   * Pauses WaveService spawning new enemies.
   *
   * @return true when successful, otherwise false
   */
  @Override
  public boolean action(ArrayList<String> args) {
    try {
      ServiceLocator.getWaveService().debugStopSpawning();
    } catch (NullPointerException e) {
      logger.warn("This service is not available on this screen.");
      return false;
    }
    return true;
  }
}
