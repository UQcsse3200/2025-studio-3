package com.csse3200.game.components.tile;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.areas.AreaAPI;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.input.InputComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Input handler for tiles for mouse input. This input handler uses touch input. */
public class TileInputComponent extends InputComponent {

  private static final Logger logger = LoggerFactory.getLogger(TileInputComponent.class);

  private final AreaAPI area;

  public TileInputComponent(AreaAPI area) {
    super(5);
    this.area = area;
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
    Entity selectedUnit = area.getSelectedUnit();

    float tileSize = area.getTileSize();
    GridPoint2 clickInWorld = area.stageToWorld(new GridPoint2(screenX, screenY));

    // Is click on entity?
    if (clickInWorld.x >= position.x
        && clickInWorld.x <= position.x + tileSize
        && clickInWorld.y >= position.y
        && clickInWorld.y <= position.y + tileSize) {
      logger.info("Tile Clicked");
      return switch (button) {
        case Input.Buttons.LEFT -> {
          if (!tileStatus.hasUnit() && selectedUnit != null && area.isCharacterSelected()) {
            tileStatus.triggerSpawnUnit();
            area.cancelDrag();
            area.setIsCharacterSelected(false);
            area.setSelectedUnit(null);
          }
          yield true;
        }
        case Input.Buttons.RIGHT -> {
          if (tileStatus.hasUnit()) {
            tileStatus.removeTileUnit();
          }
          yield true;
        }
        default -> false;
      };
    }
    return false;
  }
}
