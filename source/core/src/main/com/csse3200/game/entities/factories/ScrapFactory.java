package com.csse3200.game.entities.factories;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.components.currency.ScrapComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.rendering.TextureRenderComponent;

public final class ScrapFactory {
  private static final String SCRAP_TEXTURE = "images/scrap_metal.png";

  private ScrapFactory() {}

  public static Entity createScrap(Vector2 worldPos) {
    Entity e =
        new Entity()
            .addComponent(new ScrapComponent())
            .addComponent(new TextureRenderComponent(SCRAP_TEXTURE));
    e.setPosition(worldPos);
    return e;
  }
}
