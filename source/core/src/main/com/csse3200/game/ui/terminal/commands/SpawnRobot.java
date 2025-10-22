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
   * Pauses WaveService spawning new enemies.
   *
   * @return true when successful, otherwise false
   */
  @Override
  public boolean action(ArrayList<String> args) {
    WaveService waveService = ServiceLocator.getWaveService();
    if (waveService == null) {
      logger.warn("spawnRobot service is not available on this screen.");
      return false;
    }

    if (args.isEmpty()) {
      logger.warn("Invalid arguments received for 'spawnRobot' command: {}", args);
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
          logger.warn("{} is not a valid number. Defaulting to {}", args.get(1), DEFAULT_LANE);
        }
      }
      waveService.spawnEnemyDebug(lane, robotType);
    }

    return true;
  }
}
