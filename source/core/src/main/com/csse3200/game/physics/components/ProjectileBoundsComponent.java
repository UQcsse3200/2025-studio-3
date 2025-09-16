package com.csse3200.game.physics.components;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.components.Component;

/**
 * This component ensures that the projectile entity is disposed off when the laser leaves the
 * boundaries of the game window/map. The worldWidth and worldHeight variables are required to
 * establish the game boundaries. update() checks the current position of the entity(i.e. laser) and
 * disposes it if out of bounds.
 */
public class ProjectileBoundsComponent extends Component {
  private final float worldWidth;
  private final float worldHeight;

  public ProjectileBoundsComponent(float worldWidth, float worldHeight) {
    this.worldWidth = worldWidth;
    this.worldHeight = worldHeight;
  }

  @Override
  public void update() {
    Vector2 position = entity.getPosition();
    if (position.x < 0 || position.x > worldWidth || position.y < 0 || position.y > worldHeight) {
      entity.dispose();
    }
  }
}
