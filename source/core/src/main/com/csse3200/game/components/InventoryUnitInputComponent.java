package com.csse3200.game.components;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.areas.AreaAPI;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.input.InputComponent;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.ServiceLocator;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Input handler for inventory units for mouse input. This input handler uses touch input. */
public class InventoryUnitInputComponent extends InputComponent {

  private static final Logger logger = LoggerFactory.getLogger(InventoryUnitInputComponent.class);
  private final AreaAPI area;
  private final float tileSize;
  private Supplier<Entity> supplier;

  public InventoryUnitInputComponent(AreaAPI area, Supplier<Entity> supplier) {
    super(5);
    this.area = area;
    tileSize = area.getTileSize();
    this.supplier = supplier;
  }

  /**
   * Getter for the supplier, used when we need an instance of the inventory unit
   *
   * @return the supplier for that entity
   */
  public Supplier<Entity> getEntitySupplier() {
    return this.supplier;
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

    // need to convert grid to click coords
    float stageHeight = ServiceLocator.getRenderService().getStage().getHeight();
    float stageWidth = ServiceLocator.getRenderService().getStage().getWidth();
    float stageToWorldRatio = Renderer.GAME_SCREEN_WIDTH / stageWidth;

    // Is click on entity
    if (screenX * stageToWorldRatio >= position.x
        && screenX * stageToWorldRatio <= position.x + tileSize
        && screenY * stageToWorldRatio <= (stageHeight * stageToWorldRatio) - position.y
        && screenY * stageToWorldRatio
            >= (stageHeight * stageToWorldRatio) - (position.y + tileSize)) {
      logger.info("Inventory Entity clicked");
      return switch (button) {
        case Input.Buttons.LEFT -> {
          area.setSelectedUnit(entity);
          yield true;
        }
        case Input.Buttons.RIGHT -> {
          area.setSelectedUnit(null);
          yield true;
        }
        default -> false;
      };
    }
    return false;
  }
}
