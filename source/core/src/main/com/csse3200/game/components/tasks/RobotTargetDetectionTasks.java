package com.csse3200.game.components.tasks;

import static java.lang.Math.abs;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.csse3200.game.ai.tasks.DefaultTask;
import com.csse3200.game.ai.tasks.PriorityTask;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.PhysicsEngine;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.raycast.RaycastHit;
import com.csse3200.game.rendering.DebugRenderer;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RobotTargetDetectionTasks extends DefaultTask implements PriorityTask {
  private static final Logger logger = LoggerFactory.getLogger(RobotTargetDetectionTasks.class);
  protected final float attackRange;
  protected final PhysicsEngine physics;
  protected final DebugRenderer debugRenderer;
  protected final RaycastHit hit = new RaycastHit();
  protected short targetLayer;

  public RobotTargetDetectionTasks(float attackRange, short targetLayer) {
    this.attackRange = attackRange;
    this.targetLayer = targetLayer;
    physics = ServiceLocator.getPhysicsService().getPhysics();
    debugRenderer = ServiceLocator.getRenderService().getDebug();
  }

  /**
   * Gets the distance to the nearest visible target
   *
   * @return the distance to the nearest target or a MAX VALUE if there is no target
   */
  protected float getDistanceToTarget() {
    Entity target = getNearestVisibleTarget();
    if (target == null) {
      return Float.MAX_VALUE;
    }
    return owner.getEntity().getPosition().dst(target.getPosition());
  }

  /**
   * Determines the tasks priority
   *
   * <ul>
   *   <li>When active: returns {@code 1} if target is in range and visible, otherwise {@code -1}.
   *   <li>When inactive: returns {@code 1} if target is in range and visible, otherwise {@code -1}.
   * </ul>
   *
   * @return the tasks priority
   */
  @Override
  public int getPriority() {
    Entity target = getNearestVisibleTarget();
    if (target == null) {
      return -1;
    }
    //    logger.info("Active target in range, priority 10");
    return 10;
  }

  /**
   * Determines if a target is visible by checking for obstacles in the current entities line of
   * sight
   *
   * @param target the target to check
   * @return {@code true} if the target is visible, {@code false} otherwise
   */
  //    protected boolean isTargetVisible(Entity target) {
  //        Vector2 from = owner.getEntity().getCenterPosition();
  //        Vector2 to = target.getCenterPosition();
  //
  //        // If there is an obstacle in the path to the player, not visible.
  //        if (physics.raycast(from, to, PhysicsLayer.OBSTACLE, hit)) {
  //            debugRenderer.drawLine(from, hit.point);
  //            return false;
  //        }
  //        debugRenderer.drawLine(from, to);
  //        return true;
  //    }

  /**
   * Finds the nearest visible target within attack range.
   *
   * @return the closest visible target within range, or {@code null} if none
   */
  protected Entity getNearestVisibleTarget() {
    Vector2 from = owner.getEntity().getCenterPosition();
    Array<Entity> targets = new Array<>(ServiceLocator.getEntityService().getEntities());

    //        logger.info("Number of targets: " + targets.size);
    for (Entity target : targets) {
      HitboxComponent hitbox = target.getComponent(HitboxComponent.class);

      if (hitbox == null) {
        continue;
      }

      if (hitbox.getLayer() != targetLayer) {
        continue;
      }

      Vector2 targetPos = target.getCenterPosition();
      float distance = from.dst(targetPos);
      if (abs(targetPos.y - from.y) > 10f) {
        continue;
      }
      if (distance <= 90f) { // if target visible and in range
        // logger.info("Target detected within range: " + target);
        return target;
      }
    }
    return null;
  }
}
