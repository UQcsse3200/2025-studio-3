package com.csse3200.game.components.projectiles;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.components.Component;
import com.csse3200.game.components.tasks.TargetDetectionTasks;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.services.ServiceLocator;

public class MoveDirectionComponent extends Component {
  private float speed;
  private boolean shoot = false;
  private TargetDetectionTasks.AttackDirection direction;

  public MoveDirectionComponent(TargetDetectionTasks.AttackDirection direction, float speed) {
    this.direction = direction;
    this.speed = speed;
  }

  @Override
  public void update() {
    float delta = ServiceLocator.getTimeSource().getDeltaTime();
    Entity entity = getEntity(); // from the Component

    Vector2 pos = entity.getPosition();

    if (direction == TargetDetectionTasks.AttackDirection.RIGHT) { // forward shooting
      entity.setPosition(pos.x + speed * delta, pos.y);
    } else {
      entity.setPosition(pos.x - speed * delta, pos.y);
    }

    if (shoot) {
      entity.getEvents().trigger("spawnProjectile", entity.getPosition());
      shoot = false;
    }
  }

  public void setShoot(boolean shoot) {
    this.shoot = shoot;
  }
}
