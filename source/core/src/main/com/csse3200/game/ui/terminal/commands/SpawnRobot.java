package com.csse3200.game.ui.terminal.commands;

import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpawnRobot implements Command {
  private static final Logger logger = LoggerFactory.getLogger(SpawnRobot.class);

  /**
   * Spawns the next robot in the spawn queue in the lane corresponding to the first argument
   * expressed as an int.
   *
   * @return true when successful, otherwise false
   */
  @Override
  public boolean action(ArrayList<String> args) {
    if (args.isEmpty()) {
      logger.debug("Invalid arguments received for 'spawnRobot' command: {}", args);
      return false;
    }
    try {
      ServiceLocator.getWaveService().spawnEnemy(Integer.parseInt(args.getFirst()));
    } catch (NullPointerException e) {
      logger.debug("This service is not available on this screen.");
      return false;
    }
    return true;
  }
}
