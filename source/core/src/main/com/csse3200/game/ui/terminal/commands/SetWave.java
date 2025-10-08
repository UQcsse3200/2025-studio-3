package com.csse3200.game.ui.terminal.commands;

import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetWave implements Command {
  private static final Logger logger = LoggerFactory.getLogger(SetWave.class);

  /**
   * Sets the current wave inside the level to the first argument,
   * expressed as an integer.
   *
   * @return true when successful, false when unsuccessful
   */
  @Override
  public boolean action(ArrayList<String> args) {
    if (args.isEmpty()) {
      logger.debug("Invalid arguments received for 'setWave' command: {}", args);
      return false;
    }
    try {
      ServiceLocator.getWaveService().debugSetCurrentWave(Integer.parseInt(args.getFirst()));
    } catch (NullPointerException e) {
      logger.debug("This service is not available on this screen.");
      return false;
    }
    return true;
  }
}
