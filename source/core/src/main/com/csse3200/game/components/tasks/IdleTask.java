package com.csse3200.game.components.tasks;

import com.csse3200.game.entities.Entity;

/**
 * Keeps an entity idle until a target entity is detected within the attack range.
 *
 * <p>A list of available target entities is monitored to determine whether the current entity
 * should remain idle or prepare for an attack, based on the target's distance and visibility. The
 * closest visible target is found and if the target is outside the entity's range of attack, entity
 * remains idle. Otherwise, this task becomes inactive and another task (attack task) takes over.
 */
public class IdleTask extends TargetDetectionTasks {
  private final float attackRange;

  /**
   * Creates an IdleTask
   *
   * @param attackRange the maximum distance at which the entity can attack a target
   */
  public IdleTask(float attackRange) {
    super(attackRange);
    this.attackRange = attackRange;
  }

  @Override
  public void update() {
    owner.getEntity().getEvents().trigger("idleStart");
  }

  /**
   * If task is currently running, determine whether to stay running
   *
   * @return {@code -1} if an enemy is within attack range (deactivate), otherwise {@code 1} (stay
   *     idle)
   */
  @Override
  protected int getActivePriority(float dst, Entity target) {
    if (dst <= attackRange) {
      return -1; // stop task when target in range
    }
    return 1; // keep idle; active
  }

  /**
   * If task is not currently running, determine if it should run
   *
   * @return {@code -1} if an enemy is within attack range otherwise {@code 1} (start task)
   */
  @Override
  protected int getInactivePriority(float dst, Entity target) {
    if (dst <= attackRange) {
      return -1; // if target is in range, don't start task
    }
    return 1;
  }
}
