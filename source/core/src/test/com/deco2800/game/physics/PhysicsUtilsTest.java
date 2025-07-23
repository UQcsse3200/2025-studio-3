package com.deco2800.game.physics;

import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.EntityService;
import com.deco2800.game.extensions.GameExtension;
import com.deco2800.game.utils.math.Vector2Utils;
import com.deco2800.game.physics.components.ColliderComponent;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.services.ServiceLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(GameExtension.class)
class PhysicsUtilsTest {
  Entity entity;

  @BeforeEach
  void beforeEach() {
    ServiceLocator.registerEntityService(new EntityService());
    ServiceLocator.registerPhysicsService(new PhysicsService());

    entity =
        new Entity().addComponent(new ColliderComponent()).addComponent(new PhysicsComponent());
  }

  @Test
  void shouldScaleFullCollider() {
    setAndCheckScale(new Vector2(3.5f, 4.5f), Vector2Utils.ONE);
  }

  @Test
  void shouldScaleHalfCollider() {
    setAndCheckScale(Vector2Utils.ONE, new Vector2(0.5f, 0.5f));
  }

  @Test
  void shouldScaleBigCollider() {
    setAndCheckScale(new Vector2(0.5f, 0.5f), new Vector2(2f, 2f));
  }

  private void setAndCheckScale(Vector2 entityScale, Vector2 colliderScale) {
    entity.setScale(entityScale);
    PhysicsUtils.setScaledCollider(entity, colliderScale.x, colliderScale.y);
    ServiceLocator.getEntityService().register(entity);

    PhysicsTestUtils.checkPolygonCollider(
        entity.getComponent(ColliderComponent.class), entityScale.cpy().scl(colliderScale));
  }
}
