package com.csse3200.game.components.tile;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.input.InputComponent;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Input handler for tiles for mouse input. This input handler uses touch input. */
public class TileInputComponent extends InputComponent {

  private static final Logger logger = LoggerFactory.getLogger(TileInputComponent.class);

  public TileInputComponent() {
    super(5);
  }

  /**
   * Action on mouse click on entity
   *
   * @param screenX The x coordinate, origin is in the upper left corner
   * @param screenY The y coordinate, origin is in the upper left corner
   * @param pointer the pointer for the event.
   * @param button the button
   * @return true if action taken, otherwise false
   */
  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    Vector2 position = entity.getPosition();

    TileStorageComponent tileStatus = entity.getComponent(TileStorageComponent.class);
    Entity selected_unit = tileStatus.getArea().getSelectedUnit();

    float tileSize = tileStatus.getArea().getTileSize();
    // need to convert grid to click coords
    float stageHeight = ServiceLocator.getRenderService().getStage().getHeight();
    float stageWidth = ServiceLocator.getRenderService().getStage().getWidth();
    float stageToWorldRatio = Renderer.GAME_SCREEN_WIDTH / stageWidth;

    // Is click on entity?
    if (screenX * stageToWorldRatio >= position.x
        && screenX * stageToWorldRatio <= position.x + tileSize
        && screenY * stageToWorldRatio <= (stageHeight * stageToWorldRatio) - position.y
        && screenY * stageToWorldRatio
            >= (stageHeight * stageToWorldRatio) - (position.y + tileSize)) {
      logger.info("Tile Clicked");
      switch (button) {
        case Input.Buttons.LEFT -> {
          if (!tileStatus.hasUnit() && selected_unit != null) {
            tileStatus.triggerSpawnUnit();
          }
          return true;
        }
        case Input.Buttons.RIGHT -> {
          if (tileStatus.hasUnit()) {
            tileStatus.removeTileUnit();
          }
          return true;
        }
        default -> {
          return false;
        }
      }
    }
    return false;
  }
}
