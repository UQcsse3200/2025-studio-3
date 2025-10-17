package com.csse3200.game.ui.terminal.commands;

import com.csse3200.game.entities.factories.RobotFactory;
import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;

import com.csse3200.game.services.WaveService;
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
    WaveService waveService = ServiceLocator.getWaveService();
    if (waveService == null) {
      logger.debug("spawnRobot service is not available on this screen.");
      return false;
    }

    if (args.isEmpty()) {
      logger.debug("Invalid arguments received for 'spawnRobot' command: {}", args);
      return false;
    } else if (args.size() == 1) {
      try {
        int lane = Integer.parseInt(args.getFirst());
        waveService.spawnEnemy(lane);
      } catch (NumberFormatException e) {
        logger.debug("{} is not a valid number", args.getFirst());
        return false;
      }
    } else if (args.size() == 2) {
      // This will be a standard robot if invalid
      RobotFactory.RobotType robotType = RobotFactory.RobotType.fromString(args.getFirst());
      try {
        int lane = Integer.parseInt(args.get(1));
        waveService.spawnEnemyDebug(lane, robotType);
      } catch (NumberFormatException e) {
        logger.debug("{} is not a valid number. Cannot spawn {}", args.get(1), robotType.get());
        return false;
      }
    } else {
      logger.debug("Invalid number of arguments in spawnRobot. args: {}", args);
      return false;
    }

    return true;
  }
}
