package com.csse3200.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.areas.AreaAPI;
import com.csse3200.game.services.ServiceLocator;

/**
 * A simple overlay image that follows the cursor while dragging. When the cursor clicks on a tile,
 * the drag is cancelled.
 */
public class DragOverlay extends UIComponent {
  private final AreaAPI area; // Reference to the game area
  private Image image; // The image actor to display the drag texture
  private boolean active = false; // Flag to indicate if a drag is in progress
  float sizeScale = 0.8f; // Scale of the image

  /**
   * Create a drag overlay for the given area.
   *
   * @param area Area to place units in.
   */
  public DragOverlay(com.csse3200.game.areas.AreaAPI area) {
    this.area = area;
  }

  /** Creates the drag overlay image actor and adds it to the stage. */
  @Override
  public void create() {
    super.create();
    image = new Image();
    image.setVisible(false); // Hidden until begin() is called
    stage.addActor(image); // Add the image actor to the stage
  }

  /**
   * Start a drag with the given texture.
   *
   * @param texture Texture to show while dragging.
   */
  public void begin(Texture texture) {
    if (texture == null) return;
    image.setDrawable(new TextureRegionDrawable(texture));
    float size = area.getTileSize() * sizeScale; // Set Scale
    image.setSize(size, size);
    image.setVisible(true); // Show the image
    active = true;
  }

  /** Stop the drag without placing. */
  public void cancel() {
    active = false;
    image.setVisible(false);
  }

  /**
   * Renders the drag image if active.
   *
   * @param batch Batch to render to.
   */
  @Override
  protected void draw(SpriteBatch batch) {
    // it's required by the superclass however, it is not used because Scene2D handles rendering

  }

  /**
   * Sets the scale of the drag image.
   *
   * @param scale
   */
  public void setImageScale(float scale) {
    this.sizeScale = scale;
    if (active) {
      float size = area.getTileSize() * scale;
      image.setSize(size, size);
    }
  }

  /** Updates the position of the drag image to follow the cursor. */
  @Override
  public void update() {
    if (!active) return;

    float stageH = ServiceLocator.getRenderService().getStage().getHeight();
    float mx = Gdx.input.getX();
    float my = Gdx.input.getY();

    // Center the image on the cursor
    float x = mx - image.getWidth() / 2f;
    float y = (stageH - my) - image.getHeight() / 2f;
    image.setPosition(x, y);
  }

  /** Cancels a drag in progress. */
  @Override
  public void dispose() {
    image.remove();
  }
}
