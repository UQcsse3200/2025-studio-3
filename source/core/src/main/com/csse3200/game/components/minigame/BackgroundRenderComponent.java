package com.csse3200.game.components.minigame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.rendering.RenderComponent;

/**
 * Renders a background texture.
 */
public class BackgroundRenderComponent extends RenderComponent{
  private final Texture backgroundTexture;

  /**
   * Creates a new BackgroundRenderComponent.
   * 
   * @param backgroundTexturePath the path to the background texture
   */
  public BackgroundRenderComponent(String backgroundTexturePath) {
    this.backgroundTexture = ServiceLocator.getResourceService()
        .getAsset(backgroundTexturePath, Texture.class);
  }

  @Override
  protected void draw(SpriteBatch batch) {
    // Draw background to fill the entire screen
    batch.draw(backgroundTexture, 0, 0, 1280f, 720f);
  }

  @Override
  public int getLayer() {
    return 0;
  }

  @Override
  public float getZIndex() {
    return -1000f;
  }
}