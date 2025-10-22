package com.csse3200.game.ui.terminal.commands;

import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.BaseEnemyConfig;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClearRobots implements Command {
  private static final Logger logger = LoggerFactory.getLogger(ClearRobots.class);

  /**
   * Immediately ends the current wave inside a level
   *
   * @return true when successful, otherwise false
   */
  @Override
  public boolean action(ArrayList<String> args) {
    try {
      List<Entity> entities = ServiceLocator.getGameArea().getEntities();
      Map<String, BaseEnemyConfig> enemyConfigs =
          ServiceLocator.getConfigService().getEnemyConfigs();
      for (Entity entity : entities) {
        if (isEnemy(entity)) {
          ServiceLocator.getGameArea().requestDespawn(entity);
        }
      }

    } catch (NullPointerException e) {
      logger.warn("This service is not available on this screen.");
      return false;
    }
    return true;
  }

  private static boolean isEnemy(Entity e) {
    HitboxComponent hb = e.getComponent(HitboxComponent.class);
    return hb != null && PhysicsLayer.contains(hb.getLayer(), PhysicsLayer.ENEMY);
  }
}
