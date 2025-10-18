package com.csse3200.game.ui.terminal.commands;

import com.csse3200.game.entities.factories.RobotFactory;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.WaveService;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpawnRobot implements Command {
  private static final Logger logger = LoggerFactory.getLogger(SpawnRobot.class);

  private static final int DEFAULT_LANE = 0;

  /**
   * Spawns the specified robot. robot in the spawn queue in the lane corresponding to the first
   * argument expressed as an int.
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
    } else {
      // This will be a standard robot if invalid
      RobotFactory.RobotType robotType = RobotFactory.RobotType.fromString(args.getFirst());
      int lane = DEFAULT_LANE;
      // If the lane isn't specified in the args, just keep the default (0)
      if (args.size() > 1) {
        try {
          lane = Integer.parseInt(args.get(1));
        } catch (NumberFormatException e) {
          logger.debug("{} is not a valid number. Defaulting to {}", args.get(1), DEFAULT_LANE);
        }
      }
      waveService.spawnEnemyDebug(lane, robotType);
    }

    return true;
  }
}
