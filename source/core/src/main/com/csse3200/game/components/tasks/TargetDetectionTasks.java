package com.csse3200.game.components.tasks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.csse3200.game.ai.tasks.DefaultTask;
import com.csse3200.game.ai.tasks.PriorityTask;
import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.PhysicsEngine;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.raycast.RaycastHit;
import com.csse3200.game.rendering.DebugRenderer;
import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TargetDetectionTasks extends DefaultTask implements PriorityTask {
  protected final float attackRange;
  protected final AttackDirection direction;
  protected final PhysicsEngine physics;
  protected final DebugRenderer debugRenderer;
  private static final Logger logger = LoggerFactory.getLogger(TargetDetectionTasks.class);

  // temp variables to be reused when getting nearest target
  private final Vector2 castDir = new Vector2();
  private final Vector2 offsetFrom = new Vector2();
  private final Vector2 end = new Vector2();
  private final RaycastHit tempHit = new RaycastHit();

  public enum AttackDirection {
    LEFT,
    RIGHT,
  }

  /**
   * Creates a TargetDirection task to detect an enemy entity in the specified direction and attack
   * range
   *
   * @param attackRange, the range an enemey is detected from
   * @param direction the attack direction, left or right
   */
  protected TargetDetectionTasks(float attackRange, AttackDirection direction) {
    this.attackRange = attackRange;
    this.direction = direction;
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
   * Gets the priority when the task is active.
   *
   * @param distance the distance to the target
   * @param target the target entity
   * @return the priority value
   */
  protected abstract int getActivePriority(float distance, Entity target);

  /**
   * Gets the priority when the task is inactive.
   *
   * @param distance the distance to the target
   * @param target the target entity
   * @return the priority value
   */
  protected abstract int getInactivePriority(float distance, Entity target);

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
    float dst = getDistanceToTarget();
    Entity target = getNearestVisibleTarget();
    if (status == Status.ACTIVE) {
      return getActivePriority(dst, target);
    } else {
      return getInactivePriority(dst, target);
    }
  }

  /**
   * Finds the nearest visible target within attack range.
   *
   * @return the closest visible target within range, or {@code null} if none
   */
  protected Entity getNearestVisibleTarget() {
    Vector2 from = owner.getEntity().getCenterPosition();
    castDir.set((direction == AttackDirection.RIGHT) ? 1f : -1f, 0f);

    LevelGameArea area = (LevelGameArea) ServiceLocator.getGameArea();

    // done with the help of OpenAI
    offsetFrom.set(from.x, from.y - 20);
    end.set(offsetFrom).mulAdd(castDir, attackRange);

    // find first enemy entity in current entities line of sight in the given direction and range
    boolean didHit =
        physics.raycast(offsetFrom, end, (short) (PhysicsLayer.ENEMY | PhysicsLayer.BOSS), tempHit);
    if (didHit) {
      Fixture hitFixture = tempHit.getFixture();
      if (hitFixture != null && hitFixture.getUserData() instanceof Entity entity) {
        return entity;
      }
    }

    return null;
  }

  protected List<Entity> getAllTargets() {
    Array<Entity> allEntities = ServiceLocator.getEntityService().getEntities();
    Array<Entity> copy = new Array<>(allEntities);
    List<Entity> targets = new ArrayList<>();
    for (Entity e : copy) {
      HitboxComponent hitbox = e.getComponent(HitboxComponent.class);
      if (hitbox != null
          && (hitbox.getLayer() == PhysicsLayer.ENEMY || hitbox.getLayer() == PhysicsLayer.BOSS)
          && e.getComponent(CombatStatsComponent.class) != null) {
        targets.add(e);
      }
    }
    return targets;
  }
}
