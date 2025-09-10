package com.csse3200.game.components.tasks;

import com.csse3200.game.components.DefenceStatsComponent;
import com.csse3200.game.entities.Entity;
import java.util.List;

import com.csse3200.game.entities.factories.ProjectileFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO : integrate with attack system team

/**
 * Allows an entity to attack the closest target entity from a list of potential targets. This task
 * runs when there is a visible target within the entities range of attack
 */
public class AttackTask extends TargetDetectionTasks {
  private static final Logger logger = LoggerFactory.getLogger(AttackTask.class);

  /**
   * Creates an attack task
   *
   * @param targets a list of potential targets
   * @param attackRange the maximum distance the entity can find a target to attack
   */
  public AttackTask(List<Entity> targets, float attackRange) {
    super(targets, attackRange);
  }

  /**
   * Starts the attack task. The closest visible target within the entity's attack range is found
   * and ATTACK LOGIC BEGINS.
   */
  @Override
  public void start() {
    super.start();
    Entity target = getNearestVisibleTarget();
    if (target != null) {
      DefenceStatsComponent stats = owner.getEntity().getComponent(DefenceStatsComponent.class);
      int damage = stats.getBaseAttack();
      // TODO this should be specific to defender type??
      Entity slingshot = ProjectileFactory.createSlingShot(damage);
    }

    this.owner.getEntity().getEvents().trigger("chaseStart");
  }

  /** Updates the task each game frame */
  @Override
  public void update() {
    logger.info("AttackTask priority: {}", getPriority());
    Entity target = getNearestVisibleTarget();

    if (target == null) {
      return;
    }

    if (getDistanceToTarget() <= attackRange && isTargetVisible(target)) {
      // TODO: attack
    }
  }

  /** Stops the attack */
  @Override
  public void stop() {
    super.stop();
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
    if (dst > attackRange || !isTargetVisible(target)) {
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
    if (dst <= attackRange && isTargetVisible(target)) {
      return 1; // start task if target is visible and in range
    }
    return -1;
  }
}
