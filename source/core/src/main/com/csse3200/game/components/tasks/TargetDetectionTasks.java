package com.csse3200.game.components.tasks;

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
import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for AI tasks that detect and prioritize nearby enemy targets within a
 * specified attack range and direction.
 *
 * <p>This class provides core logic for:
 *
 * <ul>
 *   <li>Detecting enemy entities via raycasting
 *   <li>Filtering targets based on physics layers
 *   <li>Calculating task priority
 *   <li>Providing extension points for attack behavior via {@link #getActivePriority} and {@link
 *       #getInactivePriority}
 * </ul>
 *
 * <p>
 */
public abstract class TargetDetectionTasks extends DefaultTask implements PriorityTask {
  /** Maximum range at which the task can detect targets. */
  protected final float attackRange;

  /** Direction in which to search for targets (left or right). */
  protected final AttackDirection direction;

  /** Reference to the physics engine used for raycasting. */
  protected final PhysicsEngine physics;

  // temp variables to be reused when getting nearest target
  private final Vector2 castDir = new Vector2();
  private final Vector2 offsetFrom = new Vector2();
  private final Vector2 end = new Vector2();
  private final RaycastHit tempHit = new RaycastHit();

  /** Enum representing the direction the entity should attack or scan for targets. */
  public enum AttackDirection {
    LEFT,
    RIGHT,
  }

  /**
   * Creates a new {@code TargetDetectionTasks} to detect an enemy entity in the specified direction
   * and attack range
   *
   * @param attackRange, the range an enemey is detected from
   * @param direction the attack direction, left or right
   */
  protected TargetDetectionTasks(float attackRange, AttackDirection direction) {
    this.attackRange = attackRange;
    this.direction = direction;
    physics = ServiceLocator.getPhysicsService().getPhysics();
  }

  /**
   * Gets the distance to the nearest visible target from the current entity.
   *
   * @return the distance to the nearest target or a {@code Float.MAX_VALUE} if there is no target
   *     found
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
   * Performs a raycast in the specified attack direction to find the closest visible enemy within
   * attack range.
   *
   * <p>Scans vertically from -1 to +1 tile around the entity’s Y-center to improve hit accuracy on
   * tall or offset targets.
   *
   * @return The closest visible entity matching enemy or boss layer, or {@code null} if none found.
   */
  protected Entity getNearestVisibleTarget() {
    Vector2 from = owner.getEntity().getCenterPosition();
    castDir.set((direction == AttackDirection.RIGHT) ? 1f : -1f, 0f);

    LevelGameArea area = (LevelGameArea) ServiceLocator.getGameArea();
    float tileSize = area.getTileSize();

    // done with the help of OpenAI
    for (float yOffset = -tileSize; yOffset <= tileSize; yOffset += 0.5) {
      offsetFrom.set(from.x, from.y + yOffset);
      end.set(offsetFrom).mulAdd(castDir, attackRange);

      // find first enemy entity in current entities line of sight in the given direction and range
      boolean didHit =
          physics.raycast(
              offsetFrom, end, (short) (PhysicsLayer.ENEMY | PhysicsLayer.BOSS), tempHit);

      // if enemy in front of defender within the attack range, return enemy
      if (didHit) {
        Fixture hitFixture = tempHit.getFixture();
        if (hitFixture != null && hitFixture.getUserData() instanceof Entity entity) {
          return entity;
        }
      }
    }
    return null;
  }

  /**
   * Retrieves all current enemy entities in the game world.
   *
   * @return List of all potential attack targets (enemy or boss entities).
   */
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
