package com.csse3200.game.entities.factories;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.components.currency.SunlightComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.rendering.TextureRenderComponent;

public final class SunlightFactory {
  private static final String SUN_TEXTURE = "images/normal_sunlight.png";

  private SunlightFactory() {}

  public static Entity createSunlight(Vector2 worldPos) {
    Entity e =
        new Entity()
            .addComponent(new SunlightComponent())
            .addComponent(new TextureRenderComponent(SUN_TEXTURE));
    e.setPosition(worldPos);
    return e;
  }
}
