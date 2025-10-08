package com.csse3200.game.components.tasks;

import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.services.ServiceLocator;

/**
 * Allows an entity to attack the closest target entity from a list of potential targets. This task
 * runs when there is a target within the entities range of attack and in the same lane.
 */
public class AttackTask extends TargetDetectionTasks {
  // cooldown fields
  private float fireCooldown; // time between attacks
  private float timeSinceLastFire = 0f;

  /**
   * Creates an attack task
   *
   * @param attackRange the maximum distance the entity can find a target to attack
   * @param attackSpeed attacking speed of the entity
   * @param direction the direction the projectile will travel
   */
  public AttackTask(float attackRange, float attackSpeed, AttackDirection direction) {
    super(attackRange, direction);
    this.fireCooldown = attackSpeed;
  }

  /**
   * Starts the attack task. The closest visible target within the entity's attack range is found
   * and ATTACK LOGIC BEGINS.
   */
  @Override
  public void start() {
    super.start();

    this.owner.getEntity().getEvents().trigger("attackStart");
    owner.getEntity().getEvents().trigger("fire", direction);
  }

  /** Updates the task each game frame */
  @Override
  public void update() {
    Entity target = getNearestVisibleTarget();
    if (target == null || !isTargetInSameLane(target)) {
      return;
    }

    if (getDistanceToTarget() <= attackRange) {
      timeSinceLastFire += ServiceLocator.getTimeSource().getDeltaTime();

      if (timeSinceLastFire >= fireCooldown) {
        // tell listeners (LevelGameArea) to spawn a projectile
        owner.getEntity().getEvents().trigger("fire", direction); // <-- this is the key bit
        timeSinceLastFire = 0f;
      }
    }
  }

  /**
   * Determines the tasks priority when the task is running.
   *
   * @return {@code 1} if a target is visible, in the same lane, and within attack range, otherwise
   *     {@code -1}
   */
  @Override
  protected int getActivePriority(float dst, Entity target) {
    if (target == null) {
      return -1; // stop task if no target
    }
    if (dst > attackRange || !isTargetInSameLane(target)) {
      return -1; // stop task when target not visible, out of range, or not in the same lane
    }
    return 1;
  }

  /**
   * Computes the priority when the task is inactive.
   *
   * @return {@code 1} if the target is visible, in the same lane, and within attack range,
   *     otherwise {@code -1}
   */
  @Override
  protected int getInactivePriority(float dst, Entity target) {
    if (target == null) {
      return -1;
    }
    if (dst <= attackRange && isTargetInSameLane(target)) {
      return 1; // start task if target is visible, in range, and in the same lane
    }
    return -1;
  }

  /**
   * Checks if the target entity is in the same lane as the owner entity. A lane is defined by the
   * y-coordinate, and entities are considered in the same lane if their vertical distance is less
   * than half a tile size.
   *
   * @param target The entity to check against.
   * @return true if the target is in the same lane, false otherwise.
   */
  private boolean isTargetInSameLane(Entity target) {
    // This call will now work correctly because we registered the GameArea in LevelGameArea.
    if (ServiceLocator.getGameArea() instanceof LevelGameArea) {
      LevelGameArea area = (LevelGameArea) ServiceLocator.getGameArea();
      float tileSize = area.getTileSize();

      // Get the vertical center position of the owner (slingshooter) and the target
      float ownerY = owner.getEntity().getCenterPosition().y;
      float targetY = target.getCenterPosition().y;

      // Check if the absolute vertical distance between the entities' centers
      // is less than half a tile. This confirms they are on the same row.
      return Math.abs(ownerY - targetY) < (tileSize / 2);
    }
    // If we're not in a LevelGameArea, we can't determine lanes, so fallback to false.
    return false;
  }
}
