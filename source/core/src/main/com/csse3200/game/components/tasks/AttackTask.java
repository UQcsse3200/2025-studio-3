package com.csse3200.game.components.tasks;

import com.csse3200.game.entities.Entity;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO : integrate with attack system team

/**
 * Allows an entity to attack the closest target entity from a list of potential targets. This task
 * runs when there is a target within the entities range of attack
 */
public class AttackTask extends TargetDetectionTasks {
  // cooldown fields
  private static final float FIRE_COOLDOWN = 0.95f; // seconds between shots (tweak as needed)
  private float timeSinceLastFire = 0f;
  private static final Logger logger = LoggerFactory.getLogger(AttackTask.class);

  /**
   * Creates an attack task
   *
   * @param attackRange the maximum distance the entity can find a target to attack
   */
  public AttackTask(float attackRange) {
    super(attackRange);
  }

  /**
   * Starts the attack task. The closest visible target within the entity's attack range is found
   * and ATTACK LOGIC BEGINS.
   */
  @Override
  public void start() {
    super.start();
    this.owner.getEntity().getEvents().trigger("attackStart");
    owner.getEntity().getEvents().trigger("fire");
  }

  /** Updates the task each game frame*/
  @Override
  public void update() {
    Entity target = getNearestVisibleTarget();
    if (target == null) {
      return;
    }

    if (getDistanceToTarget() <= attackRange) {
      timeSinceLastFire += ServiceLocator.getTimeSource().getDeltaTime();

      if (timeSinceLastFire >= FIRE_COOLDOWN) {
        // tell listeners (LevelGameArea) to spawn a projectile
        owner.getEntity().getEvents().trigger("fire"); // <-- this is the key bit
        timeSinceLastFire = 0f;
      }
    }
  }

  /**
   * Determines the tasks priority when the task is running.
   *
   * @return {@code 1} if a target is visible and within attack range, otherwise {@code -1}
   */
  protected int getActivePriority(float dst, Entity target) {
    if (target == null) {
      return -1; // stop task if no target
    }
    if (dst > attackRange) {
      return -1; // stop task when target not visible or out of range
    }
    return 1;
  }

  /**
   * Computes the priority when the task is inactive.
   *
   * @return {@code 1} if the target is visible and within attack range, otherwise {@code -1}
   */
  protected int getInactivePriority(float dst, Entity target) {
    if (target == null) {
      return -1;
    }
    if (dst <= attackRange) {
      return 1; // start task if target is visible and in range
    }
    return -1;
  }
}
