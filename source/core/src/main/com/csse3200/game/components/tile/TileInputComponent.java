package com.csse3200.game.components.tile;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.input.InputComponent;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Input handler for tiles for mouse input.
 * This input handler uses touch input.
 */
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

        TileStatusComponent tileStatus = entity.getComponent(TileStatusComponent.class);
        Entity selected_unit = tileStatus.getArea().getSelectedUnit();
        float tileSize = tileStatus.getArea().getTileSize();
        float screenHeight = ServiceLocator.getRenderService().getStage().getHeight();

        Vector2 position = entity.getPosition();
        logger.info("Entity position is ({}, {})", position.x, position.y);
        logger.info("Click position is ({}, {})", screenX, screenY);

        // Is click on entity?
        if (screenX >= position.x
                && screenX <= position.x + tileSize
                && screenY <= screenHeight - position.y
                && screenY >= screenHeight - (position.y + tileSize)) {

            switch (button) {
                case Input.Buttons.LEFT -> {
                    if (!tileStatus.hasUnit() && selected_unit != null) {
                        tileStatus.addUnit();
                    }
                    return true;
                }
                case Input.Buttons.RIGHT -> {
                    if (tileStatus.hasUnit()) {
                        tileStatus.removeUnit();
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
