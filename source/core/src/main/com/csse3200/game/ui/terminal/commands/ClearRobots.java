package com.csse3200.game.ui.terminal.commands;

import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClearRobots implements Command {
  private static final Logger logger = LoggerFactory.getLogger(ClearRobots.class);

  /**
   * Defeats all robots on screen.
   *
   * @return true when successful, otherwise false
   */
  @Override
  public boolean action(ArrayList<String> args) {
    try {
      List<Entity> entities = ServiceLocator.getGameArea().getEntities();
      for (Entity entity : entities) {
        if (isEnemy(entity)) {
          ServiceLocator.getGameArea().requestDespawn(entity);
          ServiceLocator.getWaveService().onEnemyDispose();
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
