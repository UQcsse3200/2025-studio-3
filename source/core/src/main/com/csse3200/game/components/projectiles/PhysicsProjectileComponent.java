package com.csse3200.game.components.projectiles;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.components.Component;
import com.csse3200.game.components.tasks.TargetDetectionTasks.AttackDirection;
import com.csse3200.game.services.ServiceLocator;

/**
 * Component that moves a projectile in a parabolic arc using simple physics. The projectile is
 * fired at a 45 degree angle either LEFT or RIGHT, and lands approximately at the given horizontal
 * distance from its start.
 */
public class PhysicsProjectileComponent extends Component {
  private final float gravity = 9.8f * 50; // scaled gravity for game feel
  private Vector2 velocity;
  private Vector2 startPos;
  private final AttackDirection direction;
  private final float distance;

  /**
   * @param distance horizontal distance before landing
   * @param direction AttackDirection.LEFT or RIGHT
   */
  public PhysicsProjectileComponent(float distance, AttackDirection direction) {
    this.distance = distance;
    this.direction = direction;
  }

  @Override
  public void create() {
    this.startPos = getEntity().getPosition().cpy();

    // Fixed launch angle: 45 degrees
    float angleRad = (float) Math.toRadians(45);

    // Compute initial speed required for given range (R = v^2 * sin(2θ) / g)
    float v = (float) Math.sqrt(distance * gravity / Math.sin(2 * angleRad));

    // Components of velocity
    float vx = v * (float) Math.cos(angleRad);
    float vy = v * (float) Math.sin(angleRad);

    // Flip x-velocity if going LEFT
    if (direction == AttackDirection.LEFT) {
      vx = -vx;
    }

    this.velocity = new Vector2(vx, vy);
  }

  @Override
  public void update() {
    float dt = ServiceLocator.getTimeSource().getDeltaTime();
    Vector2 pos = getEntity().getPosition();

    // Apply gravity to vertical velocity
    velocity.y -= gravity * dt;

    // Update position with velocity
    pos.x += velocity.x * dt;
    pos.y += velocity.y * dt;

    getEntity().setPosition(pos);

    // When it "lands" at or below the starting Y, trigger event and destroy
    if (pos.y <= startPos.y) {
      getEntity().getEvents().trigger("projectileLanded", pos);
      handleImpact(pos);
      getEntity().dispose();
    }
  }

  private void handleImpact(Vector2 impactPos) {
    if (getEntity() != null && getEntity().getEvents() != null) {
      getEntity().getEvents().trigger("despawnShell", impactPos);
    }
  }
}
