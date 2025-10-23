package com.csse3200.game.components.tasks;

import com.badlogic.gdx.physics.box2d.Body;
import com.csse3200.game.components.DefenderStatsComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.services.ServiceLocator;

/**
 * A task that make a robot jump over a defence (that is not the wall) one time.
 * After jumping, should not jump again.
 */
public class JumpTask extends RobotTargetDetectionTasks {
  private boolean hasJumped = false;
  private float jumpTimer = 0f;
  private boolean isJumping = false;
  private float startY;

  /**
   * Initialises the jump task with the range it will jump at, and
   * the target layer to jump over
   * @param range The range from a target entity to trigger a jump
   * @param targetLayer Entities in the target layer will be jumped over
   */
  public JumpTask(float range, short targetLayer) {
    super(range, targetLayer);
  }

  /**
   * Determines the task's priority. For jumpTask, the
   * priority will be 200 (above attackTask) if it is jumping
   * or should jump, -1 otherwise.
   * @return The task priority
   */
  @Override
  public int getPriority() {
    if (isJumping) {
      return 200; // High priority while jumping
    }
    if (hasJumped) {
      return -1; // Low priority after jump
    }
    Entity target = getNearestVisibleTarget();
    if (target == null) {
      return -1; // No target, low priority
    }
    // The wall has a base attack of 1000, so this stops jumping over it.
    // Hardcoding this is kind of questionable though.
    if (target.getComponent(DefenderStatsComponent.class) != null
        && target.getComponent(DefenderStatsComponent.class).getBaseAttack() == 1000) {
      return -1; // the entity is the wall, don't allow jump
    }
    return 200;
  }

  /**
   * Begins the jump animation and stores the starting y position.
   * There is no jump animation implemented currently, but if one is
   * to be added, it would use an event listener for "jumpStart"
   */
  @Override
  public void start() {
    super.start();
    startY = this.owner.getEntity().getPosition().y;
    this.owner.getEntity().getEvents().trigger("jumpStart");
  }

  /**
   * Updates the jump. If the jump is in progress, progresses it. If it is finished,
   * it will set hasJumped to not jump again, and will set the y position to the
   * original position to ensure it stays at the same y level.
   */
  @Override
  public void update() {
    Entity target = getNearestVisibleTarget();
    PhysicsComponent phys = owner.getEntity().getComponent(PhysicsComponent.class);
    if (phys == null || phys.getBody() == null) return;
    Body body = phys.getBody();

    float jumpDuration = 0.75f;
    if (isJumping) {
      jumpTimer -= ServiceLocator.getTimeSource().getDeltaTime();

      if (jumpTimer >= jumpDuration / 2) {
        // Ascend
        body.setLinearVelocity(-5000f, 2500f);
      } else {
        // Descend
        body.setLinearVelocity(-5000f, -2500f);
      }
      if (jumpTimer <= 0) {
        isJumping = false;
        hasJumped = true;
        this.owner.getEntity().setPosition(this.owner.getEntity().getPosition().x, startY + 1);
      }
      return;
    }

    if (target == null || hasJumped) {
      return;
    }
    jumpTimer = jumpDuration;
    isJumping = true;
  }
}
