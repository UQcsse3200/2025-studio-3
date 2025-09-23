package com.csse3200.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.services.ServiceLocator;

/**
 * A draggable character UI component that can be moved around the screen. The character can be
 * removed by clicking on it and pressing the 'R' key.
 */
public class DraggableCharacter extends UIComponent {
  private Image image; // The image representing the character

  // Default values for the customisable parameters
  private String texturePath = "images/entities/character.png"; // Default texture path
  private float offsetX = 0f; // Default y position
  private float offsetY = 0f; // Default x position
  private float scale = 0.15f; // Scale factor for the image size

  private final LevelGameArea area;

  public DraggableCharacter(LevelGameArea area) {
    this.area = area;
  }

  private static final float GRID_MIN_X = 235f;
  private static final float GRID_MIN_Y = 145f;
  private static final float GRID_MAX_X = 1245f;
  private static final float GRID_MAX_Y = 650f;

  private static final int GRID_COLS = 10;
  private static final int GRID_ROWS = 5;

  private static final float CELL_W = (GRID_MAX_X - GRID_MIN_X) / GRID_COLS;
  private static final float CELL_H = (GRID_MAX_Y - GRID_MIN_Y) / GRID_ROWS;

  /**
   * Initializes the draggable character by adding the image actor to the stage and setting up the
   * drag-and-drop functionality.
   */
  @Override
  public void create() {
    super.create();
    addActors();
    setupDragAndDrop();
  }

  /** Adds the character image actor to the stage with the specified image and scale. */
  private void addActors() {
    image = new Image(ServiceLocator.getResourceService().getAsset(texturePath, Texture.class));
    image.setSize(image.getWidth() * scale, image.getHeight() * scale);
    stage.addActor(image);
  }

  /**
   * Sets up the drag-and-drop functionality for the character image. Allows the image to be dragged
   * around the screen.
   */
  private void setupDragAndDrop() {
    DragAndDrop dragAndDrop = new DragAndDrop();

    dragAndDrop.addSource(
        new DragAndDrop.Source(image) {
          @Override
          public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
            DragAndDrop.Payload payload = new DragAndDrop.Payload();
            Image dragImg = new Image(image.getDrawable());
            dragImg.setSize(image.getWidth(), image.getHeight());
            payload.setDragActor(dragImg); // Attaches the image to payload
            payload.setObject(texturePath); // Sets the texture path as the payload object.
            // This will be helpful for other teams to know which character has been selected
            return payload;
          }

          @Override
          public void dragStop(
              InputEvent event,
              float x,
              float y,
              int pointer,
              DragAndDrop.Payload payload,
              DragAndDrop.Target target) {

            image.setPosition(x - image.getWidth() / 2f, y - image.getHeight() / 2f);
            float stageX = event.getStageX();
            float stageY = event.getStageY();
            if (stageX < GRID_MIN_X
                || stageX >= GRID_MAX_X
                || stageY < GRID_MIN_Y
                || stageY >= GRID_MAX_Y) {
              return;
            }
            int col = (int) ((stageX - GRID_MIN_X) / CELL_W);
            int row = (int) ((stageY - GRID_MIN_Y) / CELL_H);

            if (col < 0 || col >= GRID_COLS || row < 0 || row >= GRID_ROWS) {
              return;
            }

            int tileIndex = row * GRID_COLS + col;
            area.spawnUnit(tileIndex);
          }
        });
  }

  /** Disposes of the character image. */
  @Override
  public void dispose() {
    super.dispose();
    image.remove();
  }

  /**
   * Draws the character image at the specified offsets.
   *
   * @param batch The SpriteBatch used for drawing.
   */
  @Override
  protected void draw(SpriteBatch batch) {
    image.setPosition(offsetX, offsetY);
  }

  /**
   * Gets the current texture path of the character.
   *
   * @return The texture path as a String.
   */
  public String getTexturePath() {
    return this.texturePath;
  }

  /**
   * Gets the current X offset of the character image.
   *
   * @return The X offset as a float.
   */
  public float getOffsetX() {
    return this.offsetX;
  }

  /**
   * Gets the current Y offset of the character image.
   *
   * @return The Y offset as a float.
   */
  public float getOffsetY() {
    return this.offsetY;
  }

  /**
   * Gets the current scale of the character image.
   *
   * @return The scale as a float.
   */
  public float getScale() {
    return this.scale;
  }

  /**
   * A function that checks if the 'R' key is pressed while the mouse is over the character image.
   * If so, it removes the image from the stage and disposes of the entity.
   */
  @Override
  public void update() {
    if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
      float x = Gdx.input.getX();
      float y = Gdx.graphics.getHeight() - (float) Gdx.input.getY();

      if (x >= image.getX()
          && x <= image.getX() + image.getWidth()
          && y >= image.getY()
          && y <= image.getY() + image.getHeight()) {

        Gdx.app.postRunnable(
            () -> {
              image.remove();
              entity.dispose();
            });
      }
    }
  }

  // Setters for the customisable parameters
  public void setTexture(String path) {
    this.texturePath = path;
  }

  public void setOffsets(float x, float y) {
    this.offsetX = x;
    this.offsetY = y;
  }

  public void setScale(float scale) {
    this.scale = scale;
  }
}
