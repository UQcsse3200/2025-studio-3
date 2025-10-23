package com.csse3200.game.components.projectiles;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.components.Component;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.services.ServiceLocator;

public class MoveLeftComponent extends Component {
  private float speed;
  private boolean shoot = false;
  private PhysicsComponent physicsComponent;

  public MoveLeftComponent(float speed) {
    this.speed = speed;
  }

  @Override
  public void create() {
    physicsComponent = entity.getComponent(PhysicsComponent.class);
    if (physicsComponent != null) {
      physicsComponent.getBody().setLinearVelocity(-speed, 0);
    }
  }

  @Override
  public void update() {
    float delta = ServiceLocator.getTimeSource().getDeltaTime();
    Entity entity = getEntity(); // from the Component

    Vector2 pos = entity.getPosition();
    entity.setPosition(pos.x - speed * delta, pos.y); // move left

    if (shoot) {
      entity.getEvents().trigger("spawnProjectile", entity.getPosition());
      shoot = false;
    }
  }

  public void setShoot(boolean shoot) {
    this.shoot = shoot;
  }
}
