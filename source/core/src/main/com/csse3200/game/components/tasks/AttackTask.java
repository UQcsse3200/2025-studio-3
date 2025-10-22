package com.csse3200.game.components.tasks;

import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Allows an entity to attack the closest target entity. This task runs when there is a target
 * within the defense entities range of attack, in the given direction and in the same lane.
 */
public class AttackTask extends TargetDetectionTasks {
  // cooldown fields
  private final float baseFireCooldown;
  private float fireCooldown; // time between attacks
  private float timeSinceLastFire = 0f;

  /**
   * Creates an attack task
   *
   * @param attackRange the maximum distance the entity can find a target to attack
   * @param attackSpeed attacking speed of the entity
   * @param direction the direction the projectile will travel/the direction of attack
   */
  public AttackTask(float attackRange, float attackSpeed, AttackDirection direction) {
    super(attackRange, direction);
    this.baseFireCooldown = attackSpeed;
    this.fireCooldown = attackSpeed;
  }

  /** Doubles the entity's fire rate (half cooldown). */
  public void enableDoubleFireRate() {
    fireCooldown = baseFireCooldown / 2f;
    owner.getEntity().getEvents().trigger("doubleAttackStart");
  }

  /** Resets the fire rate to its original value. */
  public void resetFireRate() {
    fireCooldown = baseFireCooldown;
    owner.getEntity().getEvents().trigger("attackStart");
  }

  /**
   * Starts the attack task. The closest visible target within the entity's attack range is found
   * and an event listener is triggered to start attack logic.
   */
  @Override
  public void start() {
    super.start();

    this.owner.getEntity().getEvents().trigger("attackStart");
    owner.getEntity().getEvents().trigger("fire", direction);
  }

  /**
   * Updates the attack logic each game frame. If a valid target is found in range and the correct
   * lane, and enough time has passed since the last attack, a "fire" event is triggered to spawn
   * and fire a projectile.
   */
  @Override
  public void update() {
    Entity target = getNearestVisibleTarget();
    if (target == null) {
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
    if (dst > attackRange) {
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
    if (dst <= attackRange) {
      return 1; // start task if target is visible, in range, and in the same lane
    }
    return -1;
  }
}
