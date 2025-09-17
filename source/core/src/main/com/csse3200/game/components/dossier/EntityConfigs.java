package com.csse3200.game.components.dossier;

import com.csse3200.game.entities.configs.BaseEnemyConfig;
import com.csse3200.game.entities.configs.TeleportRobotConfig;

/**
 * Represents the top-level structure of the Enemies.json file. Each field in this class corresponds
 * to a top-level key in the JSON.
 */
public class EntityConfigs {

  public BaseEnemyConfig standardRobot;
  public BaseEnemyConfig fastRobot;
  public BaseEnemyConfig tankyRobot;
  public BaseEnemyConfig bungeeRobot;
  public TeleportRobotConfig teleportRobot;
}
