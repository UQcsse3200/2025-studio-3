package com.csse3200.game.physics;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.components.ProjectileBoundsComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProjectileBoundsComponentTest {
  private static final float width = 15f;
  private static final float height = 15f;
  private boolean disposed;
  private Entity laser;

  @BeforeEach
  void setup() {
    disposed = false;
    laser =
        new Entity() {
          private Vector2 position = new Vector2(0f, 0f);

          @Override
          public Vector2 getPosition() {
            return position;
          }

          @Override
          public void setPosition(float x, float y) {
            position.set(x, y);
          }

          @Override
          public void dispose() {
            disposed = true;
          }
        };

    ProjectileBoundsComponent bounds = new ProjectileBoundsComponent(width, height);
    laser.addComponent(bounds);
    laser.create();
  }

  @Test
  void projectileWithinBounds() {
    laser.setPosition(5f, 5f);
    laser.update();
    assertFalse(disposed, " Projectile within bounds not disposed");
  }

  @Test
  void projectileOnBoundary() {
    laser.setPosition(0f, 0f);
    laser.update();
    assertFalse(disposed, " Projectile within bounds not disposed");
  }

  @Test
  void projectileOutOfBoundsDisposed() {
    laser.setPosition(width + 1, height + 1);
    laser.update();
    assertTrue(disposed, " Projectile out of bounds disposed");
  }

  @Test
  void projectileOutOfBoundsBelowDisposed() {
    laser.setPosition(-1f, -1f);
    laser.update();
    assertTrue(disposed, " Projectile out of bounds disposed");
  }
}
