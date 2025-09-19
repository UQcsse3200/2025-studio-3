package com.csse3200.game.rendering;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Draws a single texture as the world background, positioned at (0,0). Scales to fill the camera
 * viewport height; width is scaled by aspect.
 */
public class BackgroundMapComponent extends RenderComponent {
  private final Sprite sprite;
  private final float worldWidth;
  private final float worldHeight;

  public BackgroundMapComponent(Texture texture, float worldHeight) {
    this.worldHeight = worldHeight;
    float scale = worldHeight / texture.getHeight();
    this.worldWidth = texture.getWidth() * scale;

    this.sprite = new Sprite(texture);
    this.sprite.setSize(worldWidth, worldHeight);
    this.sprite.setPosition(0f, 0f); // left-aligned, bottom-aligned
  }

  public float getWorldWidth() {
    return worldWidth;
  }

  public float getWorldHeight() {
    return worldHeight;
  }

  @Override
  public void draw(SpriteBatch batch) {
    sprite.draw(batch);
  }

  @Override
  public float getZIndex() {
    return 0f; // behind everything
  }

  @Override
  public int getLayer() {
    return 0; // terrain layer
  }
}
