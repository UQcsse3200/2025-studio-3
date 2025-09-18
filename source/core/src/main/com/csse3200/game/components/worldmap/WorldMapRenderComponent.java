package com.csse3200.game.components.worldmap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;

/** Renders the world map background */
public class WorldMapRenderComponent extends UIComponent {
  private static final String WORLD_MAP_TEXTURE = "images/world_map.png";
  private final Vector2 worldSize;

  /**
   * Constructor for the world map render component.
   *
   * @param worldSize the size of the world map
   */
  public WorldMapRenderComponent(Vector2 worldSize) {
    this.worldSize = worldSize;
  }

  @Override
  protected void draw(SpriteBatch batch) {
    Texture worldMapTexture =
        ServiceLocator.getResourceService().getAsset(WORLD_MAP_TEXTURE, Texture.class);
    batch.draw(worldMapTexture, 0, 0, worldSize.x, worldSize.y);
  }
}
