package com.csse3200.game.components;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(GameExtension.class)
@ExtendWith(MockitoExtension.class)
class HitMarkerComponentTest {
  @Mock Texture texture;

  @BeforeEach
  void beforeEach() {
    ServiceLocator.registerPhysicsService(new PhysicsService());
    ServiceLocator.registerRenderService(new RenderService());
  }

  @Test
  void NoFlashTime() {
    Entity entity = createTarget();
    HitMarkerComponent hitMarker = entity.getComponent(HitMarkerComponent.class);
    assertEquals(0, hitMarker.flashTime);
  }

  @Test
  void FlashTimeOnHit() {
    Entity entity = createTarget();
    HitMarkerComponent hitMarker = entity.getComponent(HitMarkerComponent.class);
    entity.getEvents().trigger("hitMarker", entity);
    assertTrue(hitMarker.flashTime > 0);
  }

  @Test
  void NoFlashColour() {
    Entity entity = createTarget();
    HitMarkerComponent hitMarker = entity.getComponent(HitMarkerComponent.class);
    assertEquals(Color.WHITE, hitMarker.render.getColour());
  }

  @Test
  void FlashColourOnHit() {
    Entity entity = createTarget();
    HitMarkerComponent hitMarker = entity.getComponent(HitMarkerComponent.class);
    entity.getEvents().trigger("hitMarker", entity);
    hitMarker.update(); // Update to apply flash colour
    assertEquals(HitMarkerComponent.FLASH_COLOUR, hitMarker.render.getColour());
  }

  @Test
  void FlashColourReset() {
    Entity entity = createTarget();
    HitMarkerComponent hitMarker = entity.getComponent(HitMarkerComponent.class);
    entity.getEvents().trigger("hitMarker", entity);
    // Simulate enough time passing to exceed flash duration
    Gdx.graphics = mock(Graphics.class);
    when(Gdx.graphics.getDeltaTime()).thenReturn(0.2f);
    hitMarker.update();
      assertEquals(HitMarkerComponent.FLASH_COLOUR, hitMarker.render.getColour());
    hitMarker.update();
    assertEquals(Color.WHITE, hitMarker.render.getColour());
  }

  @Test
  void NoFlashWithoutRenderComponent() {
    Entity entity = new Entity().addComponent(new HitMarkerComponent());
    entity.create();
    // No exceptions should be thrown and entity will not flash
    assertDoesNotThrow(() -> entity.getEvents().trigger("hitMarker", entity));
  }

  Entity createTarget() {
    Entity target =
        new Entity()
            .addComponent(new TextureRenderComponent(texture))
            .addComponent(new HitMarkerComponent());
    target.create();
    return target;
  }
}
