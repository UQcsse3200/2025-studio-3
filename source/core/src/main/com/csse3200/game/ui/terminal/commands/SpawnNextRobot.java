package com.csse3200.game.ui.terminal.commands;

import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.WaveService;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpawnNextRobot implements Command {
  private static final Logger logger = LoggerFactory.getLogger(SpawnNextRobot.class);

  private static final int DEFAULT_LANE = 0;

  /**
   * Spawns the next robot in the spawn queue. The robot will be spawned in the lane specified by
   * the first argument expressed as an int. If no/invalid arguments are given, spawns in lane 0.
   * Args after the first one are ignored. Does not work outside of levels.
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
      waveService.spawnNextEnemy(DEFAULT_LANE);
      logger.debug("No lane given to spawnNextRobot. Spawning in lane {}.", DEFAULT_LANE);
    } else {
      int lane = DEFAULT_LANE;
      try {
        lane = Integer.parseInt(args.getFirst());
      } catch (NumberFormatException e) {
        logger.debug(
            "{} is not a valid number. Spawning in lane {}", args.getFirst(), DEFAULT_LANE);
      }
      waveService.spawnNextEnemy(lane);
    }

    return true;
  }
}
