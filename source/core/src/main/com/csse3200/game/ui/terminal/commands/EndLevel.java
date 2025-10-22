package com.csse3200.game.ui.terminal.commands;

import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndLevel implements Command {
  private static final Logger logger = LoggerFactory.getLogger(EndLevel.class);

  /**
   * Sets the current wave to a value higher than the max wave. Ends the level immediately with a
   * win.
   *
   * @return true when successful, false when unsuccessful
   */
  @Override
  public boolean action(ArrayList<String> args) {
    try {
      ServiceLocator.getWaveService().debugSetCurrentWave(10);
    } catch (NullPointerException e) {
      logger.warn("This service is not available on this screen.");
      return false;
    }
    return true;
  }
}
