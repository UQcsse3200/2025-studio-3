package com.csse3200.game.components.projectiles;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.components.Component;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.services.ServiceLocator;

public class MoveRightComponent extends Component {
  private float speed = 150f;
  private boolean shoot = false;

  @Override
  public void update() {
    float delta = ServiceLocator.getTimeSource().getDeltaTime();
    Entity entity = getEntity(); // from the Component

    Vector2 pos = entity.getPosition();
    entity.setPosition(pos.x + speed * delta, pos.y);

    if (shoot) {
      entity.getEvents().trigger("spawnProjectile", entity.getPosition());
      shoot = false;
    }
  }

  public void setShoot(boolean shoot) {
    this.shoot = shoot;
  }
}
