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

public abstract class RobotTargetDetectionTasks extends DefaultTask implements PriorityTask {
  protected final float attackRange;
  protected final PhysicsEngine physics;
  protected final DebugRenderer debugRenderer;
  protected final RaycastHit hit = new RaycastHit();
  protected short targetLayer;

  protected RobotTargetDetectionTasks(float attackRange, short targetLayer) {
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
    return 100;
  }

  /**
   * Finds the nearest visible target within attack range.
   *
   * @return the closest visible target within range, or {@code null} if none
   */
  protected Entity getNearestVisibleTarget() {
    Vector2 from = owner.getEntity().getCenterPosition();
    Array<Entity> targets = new Array<>(ServiceLocator.getEntityService().getEntities());

    for (Entity target : targets) {
      HitboxComponent hitbox = target.getComponent(HitboxComponent.class);

      if (hitbox == null || hitbox.getLayer() != targetLayer) {
        continue;
      }

      Vector2 targetPos = target.getCenterPosition();
      // dst2 instead of dst to avoid square root calculation because calculating square root is expensive.
      float distance = from.dst2(targetPos);
      if ((abs(targetPos.y - from.y) <= 5f) && (distance <= attackRange * attackRange)) {
        return target;
      }
    }
    return null;
  }
}
