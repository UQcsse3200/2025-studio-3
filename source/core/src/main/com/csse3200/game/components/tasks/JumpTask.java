package com.csse3200.game.components.tasks;

import com.badlogic.gdx.physics.box2d.Body;
import com.csse3200.game.components.DefenderStatsComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.services.ServiceLocator;

public class JumpTask extends RobotTargetDetectionTasks {
  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JumpTask.class);
  private boolean hasJumped = false;
  private static float jumpDuration = 0.5f;
  private float jumpTimer = 0f;
  private boolean isJumping = false;
  private float startY;
  private int WALL_DEATH_DAMAGE = 1000;

  public JumpTask(float range, short targetLayer) {
    super(range, targetLayer);
  }

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
    if (target.getComponent(DefenderStatsComponent.class) != null) {
      if (target.getComponent(DefenderStatsComponent.class).getBaseAttack() == WALL_DEATH_DAMAGE) {
        return -1; // the entity is the wall, don't allow jump
      }
    }
    return 200;
  }

  @Override
  public void start() {
    super.start();
    startY = this.owner.getEntity().getPosition().y;
    this.owner.getEntity().getEvents().trigger("jumpStart");
  }

  @Override
  public void update() {
    Entity target = getNearestVisibleTarget();
    PhysicsComponent phys = owner.getEntity().getComponent(PhysicsComponent.class);
    if (phys == null || phys.getBody() == null) return;
    Body body = phys.getBody();

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
        body.setTransform(body.getPosition().x, startY, body.getAngle());
        return;
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
